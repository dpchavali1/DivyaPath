package com.divyapath.app.ui.aarti;
import android.app.Application; import androidx.annotation.NonNull; import androidx.lifecycle.AndroidViewModel; import androidx.lifecycle.LiveData;
import com.divyapath.app.data.local.entity.AartiEntity; import com.divyapath.app.data.local.entity.DeityEntity; import com.divyapath.app.data.repository.DivyaPathRepository; import java.util.List;
public class AartiViewModel extends AndroidViewModel {
    private final DivyaPathRepository repo; private final LiveData<List<DeityEntity>> deities; private final LiveData<List<AartiEntity>> allAartis;
    public AartiViewModel(@NonNull Application app){super(app);repo=new DivyaPathRepository(app);deities=repo.getAllDeities();allAartis=repo.getAllAartis();}
    public LiveData<List<DeityEntity>> getDeities(){return deities;}
    public LiveData<List<AartiEntity>> getAllAartis(){return allAartis;}
    public LiveData<AartiEntity> getAartiById(int id){return repo.getAartiById(id);}
    public LiveData<List<AartiEntity>> getAartisByDeity(int did){return repo.getAartisByDeity(did);}
    public LiveData<Boolean> isBookmarked(int id){return repo.isBookmarked("aarti",id);}
    public void toggleBookmark(int id,boolean curr){if(curr)repo.removeBookmark("aarti",id);else repo.addBookmark("aarti",id);}
}
