package com.divyapath.app.ui.mantra;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.divyapath.app.data.local.entity.MantraEntity;
import com.divyapath.app.data.repository.DivyaPathRepository;

import java.util.List;

public class MantraViewModel extends AndroidViewModel {

    private final DivyaPathRepository repo;
    private final MutableLiveData<String> selectedCategory = new MutableLiveData<>(null);
    private final LiveData<List<MantraEntity>> filteredMantras;

    public MantraViewModel(@NonNull Application app) {
        super(app);
        repo = new DivyaPathRepository(app);
        filteredMantras = Transformations.switchMap(selectedCategory, category -> {
            if (category == null || category.isEmpty()) {
                return repo.getAllMantras();
            }
            return repo.getMantrasByCategory(category);
        });
    }

    public LiveData<List<String>> getCategories() {
        return repo.getAllMantraCategories();
    }

    public LiveData<List<MantraEntity>> getFilteredMantras() {
        return filteredMantras;
    }

    public void setSelectedCategory(String category) {
        selectedCategory.setValue(category);
    }

    public LiveData<MantraEntity> getMantraById(int id) {
        return repo.getMantraById(id);
    }
}
