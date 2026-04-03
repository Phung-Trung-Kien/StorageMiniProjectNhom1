package com.example.storageminiprojectcodebase2.ui.product;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shoppingapp.R;

public class ProductFragment extends Fragment {

    private ProductViewModel productViewModel;
    private ProductAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.rvProducts);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        adapter = new ProductAdapter(product -> {
            Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
            intent.putExtra("productId", product.id);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        productViewModel.products.observe(getViewLifecycleOwner(), products -> {
            adapter.setProducts(products);
        });

        return view;
    }
}
