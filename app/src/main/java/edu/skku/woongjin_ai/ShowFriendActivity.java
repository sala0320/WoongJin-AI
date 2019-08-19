package edu.skku.woongjin_ai;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class ShowFriendActivity extends AppCompatActivity {

    private DatabaseReference mPostReference;
    ListView friend_list;
    ArrayList<String> data;
    ArrayAdapter<String> arrayAdapter;
    String id_key;
    Intent intentAddFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showfriend);

        friend_list = findViewById(R.id.friend_list);
        Button buttonAddFriend = (Button) findViewById(R.id.addFriend);

        data = new ArrayList<String>();

        final Intent intent = getIntent();
        id_key = intent.getStringExtra("id");

        mPostReference = FirebaseDatabase.getInstance().getReference().child("user_list").child(id_key).child("friend");
        arrayAdapter = new ArrayAdapter<String>(ShowFriendActivity.this, android.R.layout.simple_list_item_1);
        friend_list.setAdapter(arrayAdapter);
        getFirebaseDatabase();

        buttonAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentAddFriend = new Intent(ShowFriendActivity.this, AddFriendActivity.class);
                intentAddFriend.putExtra("id", id_key);
                startActivity(intentAddFriend);
                finish();
            }
        });
    }

    public void getFirebaseDatabase() {
        try {
            final ValueEventListener postListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String key = snapshot.getKey();
                        Log.d("friend key", key);
                        //if (key.equals(id_key))
                        data.add(key);
                    }
                    arrayAdapter.clear();
                    arrayAdapter.addAll(data);
                    arrayAdapter.notifyDataSetChanged();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            };
            mPostReference.addValueEventListener(postListener);

        } catch (java.lang.NullPointerException e) {

        }
    }
}