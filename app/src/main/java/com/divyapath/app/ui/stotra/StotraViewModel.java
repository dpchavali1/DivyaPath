package com.divyapath.app.ui.stotra;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.divyapath.app.data.local.entity.StotraEntity;
import com.divyapath.app.data.repository.DivyaPathRepository;

import java.util.List;

public class StotraViewModel extends AndroidViewModel {

    private final DivyaPathRepository repo;
    private final LiveData<List<StotraEntity>> allStotras;

    public StotraViewModel(@NonNull Application app) {
        super(app);
        repo = new DivyaPathRepository(app);
        allStotras = repo.getAllStotras();
    }

    public LiveData<List<StotraEntity>> getAllStotras() {
        return allStotras;
    }

    public LiveData<StotraEntity> getStotraById(int id) {
        return repo.getStotraById(id);
    }
}
