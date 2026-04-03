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
import com.example.storageminiprojectcodebase2.R;

public class ProductFragment extends Fragment {
    private ProductViewModel viewModel;
    private ProductAdapter adapter;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.product_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        viewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        adapter = new ProductAdapter(null, product -> {
            Intent intent = new Intent(getContext(), ProductDetailActivity.class);
            intent.putExtra("productId", product.id);
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        viewModel.products.observe(getViewLifecycleOwner(), products -> {
            adapter.setProducts(products);
        });
    }
}
