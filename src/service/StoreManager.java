package service;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import io.CsvOrderIO;
import io.CsvProductIO;
import model.Product;
import model.Order;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StoreManager {
    private List<Product> products;
    private List<Order> orders;

    // Constructor nhận vào dữ liệu đã đọc từ file CSV
    public StoreManager(List<Product> products, List<Order> orders) {
        this.products = products;
        this.orders = orders;
    }

    // Hoàn thiện hàm hiển thị danh sách sản phẩm chuyên nghiệp, đẹp mắt
    public void DisplayAllProducts() {
        System.out.println("\n==================================================================");
        System.out.println("                         DANH SÁCH SẢN PHẨM                       ");
        System.out.println("==================================================================");

        if (products == null || products.isEmpty()) {
            System.out.println("⚠️ Kho hàng hiện đang trống!");
            return;
        }

        // Định dạng tiền tệ có dấu phẩy ngăn cách (Ví dụ: 1,200.00 đ)
        DecimalFormat df = new DecimalFormat("#,##0.00");

        // In tiêu đề cột với độ rộng cố định để căn lề thẳng hàng
        System.out.printf("%-8s | %-20s | %-15s | %12s | %6s\n",
                "Mã SP", "Tên Sản Phẩm", "Danh Mục", "Giá Bán", "Tồn Kho");
        System.out.println("------------------------------------------------------------------");

        // Duyệt danh sách và in từng dòng dữ liệu
        for (Product p : products) {
            System.out.printf("%-8s | %-20s | %-15s | %12s | %6d\n",
                    p.getProductId(),
                    p.getProductName(),
                    p.getCategory(),
                    df.format(p.getPrice()) + " đ",
                    p.getStock());
        }
        System.out.println("==================================================================\n");
    }

    public void SearchProductByName(String keyword) {
        System.out.println("\n🔍 Kết quả tìm kiếm cho từ khóa: \"" + keyword + "\"");
        if (products == null || products.isEmpty()) {
            System.out.println("⚠️ Kho hàng hiện đang trống!");
            return;
        }
        List<Product> matchedProducts = products.stream()
                .filter(p -> p.getProductName().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
        if (matchedProducts.isEmpty()) {
            System.out.println("❌ Không tìm thấy sản phẩm nào phù hợp!");
            return;
        }
        DecimalFormat df = new DecimalFormat("#,##0.00");
        System.out.println("------------------------------------------------------------------");
        System.out.printf("%-8s | %-20s | %-15s | %12s | %6s\n",
                "Mã SP", "Tên Sản Phẩm", "Danh Mục", "Giá Bán", "Tồn Kho");
        System.out.println("------------------------------------------------------------------");

        for (Product p : matchedProducts) {
            System.out.printf("%-8s | %-20s | %-15s | %12s | %6d\n",
                    p.getProductId(),
                    p.getProductName(),
                    p.getCategory(),
                    df.format(p.getPrice()) + " đ",
                    p.getStock());
        }
        System.out.println("------------------------------------------------------------------\n");
    }

    public void addOrder() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n--- 🛒 TẠO ĐƠN HÀNG MỚI ---");

        try {
            System.out.print("Nhập mã đơn hàng (Ví dụ: ORD03): ");
            String orderId = scanner.nextLine().trim();

            // Kiểm tra trùng mã đơn hàng
            boolean exists = orders.stream().anyMatch(o -> o.getOrderId().equalsIgnoreCase(orderId));
            if (exists) {
                System.out.println("❌ Lỗi: Mã đơn hàng \"" + orderId + "\" đã tồn tại!");
                return;
            }

            System.out.print("Nhập tên khách hàng: ");
            String customerName = scanner.nextLine().trim();

            // Khởi tạo đơn hàng mới với ngày hiện tại và trạng thái PENDING
            Order newOrder = new Order(orderId, customerName, LocalDate.now(), "PENDING");

            // Vòng lặp cho phép khách mua nhiều sản phẩm trong 1 đơn
            boolean addingItems = true;
            while (addingItems) {
                System.out.print("Nhập mã sản phẩm muốn mua: ");
                String productId = scanner.nextLine().trim();

                // Tìm sản phẩm trong kho bằng Stream API
                Product product = products.stream()
                        .filter(p -> p.getProductId().equalsIgnoreCase(productId))
                        .findFirst()
                        .orElse(null);

                if (product == null) {
                    System.out.println("❌ Không tìm thấy sản phẩm có mã \"" + productId + "\" trong kho!");
                } else {
                    System.out.print("Nhập số lượng muốn mua (Tồn kho: " + product.getStock() + "): ");
                    int quantity = Integer.parseInt(scanner.nextLine().trim());

                    if (quantity <= 0) {
                        System.out.println("❌ Số lượng phải lớn hơn 0!");
                    } else if (quantity > product.getStock()) {
                        System.out.println("❌ Không đủ hàng trong kho! (Chỉ còn " + product.getStock() + ")");
                    } else {
                        // Thêm vào đơn hàng dạng gộp P01:2
                        newOrder.addItem(product.getProductId(), quantity);

                        // Trừ trực tiếp số lượng tồn kho của sản phẩm
                        // (Trong thực tế thực tế doanh nghiệp sẽ trừ khi đơn hàng COMPLETED, ở đây ta
                        // minh họa luôn)
                        // product.setStock(product.getStock() - quantity);

                        System.out.println("➕ Đã thêm sản phẩm vào đơn hàng.");
                    }
                }

                System.out.print("Bạn có muốn mua thêm sản phẩm khác trong đơn này không? (y/n): ");
                String choice = scanner.nextLine().trim();
                if (choice.equalsIgnoreCase("n")) {
                    addingItems = false;
                }
            }

            if (newOrder.getItems().isEmpty()) {
                System.out.println("⚠️ Đơn hàng trống, hủy tạo đơn!");
                return;
            }

            // Thêm vào danh sách đơn hàng chung
            orders.add(newOrder);

            // Tự động ghi lại file CSV đơn hàng
            CsvOrderIO orderIO = new CsvOrderIO();
            orderIO.writeOrders("src/data/orders.csv", orders);

            System.out.println("✅ Tạo đơn hàng thành công và đã lưu vào hệ thống!");

        } catch (NumberFormatException e) {
            System.out.println("❌ Lỗi nhập số lượng! Vui lòng nhập số nguyên hợp lệ.");
        } catch (IOException e) {
            System.out.println("❌ Lỗi ghi file CSV đơn hàng: " + e.getMessage());
        }
    }

    public void addProduct() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n--- ➕ THÊM SẢN PHẨM MỚI VÀO KHO ---");

        try {
            System.out.print("Nhập mã sản phẩm (Ví dụ: P06): ");
            String id = scanner.nextLine().trim();

            // Kiểm tra trùng mã sản phẩm
            boolean exists = products.stream().anyMatch(p -> p.getProductId().equalsIgnoreCase(id));
            if (exists) {
                System.out.println("❌ Lỗi: Mã sản phẩm \"" + id + "\" đã tồn tại trong kho!");
                return;
            }

            System.out.print("Nhập tên sản phẩm: ");
            String name = scanner.nextLine().trim();

            System.out.print("Nhập danh mục (Ví dụ: Electronics / Accessories): ");
            String category = scanner.nextLine().trim();

            System.out.print("Nhập giá bán ($ hoặc đ): ");
            double price = Double.parseDouble(scanner.nextLine().trim());
            if (price < 0) {
                System.out.println("❌ Giá tiền không được nhỏ hơn 0!");
                return;
            }

            System.out.print("Nhập số lượng tồn kho: ");
            int stock = Integer.parseInt(scanner.nextLine().trim());
            if (stock < 0) {
                System.out.println("❌ Số lượng tồn kho không được nhỏ hơn 0!");
                return;
            }

            // Tạo đối tượng Product mới và thêm vào danh sách
            Product newProduct = new Product(id, name, category, price, stock);
            products.add(newProduct);

            // Tự động ghi lại file CSV sản phẩm để lưu vĩnh viễn
            CsvProductIO productIO = new CsvProductIO();
            productIO.writeProducts("src/data/products.csv", products);

            System.out.println("✅ Thêm sản phẩm thành công và đã cập nhật file CSV!");

        } catch (NumberFormatException e) {
            System.out.println("❌ Lỗi định dạng số! Vui lòng nhập lại giá hoặc số lượng bằng chữ số hợp lệ.");
        } catch (IOException e) {
            System.out.println("❌ Lỗi ghi file CSV: " + e.getMessage());
        }
    }

    public void updateOrderStatus() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n--- 🔄 CẬP NHẬT TRẠNG THÁI ĐƠN HÀNG ---");

        if (orders == null || orders.isEmpty()) {
            System.out.println("⚠️ Hệ thống chưa có đơn hàng nào!");
            return;
        }

        System.out.print("Nhập mã đơn hàng cần cập nhật (Ví dụ: ORD01): ");
        String orderId = scanner.nextLine().trim();

        // Tìm đơn hàng trong danh sách
        Order targetOrder = orders.stream()
                .filter(o -> o.getOrderId().equalsIgnoreCase(orderId))
                .findFirst()
                .orElse(null);

        if (targetOrder == null) {
            System.out.println("❌ Không tìm thấy đơn hàng có mã: " + orderId);
            return;
        }

        System.out.println("📌 Trạng thái hiện tại của đơn " + orderId + ": " + targetOrder.getStatus());
        System.out.println("Chọn trạng thái mới:");
        System.out.println("1. COMPLETED (Hoàn thành)");
        System.out.println("2. CANCELLED (Đã hủy)");
        System.out.print("Nhập lựa chọn (1 hoặc 2): ");

        String choice = scanner.nextLine().trim();
        String newStatus = "";

        if (choice.equals("1")) {
            newStatus = "COMPLETED";
        } else if (choice.equals("2")) {
            newStatus = "CANCELLED";
        } else {
            System.out.println("❌ Lựa chọn không hợp lệ, hủy cập nhật!");
            return;
        }

        targetOrder.setStatus(newStatus);

        // Lưu lại thay đổi vào file CSV đơn hàng
        try {
            CsvOrderIO orderIO = new CsvOrderIO();
            orderIO.writeOrders("src/data/orders.csv", orders);
            System.out
                    .println("✅ Đã cập nhật trạng thái đơn hàng thành công sang [" + newStatus + "] và lưu vào file!");
        } catch (IOException e) {
            System.out.println("❌ Lỗi ghi file CSV: " + e.getMessage());
        }
    }

    public double calculateOrderTotal(Order order) {
        return order.getItems().entrySet().stream()
                .mapToDouble(entry -> {
                    String productId = entry.getKey();
                    double qty = entry.getValue();
                    double price = products.stream()
                            .filter(p -> p.getProductId().equalsIgnoreCase(productId))
                            .mapToDouble(p -> p.getPrice())
                            .findFirst()
                            .orElse(0.0);
                    return price * qty;
                })
                .sum();
    }

    // A. Thống kê doanh thu theo Tháng (Dùng groupingBy + summingDouble)
    public void printRevenueByMonth() {
        System.out.println("\n--- 📈 THỐNG KÊ DOANH THU THEO THÁNG (Đơn COMPLETED) ---");

        DecimalFormat df = new DecimalFormat("#,##0.00");

        // Gom nhóm theo năm-tháng (YYYY-MM) của các đơn hàng có trạng thái COMPLETED
        Map<String, Double> revenueByMonth = orders.stream()
                .filter(o -> "COMPLETED".equalsIgnoreCase(o.getStatus()))
                .collect(Collectors.groupingBy(
                        o -> o.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM")),
                        Collectors.summingDouble(this::calculateOrderTotal)));

        if (revenueByMonth.isEmpty()) {
            System.out.println("⚠️ Chưa có dữ liệu doanh thu từ các đơn hàng hoàn thành.");
            return;
        }

        revenueByMonth
                .forEach((month, revenue) -> System.out.printf("   📅 Tháng %s: %15s đ\n", month, df.format(revenue)));
    }

    // B. Thống kê sản phẩm bán chạy nhất
    public void printBestSellingProducts() {
        System.out.println("\n--- 🏆 THỐNG KÊ SẢN PHẨM BÁN CHẠY NHẤT ---");

        // Gom nhóm tổng số lượng bán ra của từng productId từ tất cả đơn hàng COMPLETED
        Map<String, Integer> productSales = orders.stream()
                .filter(o -> "COMPLETED".equalsIgnoreCase(o.getStatus()))
                .flatMap(o -> o.getItems().entrySet().stream())
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.summingInt(Map.Entry::getValue)));

        if (productSales.isEmpty()) {
            System.out.println("⚠️ Chưa có dữ liệu bán hàng.");
            return;
        }

        // Tìm sản phẩm có số lượng bán cao nhất
        productSales.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(3) // Lấy top 3 sản phẩm bán chạy
                .forEach(entry -> {
                    String prodId = entry.getKey();
                    int totalSold = entry.getValue();
                    // Lấy tên sản phẩm cho dễ đọc
                    String prodName = products.stream()
                            .filter(p -> p.getProductId().equalsIgnoreCase(prodId))
                            .map(Product::getProductName)
                            .findFirst()
                            .orElse("Không rõ");
                    System.out.printf("   🔥 [%s] %-18s -- Đã bán: %d sản phẩm\n", prodId, prodName, totalSold);
                });
    }

    // C. Tìm Top Khách hàng VIP (Chi tiêu nhiều nhất)
    public void printTopVipCustomers() {
        System.out.println("\n--- 👑 TOP KHÁCH HÀNG TIỀM NĂNG (VIP) ---");

        DecimalFormat df = new DecimalFormat("#,##0.00");

        // Gom nhóm tổng tiền chi tiêu theo tên khách hàng từ các đơn COMPLETED
        Map<String, Double> customerSpending = orders.stream()
                .filter(o -> "COMPLETED".equalsIgnoreCase(o.getStatus()))
                .collect(Collectors.groupingBy(
                        Order::getCustomerName,
                        Collectors.summingDouble(this::calculateOrderTotal)));

        if (customerSpending.isEmpty()) {
            System.out.println("⚠️ Chưa có dữ liệu chi tiêu từ khách hàng.");
            return;
        }

        // Sắp xếp giảm dần theo giá trị chi tiêu và hiển thị Top khách hàng
        customerSpending.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(3) // Lấy top 3 khách hàng chi tiêu nhiều nhất
                .forEach(entry -> System.out.printf("   💎 Khách hàng: %-15s -- Tổng chi tiêu: %15s đ\n",
                        entry.getKey(), df.format(entry.getValue())));
    }
}
