package com.example.eggapp.dao;

import com.example.eggapp.model.*;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseHelper {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseHelper.class);
    private static final HikariConfig config = new HikariConfig();
    private static final HikariDataSource dataSource;

    static {
        config.setJdbcUrl("jdbc:mysql://localhost:3306/inventory_db?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai");
        config.setUsername("root");
        config.setPassword("1234");
        config.setMaximumPoolSize(10); // 设置连接池大小
        config.setMinimumIdle(2);     // 设置最小空闲连接数
        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * 获取所有分拣规则
     */
    public static List<SortingRule> getAllSortingRules() throws SQLException {
        List<SortingRule> rules = new ArrayList<>();
        String sql = "SELECT * FROM sorting_rules";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                rules.add(mapToSortingRule(rs));
            }
        } catch (SQLException e) {
            logger.error("Failed to load sorting rules from database", e);
            throw e;
        }
        return rules;
    }

    /**
     * 保存分拣规则
     */
    public static void saveSortingRule(SortingRule rule) throws SQLException {
        String sql = "INSERT INTO sorting_rules (egg_type_id, packaging_type, damage_ratio, packing_cost) " +
                "VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, rule.getEggTypeId());
            pstmt.setString(2, rule.getPackagingType());
            pstmt.setDouble(3, rule.getDamageRatio());
            pstmt.setDouble(4, rule.getPackingCost());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Failed to save sorting rule: {}", rule, e);
            throw e;
        }
    }

    /**
     * 删除分拣规则
     */
    public static void deleteSortingRule(int ruleId) throws SQLException {
        String sql = "DELETE FROM sorting_rules WHERE id=?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ruleId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Failed to delete sorting rule with ID: {}", ruleId, e);
            throw e;
        }
    }

    /**
     * 将 ResultSet 映射为 SortingRule 对象
     */
    private static SortingRule mapToSortingRule(ResultSet rs) throws SQLException {
        return new SortingRule(
                rs.getInt("egg_type_id"),
                rs.getString("packaging_type"),
                rs.getDouble("damage_ratio"),
                rs.getDouble("packing_cost")
        );
    }

    // ==================== 库存 ====================

    /**
     * 查询全部库存项
     */
    public static List<InventoryItem> getAllInventoryItems() throws SQLException {
        List<InventoryItem> items = new ArrayList<>();
        String sql = "SELECT * FROM inventory";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                items.add(mapToInventoryItem(rs));
            }
        } catch (SQLException e) {
            logger.error("Failed to load inventory items", e);
            throw e;
        }
        return items;
    }

    /**
     * 按 ID 查询库存项
     */
    public static InventoryItem getInventoryItem(int id) throws SQLException {
        String sql = "SELECT * FROM inventory WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapToInventoryItem(rs);
                }
            }
        }
        return null;
    }

    /**
     * 更新库存项
     */
    public static void updateInventory(InventoryItem item) throws SQLException {
        String sql = "UPDATE inventory SET name=?, quantity=?, safety_stock=?, reorder_point=?, " +
                "egg_type_id=?, cost_price=?, packaging_type=?, is_virtual=? WHERE id=?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, item.getName());
            pstmt.setInt(2, item.getQuantity());
            pstmt.setInt(3, item.getSafetyStock());
            pstmt.setInt(4, item.getReorderPoint());
            pstmt.setInt(5, item.getEggTypeId());
            pstmt.setDouble(6, item.getCostPrice());
            pstmt.setString(7, item.getPackagingType());
            pstmt.setBoolean(8, item.isVirtual());
            pstmt.setInt(9, item.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Failed to update inventory item: {}", item, e);
            throw e;
        }
    }

    /**
     * 查询低于安全库存的库存项（用于预警）
     */
    public static List<InventoryItem> getLowStockItems() throws SQLException {
        List<InventoryItem> items = new ArrayList<>();
        String sql = "SELECT * FROM inventory WHERE quantity <= safety_stock";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                items.add(mapToInventoryItem(rs));
            }
        }
        return items;
    }

    private static InventoryItem mapToInventoryItem(ResultSet rs) throws SQLException {
        return new InventoryItem(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getInt("quantity"),
                rs.getInt("safety_stock"),
                rs.getInt("reorder_point"),
                rs.getInt("egg_type_id"),
                rs.getDouble("cost_price"),
                rs.getString("packaging_type"),
                rs.getBoolean("is_virtual")
        );
    }

    // ==================== 订单 ====================

    /**
     * 保存一条通用订单（供 Swing 界面调用）
     */
    public static void saveOrder(Order order) throws SQLException {
        String sql = "INSERT INTO orders (shop_id, inventory_id, quantity, price, cost_price, order_type, " +
                "order_date, status, paid, payment_method, packaging_type_id, packaging_type) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, order.getShopId());
            pstmt.setInt(2, order.getInventoryId());
            pstmt.setInt(3, order.getQuantity());
            pstmt.setDouble(4, order.getPrice());
            pstmt.setDouble(5, order.getCostPrice());
            pstmt.setString(6, order.getOrderType());
            pstmt.setTimestamp(7, toTimestamp(order.getOrderDate()));
            pstmt.setString(8, order.getStatus() == null ? null : order.getStatus().name());
            pstmt.setBoolean(9, order.isPaid());
            pstmt.setString(10, order.getPaymentMethod());
            pstmt.setInt(11, order.getPackagingTypeId());
            pstmt.setString(12, order.getPackagingType());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Failed to save order: {}", order, e);
            throw e;
        }
    }

    /**
     * 新增采购订单
     */
    public static void addPurchaseOrder(PurchaseOrder order) throws SQLException {
        String sql = "INSERT INTO purchase_orders (supplier_id, inventory_id, quantity, price, order_date, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, order.getSupplierId());
            pstmt.setInt(2, order.getInventoryId());
            pstmt.setInt(3, order.getQuantity());
            pstmt.setDouble(4, order.getPrice());
            pstmt.setTimestamp(5, toTimestamp(order.getOrderDate()));
            pstmt.setString(6, order.getStatus());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Failed to add purchase order", e);
            throw e;
        }
    }

    /**
     * 新增销售订单
     */
    public static void addSalesOrder(SalesOrder order) throws SQLException {
        String sql = "INSERT INTO sales_orders (customer_id, inventory_id, quantity, price, order_date, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, order.getCustomerId());
            pstmt.setInt(2, order.getInventoryId());
            pstmt.setInt(3, order.getQuantity());
            pstmt.setDouble(4, order.getPrice());
            pstmt.setTimestamp(5, toTimestamp(order.getOrderDate()));
            pstmt.setString(6, order.getStatus());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Failed to add sales order", e);
            throw e;
        }
    }

    /**
     * 查询指定时间之后的采购订单
     */
    public static List<PurchaseOrder> getPurchaseOrdersAfter(Date since) throws SQLException {
        List<PurchaseOrder> list = new ArrayList<>();
        String sql = "SELECT * FROM purchase_orders WHERE order_date >= ? ORDER BY order_date DESC";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setTimestamp(1, new Timestamp(since.getTime()));
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PurchaseOrder order = new PurchaseOrder(
                            rs.getInt("supplier_id"),
                            rs.getInt("inventory_id"),
                            rs.getInt("quantity"),
                            rs.getDouble("price"),
                            rs.getTimestamp("order_date"),
                            rs.getString("status"));
                    order.setId(rs.getInt("id"));
                    list.add(order);
                }
            }
        }
        return list;
    }

    /**
     * 查询指定时间之后的销售订单
     */
    public static List<SalesOrder> getSalesOrdersAfter(Date since) throws SQLException {
        List<SalesOrder> list = new ArrayList<>();
        String sql = "SELECT * FROM sales_orders WHERE order_date >= ? ORDER BY order_date DESC";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setTimestamp(1, new Timestamp(since.getTime()));
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    SalesOrder order = new SalesOrder(
                            rs.getInt("customer_id"),
                            rs.getInt("inventory_id"),
                            rs.getInt("quantity"),
                            rs.getDouble("price"),
                            rs.getTimestamp("order_date"),
                            rs.getString("status"));
                    order.setId(rs.getInt("id"));
                    list.add(order);
                }
            }
        }
        return list;
    }

    private static Timestamp toTimestamp(Date date) {
        return new Timestamp(date == null ? System.currentTimeMillis() : date.getTime());
    }
}
