package heycompany.heychat;

import android.app.DownloadManager;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class GroupMessageAdapter extends RecyclerView.Adapter{

    private FirebaseAuth mAuth;
    private List<Messages> mMessageList;
    private Messages c;
    public TextView messageText;

    private String textMessage_SEND = "text";

    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_TEXTMESSAGE = 1;
    private static final int VIEW_TYPE_IMAGEMESSAGE = 2;
    private static final int VIEW_TYPE_VOICEMESSAGE = 3;
    private static final int VIEW_TYPE_VIDEOMESSAGE = 4;

    private static final int VIEW_TYPE_TEXTMESSAGESEND = 5;
    private static final int VIEW_TYPE_VOICEMESSAGESEND = 6;
    private static final int VIEW_TYPE_VIDEOMESSAGESEND = 7;


    public GroupMessageAdapter(List<Messages> mMessageList) {

        this.mMessageList = mMessageList;

    }

    @Override
    public int getItemCount() {

        return mMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Messages message = (Messages) mMessageList.get(position);

        mAuth = FirebaseAuth.getInstance();
        String current_user_id = mAuth.getCurrentUser().getUid();
        c = mMessageList.get(position);
        String from_user = c.getFrom();

        if (message.getType().equals("text") && !(from_user.equals(current_user_id))) {
            return VIEW_TYPE_TEXTMESSAGE;
        } else if(message.getType().equals("text") && from_user.equals(current_user_id)){
            return VIEW_TYPE_TEXTMESSAGESEND;
        } else if (message.getType().equals("voice") && !(from_user.equals(current_user_id))) {
            return VIEW_TYPE_VOICEMESSAGE;
        } else if (message.getType().equals("voice") && from_user.equals(current_user_id)) {
            return VIEW_TYPE_VOICEMESSAGESEND;
        } else if (message.getType().equals("image")) {
            return VIEW_TYPE_IMAGEMESSAGE;
        } else if(message.getType().equals("video") && !(from_user.equals(current_user_id))) {
            return VIEW_TYPE_VIDEOMESSAGE;
        } else if(message.getType().equals("video") && from_user.equals(current_user_id)) {
            return VIEW_TYPE_VIDEOMESSAGESEND;
        } else{
            return VIEW_TYPE_EMPTY;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        String VIEW_TYPE = String.valueOf(viewType);

        if(viewType == VIEW_TYPE_TEXTMESSAGE){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout_group, parent, false);
            return new TextViewholder(v);
        }
        else if(viewType == VIEW_TYPE_IMAGEMESSAGE){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_single_layout, parent, false);
            return new ImageViewholder(v);
        }
        else if(viewType == VIEW_TYPE_VOICEMESSAGE){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.voice_single_layout_group, parent, false);
            return new VoiceViewholder(v);
        }
        else if(viewType == VIEW_TYPE_VIDEOMESSAGE){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_single_layout, parent, false);
            return new VideoViewholder(v);
        }

        else if(viewType == VIEW_TYPE_TEXTMESSAGESEND){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout_sent, parent, false);
            return new TextViewholdersend(v);
        }
        else if(viewType == VIEW_TYPE_VOICEMESSAGESEND){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.voice_single_layout_sent, parent, false);
            return new VoiceViewholdersend(v);
        }
        else if(viewType == VIEW_TYPE_VIDEOMESSAGESEND){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_single_layout_sent, parent, false);
            return new VideoViewholdersend(v);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        mAuth = FirebaseAuth.getInstance();
        String current_user_id = mAuth.getCurrentUser().getUid();
        c = mMessageList.get(i);
        String from_user = c.getFrom();
        String message_type = c.getType();


        if(message_type.equals("text") && !(from_user.equals(current_user_id))){

            ((TextViewholder) viewHolder).bindText(c);
        }
        else if(message_type.equals("text")&& from_user.equals(current_user_id)){

            ((TextViewholdersend) viewHolder).bindText(c);
        }

        else if (message_type.equals("voice") && !(from_user.equals(current_user_id))){

            ((VoiceViewholder) viewHolder).bindVoice(c);
        }
        else if(message_type.equals("voice") && from_user.equals(current_user_id)){

            ((VoiceViewholdersend) viewHolder).bindVoice(c);
        }
        else if( message_type.equals("image")) {

            ((ImageViewholder) viewHolder).bindImage(c);
        }
        else if( message_type.equals("video") && !(from_user.equals(current_user_id))) {

            ((VideoViewholder) viewHolder).bindVideo(c);
        }
        else if( message_type.equals("video") && from_user.equals(current_user_id)) {

            ((VideoViewholdersend) viewHolder).bindVideo(c);
        }

    }

    private void play_sound(View v, String url){
        final MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try{
                mediaPlayer.setDataSource(url);
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                            mediaPlayer.start();
                    }
                });
                mediaPlayer.prepare();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void play_video(View v, String url){

        VideoView videoView = (VideoView)v.findViewById(R.id.videoView);
        //MediaController mediaController= new MediaController(this);
        //mediaController.setAnchorView(videoView);
        //Uri uri=Uri.parse("rtsp://r2---sn-5hnekn7s.googlevideo.com/Cj0LENy73wIaNAlkloBQ6zhM9BMYDSANFC3hp_1bMOCoAUIASARg9v247KDv6eFZigELYUUtT2dxaG5EMnMM/DF798CD202779002371993871819FE595DDC140B.87F309B2D9F004DCF34561DB660C7FDD6C5934BF/yt6/1/video.3gp");
        Uri uri=Uri.parse(url);
        //videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.setZOrderOnTop(false);

        videoView.start();

    }

    private void stop_video(View v, String url){

        VideoView videoView = (VideoView)v.findViewById(R.id.videoView);

        videoView.stopPlayback();
    }

    private void download_video (final View v,final String url){

        /*FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference island = storage.getReferenceFromUrl(url);

        File rootPath = new File(Environment.getExternalStorageDirectory(), "HeyChat");
        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }

        final File localFile = new File(rootPath,"video.mp4");
        Log.d("toast", "downloaded" + island);
        island.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.d("toast", "downloaded");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("toast", "failed");
            }
        });*/


        StorageReference  video = FirebaseStorage.getInstance().getReference();
        StorageReference videoref = FirebaseStorage.getInstance().getReferenceFromUrl(url);
        //StorageReference videoref = video.child("video_message").child(url);
        try {
            File file = File.createTempFile("videos", "mp4");

            videoref.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                downloadfile(v.getContext(),"Mobile", "mp4", DIRECTORY_DOWNLOADS, url);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

        }catch (IOException e){

        }
      /*  videoref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url = uri.toString();
                //downloadfile(MessageAdapter.this , "Mobile", ".mp4", DIRECTORY_DOWNLOADS,url);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });*/

    }

    private void downloadfile(Context context, String fileName, String fileExtension, String destinationDirectory, String uri){

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        Uri url = Uri.parse(uri);
        DownloadManager.Request request = new DownloadManager.Request(url);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context,destinationDirectory, fileName + fileExtension);

        downloadManager.enqueue(request);
    }

    private class TextViewholder extends  RecyclerView.ViewHolder{
        public TextView messageText;
        public TextView messageTime;
        public TextView messageName;
        private DatabaseReference mUserRef;

        public TextViewholder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.message_text_layout);
            messageTime = (TextView) itemView.findViewById(R.id.message_time);
            messageName = (TextView) itemView.findViewById(R.id.message_name);
        }
        void bindText(final Messages c){

            //Bind Date
            Long timestamp = c.getTime();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(timestamp);

            android.text.format.DateFormat df = new android.text.format.DateFormat();
            messageTime.setText(df.format("kk:mm", cal).toString());

            //Bind Text
            messageText.setText(c.getMessage());

            //Bind Name
            final String userid = c.getFrom();
            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userid).child("name");

            mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final String username = dataSnapshot.getValue().toString();

                    messageName.setText(username);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    private class VideoViewholder extends RecyclerView.ViewHolder{
        VideoView messageVideo;
        public TextView messageTime;


        public VideoViewholder(View itemView) {
            super(itemView);

            messageVideo = (VideoView) itemView.findViewById(R.id.videoView);
            messageTime = (TextView) itemView.findViewById(R.id.message_time);
        }
        void bindVideo(final Messages c){

            Long timestamp = c.getTime();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(timestamp);

            android.text.format.DateFormat df = new android.text.format.DateFormat();
            messageTime.setText(df.format("kk:mm", cal).toString());

            messageVideo.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    //download_video(v, c.getMessage());
                    play_video(v, c.getMessage());
                    return false;
                }
            });
        }

    }

    private class ImageViewholder extends RecyclerView.ViewHolder{
        public ImageView messageImage;
        public TextView messageTime;

        public ImageViewholder(View itemView) {
            super(itemView);

            messageImage = (ImageView) itemView.findViewById(R.id.message_image_layout);
            messageTime = (TextView) itemView.findViewById(R.id.message_time);
        }
        void bindImage(final Messages c){

            Long timestamp = c.getTime();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(timestamp);

            android.text.format.DateFormat df = new android.text.format.DateFormat();
            messageTime.setText(df.format("kk:mm", cal).toString());

            Picasso.get().load(c.getMessage()).placeholder(R.drawable.placeholder).into(messageImage);
        }
    }

    private class VoiceViewholder extends RecyclerView.ViewHolder{
        public Button messageVoice;
        public TextView messageTime;
        public TextView messageName;
        private DatabaseReference mUserRef;

        public VoiceViewholder(View itemView) {
            super(itemView);

            messageVoice = (Button) itemView.findViewById(R.id.message_voice_layout);
            messageTime = (TextView) itemView.findViewById(R.id.message_time);
            messageName = (TextView) itemView.findViewById(R.id.message_name);
        }

            void bindVoice(final Messages c){

                //Bind Name
                final String userid = c.getFrom();
                mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userid).child("name");

                mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String username = dataSnapshot.getValue().toString();

                        messageName.setText(username);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                Long timestamp = c.getTime();
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(timestamp);

                android.text.format.DateFormat df = new android.text.format.DateFormat();
                messageTime.setText(df.format("kk:mm", cal).toString());

            messageVoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    play_sound(v, c.getMessage());
                }
            });
        }
    }

    private class TextViewholdersend extends RecyclerView.ViewHolder{
        public TextView messageText;
        public TextView messageTime;
        public TextView messageName;

        public TextViewholdersend (View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.message_text_layout);
            messageTime = (TextView) itemView.findViewById(R.id.message_time);
            messageName = (TextView) itemView.findViewById(R.id.message_name);
        }
        void bindText(final Messages c){

            //Bind Message
            messageText.setText(c.getMessage());

            //Bind Date
            Long timestamp = c.getTime();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(timestamp);

            android.text.format.DateFormat df = new android.text.format.DateFormat();
            messageTime.setText(df.format("kk:mm", cal).toString());
        }
    }
    private class VoiceViewholdersend extends RecyclerView.ViewHolder{
        public Button messageVoice;
        int x = 0;
        public TextView messageTime;

        public VoiceViewholdersend(View itemView) {
            super(itemView);

            messageVoice = (Button) itemView.findViewById(R.id.message_voice_layout);
            messageTime = (TextView) itemView.findViewById(R.id.message_time);
        }

        void bindVoice(final Messages c){

            Long timestamp = c.getTime();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(timestamp);

            android.text.format.DateFormat df = new android.text.format.DateFormat();
            messageTime.setText(df.format("kk:mm", cal).toString());

                messageVoice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(x == 0) {
                            Log.d("voice", "voice" + x);
                            play_sound(v, c.getMessage());
                            messageVoice.setBackgroundResource(R.drawable.pause);
                            x = 1;
                        }else if(x == 1){
                            messageVoice.setBackgroundResource(R.drawable.playbutton);
                            x = 0;
                        }
                    }
                });

        }
    }

    private class VideoViewholdersend extends RecyclerView.ViewHolder{
        VideoView messageVideo;
        public TextView messageTime;

        public VideoViewholdersend(View itemView) {
            super(itemView);

            messageVideo = (VideoView) itemView.findViewById(R.id.videoView);
            messageTime = (TextView) itemView.findViewById(R.id.message_time);
        }
        void bindVideo(final Messages c){

            Long timestamp = c.getTime();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(timestamp);

            android.text.format.DateFormat df = new android.text.format.DateFormat();
            messageTime.setText(df.format("kk:mm", cal).toString());

            messageVideo.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    //download_video(v, c.getMessage());
                    play_video(v, c.getMessage());
                    return false;
                }
            });
        }

    }

}