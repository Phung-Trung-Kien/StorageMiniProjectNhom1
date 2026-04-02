package com.example.storageminiprojectcodebase2.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.storageminiprojectcodebase2.data.entity.Category;
import java.util.List;

@Dao
public interface CategoryDao {
    @Insert
    void insert(Category category);

    @Query("SELECT * FROM categories")
    LiveData<List<Category>> getAll();

    @Query("SELECT * FROM categories WHERE id = :id LIMIT 1")
    Category findById(int id);
}
