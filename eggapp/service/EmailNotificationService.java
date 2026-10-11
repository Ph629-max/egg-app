package com.example.eggapp.service;

import com.example.eggapp.model.Notification;
import com.example.eggapp.model.NotificationType;
import com.example.eggapp.utils.LogUtil;

/**
 * 通知服务的默认实现。
 * 当前以日志/控制台输出模拟发送；接入生产环境时替换为 JavaMail 或短信网关即可。
 */
public class EmailNotificationService implements NotificationService {

    @Override
    public void sendNotification(Notification notification) {
        if (notification == null) {
            return;
        }
        String text = String.format("[%s] %s - %s",
                notification.getType(), notification.getTitle(), notification.getContent());
        LogUtil.info(text);

        if (notification.getType() == NotificationType.EMAIL) {
            // TODO: 接入 JavaMail 发送邮件
        } else if (notification.getType() == NotificationType.SMS) {
            // TODO: 接入短信网关
        }
        System.out.println("[通知] " + text);
    }
}
