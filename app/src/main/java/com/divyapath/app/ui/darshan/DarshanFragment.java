package com.divyapath.app.ui.darshan;
import android.os.Bundle; import android.view.*; import androidx.annotation.*; import androidx.fragment.app.Fragment;
import com.divyapath.app.databinding.FragmentDarshanBinding;
import com.google.android.material.tabs.TabLayoutMediator;
public class DarshanFragment extends Fragment {
    private FragmentDarshanBinding binding;
    private static final String[] TABS={"Live Darshan","Temple Directory","Pilgrimage Log"};
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater i,@Nullable ViewGroup c,@Nullable Bundle b){binding=FragmentDarshanBinding.inflate(i,c,false);return binding.getRoot();}
    @Override public void onViewCreated(@NonNull View v,@Nullable Bundle b){super.onViewCreated(v,b);
        binding.viewPagerDarshan.setAdapter(new DarshanPagerAdapter(this));
        new TabLayoutMediator(binding.tabLayoutDarshan,binding.viewPagerDarshan,(tab,pos)->tab.setText(TABS[pos])).attach();
        binding.toolbarDarshan.setNavigationOnClickListener(x->androidx.navigation.Navigation.findNavController(v).popBackStack());}
    @Override public void onDestroyView(){super.onDestroyView();binding=null;}
}
