package com.example.eggapp.service;

/**
 * 库存不足异常：销售订单数量超过当前库存时抛出。
 */
public class InsufficientStockException extends OrderProcessingException {

    private static final long serialVersionUID = 1L;

    public InsufficientStockException(String message) {
        super(message);
    }
}
