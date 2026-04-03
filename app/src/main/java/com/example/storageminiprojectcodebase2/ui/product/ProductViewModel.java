package com.example.storageminiprojectcodebase2.ui.product;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.storageminiprojectcodebase2.data.entity.Product;
import com.example.storageminiprojectcodebase2.repository.ProductRepository;
import java.util.List;

public class ProductViewModel extends AndroidViewModel {
    private final ProductRepository repository;
    public LiveData<List<Product>> products;

    public ProductViewModel(Application app) {
        super(app);
        repository = new ProductRepository(app);
        products = repository.getAll();
    }

    public LiveData<List<Product>> getByCategory(int categoryId) {
        return repository.getByCategoryId(categoryId);
    }
}
