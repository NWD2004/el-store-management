package io;

import model.Product;
import java.io.IOException;
import java.util.List;

public interface ProductIO {
    List<Product> readProducts(String filePath) throws IOException;
    void writeProducts(String filePath, List<Product> products) throws IOException;
}