package com.example.storageminiprojectcodebase2.data.database;


import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.example.storageminiprojectcodebase2.data.dao.*;
import com.example.storageminiprojectcodebase2.data.entity.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {User.class, Category.class, Product.class, Order.class, OrderDetail.class},
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract CategoryDao categoryDao();
    public abstract ProductDao productDao();
    public abstract OrderDao orderDao();
    public abstract OrderDetailDao orderDetailDao();

    private static volatile AppDatabase INSTANCE;
    public static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(4);

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "shopping_app_db"
                            )
                            .addCallback(seedCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // Seed dữ liệu mẫu khi tạo DB lần đầu
    private static final RoomDatabase.Callback seedCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseExecutor.execute(() -> {
                UserDao userDao = INSTANCE.userDao();
                CategoryDao categoryDao = INSTANCE.categoryDao();
                ProductDao productDao = INSTANCE.productDao();

                // Users
                User admin = new User();
                admin.username = "admin";
                admin.password = "123456";
                admin.fullName = "Administrator";
                admin.email = "admin@shop.com";
                userDao.insert(admin);

                User user1 = new User();
                user1.username = "user1";
                user1.password = "123456";
                user1.fullName = "Nguyen Van A";
                user1.email = "user1@shop.com";
                userDao.insert(user1);

                // Categories
                Category cat1 = new Category();
                cat1.name = "Điện thoại";
                cat1.description = "Điện thoại di động các loại";
                categoryDao.insert(cat1);

                Category cat2 = new Category();
                cat2.name = "Laptop";
                cat2.description = "Máy tính xách tay";
                categoryDao.insert(cat2);

                Category cat3 = new Category();
                cat3.name = "Phụ kiện";
                cat3.description = "Phụ kiện điện tử";
                categoryDao.insert(cat3);

                // Products
                Product p1 = new Product();
                p1.name = "iPhone 15 Pro";
                p1.description = "Apple iPhone 15 Pro 256GB";
                p1.price = 28990000;
                p1.stock = 10;
                p1.categoryId = 1;
                productDao.insert(p1);

                Product p2 = new Product();
                p2.name = "Samsung Galaxy S24";
                p2.description = "Samsung Galaxy S24 256GB";
                p2.price = 22990000;
                p2.stock = 15;
                p2.categoryId = 1;
                productDao.insert(p2);

                Product p3 = new Product();
                p3.name = "MacBook Air M3";
                p3.description = "Apple MacBook Air 13 inch M3 8GB 256GB";
                p3.price = 32990000;
                p3.stock = 5;
                p3.categoryId = 2;
                productDao.insert(p3);

                Product p4 = new Product();
                p4.name = "Dell XPS 13";
                p4.description = "Dell XPS 13 Intel Core i7 16GB 512GB";
                p4.price = 27990000;
                p4.stock = 8;
                p4.categoryId = 2;
                productDao.insert(p4);

                Product p5 = new Product();
                p5.name = "Tai nghe AirPods Pro";
                p5.description = "Apple AirPods Pro Gen 2";
                p5.price = 6490000;
                p5.stock = 20;
                p5.categoryId = 3;
                productDao.insert(p5);

                Product p6 = new Product();
                p6.name = "Chuột Logitech MX Master 3";
                p6.description = "Chuột không dây cao cấp";
                p6.price = 1990000;
                p6.stock = 30;
                p6.categoryId = 3;
                productDao.insert(p6);
            });
        }
    };
}
