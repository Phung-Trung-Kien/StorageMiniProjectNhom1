package com.example.storageminiprojectcodebase2.ui.product;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.shoppingapp.R;
import com.example.storageminiprojectcodebase2.data.database.AppDatabase;
import com.example.storageminiprojectcodebase2.data.entity.Product;
import com.example.storageminiprojectcodebase2.repository.ProductRepository;
import com.example.storageminiprojectcodebase2.ui.auth.LoginActivity;
import com.example.storageminiprojectcodebase2.ui.order.CheckoutActivity;
import com.example.storageminiprojectcodebase2.ui.order.OrderViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.storageminiprojectcodebase2.utils.DrawableUtils;
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
    private OrderViewModel orderViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        repository = new ProductRepository(getApplication());
        sessionManager = new SessionManager(this);
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
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
                    addToCart();
                }
            }
        });
    }

    private void addToCart() {
        orderViewModel.getOrCreatePendingOrder(orderId -> {
            if (orderId > 0) {
                orderViewModel.addProductToCart(orderId, currentProduct.id, currentProduct.price, () -> {
                    runOnUiThread(() -> {
                        showPostAddDialog(orderId);
                    });
                });
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi khi tạo giỏ hàng", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void showPostAddDialog(int orderId) {
        new AlertDialog.Builder(this)
                .setTitle("Đã thêm vào giỏ hàng")
                .setMessage("Bạn có muốn tiếp tục chọn sản phẩm hay đi đến thanh toán?")
                .setPositiveButton("Thanh toán", (dialog, which) -> {
                    Intent intent = new Intent(this, CheckoutActivity.class);
                    intent.putExtra("orderId", orderId);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Tiếp tục chọn", (dialog, which) -> {
                    finish(); // Quay lại màn hình danh sách sản phẩm
                })
                .setCancelable(false)
                .show();
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
                    
                    // Load image from drawable
                    int imageResId = DrawableUtils.getDrawableResourceId(this, currentProduct.imageUrl);
                    if (imageResId != 0) {
                        imgProduct.setImageResource(imageResId);
                    } else {
                        imgProduct.setImageResource(android.R.drawable.ic_menu_gallery);
                    }
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
