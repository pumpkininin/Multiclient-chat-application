package server;

import java.io.Serializable;
import java.util.List;

public class Message implements Serializable {
    private String sender;
    private String receiver;
    private String content;
    private Type type;
    private List<String> activeUsers;

    public Message() {
    }

    public Message(String sender, String receiver, String content, List<String> activeUsers) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.activeUsers = activeUsers;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(List<String> activeUsers) {
        this.activeUsers = activeUsers;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
    public enum Type{
        LOGIN, MSG, LOGOUT
    }
}