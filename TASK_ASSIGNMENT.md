# 📋 Phân Chia Nhiệm Vụ – Shopping App (5 Thành Viên)

> Sau khi **clone code base từ GitHub về**, mỗi thành viên thực hiện nhiệm vụ của mình trên một **branch riêng**, rồi tạo Pull Request để merge vào `main`.

---

## 🌿 Quy tắc làm việc chung

1. Clone repo: `git clone <URL_REPO>`
2. Tạo branch trước khi làm: `git checkout -b feature/<ten-tinh-nang>`
3. Commit thường xuyên với message rõ ràng
4. Khi xong, tạo Pull Request lên `main` và báo nhóm trưởng review
5. **Không commit thẳng lên `main`**

---

## 👤 THÀNH VIÊN 1 – Màn hình Home & Đăng nhập

**Branch:** `feature/auth-home`

### Mục tiêu
Xây dựng màn hình chính (Home) và luồng đăng nhập/đăng xuất.

---

### Nhiệm vụ chi tiết

#### 1. Tạo `LoginActivity`

**File:** `ui/auth/LoginActivity.java`

- Layout gồm: EditText username, EditText password (inputType=textPassword), Button đăng nhập, TextView thông báo lỗi
- Khi nhấn đăng nhập:
  - Validate không để trống username/password
  - Gọi `UserRepository.login(username, password)` trên background thread
  - Nếu thành công: gọi `SessionManager.createSession(userId, username)` rồi `finish()`
  - Nếu thất bại: hiển thị thông báo lỗi "Sai tên đăng nhập hoặc mật khẩu"
- Nếu đã đăng nhập (`SessionManager.isLoggedIn()`), tự động `finish()` ngay khi mở

**File layout:** `res/layout/activity_login.xml`

---

#### 2. Tạo `HomeActivity` (Activity chính)

**File:** `ui/home/HomeActivity.java`

- Là màn hình chính sau khi app khởi động
- Sử dụng `BottomNavigationView` với 3 tab: **Sản phẩm**, **Danh mục**, **Đơn hàng**
- Góc trên bên phải:
  - Nếu chưa đăng nhập: hiển thị Button "Đăng nhập"
  - Nếu đã đăng nhập: hiển thị tên user + Button "Đăng xuất"
- Khi nhấn "Đăng xuất": gọi `SessionManager.logout()`, cập nhật lại UI

**File layout:** `res/layout/activity_home.xml`

```xml
<!-- Gợi ý cấu trúc layout -->
<!-- CoordinatorLayout chứa: -->
<!--   AppBarLayout > Toolbar (với title + login button) -->
<!--   FrameLayout id="fragment_container" (chứa Fragment) -->
<!--   BottomNavigationView -->
```

---

#### 3. Cập nhật `MainActivity.java`

- Kiểm tra xem `SessionManager` đã có session chưa (không cần thiết, chỉ redirect sang `HomeActivity`)
- `MainActivity` chỉ đóng vai trò Splash/Redirect, chuyển sang `HomeActivity` rồi `finish()`

---

#### 4. Cập nhật `AndroidManifest.xml`

Thêm khai báo `LoginActivity` và `HomeActivity`:

```xml
<activity android:name=".ui.auth.LoginActivity" />
<activity android:name=".ui.home.HomeActivity"
    android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
```

---

### Kết quả bàn giao
- [ ] `LoginActivity.java` + `activity_login.xml` hoạt động đúng
- [ ] `HomeActivity.java` + `activity_home.xml` có BottomNavigation
- [ ] Đăng nhập/đăng xuất cập nhật UI đúng
- [ ] Không crash khi mở app lần đầu

---

## 👤 THÀNH VIÊN 2 – Danh sách Danh mục & Sản phẩm theo Danh mục

**Branch:** `feature/category-list`

### Mục tiêu
Xây dựng màn hình danh sách danh mục và xem sản phẩm theo danh mục.

---

### Nhiệm vụ chi tiết

#### 1. Tạo `CategoryViewModel`

**File:** `ui/category/CategoryViewModel.java`

```java
public class CategoryViewModel extends AndroidViewModel {
    private final CategoryRepository repository;
    public LiveData<List<Category>> categories;

    public CategoryViewModel(Application app) {
        super(app);
        repository = new CategoryRepository(app);
        categories = repository.getAll();
    }
}
```

---

#### 2. Tạo `CategoryFragment`

**File:** `ui/category/CategoryFragment.java`

- Hiển thị danh sách danh mục bằng `RecyclerView`
- Mỗi item hiển thị: tên danh mục, mô tả
- Nhấn vào 1 danh mục → mở `ProductByCategoryActivity` (truyền `categoryId` và `categoryName` qua Intent)

