package com.example.storageminiprojectcodebase2.ui.order;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shoppingapp.R;
import com.example.storageminiprojectcodebase2.MainActivity;
import com.example.storageminiprojectcodebase2.data.database.AppDatabase;
import com.example.storageminiprojectcodebase2.data.entity.Order;
import com.example.storageminiprojectcodebase2.data.entity.OrderDetail;
import com.example.storageminiprojectcodebase2.repository.OrderRepository;
import com.example.storageminiprojectcodebase2.utils.DateUtils;
import com.example.storageminiprojectcodebase2.utils.SessionManager;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class InvoiceActivity extends AppCompatActivity {

    private TextView tvOrderId, tvInvoiceDate, tvInvoiceCustomer, tvInvoiceTotal;
    private TableLayout tlInvoiceDetails;
    private Button btnContinueShopping;
    private OrderRepository orderRepository;
    private SessionManager sessionManager;
    private int orderId;
    private final NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        orderId = getIntent().getIntExtra("orderId", -1);
        orderRepository = new OrderRepository(getApplication());
        sessionManager = new SessionManager(this);

        initViews();
        loadInvoiceData();

        btnContinueShopping.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class); // Giả định MainActivity là splash/home router
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void initViews() {
        tvOrderId = findViewById(R.id.tvOrderId);
        tvInvoiceDate = findViewById(R.id.tvInvoiceDate);
        tvInvoiceCustomer = findViewById(R.id.tvInvoiceCustomer);
        tvInvoiceTotal = findViewById(R.id.tvInvoiceTotal);
        tlInvoiceDetails = findViewById(R.id.tlInvoiceDetails);
        btnContinueShopping = findViewById(R.id.btnContinueShopping);
    }

    private void loadInvoiceData() {
        AppDatabase.databaseExecutor.execute(() -> {
            Order order = orderRepository.findOrderById(orderId);
            if (order != null) {
                runOnUiThread(() -> {
                    tvOrderId.setText("Mã đơn hàng: #" + order.id);
                    tvInvoiceDate.setText("Ngày đặt: " + DateUtils.formatDateTime(order.createdAt));
                    tvInvoiceCustomer.setText("Khách hàng: " + sessionManager.getUsername());
                    tvInvoiceTotal.setText(formatter.format(order.totalAmount) + " đ");
                });

                // Tạm thời lấy list details từ LiveData (trong thực tế nên dùng callback hoặc flow)
                // Vì đây là màn hình hóa đơn tĩnh, ta chỉ cần load 1 lần
            }
        });

        orderRepository.getDetailsByOrder(orderId).observe(this, this::fillInvoiceTable);
    }

    private void fillInvoiceTable(List<OrderDetail> details) {
        // Xóa các dòng cũ trừ header (dòng 0)
        int childCount = tlInvoiceDetails.getChildCount();
        if (childCount > 1) {
            tlInvoiceDetails.removeViews(1, childCount - 1);
        }

        for (int i = 0; i < details.size(); i++) {
            OrderDetail detail = details.get(i);
            TableRow row = new TableRow(this);
            row.setPadding(0, 8, 0, 8);

            TextView tvStt = createCell(String.valueOf(i + 1));
            TextView tvName = createCell("Sản phẩm #" + detail.productId); // Cần JOIN để lấy tên, tạm để ID
            TextView tvQty = createCell(String.valueOf(detail.quantity));
            TextView tvSubtotal = createCell(formatter.format(detail.quantity * detail.unitPrice) + " đ");

            row.addView(tvStt);
            row.addView(tvName);
            row.addView(tvQty);
            row.addView(tvSubtotal);

            tlInvoiceDetails.addView(row);
        }
    }

    private TextView createCell(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setPadding(8, 8, 8, 8);
        return tv;
    }
}