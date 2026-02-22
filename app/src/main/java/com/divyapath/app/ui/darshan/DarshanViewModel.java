package com.divyapath.app.ui.darshan;
import android.app.Application; import androidx.annotation.NonNull; import androidx.lifecycle.*;
import com.divyapath.app.data.local.entity.TempleEntity; import com.divyapath.app.data.repository.DivyaPathRepository; import java.util.List;
public class DarshanViewModel extends AndroidViewModel {
    private final DivyaPathRepository repository;
    private final LiveData<List<TempleEntity>> allTemples;
    private final LiveData<List<TempleEntity>> liveTemples;
    public DarshanViewModel(@NonNull Application app){super(app);repository=new DivyaPathRepository(app);
        allTemples=repository.getAllTemples();liveTemples=repository.getLiveDarshanTemples();}
    public LiveData<List<TempleEntity>> getAllTemples(){return allTemples;}
    public LiveData<List<TempleEntity>> getLiveTemples(){return liveTemples;}
    public LiveData<TempleEntity> getTempleById(int id){return repository.getTempleById(id);}
}
