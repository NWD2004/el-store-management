package io;

import model.Order;
import java.io.IOException;
import java.util.List;

public interface OrderIO {
    List<Order> readOrders(String filePath) throws IOException;
    void writeOrders(String filePath, List<Order> orders) throws IOException;
}