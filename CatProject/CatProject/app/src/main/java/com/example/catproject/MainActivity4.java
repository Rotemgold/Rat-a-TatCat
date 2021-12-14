package com.example.catproject;
/*
Final project
Author 1 - Rotem Reshef, ID - 308577188
Author 2 - Dor Hazout, ID - 313560328
 */
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class MainActivity4 extends AppCompatActivity implements View.OnClickListener {
    ImageView imageView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        imageView2 = findViewById(R.id.imageView2);
        imageView2.setOnClickListener(this);
        imageView2.setVisibility(View.VISIBLE);
        imageView2.setClickable(true);
    }

    @Override
    public void onClick(View v) {
        if(v==imageView2){
            Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
            startActivity(intent);
        }
    }
}