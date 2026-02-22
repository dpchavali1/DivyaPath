package com.divyapath.app.ui.shraddha;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.divyapath.app.data.local.entity.ShraddhaEntity;
import com.divyapath.app.data.repository.DivyaPathRepository;

import java.util.List;

public class ShraddhaViewModel extends AndroidViewModel {

    private final DivyaPathRepository repository;
    private final LiveData<List<ShraddhaEntity>> allShraddha;

    public ShraddhaViewModel(@NonNull Application application) {
        super(application);
        repository = new DivyaPathRepository(application);
        allShraddha = repository.getAllShraddha();
    }

    public LiveData<List<ShraddhaEntity>> getAllShraddha() {
        return allShraddha;
    }

    public LiveData<ShraddhaEntity> getShraddhaById(int id) {
        return repository.getShraddhaById(id);
    }

    public void insert(ShraddhaEntity shraddha) {
        repository.insertShraddha(shraddha);
    }

    public void update(ShraddhaEntity shraddha) {
        repository.updateShraddha(shraddha);
    }

    public void delete(ShraddhaEntity shraddha) {
        repository.deleteShraddha(shraddha);
    }
}
