package com.divyapath.app.audio;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * Microsoft Edge TTS client using WebSocket protocol.
 *
 * Uses the same neural voices as Microsoft Edge's "Read Aloud" feature.
 * Completely free — no API key, no billing, no limits.
 *
 * Hindi voices:
 *   - hi-IN-SwaraNeural  (female, warm and clear)
 *   - hi-IN-MadhurNeural (male, deep and natural)
 *
 * Protocol:
 *   1. Connect to Edge TTS WebSocket endpoint
 *   2. Send speech.config message (output format)
 *   3. Send ssml message (text + voice + prosody)
 *   4. Receive binary audio chunks (parse header, extract audio data)
 *   5. Receive turn.end text message when synthesis is complete
 */
public class EdgeTtsClient {

    private static final String TAG = "EdgeTtsClient";

    // Edge TTS WebSocket endpoint
    private static final String WSS_URL =
            "wss://speech.platform.bing.com/consumer/speech/synthesize/readaloud/edge/v1"
                    + "?TrustedClientToken=6A5AA1D4EAFF4E9FB37E23D68491D6F4"
                    + "&ConnectionId=";

    // Audio output format — MP3 is universally supported on Android
    private static final String OUTPUT_FORMAT = "audio-24khz-48kbitrate-mono-mp3";

    // Voice IDs
    public static final String VOICE_FEMALE = "hi-IN-SwaraNeural";
    public static final String VOICE_MALE = "hi-IN-MadhurNeural";

    // Timeout for synthesis
    private static final long SYNTHESIS_TIMEOUT_SECONDS = 30;

    private final OkHttpClient httpClient;

    public EdgeTtsClient() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
    }

    /**
     * Synthesize SSML to an MP3 file.
     *
     * @param ssml       the SSML markup (must include voice name)
     * @param outputFile the file to write MP3 audio to
     * @return true if synthesis succeeded
     */
    public boolean synthesize(@NonNull String ssml, @NonNull File outputFile) {
        String connectionId = UUID.randomUUID().toString().replace("-", "");
        String requestId = UUID.randomUUID().toString().replace("-", "");
        String timestamp = getTimestamp();

        CountDownLatch completionLatch = new CountDownLatch(1);
        ByteArrayOutputStream audioBuffer = new ByteArrayOutputStream();
        boolean[] success = {false};

        String url = WSS_URL + connectionId;
        Request request = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
                        + "AppleWebKit/537.36 (KHTML, like Gecko) "
                        + "Chrome/120.0.0.0 Safari/537.36 Edg/120.0.0.0")
                .addHeader("Origin", "chrome-extension://jdiccldimpdaibmpdmdrat")
                .build();

        WebSocket ws = httpClient.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                // Step 1: Send speech config
                String configMessage = "X-Timestamp:" + timestamp + "\r\n"
                        + "Content-Type:application/json; charset=utf-8\r\n"
                        + "Path:speech.config\r\n\r\n"
                        + "{\"context\":{\"synthesis\":{\"audio\":{"
                        + "\"metadataoptions\":{\"sentenceBoundaryEnabled\":\"false\","
                        + "\"wordBoundaryEnabled\":\"false\"},"
                        + "\"outputFormat\":\"" + OUTPUT_FORMAT + "\"}}}}";
                webSocket.send(configMessage);

                // Step 2: Send SSML
                String ssmlMessage = "X-RequestId:" + requestId + "\r\n"
                        + "Content-Type:application/ssml+xml\r\n"
                        + "X-Timestamp:" + timestamp + "\r\n"
                        + "Path:ssml\r\n\r\n"
                        + ssml;
                webSocket.send(ssmlMessage);
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                // Check for turn.end — synthesis complete
                if (text.contains("Path:turn.end")) {
                    success[0] = true;
                    webSocket.close(1000, "Done");
                    completionLatch.countDown();
                }
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull ByteString bytes) {
                // Binary message: header + audio data
                // Header format: 2 bytes (big-endian) = header length, then header text, then audio
                try {
                    byte[] data = bytes.toByteArray();
                    if (data.length < 2) return;

                    // Read header length (2 bytes, big-endian unsigned short)
                    int headerLen = ((data[0] & 0xFF) << 8) | (data[1] & 0xFF);

                    if (data.length > headerLen + 2) {
                        // Extract audio data after header
                        String header = new String(data, 2, headerLen, StandardCharsets.UTF_8);
                        if (header.contains("Path:audio")) {
                            audioBuffer.write(data, headerLen + 2, data.length - headerLen - 2);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing audio chunk", e);
                }
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t,
                                  @Nullable Response response) {
                Log.e(TAG, "WebSocket failure", t);
                completionLatch.countDown();
            }

            @Override
            public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                completionLatch.countDown();
            }
        });

        // Wait for synthesis to complete
        try {
            completionLatch.await(SYNTHESIS_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Log.e(TAG, "Synthesis interrupted", e);
            ws.cancel();
            return false;
        }

        // Write audio to file
        if (success[0] && audioBuffer.size() > 0) {
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                fos.write(audioBuffer.toByteArray());
                fos.flush();
                Log.d(TAG, "Synthesized " + audioBuffer.size() + " bytes to " + outputFile.getName());
                return true;
            } catch (IOException e) {
                Log.e(TAG, "Failed to write audio file", e);
                return false;
            }
        }

        if (!success[0]) {
            Log.w(TAG, "Synthesis did not complete successfully");
        }
        return false;
    }

    private String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date());
    }
}
