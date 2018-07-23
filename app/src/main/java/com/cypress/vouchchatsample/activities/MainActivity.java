package com.cypress.vouchchatsample.activities;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cypress.vouchchatsample.R;
import com.cypress.vouchchatsample.models.Channel;
import com.cypress.vouchchatsample.models.ChatMessage;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.stfalcon.chatkit.utils.DateFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    private final int CAMERA_REQCODE = 54;
    private final int CAMERA_PERMISSION_REQCODE = 55;
    private final int GALLERY_REQCODE = 45;
    private final int GALLERY_PERMISSION_REQCODE = 44;

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

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case CAMERA_REQCODE:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    uploadImage(selectedImage);
                }

                break;
            case GALLERY_REQCODE:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    uploadImage(selectedImage);
                }
                break;
        }
    }

    private void uploadImage(Uri uri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        ContentResolver cR = MainActivity.this.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String fileExtension = mime.getExtensionFromMimeType(cR.getType(uri));
        String path = "channels/" + mainChannel.getChannelId() + "/" + UUID.randomUUID() + fileExtension;
        StorageReference currentChanelRef = storage.getReference(path);
        UploadTask task = currentChanelRef.putFile(uri);
        Task<Uri> urlTask = task.continueWithTask(taskInProgress -> {
            if (!taskInProgress.isSuccessful()) {
                throw taskInProgress.getException();
            }

            return currentChanelRef.getDownloadUrl();
        }).addOnCompleteListener(MainActivity.this, taskCompleted -> {
            if (taskCompleted.isSuccessful()) {
                Uri url = taskCompleted.getResult();
                ChatMessage imageMessage = new ChatMessage(null, displayName, userId);
                imageMessage.setPhotoUrl(url.toString());
                messagesReference.push().setValue(imageMessage)
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "onSuccess: ");
                        })
                        .addOnFailureListener(e -> {
                            Log.d(TAG, "initInputEvent: " + e.getMessage());
                        });
            } else {
                //TODO: handle failure
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
        input.setAttachmentsListener(() -> {
            DialogInterface.OnClickListener cameraListener = (dialog, which) -> {
                requestCamera();
                dialog.cancel();
            };
            DialogInterface.OnClickListener galleryListener = (dialog, which) -> {
                requestGallery();
                dialog.cancel();
            };

            showPickerDialog(cameraListener, galleryListener);
        });
    }

    private void requestCamera() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQCODE);
        } else {
            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePicture, CAMERA_REQCODE);
        }
    }

    private void requestGallery() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    GALLERY_PERMISSION_REQCODE);
        } else {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, GALLERY_REQCODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_REQCODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, CAMERA_REQCODE);
                } else {
                    Toast.makeText(MainActivity.this, R.string.permission_denied_toast, Toast.LENGTH_LONG).show();
                }
                return;
            }
            case GALLERY_PERMISSION_REQCODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, GALLERY_REQCODE);
                } else {
                    Toast.makeText(MainActivity.this, R.string.permission_denied_toast, Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }


}
