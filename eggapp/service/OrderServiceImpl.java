package com.example.eggapp.service;

import com.example.eggapp.dao.DatabaseHelper;
import com.example.eggapp.model.*;
import java.sql.SQLException;
import java.util.Date;

public class OrderServiceImpl extends OrderService {
    private final InventoryManager inventoryManager;
    private final NotificationService notificationService;

    public OrderServiceImpl(InventoryManager inventoryManager,
                            NotificationService notificationService) {
        this.inventoryManager = inventoryManager;
        this.notificationService = notificationService;
    }

    @Override
    public void processPurchaseOrder(PurchaseOrder order) throws OrderProcessingException {
        try {
            // 1. 保存订单
            DatabaseHelper.addPurchaseOrder(order);

            // 2. 更新库存
            InventoryItem item = DatabaseHelper.getInventoryItem(order.getInventoryId());
            item.updateQuantity(order.getQuantity());
            DatabaseHelper.updateInventory(item);

            // 3. 记录审计日志
            AuditLog.log(AuditLog.EventType.PURCHASE,
                    String.format("采购订单%d: +%d件", order.getId(), order.getQuantity()));

            // 4. 通知相关方
            notificationService.sendNotification(
                    new Notification(
                            "采购完成通知",
                            String.format("已成功采购%d件%s",
                                    order.getQuantity(), item.getName()),
                            NotificationType.EMAIL)
            );

        } catch (SQLException | IllegalArgumentException e) {
            throw new OrderProcessingException("采购订单处理失败", e);
        }
    }

    @Override
    public void processSalesOrder(SalesOrder order) throws OrderProcessingException {
        try {
            // 验证库存充足
            InventoryItem item = DatabaseHelper.getInventoryItem(order.getInventoryId());
            if (item.getQuantity() < order.getQuantity()) {
                throw new InsufficientStockException("库存不足");
            }

            // 保存订单
            DatabaseHelper.addSalesOrder(order);

            // 更新库存
            item.updateQuantity(-order.getQuantity());
            DatabaseHelper.updateInventory(item);

            // 触发分拣流程
            inventoryManager.processInventory(Collections.singletonList(item));

            // 发送发货通知
            notificationService.sendNotification(
                    new Notification(
                            "订单发货通知",
                            String.format("订单%d已发货，预计3日内送达", order.getId()),
                            NotificationType.SMS)
            );

        } catch (SQLException | IllegalArgumentException e) {
            throw new OrderProcessingException("销售订单处理失败", e);
        }
    }

    @Override
    public List<PurchaseOrder> getRecentPurchases(int days) throws SQLException {
        return DatabaseHelper.getPurchaseOrdersAfter(new Date(
                System.currentTimeMillis() - (long) days * 24 * 60 * 60 * 1000
        ));
    }

    @Override
    public List<SalesOrder> getRecentSales(int days) throws SQLException {
        return DatabaseHelper.getSalesOrdersAfter(new Date(
                System.currentTimeMillis() - (long) days * 24 * 60 * 60 * 1000
        ));
    }
}
