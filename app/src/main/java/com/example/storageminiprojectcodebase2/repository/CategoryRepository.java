package com.example.storageminiprojectcodebase2.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.storageminiprojectcodebase2.data.database.AppDatabase;
import com.example.storageminiprojectcodebase2.data.dao.CategoryDao;
import com.example.storageminiprojectcodebase2.data.entity.Category;
import java.util.List;

public class CategoryRepository {
    private final CategoryDao categoryDao;

    public CategoryRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        categoryDao = db.categoryDao();
    }

    public LiveData<List<Category>> getAll() {
        return categoryDao.getAll();
    }

    public Category findById(int id) {
        return categoryDao.findById(id);
    }
}
