package com.example.storageminiprojectcodebase2.ui.product;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shoppingapp.R;
import com.example.storageminiprojectcodebase2.data.entity.Product;
import com.example.storageminiprojectcodebase2.utils.DrawableUtils;
import com.example.storageminiprojectcodebase2.utils.FormatUtils;
import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> products = new ArrayList<>();
    private final OnProductClickListener listener;

    public interface OnProductClickListener {
        void onClick(Product product);
    }

    public ProductAdapter(OnProductClickListener listener) {
        this.listener = listener;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product, listener);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgProduct;
        private final TextView tvProductName;
        private final TextView tvProductPrice;
        private final TextView tvProductStock;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductStock = itemView.findViewById(R.id.tvProductStock);
        }

        public void bind(final Product product, final OnProductClickListener listener) {
            tvProductName.setText(product.name);
            tvProductPrice.setText(FormatUtils.formatPrice(product.price));
            tvProductStock.setText("Tồn kho: " + product.stock);
            
            // Load image from drawable
            int imageResId = DrawableUtils.getDrawableResourceId(itemView.getContext(), product.imageUrl);
            if (imageResId != 0) {
                imgProduct.setImageResource(imageResId);
            } else {
                imgProduct.setImageResource(android.R.drawable.ic_menu_gallery);
            }
            
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onClick(product);
                }
            });
        }
    }
}
