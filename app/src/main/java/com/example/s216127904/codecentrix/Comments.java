package com.example.s216127904.codecentrix;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

public class Comments extends AppCompatActivity {
    ImageView imgRacer;
    ArrayList<CommentsModel> comments;
    DBAccess business;
    int ticketID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        business = new DBAccess();
        comments = business.GetComments();


        final ListView list = findViewById(R.id.lvComments);
        final CommentAdapter listAdapter = new CommentAdapter(getApplicationContext(),comments,1);

        list.setAdapter(listAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String selected = comments.get(position).CommentDescription;
                selected.equals("");
            }
        });

        Button btnBack = findViewById(R.id.btnBack);






        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backToMain =  new Intent(getApplicationContext(), RecordPenalty.class);
                startActivity(backToMain);
            }
        });

    }
}
