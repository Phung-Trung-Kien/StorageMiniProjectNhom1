package com.example.storageminiprojectcodebase2.ui.order;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoppingapp.R;
import com.example.storageminiprojectcodebase2.data.database.AppDatabase;
import com.example.storageminiprojectcodebase2.data.entity.Order;
import com.example.storageminiprojectcodebase2.data.entity.OrderDetail;
import com.example.storageminiprojectcodebase2.repository.OrderRepository;
import com.example.storageminiprojectcodebase2.utils.SessionManager;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CheckoutActivity extends AppCompatActivity {

    private TextView tvCustomerName, tvTotalAmount;
    private RecyclerView rvOrderDetails;
    private Button btnConfirmCheckout;
    private OrderRepository orderRepository;
    private SessionManager sessionManager;
    private int orderId;
    private double totalAmount = 0;

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
        sessionManager = new SessionManager(this);

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
    }

    private void loadOrderData() {
        orderRepository.getDetailsByOrder(orderId).observe(this, details -> {
            // Trong thực tế cần một adapter để hiển thị details kèm tên sản phẩm
            // Ở đây tôi giả định bạn sẽ dùng CartAdapter hoặc tương tự
            calculateTotal(details);
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
        AppDatabase.databaseExecutor.execute(() -> {
            Order order = orderRepository.findOrderById(orderId);
            if (order != null) {
                order.status = "PAID";
                order.totalAmount = totalAmount;
                orderRepository.updateOrder(order);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CheckoutActivity.this, InvoiceActivity.class);
                    intent.putExtra("orderId", orderId);
                    startActivity(intent);
                    finish();
                });
            }
        });
    }
}