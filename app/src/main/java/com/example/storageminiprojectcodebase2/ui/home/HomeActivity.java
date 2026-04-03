package com.example.storageminiprojectcodebase2.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.shoppingapp.R;
import com.example.storageminiprojectcodebase2.data.entity.Order;
import com.example.storageminiprojectcodebase2.repository.CategoryRepository;
import com.example.storageminiprojectcodebase2.repository.OrderRepository;
import com.example.storageminiprojectcodebase2.repository.ProductRepository;
import com.example.storageminiprojectcodebase2.ui.auth.LoginActivity;
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
        renderSelectedTab();
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
            selectedTabId = item.getItemId();
            renderSelectedTab();
            return true;
        });

        observeCollections(productRepository, categoryRepository);
        bottomNavigationView.setSelectedItemId(selectedTabId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshAuthUi();
        bindOrdersForCurrentSession();
        renderSelectedTab();
    }

    private void observeCollections(ProductRepository productRepository, CategoryRepository categoryRepository) {
        productRepository.getAll().observe(this, products -> {
            productCount = getSafeSize(products);
            renderSelectedTab();
        });

        categoryRepository.getAll().observe(this, categories -> {
            categoryCount = getSafeSize(categories);
            renderSelectedTab();
        });
    }

    private void onAuthClicked() {
        if (sessionManager.isLoggedIn()) {
            sessionManager.logout();
            orderCount = 0;
            refreshAuthUi();
            bindOrdersForCurrentSession();
            renderSelectedTab();
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
            tvToolbarSubtitle.setText(getString(R.string.home_subtitle_user, username));
        } else {
            tvUsername.setVisibility(View.GONE);
            btnAuth.setText(R.string.login);
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
        HomeSectionFragment fragment;
        if (selectedTabId == R.id.nav_categories) {
            fragment = HomeSectionFragment.newInstance(
                    getString(R.string.categories),
                    getString(R.string.section_categories_title),
                    getString(R.string.section_categories_description),
                    getString(R.string.categories_primary_value, categoryCount),
                    getString(R.string.summary_categories, categoryCount),
                    getString(R.string.section_categories_description)
            );
        } else if (selectedTabId == R.id.nav_orders) {
            boolean isLoggedIn = sessionManager.isLoggedIn();
            fragment = HomeSectionFragment.newInstance(
                    getString(R.string.orders),
                    getString(isLoggedIn ? R.string.section_orders_title_user : R.string.section_orders_title_guest),
                    getString(isLoggedIn ? R.string.section_orders_description_user : R.string.section_orders_description_guest),
                    isLoggedIn
                            ? getString(R.string.orders_primary_value, orderCount)
                            : getString(R.string.orders_primary_value_guest),
                    isLoggedIn
                            ? getString(R.string.summary_orders_user, orderCount)
                            : getString(R.string.summary_orders_guest),
                    getString(isLoggedIn ? R.string.cta_logout : R.string.cta_login)
            );
        } else {
            fragment = HomeSectionFragment.newInstance(
                    getString(R.string.products),
                    getString(R.string.section_products_title),
                    getString(R.string.section_products_description),
                    getString(R.string.products_primary_value, productCount),
                    getString(R.string.summary_products, productCount),
                    getString(R.string.status_ready)
            );
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