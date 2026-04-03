package com.example.storageminiprojectcodebase2.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.shoppingapp.R;
import com.example.storageminiprojectcodebase2.data.entity.Order;
import com.example.storageminiprojectcodebase2.repository.CategoryRepository;
import com.example.storageminiprojectcodebase2.repository.OrderRepository;
import com.example.storageminiprojectcodebase2.repository.ProductRepository;
import com.example.storageminiprojectcodebase2.ui.auth.LoginActivity;
import com.example.storageminiprojectcodebase2.ui.category.CategoryFragment;
import com.example.storageminiprojectcodebase2.ui.order.CartFragment;
import com.example.storageminiprojectcodebase2.ui.product.ProductFragment;
import com.example.storageminiprojectcodebase2.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private TextView tvToolbarSubtitle;
    private TextView tvUsername;
    private MaterialButton btnAuth;
    private BottomNavigationView bottomNavigationView;
    private SessionManager sessionManager;
    private OrderRepository orderRepository;

    private int productCount;
    private int categoryCount;
    private int orderCount;
    private int selectedTabId = R.id.nav_products;

    private LiveData<List<Order>> ordersLiveData;
    private final Observer<List<Order>> ordersObserver = orders -> {
        orderCount = getSafeSize(orders);
        updateToolbarSubtitle();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sessionManager = new SessionManager(this);
        ProductRepository productRepository = new ProductRepository(getApplication());
        CategoryRepository categoryRepository = new CategoryRepository(getApplication());
        orderRepository = new OrderRepository(getApplication());

        tvToolbarSubtitle = findViewById(R.id.tv_toolbar_subtitle);
        tvUsername = findViewById(R.id.tv_username);
        btnAuth = findViewById(R.id.btn_auth);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        btnAuth.setOnClickListener(view -> onAuthClicked());
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (selectedTabId != item.getItemId()) {
                selectedTabId = item.getItemId();
                renderSelectedTab();
            }
            return true;
        });

        observeCollections(productRepository, categoryRepository);

        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_products);
            renderSelectedTab();
        } else {
            selectedTabId = bottomNavigationView.getSelectedItemId();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshAuthUi();
        bindOrdersForCurrentSession();
    }

    private void observeCollections(ProductRepository productRepository, CategoryRepository categoryRepository) {
        productRepository.getAll().observe(this, products -> {
            productCount = getSafeSize(products);
            updateToolbarSubtitle();
        });

        categoryRepository.getAll().observe(this, categories -> {
            categoryCount = getSafeSize(categories);
            updateToolbarSubtitle();
        });
    }

    private void onAuthClicked() {
        if (sessionManager.isLoggedIn()) {
            sessionManager.logout();
            orderCount = 0;
            refreshAuthUi();
            bindOrdersForCurrentSession();
            if (selectedTabId == R.id.nav_orders) {
                renderSelectedTab();
            }
            return;
        }
        startActivity(new Intent(this, LoginActivity.class));
    }

    private void refreshAuthUi() {
        if (sessionManager.isLoggedIn()) {
            String username = sessionManager.getUsername();
            tvUsername.setText(getString(R.string.hello_user, username));
            tvUsername.setVisibility(View.VISIBLE);
            btnAuth.setText(R.string.logout);
        } else {
            tvUsername.setVisibility(View.GONE);
            btnAuth.setText(R.string.login);
        }
        updateToolbarSubtitle();
    }

    private void updateToolbarSubtitle() {
        if (sessionManager.isLoggedIn()) {
            String username = sessionManager.getUsername();
            tvToolbarSubtitle.setText(getString(R.string.home_subtitle_user, username));
        } else {
            tvToolbarSubtitle.setText(R.string.home_subtitle_guest);
        }
    }

    private void bindOrdersForCurrentSession() {
        if (ordersLiveData != null) {
            ordersLiveData.removeObserver(ordersObserver);
            ordersLiveData = null;
        }

        if (sessionManager.isLoggedIn()) {
            ordersLiveData = orderRepository.getOrdersByUser(sessionManager.getUserId());
            ordersLiveData.observe(this, ordersObserver);
        } else {
            orderCount = 0;
        }
    }

    private void renderSelectedTab() {
        Fragment fragment;
        if (selectedTabId == R.id.nav_categories) {
            fragment = new CategoryFragment();
        } else if (selectedTabId == R.id.nav_orders) {
            fragment = new CartFragment();
        } else {
            fragment = new ProductFragment();
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commitAllowingStateLoss();
    }

    private int getSafeSize(@Nullable List<?> items) {
        return items == null ? 0 : items.size();
    }
}
