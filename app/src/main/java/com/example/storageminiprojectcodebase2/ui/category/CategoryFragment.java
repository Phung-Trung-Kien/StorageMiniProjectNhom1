package com.example.storageminiprojectcodebase2.ui.category;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shoppingapp.R;
import com.example.storageminiprojectcodebase2.data.entity.Category;

public class CategoryFragment extends Fragment {
    private CategoryViewModel viewModel;
    private CategoryAdapter adapter;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.category_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        adapter = new CategoryAdapter(null, category -> {
            Intent intent = new Intent(getContext(), ProductByCategoryActivity.class);
            intent.putExtra("categoryId", category.id);
            intent.putExtra("categoryName", category.name);
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        viewModel.categories.observe(getViewLifecycleOwner(), categories -> {
            adapter.setCategories(categories);
        });
    }
}
