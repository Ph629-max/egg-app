package com.example.eggapp.service;

import com.example.eggapp.model.Notification;

/**
 * 通知服务接口：负责把系统事件（采购完成、发货等）通知给相关方。
 */
public interface NotificationService {

    /**
     * 发送一条通知。
     *
     * @param notification 通知内容
     */
    void sendNotification(Notification notification);
}
