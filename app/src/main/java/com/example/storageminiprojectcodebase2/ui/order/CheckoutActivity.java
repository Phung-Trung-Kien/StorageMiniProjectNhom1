package com.example.storageminiprojectcodebase2.ui.order;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoppingapp.R;
import com.example.storageminiprojectcodebase2.data.database.AppDatabase;
import com.example.storageminiprojectcodebase2.data.entity.Order;
import com.example.storageminiprojectcodebase2.data.entity.OrderDetail;
import com.example.storageminiprojectcodebase2.data.entity.Product;
import com.example.storageminiprojectcodebase2.repository.OrderRepository;
import com.example.storageminiprojectcodebase2.repository.ProductRepository;
import com.example.storageminiprojectcodebase2.utils.SessionManager;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CheckoutActivity extends AppCompatActivity {

    private TextView tvCustomerName, tvTotalAmount;
    private RecyclerView rvOrderDetails;
    private Button btnConfirmCheckout;
    private OrderRepository orderRepository;
    private ProductRepository productRepository;
    private SessionManager sessionManager;
    private int orderId;
    private OrderViewModel orderViewModel;
    private double totalAmount = 0;
    private CartAdapter cartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        orderId = getIntent().getIntExtra("orderId", -1);
        if (orderId == -1) {
            Toast.makeText(this, "Không tìm thấy đơn hàng!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        orderRepository = new OrderRepository(getApplication());
        productRepository = new ProductRepository(getApplication());
        sessionManager = new SessionManager(this);
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);

        initViews();
        loadOrderData();

        btnConfirmCheckout.setOnClickListener(v -> confirmCheckout());
    }

    private void initViews() {
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        rvOrderDetails = findViewById(R.id.rvOrderDetails);
        btnConfirmCheckout = findViewById(R.id.btnConfirmCheckout);

        tvCustomerName.setText(sessionManager.getUsername());
        rvOrderDetails.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter();
        rvOrderDetails.setAdapter(cartAdapter);
    }

    private void loadOrderData() {
        orderRepository.getDetailsByOrder(orderId).observe(this, details -> {
            if (details != null) {
                calculateTotal(details);
                loadCartItems(details);
            }
        });
    }

    private void loadCartItems(List<OrderDetail> details) {
        AppDatabase.databaseExecutor.execute(() -> {
            List<CartAdapter.CartItem> cartItems = new ArrayList<>();
            for (OrderDetail detail : details) {
                Product product = productRepository.findById(detail.productId);
                if (product != null) {
                    cartItems.add(new CartAdapter.CartItem(
                            detail,
                            product.name,
                            product.imageUrl,
                            detail.quantity * detail.unitPrice
                    ));
                }
            }
            runOnUiThread(() -> cartAdapter.setItems(cartItems));
        });
    }

    private void calculateTotal(List<OrderDetail> details) {
        totalAmount = 0;
        for (OrderDetail detail : details) {
            totalAmount += detail.quantity * detail.unitPrice;
        }
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        tvTotalAmount.setText(formatter.format(totalAmount) + " đ");
    }

    private void confirmCheckout() {
        orderViewModel.confirmCheckout(orderId, totalAmount, () -> {
            runOnUiThread(() -> {
                Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CheckoutActivity.this, InvoiceActivity.class);
                intent.putExtra("orderId", orderId);
                startActivity(intent);
                finish();
            });
        });
    }
}
