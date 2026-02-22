package com.divyapath.app.ui.search;
import android.view.*; import android.widget.TextView;
import androidx.annotation.NonNull; import androidx.recyclerview.widget.DiffUtil; import androidx.recyclerview.widget.ListAdapter; import androidx.recyclerview.widget.RecyclerView;
import com.divyapath.app.R;
public class SearchResultAdapter extends ListAdapter<SearchResultAdapter.SearchItem,SearchResultAdapter.VH> {
    public static class SearchItem{
        public final int id; public final String title; public final String subtitle; public final String type; public final int navDestination;
        public SearchItem(int id,String title,String subtitle,String type,int navDest){this.id=id;this.title=title;this.subtitle=subtitle;this.type=type;this.navDestination=navDest;}
    }
    public interface OnResultClickListener{void onClick(SearchItem item);}
    private final OnResultClickListener listener;
    public SearchResultAdapter(OnResultClickListener l){super(new DiffUtil.ItemCallback<SearchItem>(){
        @Override public boolean areItemsTheSame(@NonNull SearchItem o,@NonNull SearchItem n){return o.id==n.id&&o.type.equals(n.type);}
        @Override public boolean areContentsTheSame(@NonNull SearchItem o,@NonNull SearchItem n){return o.title.equals(n.title);}
    });this.listener=l;}
    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p,int v){return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_search_result,p,false));}
    @Override public void onBindViewHolder(@NonNull VH h,int pos){SearchItem item=getItem(pos);
        h.title.setText(item.title);h.subtitle.setText(item.subtitle);h.type.setText(item.type);
        h.itemView.setOnClickListener(v->listener.onClick(item));}
    static class VH extends RecyclerView.ViewHolder{TextView title,subtitle,type;
        VH(View v){super(v);title=v.findViewById(R.id.tv_result_title);subtitle=v.findViewById(R.id.tv_result_subtitle);type=v.findViewById(R.id.tv_result_type);}}
}
