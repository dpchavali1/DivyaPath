package com.divyapath.app.ui.store;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.divyapath.app.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private List<Product> products = new ArrayList<>();

    public void setProducts(List<Product> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);

        holder.name.setText(product.getName());
        holder.price.setText(product.getPrice());
        holder.rating.setText(String.format("★ %.1f", product.getRating()));
        holder.reviews.setText(String.format("(%d)", product.getReviewCount()));

        if (product.getOriginalPrice() != null && !product.getOriginalPrice().isEmpty()) {
            holder.originalPrice.setVisibility(View.VISIBLE);
            holder.originalPrice.setText(product.getOriginalPrice());
            holder.originalPrice.setPaintFlags(holder.originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.originalPrice.setVisibility(View.GONE);
        }

        // Load product image from URL with Glide
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            holder.image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(holder.itemView.getContext())
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.ic_om_symbol)
                    .error(R.drawable.ic_om_symbol)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(holder.image);
        } else {
            holder.image.setScaleType(ImageView.ScaleType.CENTER);
            holder.image.setImageResource(R.drawable.ic_om_symbol);
        }

        holder.buyButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(product.getAmazonUrl()));
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name, price, originalPrice, rating, reviews;
        final ImageView image;
        final MaterialButton buyButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_product_name);
            price = itemView.findViewById(R.id.tv_product_price);
            originalPrice = itemView.findViewById(R.id.tv_product_original_price);
            rating = itemView.findViewById(R.id.tv_product_rating);
            reviews = itemView.findViewById(R.id.tv_product_reviews);
            image = itemView.findViewById(R.id.iv_product_image);
            buyButton = itemView.findViewById(R.id.btn_buy);
        }
    }
}
