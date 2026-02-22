package com.divyapath.app.ui.bhajan;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.divyapath.app.data.local.entity.BhajanEntity;
import com.divyapath.app.data.repository.DivyaPathRepository;

import java.util.List;

public class BhajanViewModel extends AndroidViewModel {

    private final DivyaPathRepository repo;
    private final LiveData<List<BhajanEntity>> allBhajans;
    private final MutableLiveData<String> categoryFilter = new MutableLiveData<>("All");
    private final LiveData<List<BhajanEntity>> filteredBhajans;

    public BhajanViewModel(@NonNull Application app) {
        super(app);
        repo = new DivyaPathRepository(app);
        allBhajans = repo.getAllBhajans();
        filteredBhajans = Transformations.switchMap(categoryFilter, category -> {
            if (category == null || category.equals("All")) {
                return allBhajans;
            }
            return repo.getBhajansByCategory(category);
        });
    }

    public LiveData<List<BhajanEntity>> getAllBhajans() {
        return allBhajans;
    }

    public LiveData<List<BhajanEntity>> getFilteredBhajans() {
        return filteredBhajans;
    }

    public LiveData<BhajanEntity> getBhajanById(int id) {
        return repo.getBhajanById(id);
    }

    public void setCategory(String category) {
        categoryFilter.setValue(category);
    }
}
