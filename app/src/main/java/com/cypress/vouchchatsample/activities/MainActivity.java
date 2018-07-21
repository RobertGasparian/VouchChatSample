package com.cypress.vouchchatsample.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cypress.vouchchatsample.R;
import com.cypress.vouchchatsample.models.Channel;
import com.cypress.vouchchatsample.models.ChatMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.stfalcon.chatkit.utils.DateFormatter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    private final String TAG = this.getClass().getSimpleName();


    @BindView(R.id.action_bar)
    Toolbar toolbar;
    @BindView(R.id.message_list)
    MessagesList messagesList;
    @BindView(R.id.message_input)
    MessageInput input;

    private DatabaseReference channelReference;
    private DatabaseReference messagesReference;

    private MessagesListAdapter<ChatMessage> adapter;

    private List<ChatMessage> messages = new ArrayList<>();
    private String userId = "";
    private String displayName = "";
    private Channel mainChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        initFirebase();
        initAdapter(userId, messages);
        initInputEvent();
        initInputAttachment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sign_out:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addChangeListeners() {
        addChannelListener();
    }

    private void addChannelListener() {
        channelReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Channel channel = dataSnapshot.getValue(Channel.class);
                if (channel == null || channel.getMessages().isEmpty()) {
                    channelReference.setValue(mainChannel);
                    Log.d(TAG, "onDataChange: initial value");
                } else {
                    Log.d(TAG, "onDataChange: \nChannelName: " + channel.getChannelName() + "\nChannelId: " + channel.getChannelId());
                }
                addMessagesListener();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, R.string.message_update_error_toast, Toast.LENGTH_LONG).show();
                Log.d(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });
    }

    private void addMessagesListener() {
        messagesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot messageSnapShot :
                        dataSnapshot.getChildren()) {
                    messages.add(messageSnapShot.getValue(ChatMessage.class));
                }
                if (messages.isEmpty()) {
                    Log.d(TAG, "onDataChange: there are no messages");
                }
                addMessagesEventListener();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "error loading messages");
            }
        });
    }

    private void addMessagesEventListener() {
        messagesReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ChatMessage message = dataSnapshot.getValue(ChatMessage.class);
                if (message != null) {
                    messages.add(message);
                    adapter.addToStart(message, true);
                } else {
                    Log.d(TAG, "onChildAdded: message is null");
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //TODO: handle message edit
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                //TODO: handle message remove
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: message change error: " + databaseError.getMessage());
            }
        });
    }

    private void initAdapter(String senderId, List<ChatMessage> messages) {
        adapter = new MessagesListAdapter<>(senderId, (imageView, url) -> Glide.with(MainActivity.this).load(url).into(imageView));
        adapter.addToEnd(messages, false);
        adapter.setDateHeadersFormatter(date -> {
            if (DateFormatter.isToday(date)) {
                return DateFormatter.format(date, DateFormatter.Template.TIME);
            } else if (DateFormatter.isYesterday(date)) {
                return getString(R.string.date_header_yesterday);
            } else {
                return DateFormatter.format(date, DateFormatter.Template.STRING_DAY_MONTH_YEAR);
            }
        });
        messagesList.setAdapter(adapter);
    }

    private void initFirebase() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        userId = currentUser.getUid();
        displayName = currentUser.getDisplayName();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        mainChannel = new Channel("main_channel_id", "Main Channel");
        channelReference = database.child("channels").child(mainChannel.getChannelId());
        messagesReference = channelReference.child("messages");
        addChangeListeners();
    }

    private void initInputEvent() {
        input.setInputListener(input -> {
            ChatMessage message = new ChatMessage(input.toString(), displayName, userId);
            messagesReference.push().setValue(message)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "onSuccess: ");
                    })
                    .addOnFailureListener(e -> {
                        Log.d(TAG, "initInputEvent: " + e.getMessage());
                    });
            return true;
        });
    }

    private void initInputAttachment() {
        input.setAttachmentsListener(() -> Toast.makeText(MainActivity.this, R.string.coming_soon, Toast.LENGTH_LONG).show());
        //TODO: implement file(image) attachment
    }
}
