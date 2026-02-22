package com.divyapath.app.ui.chalisa;
import android.os.Bundle; import android.view.*; import androidx.annotation.*; import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment; import androidx.lifecycle.ViewModelProvider; import androidx.navigation.Navigation; import androidx.recyclerview.widget.GridLayoutManager;
import com.divyapath.app.R; import com.divyapath.app.databinding.FragmentChalisaListBinding; import com.divyapath.app.ui.adapters.ChalisaListAdapter;
import com.google.android.gms.ads.AdRequest;
public class ChalisaListFragment extends Fragment {
    private FragmentChalisaListBinding binding;
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater i,@Nullable ViewGroup c,@Nullable Bundle b){binding=FragmentChalisaListBinding.inflate(i,c,false);return binding.getRoot();}
    @Override public void onViewCreated(@NonNull View v,@Nullable Bundle b){super.onViewCreated(v,b);ChalisaViewModel vm=new ViewModelProvider(this).get(ChalisaViewModel.class);
        ChalisaListAdapter a=new ChalisaListAdapter(ch->{Bundle args=new Bundle();args.putInt("contentId",ch.getId());Navigation.findNavController(v).navigate(R.id.chalisaDetailFragment,args);});
        binding.rvChalisaList.setLayoutManager(new GridLayoutManager(requireContext(),2));binding.rvChalisaList.setAdapter(a);
        vm.getSearchResults().observe(getViewLifecycleOwner(),a::submitList);
        binding.searchChalisa.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override public boolean onQueryTextSubmit(String q){return false;}
            @Override public boolean onQueryTextChange(String q){vm.setSearchQuery(q);return true;}
        });
        binding.toolbarChalisa.setNavigationOnClickListener(x -> Navigation.findNavController(v).popBackStack());
        binding.adBannerChalisa.loadAd(new AdRequest.Builder().build());}
    @Override public void onDestroyView(){super.onDestroyView();binding=null;}
}
