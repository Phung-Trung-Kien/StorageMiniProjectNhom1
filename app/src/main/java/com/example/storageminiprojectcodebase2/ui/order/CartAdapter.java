package com.example.storageminiprojectcodebase2.ui.order;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoppingapp.R;
import com.example.storageminiprojectcodebase2.data.entity.OrderDetail;
import com.example.storageminiprojectcodebase2.utils.FormatUtils;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    public static class CartItem {
        public OrderDetail detail;
        public String productName;
        public double subtotal; // quantity * unitPrice

        public CartItem(OrderDetail detail, String productName, double subtotal) {
            this.detail = detail;
            this.productName = productName;
            this.subtotal = subtotal;
        }
    }

    private final List<CartItem> items = new ArrayList<>();

    public void setItems(List<CartItem> newItems) {
        items.clear();
        if (newItems != null) items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = items.get(position);
        holder.textProductName.setText(item.productName);
        holder.textQuantity.setText(String.valueOf(item.detail.quantity));
        holder.textUnitPrice.setText(FormatUtils.formatPrice(item.detail.unitPrice));
        holder.textSubtotal.setText(FormatUtils.formatPrice(item.subtotal));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        final TextView textProductName;
        final TextView textQuantity;
        final TextView textUnitPrice;
        final TextView textSubtotal;

        CartViewHolder(@NonNull View itemView) {
            super(itemView);
            textProductName = itemView.findViewById(R.id.text_product_name);
            textQuantity = itemView.findViewById(R.id.text_quantity);
            textUnitPrice = itemView.findViewById(R.id.text_unit_price);
            textSubtotal = itemView.findViewById(R.id.text_subtotal);
        }
    }
}

