package heycompany.heychat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.bhargavms.dotloader.DotLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.skyfishjy.library.RippleBackground;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.annotations.NonNull;

public class ChatActivity extends AppCompatActivity {

    private String mChatUser;
    private android.support.v7.widget.Toolbar mChatToolbar;

    private DatabaseReference mRootRef;

    private TextView mTitleView;
    private TextView mLastSeenView;
    private CircleImageView mProfileImage;
    private String mCurrentUserID;

    private ImageButton mChatAddBtn;
    private ImageButton mChatSendBtn;
    private EditText mChatMessageView;

    private RecyclerView mMessagesList;

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private MessageAdapter mAdapter;

    //private static final int GALLERY_PICK = 1;

    //Storage
    private StorageReference mImageStorage;
    private StorageReference mAudioStorage;
    private StorageReference mVideoStorage;

    //Online Status
    private DatabaseReference mUserRef;
    private FirebaseAuth mAuth;

    //Dot
    private DotLoader dotloader;

    //VoiceMessage
    private Button mRecordBtn;
    private MediaRecorder mRecorder;
    private String mFileName = null;
    private static final String LOG_TAG = "Record_log";

    private static final int SELECT_PICTURE = 1;
    private static final int SELECT_VIDEO = 2;

    //Video
    private Button Video_btn;

    //Permission
    int REQ_CODE_RECORD_AUDIO = 45;
    int REQ_CODE_WRITE_STORAGE = 44;
    int REQ_CODE_READ_STORAGE = 43;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Leiste unsichtbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        setContentView(R.layout.activity_chat);


        //Add Toolbar on Top
        mChatToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mChatToolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        mRootRef = FirebaseDatabase.getInstance().getReference();

        mChatUser = getIntent().getStringExtra("user_id");
        String userName = getIntent().getStringExtra("user_name");

        //OnlineStatus
        mAuth = FirebaseAuth.getInstance();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        mCurrentUserID = mAuth.getCurrentUser().getUid();

        getSupportActionBar().setTitle(null);

