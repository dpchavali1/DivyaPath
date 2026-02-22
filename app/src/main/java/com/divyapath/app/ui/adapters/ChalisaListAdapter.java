package com.divyapath.app.ui.adapters;
import android.view.LayoutInflater; import android.view.View; import android.view.ViewGroup; import android.widget.ImageView; import android.widget.TextView;
import androidx.annotation.NonNull; import androidx.recyclerview.widget.DiffUtil; import androidx.recyclerview.widget.ListAdapter; import androidx.recyclerview.widget.RecyclerView;
import com.divyapath.app.R; import com.divyapath.app.data.local.entity.ChalisaEntity; import com.divyapath.app.utils.DeityIconMapper;
public class ChalisaListAdapter extends ListAdapter<ChalisaEntity,ChalisaListAdapter.VH> {
    public interface OnChalisaClickListener{void onChalisaClick(ChalisaEntity chalisa);}
    private final OnChalisaClickListener listener;
    public ChalisaListAdapter(OnChalisaClickListener l){super(new DiffUtil.ItemCallback<ChalisaEntity>(){
        @Override public boolean areItemsTheSame(@NonNull ChalisaEntity o,@NonNull ChalisaEntity n){return o.getId()==n.getId();}
        @Override public boolean areContentsTheSame(@NonNull ChalisaEntity o,@NonNull ChalisaEntity n){return o.getTitle().equals(n.getTitle());}
    });this.listener=l;}
    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p,int v){return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_chalisa,p,false));}
    @Override public void onBindViewHolder(@NonNull VH h,int pos){ChalisaEntity c=getItem(pos);h.title.setText(c.getTitle());h.titleHindi.setText(c.getTitleHindi());h.verses.setText(c.getTotalVerses()+" Verses");h.icon.setImageResource(DeityIconMapper.getIconForDeityId(c.getDeityId()));h.itemView.setOnClickListener(v->listener.onChalisaClick(c));}
    static class VH extends RecyclerView.ViewHolder{ImageView icon;TextView title,titleHindi,verses;VH(View v){super(v);icon=v.findViewById(R.id.iv_chalisa_icon);title=v.findViewById(R.id.tv_chalisa_title);titleHindi=v.findViewById(R.id.tv_chalisa_title_hindi);verses=v.findViewById(R.id.tv_chalisa_verses);}}
}
