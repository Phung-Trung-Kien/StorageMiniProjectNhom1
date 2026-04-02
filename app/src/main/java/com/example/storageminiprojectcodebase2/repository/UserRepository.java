package com.example.storageminiprojectcodebase2.repository;

import android.app.Application;
import com.example.storageminiprojectcodebase2.data.database.AppDatabase;
import com.example.storageminiprojectcodebase2.data.dao.UserDao;
import com.example.storageminiprojectcodebase2.data.entity.User;

public class UserRepository {
    private final UserDao userDao;

    public UserRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        userDao = db.userDao();
    }

    public User login(String username, String password) {
        return userDao.login(username, password);
    }

    public long insert(User user) {
        return userDao.insert(user);
    }

    public User findById(int id) {
        return userDao.findById(id);
    }
}
