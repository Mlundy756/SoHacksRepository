package com.workshop.myapplication.Model;

/**
 * Created by l730832 on 8/4/2017.
 */

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public class Post {

    public MessageList.Message message;
    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(MessageList.Message message) {
        this.message = message;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", message.getMid());
        result.put("message", message.getText());
        result.put("createdAt", message.getCreatedAt());

        return result;
    }
    // [END post_to_map]

}
