package com.divyapath.app.audio;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.divyapath.app.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

/**
 * Bottom sheet dialog that embeds a YouTube player for bhajan/aarti playback.
 * Usage: YoutubePlayerSheet.newInstance("videoId", "Aarti Title", "Deity Name").show(...)
 */
public class YoutubePlayerSheet extends BottomSheetDialogFragment {

    private static final String ARG_VIDEO_ID = "video_id";
    private static final String ARG_TITLE = "title";
    private static final String ARG_SUBTITLE = "subtitle";

    private YouTubePlayerView youTubePlayerView;

    public static YoutubePlayerSheet newInstance(String videoId, String title, String subtitle) {
        YoutubePlayerSheet sheet = new YoutubePlayerSheet();
        Bundle args = new Bundle();
        args.putString(ARG_VIDEO_ID, videoId);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_SUBTITLE, subtitle);
        sheet.setArguments(args);
        return sheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sheet_youtube_player, container, false);

        TextView tvTitle = view.findViewById(R.id.tv_yt_title);
        TextView tvSubtitle = view.findViewById(R.id.tv_yt_subtitle);
        ImageButton btnClose = view.findViewById(R.id.btn_yt_close);
        youTubePlayerView = view.findViewById(R.id.youtube_player_view);

        Bundle args = getArguments();
        String videoId = args != null ? args.getString(ARG_VIDEO_ID, "") : "";
        String title = args != null ? args.getString(ARG_TITLE, "") : "";
        String subtitle = args != null ? args.getString(ARG_SUBTITLE, "") : "";

        tvTitle.setText(title);
        tvSubtitle.setText(subtitle);

        btnClose.setOnClickListener(v -> dismiss());

        // Lifecycle-aware YouTube player
        getLifecycle().addObserver(youTubePlayerView);

        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer ytPlayer) {
                ytPlayer.loadVideo(videoId, 0);
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        if (youTubePlayerView != null) {
            youTubePlayerView.release();
        }
        super.onDestroyView();
    }
}