        //Inflater for Chats
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.chat_custom_bar, null);

        actionBar.setCustomView(action_bar_view);

        //Dot
        dotloader = (DotLoader) findViewById(R.id.dot_loader);

        //Custom Action
        mTitleView = (TextView) findViewById(R.id.custom_bar_title);
        mLastSeenView = (TextView) findViewById(R.id.custom_bar_seen);
        mProfileImage = (CircleImageView) findViewById(R.id.custom_bar_image);

        mChatAddBtn = (ImageButton) findViewById(R.id.chat_add_btn);
        mChatSendBtn = (ImageButton) findViewById(R.id.chat_send_btn);
        mChatMessageView = (EditText) findViewById(R.id.chat_message_view);

        mAdapter = new MessageAdapter(messagesList);

        mMessagesList = (RecyclerView) findViewById(R.id.messages_list);
        mLinearLayout = new LinearLayoutManager(this);

        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(mLinearLayout);

        mMessagesList.setAdapter(mAdapter);

        mLinearLayout.setStackFromEnd(true);
        //mMessagesList.scrollToPosition(messagesList.size() - 1);

        //Image
        mImageStorage = FirebaseStorage.getInstance().getReference();

        mRootRef.child("Chat").child(mCurrentUserID).child(mChatUser).child("seen").setValue(true);

        loadMessages();

        mTitleView.setText(userName);

        //Chatwatcher
        mChatMessageView.addTextChangedListener(writeTextWatcher);

        //recordbtn
        mRecordBtn = (Button) findViewById(R.id.mRecordBtn);

        //Audio
        mAudioStorage = FirebaseStorage.getInstance().getReference();

        //Video
        Video_btn = (Button) findViewById(R.id.chat_video);


        //Add Listener, wenn sich in User-Branch etwas ändert dann werden Daten in der App geupdated
        mRootRef.child("Users").child(mChatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String online = dataSnapshot.child("online").getValue().toString();
                String image = dataSnapshot.child("thumb_image").getValue().toString();

                if (online.equals("true")) {
                    mLastSeenView.setText("Online");
                } else {

                    GetTimeAgo getTimeAgo = new GetTimeAgo();

                    long lastTime = Long.parseLong(online);

                    String lastSeenTime = getTimeAgo.getTimeAgo(lastTime, getApplicationContext());

                    mLastSeenView.setText(lastSeenTime);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Add Listener, wenn sich in Chat-Branch etwas ändert dann werden Daten in der App geupdated
        mRootRef.child("Chat").child(mCurrentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.hasChild(mChatUser)) {

                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen", false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chat/" + mCurrentUserID + "/" + mChatUser, chatAddMap);
                    chatUserMap.put("Chat/" + mChatUser + "/" + mCurrentUserID, chatAddMap);

                    mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if (databaseError != null) {

                                Log.d("CHAT_LOG", databaseError.getMessage().toString());

                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Onclick für den Sendenachricht Button
        mChatSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendMessage();
            }
        });

        //Onclick für Images verschicken, Öffnet Gallery
        mChatAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"), SELECT_PICTURE);
            }
        });

        //Onclick für Videos verschicken, öffnet Gallery
        Video_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent();
                galleryIntent.setType("video/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"), SELECT_VIDEO);
            }
        });

        //VoiceMessage

        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/recorded_audio.3gp";

        final RippleBackground rippleBackground = (RippleBackground) findViewById(R.id.content);

        //Erst Abfrage nach Permissions, dann bei Ontouch Mikrofon aufnehmen und Rippleeffekt starten
        mRecordBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (ActivityCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            if (event.getAction() == MotionEvent.ACTION_DOWN) {

                                startRecording();
                                rippleBackground.startRippleAnimation();

                            } else if (event.getAction() == MotionEvent.ACTION_UP) {

                                stopRecording();
                                uploadAudio();
                                rippleBackground.stopRippleAnimation();

                            }
                            return false;
                        } else {
                            ActivityCompat.requestPermissions(ChatActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_CODE_WRITE_STORAGE);
                        }
                    } else {
                        ActivityCompat.requestPermissions(ChatActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQ_CODE_READ_STORAGE);
                    }
                } else {
                    ActivityCompat.requestPermissions(ChatActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, REQ_CODE_RECORD_AUDIO);
                }
                return false;
            }
        });

    }

    //Wenn Bild ausgewählt, Speichern der Nachricht in der Datenbank mit Image Uri, bildtyp, time, von wem und speichern in einer Hashmap
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();

            final String current_user_ref = "messages/" + mCurrentUserID + "/" + mChatUser;
            final String chat_user_ref = "messages/" + mChatUser + "/" + mCurrentUserID;

            DatabaseReference user_message_push = mRootRef.child("messages")
                    .child(mCurrentUserID).child(mChatUser).push();

            final String push_id = user_message_push.getKey();


            StorageReference filepath = mImageStorage.child("message_images").child(push_id + ".jpg");

            filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@android.support.annotation.NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {

                        String download_url = task.getResult().getDownloadUrl().toString();

                        Map messageMap = new HashMap();
                        messageMap.put("message", download_url);
                        messageMap.put("seen", false);
                        messageMap.put("type", "image");
                        messageMap.put("time", ServerValue.TIMESTAMP);
                        messageMap.put("from", mCurrentUserID);

                        Map messageUserMap = new HashMap();
                        messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                        messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

                        mChatMessageView.setText("");

                        mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                if (databaseError != null) {

                                    Log.d("CHAT_LOG", databaseError.getMessage().toString());

                                }

                            }
                        });

                    }
                }
            });

        }
        if (requestCode == SELECT_VIDEO && resultCode == RESULT_OK) {

            Uri videoUri = data.getData();

            final String current_user_ref = "messages/" + mCurrentUserID + "/" + mChatUser;
            final String chat_user_ref = "messages/" + mChatUser + "/" + mCurrentUserID;

            DatabaseReference user_message_push = mRootRef.child("messages")
                    .child(mCurrentUserID).child(mChatUser).push();

            final String push_id = user_message_push.getKey();


            StorageReference filepath = mImageStorage.child("video_message").child(push_id + ".3gp");

            filepath.putFile(videoUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@android.support.annotation.NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {

                        String download_url = task.getResult().getDownloadUrl().toString();

                        Map messageMap = new HashMap();
                        messageMap.put("message", download_url);
                        messageMap.put("seen", false);
                        messageMap.put("type", "video");
                        messageMap.put("time", ServerValue.TIMESTAMP);
                        messageMap.put("from", mCurrentUserID);

                        Map messageUserMap = new HashMap();
                        messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                        messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

                        mChatMessageView.setText("");

                        mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                if (databaseError != null) {

                                    Log.d("CHAT_LOG", databaseError.getMessage().toString());

                                }

                            }
                        });

                    }
                }
            });

        }

    }

    private void loadMessages() {

        mRootRef.child("messages").child(mCurrentUserID).child(mChatUser).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Messages message = dataSnapshot.getValue(Messages.class);

                messagesList.add(message);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage() {
        String message = mChatMessageView.getText().toString();
        if (!TextUtils.isEmpty(message)) {

            String current_user_ref = "messages/" + mCurrentUserID + "/" + mChatUser;
            String chat_user_ref = "messages/" + mChatUser + "/" + mCurrentUserID;

            DatabaseReference user_message_push = mRootRef.child("messages")
                    .child(mCurrentUserID).child(mChatUser).push();
            String push_id = user_message_push.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("send", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", mCurrentUserID);

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

            //Clear Nachricht
            mChatMessageView.getText().clear();

            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if (databaseError != null) {

                        Log.d("CHAT_LOG", databaseError.getMessage().toString());

                    }
                }
            });
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            mUserRef.child("online").setValue("true");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            mUserRef.child("online").setValue("false");
        }
    }


    private TextWatcher writeTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String nachricht = mChatMessageView.getText().toString().trim();
            if (!nachricht.isEmpty()) {
                dotloader.setVisibility(View.VISIBLE);
            } else {
                dotloader.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    private void uploadAudio() {

        DatabaseReference user_message_push = mRootRef.child("messages")
                .child(mCurrentUserID).child(mChatUser).push();
        final String current_user_ref = "messages/" + mCurrentUserID + "/" + mChatUser;
        final String chat_user_ref = "messages/" + mChatUser + "/" + mCurrentUserID;

        final String push_id = user_message_push.getKey();

        StorageReference filepath = mAudioStorage.child("voice_message").child(push_id + ".3gp");

        Uri uri = Uri.fromFile(new File(mFileName));


        filepath.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@android.support.annotation.NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {

                    String download_url = task.getResult().getDownloadUrl().toString();

                    Map messageMap = new HashMap();
                    messageMap.put("message", download_url);
                    messageMap.put("seen", false);
                    messageMap.put("type", "voice");
                    messageMap.put("time", ServerValue.TIMESTAMP);
                    messageMap.put("from", mCurrentUserID);

                    Map messageUserMap = new HashMap();
                    messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                    messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

                    mChatMessageView.setText("");

                    mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if (databaseError != null) {

                                Log.d("CHAT_LOG", databaseError.getMessage().toString());

                            }

                        }
                    });

                }
            }
        });
    }
}