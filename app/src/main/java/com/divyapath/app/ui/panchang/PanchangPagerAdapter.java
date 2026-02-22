package com.divyapath.app.ui.panchang;
import androidx.annotation.NonNull; import androidx.fragment.app.Fragment; import androidx.viewpager2.adapter.FragmentStateAdapter;
public class PanchangPagerAdapter extends FragmentStateAdapter {
    public PanchangPagerAdapter(@NonNull Fragment f){super(f);}
    @NonNull @Override public Fragment createFragment(int pos){
        switch(pos){case 0:return new PanchangTodayFragment();case 1:return new PanchangCalendarFragment();case 2:return new PanchangMuhuratFragment();default:return new PanchangTodayFragment();}
    }
    @Override public int getItemCount(){return 3;}
}
