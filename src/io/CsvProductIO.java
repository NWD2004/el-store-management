package io;

import model.Product;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CsvProductIO implements ProductIO {

    @Override
    public List<Product> readProducts(String filePath) throws IOException {
        List<Product> products = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Bỏ qua tiêu đề
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                
                String[] data = line.split(",", -1);
                if (data.length != 5) {
                    throw new IOException("Dòng sản phẩm không hợp lệ: " + line);
                }

                Product product = new Product(
                    data[0].trim(),
                    data[1].trim(),
                    data[2].trim(),
                    Double.parseDouble(data[3].trim()),
                    Integer.parseInt(data[4].trim())
                );
                products.add(product);
            }
        }
        return products;
    }

    @Override
    public void writeProducts(String filePath, List<Product> products) throws IOException {
        Path path = Path.of(filePath);
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }

        try (BufferedWriter bw = Files.newBufferedWriter(path)) {
            bw.write("productId,productName,category,price,stock");
            bw.newLine();
            for (Product p : products) {
                bw.write(String.format("%s,%s,%s,%.2f,%d",
                    p.getProductId(),
                    p.getProductName(),
                    p.getCategory(),
                    p.getPrice(),
                    p.getStock()
                ));
                bw.newLine();
            }
        }
    }
}