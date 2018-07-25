package me.vouch4.app.models;

import com.google.firebase.database.IgnoreExtraProperties;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class Sender implements IUser {

    private String name;
    private String id;
    private List<String> channels;    //channel ids

    public Sender() {
        channels = new ArrayList<>();
    }

    public Sender(String id, String name) {
        this.name = name;
        this.id = id;
        channels = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    @Override
    public String getAvatar() {
        return null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getChannels() {
        return channels;
    }

    public void setChannels(List<String> channels) {
        this.channels = channels;
    }

    public void addChannel(String channelId) {
        channels.add(channelId);
    }
}
