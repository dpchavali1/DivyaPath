package com.divyapath.app.ui.chalisa;
import android.app.Application; import androidx.annotation.NonNull; import androidx.lifecycle.*;
import com.divyapath.app.data.local.entity.ChalisaEntity; import com.divyapath.app.data.repository.DivyaPathRepository; import java.util.List;
public class ChalisaViewModel extends AndroidViewModel {
    private final DivyaPathRepository repo; private final LiveData<List<ChalisaEntity>> all;
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final LiveData<List<ChalisaEntity>> searchResults;
    public ChalisaViewModel(@NonNull Application app){super(app);repo=new DivyaPathRepository(app);all=repo.getAllChalisas();
        searchResults = Transformations.switchMap(searchQuery, q -> {
            if (q == null || q.trim().isEmpty()) return all;
            return repo.searchChalisas(q.trim());
        });
    }
    public LiveData<List<ChalisaEntity>> getAllChalisas(){return all;}
    public LiveData<ChalisaEntity> getChalisaById(int id){return repo.getChalisaById(id);}
    public LiveData<List<ChalisaEntity>> getSearchResults(){return searchResults;}
    public void setSearchQuery(String query){searchQuery.setValue(query);}
}
