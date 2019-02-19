package heycompany.heychat;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    private FirebaseAuth mAuth;
    private List<Messages> mMessageList;

    public MessageAdapter(List<Messages> mMessageList) {

        this.mMessageList = mMessageList;

    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout ,parent, false);

        View r = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.textmessage_send,parent, false);

        return new MessageViewHolder(v);
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public TextView messageTextIch;
        public ImageView messageImage;
        public Button messageVoice;
        public VideoView messageVideo;
        public CircleImageView profileImage;


        public MessageViewHolder(View view) {
            super(view);

            messageText = (TextView) view.findViewById(R.id.message_text_layout);
            messageTextIch = (TextView) view.findViewById(R.id.message_text_layout);
            messageImage = (ImageView) view.findViewById(R.id.message_image_layout);
            messageVoice = (Button) view.findViewById(R.id.message_voice_layout);
            messageVideo = (VideoView) view.findViewById(R.id.VideoView);

        }
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {

        mAuth = FirebaseAuth.getInstance();
        String current_user_id = mAuth.getCurrentUser().getUid();
        final Messages c = mMessageList.get(i);
        String from_user = c.getFrom();
        String message_type = c.getType();

        if(from_user.equals(current_user_id)){
            viewHolder.messageTextIch.setBackgroundResource(R.drawable.message_text_backgroundich);
            viewHolder.messageTextIch.setTextColor(Color.BLACK);

        }else{
            viewHolder.messageText.setBackgroundResource(R.drawable.message_text_background);
            viewHolder.messageText.setTextColor(Color.WHITE);

        }
        viewHolder.messageText.setText(c.getMessage());

        if(message_type.equals("text")){

            viewHolder.messageText.setText(c.getMessage());
            viewHolder.messageImage.setVisibility(View.INVISIBLE);
            viewHolder.messageVoice.setVisibility(View.INVISIBLE);
            viewHolder.messageVideo.setVisibility(View.INVISIBLE);
        }
        else if (message_type.equals("voice")){
                viewHolder.messageImage.setVisibility(View.INVISIBLE);
                viewHolder.messageText.setVisibility(View.INVISIBLE);
                viewHolder.messageVoice.setVisibility(View.VISIBLE);
            viewHolder.messageVideo.setVisibility(View.INVISIBLE);
                viewHolder.messageVoice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        play_sound(v, c.getMessage());

                    }
                });
        }
        else if( message_type.equals("image")) {

            viewHolder.messageText.setVisibility(View.INVISIBLE);
            viewHolder.messageVoice.setVisibility(View.INVISIBLE);
            viewHolder.messageVideo.setVisibility(View.INVISIBLE);
            Picasso.get().load(c.getMessage()).placeholder(R.drawable.placeholder).into(viewHolder.messageImage);

        }
        else if( message_type.equals("video")) {

            viewHolder.messageText.setVisibility(View.INVISIBLE);
            viewHolder.messageVoice.setVisibility(View.INVISIBLE);
            viewHolder.messageVideo.setVisibility(View.VISIBLE);
            viewHolder.messageVideo.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    //download_video(v, c.getMessage());
                    play_video(v, c.getMessage());
                    return false;
                }
            });

        }

    }



    @Override
    public int getItemCount() {

        return mMessageList.size();
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

        VideoView videoView = (VideoView)v.findViewById(R.id.VideoView);
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

}