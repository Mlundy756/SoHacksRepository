package com.workshop.myapplication.Model;

/**
 * Created by l730832 on 8/4/2017.
 */

public class MessageList {

    public Message[] messages;

    public Message[] getMessages() {
        return messages;
    }

    public void setMessages(Message[] messages) {
        this.messages = messages;
    }

    public static class Message {
        public String Mid;
        public String createdAt;
        public String text;

        public String getMid() {
            return Mid;
        }

        public void setMid(String mid) {
            Mid = mid;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
