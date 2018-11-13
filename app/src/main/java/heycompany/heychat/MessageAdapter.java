package heycompany.heychat;

import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    private FirebaseAuth mAuth;
    private List<Messages> mMessageList;

    public String voice;

    public MessageAdapter(List<Messages> mMessageList) {

        this.mMessageList = mMessageList;

    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout ,parent, false);

        View r = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layoutich ,parent, false);

        return new MessageViewHolder(v);
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public TextView messageTextIch;
        public ImageView messageImage;
        public ImageView messageVoice;
        public CircleImageView profileImage;


        public MessageViewHolder(View view) {
            super(view);

            messageText = (TextView) view.findViewById(R.id.message_text_layout);
            messageTextIch = (TextView) view.findViewById(R.id.message_text_layout);
            messageImage = (ImageView) view.findViewById(R.id.message_image_layout);
            messageVoice = (ImageView) view.findViewById(R.id.message_voice_layout);
            messageVoice.setClickable(true);


            messageVoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });
        }
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {

        mAuth = FirebaseAuth.getInstance();
        String current_user_id = mAuth.getCurrentUser().getUid();
        Messages c = mMessageList.get(i);
        String from_user = c.getFrom();
        String message_type = c.getType();
        voice = c.getMessage();

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
        }
        else if (message_type.equals("voice")){
                viewHolder.messageImage.setVisibility(View.INVISIBLE);
                viewHolder.messageText.setVisibility(View.INVISIBLE);
                viewHolder.messageVoice.setVisibility(View.VISIBLE);
                viewHolder.messageVoice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        play_sound(v);
                    }
                });
        }
        else if( message_type.equals("image")) {

            viewHolder.messageText.setVisibility(View.INVISIBLE);
            viewHolder.messageVoice.setVisibility(View.INVISIBLE);
            Picasso.get().load(c.getMessage()).placeholder(R.drawable.placeholder).into(viewHolder.messageImage);


        }

    }



    @Override
    public int getItemCount() {

        return mMessageList.size();
    }

    private void play_sound(View v){

        String url = voice;
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

}