**File layout:** `res/layout/fragment_category.xml`  
**Item layout:** `res/layout/item_category.xml`

---

#### 3. Tạo `CategoryAdapter`

**File:** `ui/category/CategoryAdapter.java`

- `RecyclerView.Adapter` với ViewHolder
- Nhận `List<Category>` và `OnCategoryClickListener`
- Interface:
  ```java
  interface OnCategoryClickListener {
      void onClick(Category category);
  }
  ```

---

#### 4. Tạo `ProductByCategoryActivity`

**File:** `ui/category/ProductByCategoryActivity.java`

- Nhận `categoryId` và `categoryName` từ Intent
- Hiển thị toolbar title là tên danh mục
- Dùng `ProductRepository.getByCategoryId(categoryId)` để lấy sản phẩm
- Hiển thị danh sách bằng RecyclerView (tái sử dụng `ProductAdapter` của Thành viên 3)
- Nhấn vào sản phẩm → mở `ProductDetailActivity`

**File layout:** `res/layout/activity_product_by_category.xml`

---

#### 5. Khai báo `AndroidManifest.xml`

```xml
<activity android:name=".ui.category.ProductByCategoryActivity" />
```

---

### Kết quả bàn giao
- [ ] `CategoryFragment` hiển thị đúng danh sách
- [ ] `CategoryAdapter` có click listener
- [ ] `ProductByCategoryActivity` hiển thị đúng sản phẩm theo danh mục
- [ ] Điều hướng sang `ProductDetailActivity` hoạt động

---

## 👤 THÀNH VIÊN 3 – Danh sách Sản phẩm & Chi tiết Sản phẩm

**Branch:** `feature/product-list-detail`

### Mục tiêu
Xây dựng màn hình danh sách tất cả sản phẩm và màn hình chi tiết sản phẩm.

---

### Nhiệm vụ chi tiết

#### 1. Tạo `ProductViewModel`

**File:** `ui/product/ProductViewModel.java`

```java
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
```

---

#### 2. Tạo `ProductFragment`

**File:** `ui/product/ProductFragment.java`

- Hiển thị tất cả sản phẩm bằng `RecyclerView` dạng grid 2 cột (`GridLayoutManager`)
- Quan sát `ProductViewModel.products` (LiveData)
- Nhấn vào sản phẩm → mở `ProductDetailActivity` với `productId`

**File layout:** `res/layout/fragment_product.xml`

---

#### 3. Tạo `ProductAdapter`

**File:** `ui/product/ProductAdapter.java`

- RecyclerView Adapter với ViewHolder
- Item hiển thị: tên sản phẩm, giá (định dạng `NumberFormat`), tồn kho
- Interface:
  ```java
  interface OnProductClickListener {
      void onClick(Product product);
  }
  ```

**Item layout:** `res/layout/item_product.xml`

---

#### 4. Tạo `ProductDetailActivity`

**File:** `ui/product/ProductDetailActivity.java`

- Nhận `productId` từ Intent
- Load sản phẩm từ `ProductRepository.findById(productId)` trên background thread
- Hiển thị: tên, mô tả, giá, tồn kho
- Button **"Thêm vào giỏ hàng"**:
  - Nếu chưa đăng nhập → mở `LoginActivity`, sau khi login xong quay về và thực hiện thêm vào giỏ
  - Nếu đã đăng nhập → gọi logic thêm vào giỏ (Thành viên 4 sẽ cung cấp method)

**Tạm thời:** khi chưa có Thành viên 4, hiển thị Toast "Đã thêm vào giỏ hàng" khi nhấn nút (nếu đã login).

**File layout:** `res/layout/activity_product_detail.xml`

---

#### 5. Helper format tiền

**File:** `utils/FormatUtils.java`

```java
public class FormatUtils {
    public static String formatPrice(double price) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(price) + " đ";
    }
}
```

---

#### 6. Khai báo `AndroidManifest.xml`

```xml
<activity android:name=".ui.product.ProductDetailActivity" />
```

---

### Kết quả bàn giao
- [ ] `ProductFragment` hiển thị grid sản phẩm
- [ ] `ProductAdapter` hoạt động với click listener
- [ ] `ProductDetailActivity` hiển thị đầy đủ thông tin
- [ ] Giá hiển thị đúng định dạng VND
- [ ] Nút "Thêm vào giỏ" kiểm tra đăng nhập

---

## 👤 THÀNH VIÊN 4 – Tạo Order & Giỏ hàng (OrderDetail)

**Branch:** `feature/order-cart`

### Mục tiêu
Xây dựng logic và màn hình tạo đơn hàng, thêm/xem sản phẩm trong giỏ hàng.

---

### Nhiệm vụ chi tiết

#### 1. Tạo `OrderViewModel`

