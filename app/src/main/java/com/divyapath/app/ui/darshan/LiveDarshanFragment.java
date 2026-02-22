package com.divyapath.app.ui.darshan;
import android.content.Intent; import android.net.Uri; import android.os.Bundle; import android.view.*;
import androidx.annotation.*; import androidx.fragment.app.Fragment; import androidx.lifecycle.ViewModelProvider; import androidx.recyclerview.widget.LinearLayoutManager; import androidx.recyclerview.widget.RecyclerView;
import com.divyapath.app.R;
public class LiveDarshanFragment extends Fragment {
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater i,@Nullable ViewGroup c,@Nullable Bundle b){return i.inflate(R.layout.fragment_live_darshan,c,false);}
    @Override public void onViewCreated(@NonNull View v,@Nullable Bundle b){super.onViewCreated(v,b);
        DarshanViewModel vm=new ViewModelProvider(this).get(DarshanViewModel.class);
        TempleAdapter adapter=new TempleAdapter(true,t->{
            if(t.getYoutubeUrl()!=null&&!t.getYoutubeUrl().isEmpty()){
                startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(t.getYoutubeUrl())));}});
        RecyclerView rv=v.findViewById(R.id.rv_live_temples);rv.setLayoutManager(new LinearLayoutManager(requireContext()));rv.setAdapter(adapter);
        vm.getLiveTemples().observe(getViewLifecycleOwner(),adapter::submitList);
    }
}
