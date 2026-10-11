package io;

import model.Order;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CsvOrderIO implements OrderIO {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public List<Order> readOrders(String filePath) throws IOException {
        List<Order> orders = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Bỏ qua tiêu đề: orderId,customerName,orderDate,status,items
            
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                
                String[] data = line.split(",", -1);
                if (data.length != 5) {
                    throw new IOException("Dòng đơn hàng không hợp lệ: " + line);
                }

                String orderId = data[0].trim();
                String customerName = data[1].trim();
                LocalDate orderDate = LocalDate.parse(data[2].trim(), FORMATTER);
                String status = data[3].trim();
                
                Order order = new Order(orderId, customerName, orderDate, status);

                // Xử lý chuỗi items dạng: P01:2;P03:1
                String rawItems = data[4].trim();
                if (!rawItems.isEmpty()) {
                    String[] items = rawItems.split(";");
                    for (String item : items) {
                        String[] parts = item.split(":");
                        if (parts.length == 2) {
                            String productId = parts[0].trim();
                            int quantity = Integer.parseInt(parts[1].trim());
                            order.addItem(productId, quantity);
                        }
                    }
                }
                orders.add(order);
            }
        }
        return orders;
    }

    @Override
    public void writeOrders(String filePath, List<Order> orders) throws IOException {
        Path path = Path.of(filePath);
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }

        try (BufferedWriter bw = Files.newBufferedWriter(path)) {
            bw.write("orderId,customerName,orderDate,status,items");
            bw.newLine();
            
            for (Order order : orders) {
                // Chuyển Map<String, Integer> items thành chuỗi P01:2;P03:1
                StringBuilder itemsStr = new StringBuilder();
                for (Map.Entry<String, Integer> entry : order.getItems().entrySet()) {
                    if (itemsStr.length() > 0) {
                        itemsStr.append(";");
                    }
                    itemsStr.append(entry.getKey()).append(":").append(entry.getValue());
                }

                bw.write(String.format("%s,%s,%s,%s,%s",
                    order.getOrderId(),
                    order.getCustomerName(),
                    order.getOrderDate().format(FORMATTER),
                    order.getStatus(),
                    itemsStr.toString()
                ));
                bw.newLine();
            }
        }
    }
}