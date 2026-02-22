package com.divyapath.app.ui.adapters;
import android.view.LayoutInflater; import android.view.View; import android.view.ViewGroup; import android.widget.ImageView; import android.widget.TextView;
import androidx.annotation.NonNull; import androidx.recyclerview.widget.DiffUtil; import androidx.recyclerview.widget.ListAdapter; import androidx.recyclerview.widget.RecyclerView;
import com.divyapath.app.R; import com.divyapath.app.data.local.entity.DeityEntity; import com.divyapath.app.utils.DeityIconMapper;
public class DeityGridAdapter extends ListAdapter<DeityEntity,DeityGridAdapter.VH> {
    public interface OnDeityClickListener{void onDeityClick(DeityEntity deity);}
    private final OnDeityClickListener listener;
    public DeityGridAdapter(OnDeityClickListener l){super(new DiffUtil.ItemCallback<DeityEntity>(){
        @Override public boolean areItemsTheSame(@NonNull DeityEntity o,@NonNull DeityEntity n){return o.getId()==n.getId();}
        @Override public boolean areContentsTheSame(@NonNull DeityEntity o,@NonNull DeityEntity n){return o.getName().equals(n.getName());}
    });this.listener=l;}
    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p,int v){return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_deity_grid,p,false));}
    @Override public void onBindViewHolder(@NonNull VH h,int pos){DeityEntity d=getItem(pos);h.hindi.setText(d.getHindiName());h.english.setText(d.getName());h.img.setImageResource(DeityIconMapper.getIconForImageUrl(d.getImageUrl()));h.itemView.setOnClickListener(v->listener.onDeityClick(d));}
    static class VH extends RecyclerView.ViewHolder{ImageView img;TextView hindi,english;VH(View v){super(v);img=v.findViewById(R.id.iv_deity_image);hindi=v.findViewById(R.id.tv_deity_hindi);english=v.findViewById(R.id.tv_deity_english);}}
}
