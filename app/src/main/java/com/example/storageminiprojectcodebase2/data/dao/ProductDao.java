package com.example.storageminiprojectcodebase2.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.storageminiprojectcodebase2.data.entity.Product;
import java.util.List;
@Dao
public interface ProductDao {
    @Insert
    void insert(Product product);

    @Update
    void update(Product product);

    @Query("SELECT * FROM products")
    LiveData<List<Product>> getAll();

    @Query("SELECT * FROM products WHERE categoryId = :categoryId")
    LiveData<List<Product>> getByCategoryId(int categoryId);

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    Product findById(int id);
}