package com.divyapath.app.ui.darshan;
import android.content.SharedPreferences; import android.view.*; import android.widget.TextView;
import androidx.annotation.NonNull; import androidx.recyclerview.widget.DiffUtil; import androidx.recyclerview.widget.ListAdapter; import androidx.recyclerview.widget.RecyclerView;
import com.divyapath.app.R; import com.divyapath.app.data.local.entity.TempleEntity;
import com.google.android.material.button.MaterialButton; import com.google.android.material.chip.Chip;
public class TempleAdapter extends ListAdapter<TempleEntity,TempleAdapter.VH> {
    public interface OnTempleClickListener{void onWatchLive(TempleEntity t);}
    public interface OnTempleCardClickListener{void onCardClick(TempleEntity t);}
    private final OnTempleClickListener listener; private final boolean showLiveOnly;
    private OnTempleCardClickListener cardClickListener;
    private SharedPreferences prefs;
    public TempleAdapter(boolean liveOnly,OnTempleClickListener l){super(new DiffUtil.ItemCallback<TempleEntity>(){
        @Override public boolean areItemsTheSame(@NonNull TempleEntity o,@NonNull TempleEntity n){return o.getId()==n.getId();}
        @Override public boolean areContentsTheSame(@NonNull TempleEntity o,@NonNull TempleEntity n){return o.getName().equals(n.getName());}
    });this.listener=l;this.showLiveOnly=liveOnly;}
    public void setOnCardClickListener(OnTempleCardClickListener l){this.cardClickListener=l;}
    public void setPreferences(SharedPreferences p){this.prefs=p;}
    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p,int v){return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_temple_card,p,false));}
    @Override public void onBindViewHolder(@NonNull VH h,int pos){TempleEntity t=getItem(pos);
        h.name.setText(t.getName());h.nameHindi.setText(t.getNameHindi());h.location.setText(t.getLocation());
        h.timings.setText("Timings: "+t.getTimings());h.desc.setText(t.getDescription());
        if(t.isHasLiveDarshan()){h.chipLive.setVisibility(View.VISIBLE);h.btnWatch.setVisibility(View.VISIBLE);h.btnWatch.setOnClickListener(v->listener.onWatchLive(t));}
        else{h.chipLive.setVisibility(View.GONE);h.btnWatch.setVisibility(View.GONE);}
        if(prefs!=null&&prefs.getBoolean("temple_visited_"+t.getId(),false)){h.chipVisited.setVisibility(View.VISIBLE);}
        else{h.chipVisited.setVisibility(View.GONE);}
        h.itemView.setOnClickListener(v->{if(cardClickListener!=null)cardClickListener.onCardClick(t);});
    }
    static class VH extends RecyclerView.ViewHolder{TextView name,nameHindi,location,timings,desc;Chip chipLive,chipVisited;MaterialButton btnWatch;
        VH(View v){super(v);name=v.findViewById(R.id.tv_temple_name);nameHindi=v.findViewById(R.id.tv_temple_name_hindi);
            location=v.findViewById(R.id.tv_temple_location);timings=v.findViewById(R.id.tv_temple_timings);
            desc=v.findViewById(R.id.tv_temple_desc);chipLive=v.findViewById(R.id.chip_live);
            chipVisited=v.findViewById(R.id.chip_visited);btnWatch=v.findViewById(R.id.btn_watch_live);}}
}
