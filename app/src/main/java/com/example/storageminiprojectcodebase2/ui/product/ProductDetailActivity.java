package com.example.storageminiprojectcodebase2.ui.product;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.shoppingapp.R;
import com.example.storageminiprojectcodebase2.data.database.AppDatabase;
import com.example.storageminiprojectcodebase2.data.entity.Product;
import com.example.storageminiprojectcodebase2.repository.ProductRepository;
import com.example.storageminiprojectcodebase2.utils.FormatUtils;
import com.example.storageminiprojectcodebase2.utils.SessionManager;

public class ProductDetailActivity extends AppCompatActivity {

    private ProductRepository repository;
    private SessionManager sessionManager;
    private int productId;
    private Product currentProduct;

    private ImageView imgProduct;
    private TextView tvName, tvPrice, tvStock, tvDescription;
    private Button btnAddToCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        repository = new ProductRepository(getApplication());
        sessionManager = new SessionManager(this);
        productId = getIntent().getIntExtra("productId", -1);

        initViews();
        loadProductData();

        btnAddToCart.setOnClickListener(v -> {
            if (!sessionManager.isLoggedIn()) {
                // Redirect to LoginActivity (TV1 will create this)
                try {
                    Class<?> loginActivityClass = Class.forName("com.example.storageminiprojectcodebase2.ui.auth.LoginActivity");
                    Intent intent = new Intent(this, loginActivityClass);
                    startActivity(intent);
                } catch (ClassNotFoundException e) {
                    Toast.makeText(this, "Chức năng đăng nhập đang được phát triển", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Logic to add to cart (TV4 will provide this)
                // For now, just show a Toast
                Toast.makeText(this, "Đã thêm vào giỏ hàng: " + currentProduct.name, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews() {
        imgProduct = findViewById(R.id.imgProductDetail);
        tvName = findViewById(R.id.tvProductNameDetail);
        tvPrice = findViewById(R.id.tvProductPriceDetail);
        tvStock = findViewById(R.id.tvProductStockDetail);
        tvDescription = findViewById(R.id.tvProductDescriptionDetail);
        btnAddToCart = findViewById(R.id.btnAddToCart);
    }

    private void loadProductData() {
        AppDatabase.databaseExecutor.execute(() -> {
            currentProduct = repository.findById(productId);
            if (currentProduct != null) {
                runOnUiThread(() -> {
                    tvName.setText(currentProduct.name);
                    tvPrice.setText(FormatUtils.formatPrice(currentProduct.price));
                    tvStock.setText("Tồn kho: " + currentProduct.stock);
                    tvDescription.setText(currentProduct.description);
                    // Image loading logic would go here
                });
            }
        });
    }
}
