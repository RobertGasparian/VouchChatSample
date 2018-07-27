package me.vouch4.app.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.MessageContentType;

import java.util.Date;

@IgnoreExtraProperties
public class ChatMessage implements IMessage, MessageContentType.Image {

    @Exclude
    private String messageId;
    @Exclude
    private boolean isLoading;
    private String text;
    private String senderId;
    private String senderName;
    private String photoURL;
    private long date;

    public ChatMessage() {
    }

    public ChatMessage(String text, String senderName, String senderId) {
        this.text = text;
        this.senderName = senderName;
        this.senderId = senderId;
        date = new Date().getTime();
    }

    @Exclude
    @Override
    public String getId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getText() {
        return text;
    }

    @Exclude
    @Override
    public Sender getUser() {
        return new Sender(senderId, senderName);
    }

    @Exclude
    @Override
    public Date getCreatedAt() {
        return new Date(date);
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoUrl) {
        this.photoURL = photoUrl;
    }

    @Exclude
    @Override
    public String getImageUrl() {
        return photoURL;
    }

    @Exclude
    public void setLoading(boolean isLoading){
        this.isLoading = isLoading;
    }

    @Exclude
    public boolean isLoading(){
        return isLoading;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
