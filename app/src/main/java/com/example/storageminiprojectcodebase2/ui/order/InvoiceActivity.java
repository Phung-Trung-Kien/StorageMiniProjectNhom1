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
import com.example.storageminiprojectcodebase2.data.entity.Product;
import com.example.storageminiprojectcodebase2.repository.OrderRepository;
import com.example.storageminiprojectcodebase2.repository.ProductRepository;
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
    private ProductRepository productRepository;
    private SessionManager sessionManager;
    private int orderId;
    private final NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        orderId = getIntent().getIntExtra("orderId", -1);
        orderRepository = new OrderRepository(getApplication());
        productRepository = new ProductRepository(getApplication());
        sessionManager = new SessionManager(this);

        initViews();
        loadInvoiceData();

        btnContinueShopping.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class); 
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
            }
        });

        orderRepository.getDetailsByOrder(orderId).observe(this, this::fillInvoiceTable);
    }

    private void fillInvoiceTable(List<OrderDetail> details) {
        if (details == null) return;
        
        // Xóa các dòng cũ trừ header (dòng 0)
        int childCount = tlInvoiceDetails.getChildCount();
        if (childCount > 1) {
            tlInvoiceDetails.removeViews(1, childCount - 1);
        }

        AppDatabase.databaseExecutor.execute(() -> {
            for (int i = 0; i < details.size(); i++) {
                OrderDetail detail = details.get(i);
                int index = i + 1;
                
                Product product = productRepository.findById(detail.productId);
                String productName = (product != null) ? product.name : "Sản phẩm #" + detail.productId;
                
                runOnUiThread(() -> {
                    TableRow row = new TableRow(this);
                    row.setPadding(0, 8, 0, 8);

                    TextView tvStt = createCell(String.valueOf(index));
                    TextView tvName = createCell(productName);
                    TextView tvQty = createCell(String.valueOf(detail.quantity));
                    TextView tvSubtotal = createCell(formatter.format(detail.quantity * detail.unitPrice) + " đ");

                    row.addView(tvStt);
                    row.addView(tvName);
                    row.addView(tvQty);
                    row.addView(tvSubtotal);

                    tlInvoiceDetails.addView(row);
                });
            }
        });
    }

    private TextView createCell(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setPadding(8, 8, 8, 8);
        tv.setTextColor(getResources().getColor(android.R.color.black));
        return tv;
    }

    @Override
    public void onBackPressed() {
        // Ngăn chặn quay lại CheckoutActivity sau khi đã thanh toán thành công
        super.onBackPressed();
        btnContinueShopping.performClick();
    }
}
