package model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Order {
    private String orderId;
    private String customerName;
    private LocalDate orderDate;
    private String status;
    // Dùng Map<String, Integer> lưu: Key = ProductId, Value = Số lượng mua
    private Map<String, Integer> items = new HashMap<>();

    public Order(String orderId, String customerName, LocalDate orderDate, String status) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.orderDate = orderDate;
        this.status = status;
    }

    // Thêm sản phẩm vào đơn hàng cực nhanh
    public void addItem(String productId, int quantity) {
        items.put(productId, quantity);
    }

    public String getOrderId() { return orderId; }
    public String getCustomerName() { return customerName; }
    public LocalDate getOrderDate() { return orderDate; }
    public String getStatus() { return status; }
    public Map<String, Integer> getItems() { return items; }
    
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("Order{ID='%s', Khách='%s', Ngày=%s, Trạng thái='%s', Items=%s}", 
                orderId, customerName, orderDate, status, items);
    }
}