package com.workshop.myapplication;

import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.workshop.myapplication.Model.MessageList;
import com.workshop.myapplication.Model.Post;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class DisplayMessageActivity extends ListActivity {

    public EditText mMessage;
    public String firstName;
    public String createdAt;
    MessageList.Message[] messages;
    MessageList.Message message;
    ArrayAdapter<MessageList.Message> adapter;
    ArrayList<MessageList.Message> messageList;
    ListView listView;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        this.firstName = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        // Retrieve all messages from firebase and populate them here inside of message list.
        setContentView(R.layout.activity_display_message);
        listView = getListView();

        MessageList.Message testMessage = new MessageList.Message();

        testMessage.setCreatedAt("CREATED AT");
        testMessage.setMid("USERNAME");
        testMessage.setText("TEXT MESSAGE");


        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                // A new comment has been added, add it to the displayed list
                ArrayList<MessageList.Message> messageList1 = dataSnapshot.getValue(MessageList.Message.class);


                adapter = new MessageListAdapter(DisplayMessageActivity.this, R.layout.adapter_message_list, Arrays.toString(messageList1.getMessages()));
                listView.setAdapter(adapter);

                // ...
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                MessageList.Message message = dataSnapshot.getValue(MessageList.Message.class);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                Comment movedComment = dataSnapshot.getValue(Comment.class);
                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                Toast.makeText(mContext, "Failed to load comments.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        ref.addChildEventListener(childEventListener);

        // TODO
//        messageList = This needs to equal the return list from firebase.

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /* Called when "SEND" is clicked.
     * Posts message and sends the message content with the chat id to the server.
     */
    public void sendMessage(View view) {
        mMessage = (EditText) findViewById(R.id.message_et);
        final String message = mMessage.getText().toString();
        if (message.equals("")) return;
        mMessage.setText("");

        setEditingEnabled(false);
        Toast.makeText(this, "Sending..", Toast.LENGTH_SHORT).show();

        mDatabase.child("messages").child(message).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        MessageList.Message message1 = dataSnapshot.getValue(MessageList.Message.class);

                        // [START_EXCLUDE]
                        if (message1 == null) {
                            // User is null, error out
                            Toast.makeText(DisplayMessageActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Write new post
                            writeNewPost(message1);
                        }

                        // Finish this Activity, back to the stream
                        setEditingEnabled(true);
                        finish();
                        // [END_EXCLUDE]
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        // [START_EXCLUDE]
                        setEditingEnabled(true);
                        // [END_EXCLUDE]
                    }
                });
        // TODO
        // Send the message to firebase and update the messagelist.
        // Message = mMessage.
        // username = this.firstName.
        // set a timestamp
    }

    private void setEditingEnabled(boolean enabled) {
        mMessage.setEnabled(enabled);
    }

    // [START write_fan_out]
    private void writeNewPost(MessageList.Message message) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = mDatabase.child("posts").push().getKey();
        Post post = new Post(message);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
    }
}
