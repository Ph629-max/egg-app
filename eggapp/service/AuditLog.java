package com.example.eggapp.service;

import com.example.eggapp.utils.LogUtil;

import java.util.Date;

/**
 * 轻量审计日志：记录订单、库存、分拣规则等关键业务事件。
 */
public final class AuditLog {

    public enum EventType {
        PURCHASE, SALES, STOCK_ALERT, RULE_CHANGE
    }

    private AuditLog() {
    }

    public static void log(EventType type, String message) {
        LogUtil.info(String.format("[AUDIT][%s][%s] %s", new Date(), type, message));
    }
}
