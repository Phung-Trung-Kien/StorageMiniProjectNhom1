package com.example.storageminiprojectcodebase2.data.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.storageminiprojectcodebase2.data.entity.Order;
import java.util.List;

@Dao
public interface OrderDao {
    @Insert
    long insert(Order order);

    @Update
    void update(Order order);

    @Query("SELECT * FROM orders WHERE userId = :userId")
    LiveData<List<Order>> getByUserId(int userId);

    @Query("SELECT * FROM orders WHERE userId = :userId AND status = 'PENDING' LIMIT 1")
    Order getPendingOrderByUserId(int userId);

    @Query("SELECT * FROM orders WHERE id = :id LIMIT 1")
    Order findById(int id);
}