**File:** `ui/order/OrderViewModel.java`

```java
public class OrderViewModel extends AndroidViewModel {
    private final OrderRepository repository;
    private final ProductRepository productRepository;
    private final SessionManager session;

    public OrderViewModel(Application app) {
        super(app);
        repository = new OrderRepository(app);
        productRepository = new ProductRepository(app);
        session = new SessionManager(app);
    }

    // Lấy hoặc tạo mới PENDING order của user hiện tại
    // Chạy trên background thread, trả về orderId
    public void getOrCreatePendingOrder(Callback<Integer> callback) {
        AppDatabase.databaseExecutor.execute(() -> {
            int userId = session.getUserId();
            Order pending = repository.getPendingOrder(userId);
            if (pending == null) {
                Order newOrder = new Order();
                newOrder.userId = userId;
                newOrder.status = "PENDING";
                newOrder.createdAt = System.currentTimeMillis();
                newOrder.totalAmount = 0;
                int orderId = (int) repository.insertOrder(newOrder);
                callback.onResult(orderId);
            } else {
                callback.onResult(pending.id);
            }
        });
    }

    // Thêm sản phẩm vào giỏ (nếu đã có thì tăng quantity)
    public void addProductToCart(int orderId, int productId, double unitPrice, Runnable onDone) {
        AppDatabase.databaseExecutor.execute(() -> {
            OrderDetail existing = repository.findDetailByOrderAndProduct(orderId, productId);
            if (existing != null) {
                existing.quantity += 1;
                repository.updateOrderDetail(existing);
            } else {
                OrderDetail detail = new OrderDetail();
                detail.orderId = orderId;
                detail.productId = productId;
                detail.quantity = 1;
                detail.unitPrice = unitPrice;
                repository.insertOrderDetail(detail);
            }
            if (onDone != null) onDone.run();
        });
    }

    public LiveData<List<OrderDetail>> getCartItems(int orderId) {
        return repository.getDetailsByOrder(orderId);
    }

    // Interface callback đơn giản
    public interface Callback<T> {
        void onResult(T result);
    }
}
```

---

#### 2. Tạo `CartFragment`

**File:** `ui/order/CartFragment.java`

- Tab thứ 3 trong BottomNavigation (tab "Đơn hàng / Giỏ hàng")
- Nếu chưa đăng nhập: hiển thị thông báo "Vui lòng đăng nhập để xem giỏ hàng" + Button đăng nhập
- Nếu đã đăng nhập:
  - Gọi `getOrCreatePendingOrder()` để lấy `orderId`
  - Hiển thị `RecyclerView` danh sách sản phẩm trong giỏ
  - Mỗi item hiển thị: tên sản phẩm, số lượng, đơn giá, thành tiền
  - Hiển thị tổng tiền ở cuối
  - Button **"Thanh toán"** → gọi logic checkout (Thành viên 5)
  - Nếu giỏ trống: hiển thị thông báo "Giỏ hàng trống"

**File layout:** `res/layout/fragment_cart.xml`  
**Item layout:** `res/layout/item_cart.xml`

---

#### 3. Tạo `CartAdapter`

**File:** `ui/order/CartAdapter.java`

- Hiển thị `OrderDetail` kết hợp với tên sản phẩm (cần query thêm)
- Tạo inner class `CartItem`:
  ```java
  public class CartItem {
      public OrderDetail detail;
      public String productName;
      public double subtotal; // quantity * unitPrice
  }
  ```

---

#### 4. Expose method `addToCart` để Thành viên 3 gọi

Trong `ProductDetailActivity` (phối hợp với Thành viên 3), khi nhấn "Thêm vào giỏ":
1. Tạo `OrderViewModel`
2. Gọi `getOrCreatePendingOrder(orderId -> addProductToCart(orderId, productId, price, () -> showToast("Đã thêm!")))`

---

### Kết quả bàn giao
- [ ] `OrderViewModel` với đủ các method
- [ ] `CartFragment` hiển thị giỏ hàng đúng
- [ ] Thêm sản phẩm vào giỏ hoạt động (tăng quantity nếu đã có)
- [ ] Hiển thị tổng tiền đúng
- [ ] Điều hướng sang Checkout (Thành viên 5)

---

## 👤 THÀNH VIÊN 5 – Thanh toán (Checkout) & Hiển thị Hóa đơn

**Branch:** `feature/checkout-invoice`

### Mục tiêu
Xây dựng màn hình thanh toán và màn hình hiển thị hóa đơn sau khi đặt hàng.

---

### Nhiệm vụ chi tiết

#### 1. Tạo `CheckoutActivity`

**File:** `ui/order/CheckoutActivity.java`

