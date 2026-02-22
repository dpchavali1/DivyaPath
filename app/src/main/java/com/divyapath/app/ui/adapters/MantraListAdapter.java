package com.divyapath.app.ui.adapters;
import android.view.LayoutInflater; import android.view.View; import android.view.ViewGroup; import android.widget.TextView;
import androidx.annotation.NonNull; import androidx.recyclerview.widget.DiffUtil; import androidx.recyclerview.widget.ListAdapter; import androidx.recyclerview.widget.RecyclerView;
import com.divyapath.app.R; import com.divyapath.app.data.local.entity.MantraEntity;
public class MantraListAdapter extends ListAdapter<MantraEntity,MantraListAdapter.VH> {
    public interface OnMantraClickListener{void onMantraClick(MantraEntity mantra);}
    private final OnMantraClickListener listener;
    public MantraListAdapter(OnMantraClickListener l){super(new DiffUtil.ItemCallback<MantraEntity>(){
        @Override public boolean areItemsTheSame(@NonNull MantraEntity o,@NonNull MantraEntity n){return o.getId()==n.getId();}
        @Override public boolean areContentsTheSame(@NonNull MantraEntity o,@NonNull MantraEntity n){return o.getTitle().equals(n.getTitle());}
    });this.listener=l;}
    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p,int v){return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_mantra,p,false));}
    @Override public void onBindViewHolder(@NonNull VH h,int pos){MantraEntity m=getItem(pos);h.title.setText(m.getTitle());h.sanskrit.setText(m.getSanskrit());h.category.setText(m.getCategory());h.itemView.setOnClickListener(v->listener.onMantraClick(m));}
    static class VH extends RecyclerView.ViewHolder{TextView title,sanskrit,category;VH(View v){super(v);title=v.findViewById(R.id.tv_mantra_title);sanskrit=v.findViewById(R.id.tv_mantra_sanskrit);category=v.findViewById(R.id.tv_mantra_category);}}
}
