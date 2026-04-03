package com.example.storageminiprojectcodebase2.data.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.storageminiprojectcodebase2.data.entity.OrderDetail;
import java.util.List;

@Dao
public interface OrderDetailDao {
    @Insert
    void insert(OrderDetail orderDetail);

    @Update
    void update(OrderDetail orderDetail);

    @Query("SELECT * FROM order_details WHERE orderId = :orderId")
    LiveData<List<OrderDetail>> getByOrderId(int orderId);

    @Query("SELECT * FROM order_details WHERE orderId = :orderId AND productId = :productId LIMIT 1")
    OrderDetail findByOrderAndProduct(int orderId, int productId);

    @Query("SELECT * FROM order_details WHERE orderId = :orderId")
    List<OrderDetail> getSyncByOrderId(int orderId);

    @Query("DELETE FROM order_details WHERE orderId = :orderId")
    void deleteByOrderId(int orderId);
}
