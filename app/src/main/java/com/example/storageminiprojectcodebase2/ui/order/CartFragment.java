package com.example.storageminiprojectcodebase2.ui.order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoppingapp.R;
import com.example.storageminiprojectcodebase2.data.database.AppDatabase;
import com.example.storageminiprojectcodebase2.data.entity.OrderDetail;
import com.example.storageminiprojectcodebase2.data.entity.Product;
import com.example.storageminiprojectcodebase2.repository.ProductRepository;
import com.example.storageminiprojectcodebase2.utils.FormatUtils;
import com.example.storageminiprojectcodebase2.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {
    private OrderViewModel orderViewModel;
    private ProductRepository productRepository;
    private CartAdapter cartAdapter;

    private int orderId = -1;

    private RecyclerView recyclerCart;
    private TextView textEmpty;
    private TextView textTotal;
    private TextView textLoginRequired;
    private Button btnLogin;
    private Button btnCheckout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        productRepository = new ProductRepository(requireActivity().getApplication());

        recyclerCart = view.findViewById(R.id.recycler_cart);
        textEmpty = view.findViewById(R.id.text_empty);
        textTotal = view.findViewById(R.id.text_total);
        textLoginRequired = view.findViewById(R.id.text_login_required);
        btnLogin = view.findViewById(R.id.btn_login);
        btnCheckout = view.findViewById(R.id.btn_checkout);

        cartAdapter = new CartAdapter();
        recyclerCart.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerCart.setAdapter(cartAdapter);

        SessionManager sessionManager = new SessionManager(requireContext());
        if (!sessionManager.isLoggedIn()) {
            showLoggedOutState();
            return;
        }

        showLoggedInState();
        loadOrCreateOrderAndObserveCart();
    }

    private void showLoggedOutState() {
        textLoginRequired.setVisibility(View.VISIBLE);
        btnLogin.setVisibility(View.VISIBLE);
        recyclerCart.setVisibility(View.GONE);
        textEmpty.setVisibility(View.GONE);
        textTotal.setVisibility(View.GONE);
        btnCheckout.setVisibility(View.GONE);

        textLoginRequired.setText("Vui lòng đăng nhập để xem giỏ hàng");
        btnLogin.setText(R.string.login);
        btnLogin.setOnClickListener(v -> startLoginActivity());
    }

    private void showLoggedInState() {
        textLoginRequired.setVisibility(View.GONE);
        btnLogin.setVisibility(View.GONE);
        btnCheckout.setVisibility(View.VISIBLE);
        btnCheckout.setOnClickListener(v -> startCheckoutActivity());
    }

    private void loadOrCreateOrderAndObserveCart() {
        orderViewModel.getOrCreatePendingOrder(result -> {
            if (result <= 0) return;
            orderId = result;

            requireActivity().runOnUiThread(this::observeCart);
        });
    }

    private void observeCart() {
        orderViewModel.getCartItems(orderId).observe(getViewLifecycleOwner(), this::renderCartItems);
    }

    private void renderCartItems(List<OrderDetail> details) {
        if (details == null || details.isEmpty()) {
            cartAdapter.setItems(new ArrayList<>());
            recyclerCart.setVisibility(View.GONE);
            textEmpty.setVisibility(View.VISIBLE);
            textTotal.setVisibility(View.GONE);
            textEmpty.setText("Giỏ hàng trống");
            return;
        }

        recyclerCart.setVisibility(View.VISIBLE);
        textEmpty.setVisibility(View.GONE);
        textTotal.setVisibility(View.VISIBLE);

        // Build UI models off the main thread (avoid DB calls on main thread).
        AppDatabase.databaseExecutor.execute(() -> {
            List<CartAdapter.CartItem> items = new ArrayList<>();
            double total = 0;

            for (OrderDetail detail : details) {
                Product product = productRepository.findById(detail.productId);
                String productName = product != null ? product.name : "";
                String imageUrl = product != null ? product.imageUrl : "";

                double subtotal = detail.quantity * detail.unitPrice;
                total += subtotal;

                items.add(new CartAdapter.CartItem(detail, productName, imageUrl, subtotal));
            }

            final double finalTotal = total;
            requireActivity().runOnUiThread(() -> {
                cartAdapter.setItems(items);
                textTotal.setText("Tổng: " + FormatUtils.formatPrice(finalTotal));
            });
        });
    }

    private void startCheckoutActivity() {
        try {
            Class<?> clazz = Class.forName("com.example.storageminiprojectcodebase2.ui.order.CheckoutActivity");
            //noinspection unchecked
            Intent intent = new Intent(requireContext(), (Class<? extends Activity>) clazz);
            intent.putExtra("orderId", orderId);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Checkout sẽ do Thành viên 5 implement", Toast.LENGTH_SHORT).show();
        }
    }

    private void startLoginActivity() {
        try {
            Class<?> clazz = Class.forName("com.example.storageminiprojectcodebase2.ui.auth.LoginActivity");
            //noinspection unchecked
            Intent intent = new Intent(requireContext(), (Class<? extends Activity>) clazz);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Login sẽ do Thành viên 1 implement", Toast.LENGTH_SHORT).show();
        }
    }
}
