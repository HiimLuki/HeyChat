package heycompany.heychat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddToGroupActivity extends AppCompatActivity {

    private RecyclerView mUsersList;

    private DatabaseReference mUsersDatabase;

    //Online Status
    private DatabaseReference mUserRef;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseInfo;
    private DatabaseReference mDatabaseUser;
    private DatabaseReference mDatabaseUser2;
    private DatabaseReference mUserInfo;
    private FirebaseAuth mAuth;
    private String mCurrent_user_id;
    private Button toChat_btn;
    private Button closeBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_group);

        //OnlineStatus
        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
        mUsersDatabase.keepSynced(true);

        mUserInfo = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserInfo.keepSynced(true);

        mUsersList = (RecyclerView) findViewById(R.id.addfriend_list);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(this));

        closeBtn = (Button) findViewById(R.id.close_Btn2);


        toChat_btn = (Button) findViewById(R.id.chat_btn);

        toChat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    String group_id = getIntent().getStringExtra("group_id");

                    Intent GroupChatIntent = new Intent(AddToGroupActivity.this,GroupChatActivity.class);
                    GroupChatIntent.putExtra("group_id", group_id);
                    startActivity(GroupChatIntent);
                    Log.d("tag", group_id);
                }
            });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent closeIntent = new Intent(AddToGroupActivity.this, CreateGroupActivity.class);
                startActivity(closeIntent);
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            mUserRef.child("online").setValue(System.currentTimeMillis());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            mUserRef.child("online").setValue("true");
        }

        FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(
                Users.class,
                R.layout.users_single_layout,
                UsersViewHolder.class,
                mUsersDatabase


        ){

            @Override
            protected void populateViewHolder(final UsersViewHolder usersViewHolder, final Users users, int position) {

                final String user_id = getRef(position).getKey();

                mUserInfo.child(user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String userName = dataSnapshot.child("name").getValue().toString();
                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();

                        usersViewHolder.setDisplayName(userName);
                        usersViewHolder.setStatus(users.getStatus());
                        usersViewHolder.setUserImage(userThumb, getApplicationContext());

                        usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = getIntent();
                                String group = intent.getStringExtra("groupname");
                                String admin = intent.getStringExtra("admin");
                                String group_id = getIntent().getStringExtra("group_id");

                                //Add DatabaseInfo (GroupInfo) for UserBranch
                                mDatabaseInfo = FirebaseDatabase.getInstance().getReference().child("Groups").child(user_id).child(group_id).child("groupinfo");
                                HashMap<String, String> addMap = new HashMap<>();
                                addMap.put("name", group);
                                addMap.put("admin", admin);
                                addMap.put("groupid",group_id);
                                mDatabaseInfo.setValue(addMap);

                                //Add in Creator Branch the other Member Onclicked
                                mDatabase = FirebaseDatabase.getInstance().getReference().child("Groups").child(mCurrent_user_id).child(group_id).child("member").child(user_id);
                                HashMap<String, String> userMap = new HashMap<>();
                                userMap.put("seen", "false");
                                mDatabase.setValue(userMap);

                                //Add in User Branch the Member of Creator
                                mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Groups").child(user_id).child(group_id).child("member").child(mCurrent_user_id);
                                HashMap<String, String> memberMap = new HashMap<>();
                                memberMap.put("seen", "false");
                                mDatabaseUser.setValue(memberMap);

                                //Add in User Branch the Member of the User
                                mDatabaseUser2 = FirebaseDatabase.getInstance().getReference().child("Groups").child(user_id).child(group_id).child("member").child(user_id);
                                HashMap<String, String> memberMap2 = new HashMap<>();
                                memberMap2.put("seen", "false");
                                mDatabaseUser2.setValue(memberMap2);

                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });




            }
        };
        mUsersList.setAdapter(firebaseRecyclerAdapter);
    }


    public static class UsersViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setDisplayName(String name){
            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);
        }
        public void setStatus(String status){
            TextView statusView = (TextView) mView.findViewById(R.id.user_single_status);
            statusView.setText(status);
        }

        public void setUserImage(final String thumb_image, Context ctx){
            final CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);
            Picasso.get().load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.placeholder).into(userImageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get().load(thumb_image).placeholder(R.drawable.placeholder).into(userImageView);
                }
            });
        }

    }

}
