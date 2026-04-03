package com.example.storageminiprojectcodebase2.data.database;


import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.example.storageminiprojectcodebase2.data.dao.*;
import com.example.storageminiprojectcodebase2.data.entity.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {User.class, Category.class, Product.class, Order.class, OrderDetail.class},
        version = 2,
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
                            .addMigrations(MIGRATION_1_2)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("UPDATE products SET imageUrl = 'tao_envy' WHERE name = 'Táo Envy Mỹ'");
            database.execSQL("UPDATE products SET imageUrl = 'nho_mau_don_hq' WHERE name = 'Nho mẫu đơn Hàn Quốc'");
            database.execSQL("UPDATE products SET imageUrl = 'xoai_cat_hoa_loc' WHERE name = 'Xoài cát Hòa Lộc'");
            database.execSQL("UPDATE products SET imageUrl = 'thanh_long_ruot_do' WHERE name = 'Thanh long ruột đỏ'");
            database.execSQL("UPDATE products SET imageUrl = 'mit_say' WHERE name = 'Mít sấy'");
            database.execSQL("UPDATE products SET imageUrl = 'chuoi_say_deo' WHERE name = 'Chuối sấy dẻo'");
        }
    };

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
                cat1.name = "Hoa quả nhập khẩu";
                cat1.description = "Các loại hoa quả nhập khẩu cao cấp";
                categoryDao.insert(cat1);

                Category cat2 = new Category();
                cat2.name = "Hoa quả nội địa";
                cat2.description = "Trái cây tươi ngon từ các vùng miền Việt Nam";
                categoryDao.insert(cat2);

                Category cat3 = new Category();
                cat3.name = "Hoa quả sấy khô";
                cat3.description = "Các loại trái cây sấy khô tự nhiên";
                categoryDao.insert(cat3);

                // Products
                Product p1 = new Product();
                p1.name = "Táo Envy Mỹ";
                p1.description = "Táo Envy size lớn, giòn ngọt, nhập khẩu Mỹ";
                p1.price = 150000;
                p1.stock = 50;
                p1.categoryId = 1;
                p1.imageUrl = "tao_envy";
                productDao.insert(p1);

                Product p2 = new Product();
                p2.name = "Nho mẫu đơn Hàn Quốc";
                p2.description = "Nho mẫu đơn (Shine Muscat) giòn, thơm mùi sữa";
                p2.price = 450000;
                p2.stock = 20;
                p2.categoryId = 1;
                p2.imageUrl = "nho_mau_don_hq";
                productDao.insert(p2);

                Product p3 = new Product();
                p3.name = "Xoài cát Hòa Lộc";
                p3.description = "Xoài cát Hòa Lộc loại 1, thơm ngon đặc sản";
                p3.price = 85000;
                p3.stock = 100;
                p3.categoryId = 2;
                p3.imageUrl = "xoai_cat_hoa_loc";
                productDao.insert(p3);

                Product p4 = new Product();
                p4.name = "Thanh long ruột đỏ";
                p4.description = "Thanh long ruột đỏ Bình Thuận, ngọt lịm";
                p4.price = 35000;
                p4.stock = 200;
                p4.categoryId = 2;
                p4.imageUrl = "thanh_long_ruot_do";
                productDao.insert(p4);

                Product p5 = new Product();
                p5.name = "Mít sấy";
                p5.description = "Mít sấy giòn xuất khẩu, không đường";
                p5.price = 60000;
                p5.stock = 150;
                p5.categoryId = 3;
                p5.imageUrl = "mit_say";
                productDao.insert(p5);

                Product p6 = new Product();
                p6.name = "Chuối sấy dẻo";
                p6.description = "Chuối sứ sấy dẻo tự nhiên, vị ngọt thanh";
                p6.price = 45000;
                p6.stock = 120;
                p6.categoryId = 3;
                p6.imageUrl = "chuoi_say_deo";
                productDao.insert(p6);
            });
        }
    };
}
