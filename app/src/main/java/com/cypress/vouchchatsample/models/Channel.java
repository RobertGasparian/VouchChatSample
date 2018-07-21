package com.cypress.vouchchatsample.models;

import java.util.HashMap;

public class Channel {

    private String channelId;
    private String channelName;
    private HashMap<String, ChatMessage> messages;

    public Channel() {
        messages = new HashMap<>();
    }

    public Channel(String channelId, String channelName) {
        this.channelId = channelId;
        this.channelName = channelName;
        messages = new HashMap<>();
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public HashMap<String, ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(HashMap<String, ChatMessage> messages) {
        this.messages = messages;
    }
}
