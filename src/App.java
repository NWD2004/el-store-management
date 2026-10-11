import io.CsvOrderIO;
import io.CsvProductIO;
import model.Order;
import model.Product;
import service.StoreManager;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {

        // ép dùng unicode
        try {
            // Ép Terminal in ra theo chuẩn UTF-8
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        } catch (Exception e) {
            // Bỏ qua nếu hệ thống tự hỗ trợ
        }

        String productFile = "src/data/products.csv";
        String orderFile = "src/data/orders.csv";

        List<Product> products;
        List<Order> orders;

        // 1. Tự động nạp dữ liệu từ file CSV khi khởi động ứng dụng
        try {
            CsvProductIO productIO = new CsvProductIO();
            CsvOrderIO orderIO = new CsvOrderIO();

            products = productIO.readProducts(productFile);
            orders = orderIO.readOrders(orderFile);
            System.out.println("✅ Đã nạp thành công dữ liệu sản phẩm và đơn hàng từ file CSV!");
        } catch (IOException e) {
            System.out.println("❌ Lỗi đọc file CSV khi khởi động: " + e.getMessage());
            return;
        }

        // 2. Khởi tạo bộ quản lý nghiệp vụ StoreManager
        StoreManager manager = new StoreManager(products, orders);
        Scanner scanner = new Scanner(System.in);
        int choice;

        // 3. Vòng lặp hiển thị Menu Terminal tương tác
        do {
            System.out.println("\n==================================================");
            System.out.println("        📱 HỆ THỐNG QUẢN LÝ CỬA HÀNG ĐỒ ĐIỆN TỬ");
            System.out.println("==================================================");
            System.out.println("1. Xem danh sách sản phẩm");
            System.out.println("2. Tìm kiếm sản phẩm theo từ khóa");
            System.out.println("3. Thêm sản phẩm mới vào kho");
            System.out.println("4. Tạo đơn hàng mới");
            System.out.println("5. Cập nhật trạng thái đơn hàng (PENDING -> COMPLETED/CANCELLED)");
            System.out.println("6. Thống kê doanh thu theo Tháng");
            System.out.println("7. Thống kê sản phẩm bán chạy nhất");
            System.out.println("8. Xem Top khách hàng VIP (Chi tiêu nhiều nhất)");
            System.out.println("0. Thoát chương trình");
            System.out.println("--------------------------------------------------");
            System.out.print("Nhập lựa chọn của bạn (0-8): ");

            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                choice = -1; // Xử lý trường hợp người dùng gõ chữ cái
            }

            switch (choice) {
                case 1:
                    manager.DisplayAllProducts();
                    break;
                case 2:
                    System.out.print("Nhập từ khóa cần tìm (Mã, Tên hoặc Danh mục): ");
                    String keyword = scanner.nextLine();
                    manager.SearchProductByName(keyword);
                    break;
                case 3:
                    manager.addProduct();
                    break;
                case 4:
                    manager.addOrder();
                    break;
                case 5:
                    manager.updateOrderStatus();
                    break;
                case 6:
                    manager.printRevenueByMonth();
                    break;
                case 7:
                    manager.printBestSellingProducts();
                    break;
                case 8:
                    manager.printTopVipCustomers();
                    break;
                case 0:
                    System.out.println("👋 Cảm ơn bạn đã sử dụng hệ thống. Hẹn gặp lại!");
                    break;
                default:
                    System.out.println("❌ Lựa chọn không hợp lệ! Vui lòng nhập số từ 0 đến 8.");
            }
        } while (choice != 0);

        scanner.close();
    }
}