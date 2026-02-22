package com.divyapath.app.ui.panchang;
import android.os.Bundle; import android.view.*; import androidx.annotation.*; import androidx.fragment.app.Fragment;
import com.divyapath.app.databinding.FragmentPanchangBinding;
import com.google.android.material.tabs.TabLayoutMediator;
public class PanchangFragment extends Fragment {
    private FragmentPanchangBinding binding;
    private static final String[] TABS={"Today","Calendar","Muhurat"};
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater i,@Nullable ViewGroup c,@Nullable Bundle b){binding=FragmentPanchangBinding.inflate(i,c,false);return binding.getRoot();}
    @Override public void onViewCreated(@NonNull View v,@Nullable Bundle b){super.onViewCreated(v,b);
        binding.viewPagerPanchang.setAdapter(new PanchangPagerAdapter(this));
        new TabLayoutMediator(binding.tabLayoutPanchang,binding.viewPagerPanchang,(tab,pos)->tab.setText(TABS[pos])).attach();}
    @Override public void onDestroyView(){super.onDestroyView();binding=null;}
}
