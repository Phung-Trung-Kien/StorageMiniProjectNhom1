package com.example.storageminiprojectcodebase2.ui.product;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.storageminiprojectcodebase2.R;
import com.example.storageminiprojectcodebase2.data.entity.Product;
import com.example.storageminiprojectcodebase2.data.database.AppDatabase;
import com.example.storageminiprojectcodebase2.repository.ProductRepository;
import com.example.storageminiprojectcodebase2.utils.FormatUtils;
import com.example.storageminiprojectcodebase2.utils.SessionManager;
import com.example.storageminiprojectcodebase2.ui.auth.LoginActivity;

public class ProductDetailActivity extends AppCompatActivity {
    private TextView nameTextView;
    private TextView descriptionTextView;
    private TextView priceTextView;
    private TextView stockTextView;
    private Button addToCartButton;
    private int productId;
    private Product product;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        productId = getIntent().getIntExtra("productId", -1);
        sessionManager = new SessionManager(this);

        nameTextView = findViewById(R.id.product_detail_name);
        descriptionTextView = findViewById(R.id.product_detail_description);
        priceTextView = findViewById(R.id.product_detail_price);
        stockTextView = findViewById(R.id.product_detail_stock);
        addToCartButton = findViewById(R.id.add_to_cart_button);

        addToCartButton.setOnClickListener(v -> handleAddToCart());

        loadProduct();
    }

    private void loadProduct() {
        AppDatabase.databaseExecutor.execute(() -> {
            ProductRepository repository = new ProductRepository(getApplication());
            product = repository.findById(productId);
            runOnUiThread(() -> {
                if (product != null) {
                    nameTextView.setText(product.name);
                    descriptionTextView.setText(product.description);
                    priceTextView.setText(FormatUtils.formatPrice(product.price));
                    stockTextView.setText("Tồn kho: " + product.stock);
                }
            });
        });
    }

    private void handleAddToCart() {
        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return;
        }

        if (product != null) {
            Toast.makeText(this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
        }
    }
}
