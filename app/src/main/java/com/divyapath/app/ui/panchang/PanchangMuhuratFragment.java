package com.divyapath.app.ui.panchang;
import android.os.Bundle; import android.view.*; import android.widget.TextView;
import androidx.annotation.*; import androidx.fragment.app.Fragment;
import com.divyapath.app.R; import com.google.android.material.card.MaterialCardView; import com.google.android.material.chip.*;
public class PanchangMuhuratFragment extends Fragment {
    private static final String[][] ACTIVITIES = {
        {"Griha Pravesh","06:15 - 07:45 AM","Best time for entering a new house. The Abhijit muhurat (11:45 AM - 12:33 PM) is also excellent.","Next good date: Thursday"},
        {"Vivah (Marriage)","07:00 - 08:30 AM","Early morning hours are ideal for marriage ceremonies. Avoid Rahukaal timing.","Consult pandit for nakshatra matching"},
        {"Travel","After Rahukaal","Avoid Rahukaal for starting any journey. Check today's Rahukaal timing in the Today tab.","Thursday and Friday are generally favorable"},
        {"Business","09:00 - 11:00 AM","Good time to start new business ventures or sign important documents.","Avoid Amavasya days"},
        {"Puja / Havan","Brahma Muhurat\n04:00 - 05:30 AM","The most auspicious time for worship and spiritual practices. Also good during Abhijit muhurat.","Daily practice recommended"},
        {"Shopping","After 10:00 AM","Avoid early morning and Rahukaal for important purchases.","Friday is best for buying luxury items"}
    };
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater i,@Nullable ViewGroup c,@Nullable Bundle b){return i.inflate(R.layout.fragment_panchang_muhurat,c,false);}
    @Override public void onViewCreated(@NonNull View v,@Nullable Bundle b){super.onViewCreated(v,b);
        ChipGroup cg=v.findViewById(R.id.chip_activity_type);
        for(int i=0;i<ACTIVITIES.length;i++){
            Chip chip=new Chip(requireContext());chip.setText(ACTIVITIES[i][0]);chip.setCheckable(true);
            chip.setChipBackgroundColorResource(R.color.cream_background);chip.setTextColor(getResources().getColor(R.color.saffron_primary,null));
            final int idx=i; chip.setOnClickListener(c->{showMuhurat(v,idx);
                for(int j=0;j<cg.getChildCount();j++){View ch=cg.getChildAt(j);if(ch instanceof Chip)((Chip)ch).setChecked(ch==c);}});
            cg.addView(chip);
        }
    }
    private void showMuhurat(View v,int idx){
        v.findViewById(R.id.card_muhurat_result).setVisibility(View.VISIBLE);
        ((TextView)v.findViewById(R.id.tv_muhurat_title)).setText("Best Time for "+ACTIVITIES[idx][0]);
        ((TextView)v.findViewById(R.id.tv_muhurat_time)).setText(ACTIVITIES[idx][1]);
        ((TextView)v.findViewById(R.id.tv_muhurat_desc)).setText(ACTIVITIES[idx][2]);
        ((TextView)v.findViewById(R.id.tv_muhurat_next)).setText(ACTIVITIES[idx][3]);
    }
}
