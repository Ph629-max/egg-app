package com.example.eggapp.model;

import java.util.Date;

/**
 * 通知消息实体：由通知服务发送给相关方。
 */
public class Notification {
    private String title;
    private String content;
    private NotificationType type;
    private Date createdAt = new Date();

    public Notification() {
    }

    public Notification(String title, String content, NotificationType type) {
        this.title = title;
        this.content = content;
        this.type = type;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return String.format("[%s] %s: %s", type, title, content);
    }
}
