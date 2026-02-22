package com.divyapath.app.ui.darshan;
import androidx.annotation.NonNull; import androidx.fragment.app.Fragment; import androidx.viewpager2.adapter.FragmentStateAdapter;
public class DarshanPagerAdapter extends FragmentStateAdapter {
    public DarshanPagerAdapter(@NonNull Fragment f){super(f);}
    @NonNull @Override public Fragment createFragment(int pos){
        if(pos==0) return new LiveDarshanFragment();
        if(pos==1) return new TempleDirectoryFragment();
        return new PilgrimageLogFragment();
    }
    @Override public int getItemCount(){return 3;}
}