- Nhận `orderId` từ Intent
- Load danh sách `OrderDetail` và thông tin sản phẩm tương ứng
- Hiển thị:
  - Danh sách sản phẩm (tên, số lượng, đơn giá, thành tiền)
  - Tổng tiền
  - Thông tin người mua (username, lấy từ `SessionManager`)
- Button **"Xác nhận thanh toán"**:
  - Cập nhật `Order.status = "PAID"`
  - Tính lại `Order.totalAmount` (tổng các `quantity * unitPrice`)
  - Gọi `OrderRepository.updateOrder(order)` trên background thread
  - Sau khi xong → mở `InvoiceActivity` với `orderId`, và `finish()` màn hình này

**File layout:** `res/layout/activity_checkout.xml`

---

#### 2. Tạo `InvoiceActivity`

**File:** `ui/order/InvoiceActivity.java`

- Nhận `orderId` từ Intent
- Load đầy đủ thông tin đơn hàng
- Hiển thị hóa đơn gồm:
  - **Tiêu đề:** "HÓA ĐƠN THANH TOÁN"
  - Mã đơn hàng (Order ID)
  - Ngày giờ đặt hàng (format `dd/MM/yyyy HH:mm`)
  - Tên khách hàng
  - Bảng chi tiết: STT | Tên SP | SL | Đơn giá | Thành tiền
  - Tổng tiền (in đậm, nổi bật)
  - Trạng thái: "ĐÃ THANH TOÁN ✓"
- Button **"Tiếp tục mua sắm"**:
  - Quay về `HomeActivity`, xóa toàn bộ back stack
  ```java
  Intent intent = new Intent(this, HomeActivity.class);
  intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
  startActivity(intent);
  finish();
  ```

**File layout:** `res/layout/activity_invoice.xml`

---

#### 3. Tạo `DateUtils.java`

**File:** `utils/DateUtils.java`

```java
public class DateUtils {
    public static String formatDateTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}
```

---

#### 4. Tạo `OrderHistoryFragment` (Bonus nếu còn thời gian)

**File:** `ui/order/OrderHistoryFragment.java`

- Hiển thị lịch sử các đơn hàng đã PAID của user
- Mỗi item: mã đơn, ngày, tổng tiền, trạng thái
- Nhấn vào → mở `InvoiceActivity`

---

#### 5. Khai báo `AndroidManifest.xml`

```xml
<activity android:name=".ui.order.CheckoutActivity" />
<activity android:name=".ui.order.InvoiceActivity" />
```

---

### Kết quả bàn giao
- [ ] `CheckoutActivity` hiển thị đúng tổng kết đơn hàng
- [ ] Cập nhật status Order thành "PAID" thành công
- [ ] `InvoiceActivity` hiển thị hóa đơn đẹp, đầy đủ thông tin
- [ ] Nút "Tiếp tục mua sắm" điều hướng đúng
- [ ] Ngày giờ hiển thị đúng định dạng

---

## 🔗 Sơ đồ phụ thuộc giữa các thành viên

```
TV1 (Home + Login)
    └── TV3 dùng LoginActivity để redirect sau khi chọn sản phẩm

TV2 (Category)
    └── TV3 cung cấp ProductAdapter để dùng trong ProductByCategoryActivity

TV3 (Product List + Detail)
    └── TV4 cung cấp OrderViewModel.addToCart() cho nút "Thêm vào giỏ"

TV4 (Cart)
    └── TV5 cung cấp CheckoutActivity để CartFragment mở khi nhấn "Thanh toán"

TV5 (Checkout + Invoice)
    └── Độc lập, chỉ cần OrderRepository từ code base
```

---

## 📅 Gợi ý tiến độ

| Ngày | Mục tiêu |
|------|----------|
| Ngày 1 | TV1 xong Login + Home skeleton. TV3 xong ProductFragment |
| Ngày 2 | TV2 xong Category. TV3 xong ProductDetail. TV4 bắt đầu Cart |
| Ngày 3 | TV4 xong Cart + addToCart. TV5 xong Checkout |
| Ngày 4 | TV5 xong Invoice. Tất cả merge + test tích hợp |
| Ngày 5 | Fix bug, hoàn thiện UI, chuẩn bị báo cáo |

---

## 🐛 Lưu ý kỹ thuật chung

1. **Không truy cập database trên Main thread** – luôn dùng `AppDatabase.databaseExecutor.execute(() -> { ... })`
2. **Cập nhật UI phải trên Main thread** – dùng `runOnUiThread(() -> { ... })` hoặc `LiveData + Observer`
3. **Mọi Activity/Fragment mới** phải khai báo trong `AndroidManifest.xml`
4. **Tài khoản test mặc định:** `username: admin`, `password: 123456`
5. Nếu cần query phức tạp (JOIN), thêm DAO method mới vào các DAO tương ứng
