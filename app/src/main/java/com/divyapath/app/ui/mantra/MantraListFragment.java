package com.divyapath.app.ui.mantra;
import android.os.Bundle; import android.view.*; import androidx.annotation.*; import androidx.fragment.app.Fragment; import androidx.lifecycle.ViewModelProvider; import androidx.navigation.Navigation; import androidx.recyclerview.widget.LinearLayoutManager;
import com.divyapath.app.R; import com.divyapath.app.databinding.FragmentMantraListBinding; import com.divyapath.app.ui.adapters.MantraListAdapter; import com.google.android.material.chip.Chip;
import com.google.android.gms.ads.AdRequest;
public class MantraListFragment extends Fragment {
    private FragmentMantraListBinding binding; private MantraViewModel vm;
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater i,@Nullable ViewGroup c,@Nullable Bundle b){binding=FragmentMantraListBinding.inflate(i,c,false);return binding.getRoot();}
    @Override public void onViewCreated(@NonNull View v,@Nullable Bundle b){super.onViewCreated(v,b);vm=new ViewModelProvider(this).get(MantraViewModel.class);
        MantraListAdapter a=new MantraListAdapter(m->{Bundle args=new Bundle();args.putInt("contentId",m.getId());Navigation.findNavController(v).navigate(R.id.mantraDetailFragment,args);});
        binding.rvMantraList.setLayoutManager(new LinearLayoutManager(requireContext()));binding.rvMantraList.setAdapter(a);
        vm.getCategories().observe(getViewLifecycleOwner(),cats->{binding.chipGroupCategories.removeAllViews();Chip all=new Chip(requireContext());all.setText("All");all.setCheckable(true);all.setChecked(true);all.setOnClickListener(x->vm.setSelectedCategory(null));binding.chipGroupCategories.addView(all);
            if(cats!=null)for(String c:cats){Chip ch=new Chip(requireContext());ch.setText(c.substring(0,1).toUpperCase()+c.substring(1));ch.setCheckable(true);ch.setOnClickListener(x->vm.setSelectedCategory(c));binding.chipGroupCategories.addView(ch);}});
        vm.getFilteredMantras().observe(getViewLifecycleOwner(),a::submitList);
        binding.adBannerMantra.loadAd(new AdRequest.Builder().build());}
    @Override public void onDestroyView(){super.onDestroyView();binding=null;}
}
