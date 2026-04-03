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
import com.example.storageminiprojectcodebase2.ui.auth.LoginActivity;
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

        if (productId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        loadProductData();

        btnAddToCart.setOnClickListener(v -> {
            if (currentProduct == null) {
                return;
            }

            if (!sessionManager.isLoggedIn()) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            } else {
                if (currentProduct.stock <= 0) {
                    Toast.makeText(this, "Sản phẩm đã hết hàng", Toast.LENGTH_SHORT).show();
                } else {
                    // Logic to add to cart (sẽ được cập nhật ở các Task sau)
                    Toast.makeText(this, "Đã thêm vào giỏ hàng: " + currentProduct.name, Toast.LENGTH_SHORT).show();
                }
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
                    // Có thể thêm logic load ảnh bằng thư viện Glide/Picasso ở đây
                });
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Sản phẩm không tồn tại", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }
}
