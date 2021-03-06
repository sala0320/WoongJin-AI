package edu.skku.woongjin_ai;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/*
from MainQuizTypeFragment
질문나라 - 문제 낼 지문 선택하기
 */
public class NationQuizActivity extends AppCompatActivity {

    Intent intent, intentHome, intentMakeQuiz, intentFriendQuiz;
    String id, nickname, thisWeek;
    TextView textView;
    ImageButton homeButton;
    public DatabaseReference mPostReference;
    ListView studiedBookListView;
    SelectBookListAdapter selectBookListAdapter;
    ArrayList<String> studiedBookArrayList, backgroundArrayList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nationquiz);

        intent = getIntent();
        id = intent.getStringExtra("id");
        nickname = intent.getStringExtra("nickname");
        thisWeek = intent.getStringExtra("thisWeek");

        mPostReference = FirebaseDatabase.getInstance().getReference();

        textView = (TextView) findViewById(R.id.nationQuiz);
        homeButton = (ImageButton) findViewById(R.id.home);
        studiedBookListView = (ListView) findViewById(R.id.studiedBookList);

        studiedBookArrayList = new ArrayList<String>();
        backgroundArrayList = new ArrayList<String>();
        selectBookListAdapter = new SelectBookListAdapter();

        getFirebaseDatabaseStudiedBookList();

        textView.setText(nickname + "(이)가 읽은 책 목록이야~\n문제를 내고 싶은 책을 클릭하면 문제를 만들 수 있어!");

        studiedBookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long i) {
                intentMakeQuiz = new Intent(NationQuizActivity.this, SelectTypeActivity.class);
                intentMakeQuiz.putExtra("id", id);
                intentMakeQuiz.putExtra("scriptnm", studiedBookArrayList.get(position));
                intentMakeQuiz.putExtra("background", backgroundArrayList.get(position));
                intentMakeQuiz.putExtra("nickname", nickname);
                intentMakeQuiz.putExtra("thisWeek", thisWeek);
                startActivity(intentMakeQuiz);
                finish();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentHome = new Intent(NationQuizActivity.this, MainActivity.class);
                intentHome.putExtra("id", id);
                startActivity(intentHome);
                finish();
            }
        });
    }

    private void getFirebaseDatabaseStudiedBookList() {
        mPostReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                studiedBookArrayList.clear();
                backgroundArrayList.clear();

                for(DataSnapshot snapshot : dataSnapshot.child("user_list/" + id + "/my_script_list").getChildren()) {
                    String key = snapshot.getKey();
                    String bookname = dataSnapshot.child("script_list/" + key + "/book_name").getValue().toString();
                    studiedBookArrayList.add(key);
                    selectBookListAdapter.addItem(key, bookname);
                }
                studiedBookListView.setAdapter(selectBookListAdapter);

                for(DataSnapshot snapshot : dataSnapshot.child("script_list").getChildren()) {
                    String key = snapshot.getKey();
                    for(String script : studiedBookArrayList) {
                        if(key.equals(script)) {
                            String background = snapshot.child("background").getValue().toString();
                            backgroundArrayList.add(background);
                            break;
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {            }
        });
    }
}
