package com.example.storageminiprojectcodebase2.ui.category;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.storageminiprojectcodebase2.data.entity.Category;
import com.example.storageminiprojectcodebase2.repository.CategoryRepository;
import java.util.List;

public class CategoryViewModel extends AndroidViewModel {
    private final CategoryRepository repository;
    public LiveData<List<Category>> categories;

    public CategoryViewModel(Application app) {
        super(app);
        repository = new CategoryRepository(app);
        categories = repository.getAll();
    }
}
