package com.example.storageminiprojectcodebase2.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.storageminiprojectcodebase2.data.database.AppDatabase;
import com.example.storageminiprojectcodebase2.data.dao.ProductDao;
import com.example.storageminiprojectcodebase2.data.entity.Product;
import java.util.List;

public class ProductRepository {
    private final ProductDao productDao;

    public ProductRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        productDao = db.productDao();
    }

    public LiveData<List<Product>> getAll() {
        return productDao.getAll();
    }

    public LiveData<List<Product>> getByCategoryId(int categoryId) {
        return productDao.getByCategoryId(categoryId);
    }

    public Product findById(int id) {
        return productDao.findById(id);
    }

    public void update(Product product) {
        AppDatabase.databaseExecutor.execute(() -> productDao.update(product));
    }
}
