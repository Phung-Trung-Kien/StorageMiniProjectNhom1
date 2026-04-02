package com.example.storageminiprojectcodebase2.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.storageminiprojectcodebase2.data.database.AppDatabase;
import com.example.storageminiprojectcodebase2.data.dao.OrderDao;
import com.example.storageminiprojectcodebase2.data.dao.OrderDetailDao;
import com.example.storageminiprojectcodebase2.data.entity.Order;
import com.example.storageminiprojectcodebase2.data.entity.OrderDetail;
import java.util.List;

public class OrderRepository {
    private final OrderDao orderDao;
    private final OrderDetailDao orderDetailDao;

    public OrderRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        orderDao = db.orderDao();
        orderDetailDao = db.orderDetailDao();
    }

    public long insertOrder(Order order) {
        return orderDao.insert(order);
    }

    public void updateOrder(Order order) {
        orderDao.update(order);
    }

    public Order getPendingOrder(int userId) {
        return orderDao.getPendingOrderByUserId(userId);
    }

    public Order findOrderById(int id) {
        return orderDao.findById(id);
    }

    public LiveData<List<Order>> getOrdersByUser(int userId) {
        return orderDao.getByUserId(userId);
    }

    public void insertOrderDetail(OrderDetail detail) {
        orderDetailDao.insert(detail);
    }

    public void updateOrderDetail(OrderDetail detail) {
        orderDetailDao.update(detail);
    }

    public LiveData<List<OrderDetail>> getDetailsByOrder(int orderId) {
        return orderDetailDao.getByOrderId(orderId);
    }

    public OrderDetail findDetailByOrderAndProduct(int orderId, int productId) {
        return orderDetailDao.findByOrderAndProduct(orderId, productId);
    }
}
