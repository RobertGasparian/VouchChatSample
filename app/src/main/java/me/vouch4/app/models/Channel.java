package me.vouch4.app.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;

@IgnoreExtraProperties
public class Channel {

    @Exclude
    private String channelId;
    @Exclude
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

    @Exclude
    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    @Exclude
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
