package com.example.eggapp.utils;

import com.example.eggapp.model.InventoryItem;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class DataExporter {

    public static void exportToCsv(List<InventoryItem> items, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            // 写入CSV表头
            writer.write("ID,Name,Quantity,SafetyStock,ReorderPoint,EggTypeId,CostPrice,PackagingType\n");

            // 写入数据行
            for (InventoryItem item : items) {
                writer.write(String.format("%d,%s,%d,%d,%d,%d,%.2f,%s\n",
                        item.getId(),
                        escapeCsv(item.getName()),
                        item.getQuantity(),
                        item.getSafetyStock(),
                        item.getReorderPoint(),
                        item.getEggTypeId(),
                        item.getCostPrice(),
                        escapeCsv(item.getPackagingType())));
            }
        }
    }

    // 处理CSV特殊字符
    private static String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
