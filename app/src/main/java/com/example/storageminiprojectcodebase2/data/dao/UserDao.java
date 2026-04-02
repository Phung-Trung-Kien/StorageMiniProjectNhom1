package com.example.storageminiprojectcodebase2.data.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.storageminiprojectcodebase2.data.entity.User;

@Dao
public interface UserDao {
    @Insert
    long insert(User user);

    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    User login(String username, String password);

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    User findByUsername(String username);

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    User findById(int id);
}
