package com.example.storageminiprojectcodebase2.ui.category;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shoppingapp.R;
import com.example.storageminiprojectcodebase2.data.entity.Product;
import com.example.storageminiprojectcodebase2.ui.product.ProductAdapter;
import com.example.storageminiprojectcodebase2.ui.product.ProductDetailActivity;

public class ProductByCategoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private int categoryId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_by_category);

        categoryId = getIntent().getIntExtra("categoryId", -1);
        String categoryName = getIntent().getStringExtra("categoryName");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(categoryName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.product_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new ProductAdapter(product -> {
            Intent intent = new Intent(ProductByCategoryActivity.this, ProductDetailActivity.class);
            intent.putExtra("productId", product.id);
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        loadProducts();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void loadProducts() {
        com.example.storageminiprojectcodebase2.repository.ProductRepository repository =
                new com.example.storageminiprojectcodebase2.repository.ProductRepository(getApplication());
        repository.getByCategoryId(categoryId).observe(this, products -> {
            if (products != null) {
                adapter.setProducts(products);
            }
        });
    }
}
