package com.example.storageminiprojectcodebase2.ui.order;

import android.app.Application;

import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.storageminiprojectcodebase2.data.database.AppDatabase;
import com.example.storageminiprojectcodebase2.data.entity.Order;
import com.example.storageminiprojectcodebase2.data.entity.OrderDetail;
import com.example.storageminiprojectcodebase2.repository.OrderRepository;
import com.example.storageminiprojectcodebase2.repository.ProductRepository;
import com.example.storageminiprojectcodebase2.utils.SessionManager;

import java.util.List;

public class OrderViewModel extends AndroidViewModel {
    private final OrderRepository repository;
    private final ProductRepository productRepository;
    private final SessionManager session;

    public OrderViewModel(Application app) {
        super(app);
        repository = new OrderRepository(app);
        productRepository = new ProductRepository(app);
        session = new SessionManager(app);
    }

    // Lấy hoặc tạo mới PENDING order của user hiện tại
    public void getOrCreatePendingOrder(@Nullable Callback<Integer> callback) {
        AppDatabase.databaseExecutor.execute(() -> {
            int userId = session.getUserId();
            if (userId <= 0) {
                if (callback != null) callback.onResult(-1);
                return;
            }

            Order pending = repository.getPendingOrder(userId);
            if (pending == null) {
                Order newOrder = new Order();
                newOrder.userId = userId;
                newOrder.status = "PENDING";
                newOrder.createdAt = System.currentTimeMillis();
                newOrder.totalAmount = 0;

                int orderId = (int) repository.insertOrder(newOrder);
                if (callback != null) callback.onResult(orderId);
            } else {
                if (callback != null) callback.onResult(pending.id);
            }
        });
    }

    // Thêm sản phẩm vào giỏ (nếu đã có thì tăng quantity)
    public void addProductToCart(int orderId, int productId, double unitPrice, @Nullable Runnable onDone) {
        AppDatabase.databaseExecutor.execute(() -> {
            if (orderId <= 0) {
                if (onDone != null) onDone.run();
                return;
            }

            OrderDetail existing = repository.findDetailByOrderAndProduct(orderId, productId);
            if (existing != null) {
                existing.quantity += 1;
                repository.updateOrderDetail(existing);
            } else {
                OrderDetail detail = new OrderDetail();
                detail.orderId = orderId;
                detail.productId = productId;
                detail.quantity = 1;
                detail.unitPrice = unitPrice;
                repository.insertOrderDetail(detail);
            }

            if (onDone != null) onDone.run();
        });
    }

    public LiveData<List<OrderDetail>> getCartItems(int orderId) {
        return repository.getDetailsByOrder(orderId);
    }

    public interface Callback<T> {
        void onResult(T result);
    }
}

