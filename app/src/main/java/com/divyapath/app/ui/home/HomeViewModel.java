package com.divyapath.app.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.divyapath.app.data.local.DivyaPathDatabase;
import com.divyapath.app.data.local.entity.AartiEntity;
import com.divyapath.app.data.local.entity.DeityEntity;
import com.divyapath.app.data.local.entity.FestivalEntity;
import com.divyapath.app.data.repository.DivyaPathRepository;
import com.divyapath.app.utils.PanchangCalculator;
import com.divyapath.app.utils.PanchangInsightEngine;
import com.divyapath.app.utils.PreferenceManager;
import com.divyapath.app.utils.SevaData;
import com.divyapath.app.utils.ShlokaLoader;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class HomeViewModel extends AndroidViewModel {

    private final DivyaPathRepository repository;
    private final PreferenceManager preferenceManager;

    private final MutableLiveData<String> greeting = new MutableLiveData<>();
    private final MutableLiveData<String> dateText = new MutableLiveData<>();
    private final MutableLiveData<Map<String, String>> panchangData = new MutableLiveData<>();
    private final MutableLiveData<List<PanchangInsightEngine.PanchangInsight>> panchangInsights = new MutableLiveData<>();
    private final MutableLiveData<ShlokaData> dailyShloka = new MutableLiveData<>();
    private final LiveData<DeityEntity> todaysDeity;
    private final LiveData<List<AartiEntity>> todaysAartis;
    private final LiveData<List<AartiEntity>> popularAartis;
    private final LiveData<FestivalEntity> todaysFestival;
    private final LiveData<FestivalEntity> nextFestival;
    private final MutableLiveData<SevaData.SevaItem> todaysSeva = new MutableLiveData<>();
    private final MutableLiveData<Boolean> sevaCompletedToday = new MutableLiveData<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);
        repository = new DivyaPathRepository(application);
        preferenceManager = new PreferenceManager(application);

        // Lightweight operations — safe on main thread
        String userName = preferenceManager.getUserName();
        String greetingText = PanchangCalculator.getGreeting();
        greeting.setValue(greetingText + " " + userName + " \uD83D\uDE4F");

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.ENGLISH);
        dateText.setValue(sdf.format(new Date()));

        // Set today's deity (Room LiveData — already async)
        int dayOfWeek = PanchangCalculator.getTodaysDayOfWeek();
        todaysDeity = repository.getDeityByDay(dayOfWeek);

        // Set today's aartis (deity-specific for the day — Room LiveData, async)
        todaysAartis = repository.getAartisByDeity(dayOfWeek);

        // Set popular aartis (separate dataset — Room LiveData, async)
        popularAartis = repository.getPopularAartis(6);

        // Today's festival (Room LiveData, async)
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());
        todaysFestival = repository.getFestivalByDate(todayDate);
        nextFestival = repository.getNextFestival(todayDate);

        // Seva — lightweight SharedPrefs read
        todaysSeva.setValue(SevaData.getTodaysSeva());
        sevaCompletedToday.setValue(preferenceManager.getSharedPreferences()
                .getBoolean("seva_" + todayDate, false));

        // Heavy operations — run on background thread
        ExecutorService executor = DivyaPathDatabase.databaseWriteExecutor;
        executor.execute(() -> {
            // Panchang calculation (astronomical math — can be ~10-50ms)
            Map<String, String> panchang = PanchangCalculator.getPanchangForUser(application);
            panchangData.postValue(panchang);

            // Generate panchang insights from calculated data
            PanchangInsightEngine.InsightResult insightResult =
                    PanchangInsightEngine.generateInsights(panchang, Calendar.getInstance());
            panchangInsights.postValue(insightResult.getCombined(3, 2));

            // Load shloka from JSON assets (file I/O)
            loadDailyShloka();
        });
    }

    private void loadDailyShloka() {
        // Try loading from assets/shlokas.json first
        ShlokaLoader.Shloka shloka = ShlokaLoader.getTodaysShloka(getApplication());
        if (shloka != null) {
            dailyShloka.postValue(new ShlokaData(
                    shloka.sanskrit,
                    shloka.englishMeaning,
                    "— " + shloka.source
            ));
        } else {
            // Fallback
            dailyShloka.postValue(new ShlokaData(
                    "कर्मण्येवाधिकारस्ते मा फलेषु कदाचन ।\nमा कर्मफलहेतुर्भूर्मा ते सङ्गोऽस्त्वकर्मणि ॥",
                    "You have the right to perform your duty, but never to the fruits of your actions.",
                    "— Bhagavad Gita 2.47"
            ));
        }
    }

    public LiveData<String> getGreeting() { return greeting; }
    public LiveData<String> getDateText() { return dateText; }
    public LiveData<Map<String, String>> getPanchangData() { return panchangData; }
    public LiveData<DeityEntity> getTodaysDeity() { return todaysDeity; }
    public LiveData<List<AartiEntity>> getTodaysAartis() { return todaysAartis; }
    public LiveData<List<AartiEntity>> getPopularAartis() { return popularAartis; }
    public LiveData<ShlokaData> getDailyShloka() { return dailyShloka; }
    public LiveData<FestivalEntity> getTodaysFestival() { return todaysFestival; }
    public LiveData<FestivalEntity> getNextFestival() { return nextFestival; }
    public LiveData<List<PanchangInsightEngine.PanchangInsight>> getPanchangInsights() { return panchangInsights; }
    public LiveData<SevaData.SevaItem> getTodaysSeva() { return todaysSeva; }
    public LiveData<Boolean> getSevaCompletedToday() { return sevaCompletedToday; }

    public static class ShlokaData {
        public final String sanskrit;
        public final String translation;
        public final String source;

        public ShlokaData(String sanskrit, String translation, String source) {
            this.sanskrit = sanskrit;
            this.translation = translation;
            this.source = source;
        }
    }
}
