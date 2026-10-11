package com.example.eggapp.service;

/**
 * 订单处理失败时抛出的受检异常。
 */
public class OrderProcessingException extends Exception {

    private static final long serialVersionUID = 1L;

    public OrderProcessingException(String message) {
        super(message);
    }

    public OrderProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
