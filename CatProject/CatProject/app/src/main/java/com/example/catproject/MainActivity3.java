package com.example.catproject;
/*
Final project
Author 1 - Rotem Reshef, ID - 308577188
Author 2 - Dor Hazout, ID - 313560328
 */
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity3 extends AppCompatActivity implements View.OnClickListener {
    TextView textTurn;
    ImageView card1, card2, card3, card4, card5, card6, card7, card8, MiddleJackpot, RightJackpot, Jackpot;
    ArrayList<Long> cards = new ArrayList<Long>();
    ArrayList<Long> player1Cards;
    ArrayList<Long> player2Cards;
    Button Start, take, drop, declareWin;
    int jackpotIndex = 8;
    long middlecard;
    long currentCard;
    long tmp;
    String playerName = "";
    String roomName = "";
    String role = "";
    String message = "";
    Boolean end = false;
    Boolean peekguest = false, swapguest = false, takeTwoguest = false, peekhost = false, swaphost = false, takeTwohost = false, swap1 = false, swap2 = false, swap3 = false, swap4 = false;
    FirebaseDatabase database;
    DatabaseReference messageRef;
    DatabaseReference cardsRef;
    DatabaseReference checkRef;
    DatabaseReference roomRef;
    final Context context = this;


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        database = FirebaseDatabase.getInstance();

        SharedPreferences preferences = getSharedPreferences("PREFS", 0);
        //set player name
        playerName = preferences.getString("playerName", "");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            roomName = extras.getString("roomName");
            //set player rule, host for the one how open the room, guest for the other one
            if (roomName.equals(playerName)) {
                role = "host";
            } else {
                role = "guest";
            }
        }

        player1Cards = new ArrayList<>(4);
        player2Cards = new ArrayList<>(4);
        textTurn = findViewById(R.id.textTurn);
        card1 = findViewById(R.id.Card1);
        card1.setOnClickListener(this);
        card1.setClickable(false);
        card2 = findViewById(R.id.Card2);
        card2.setOnClickListener(this);
        card2.setClickable(false);
        card3 = findViewById(R.id.Card3);
        card3.setOnClickListener(this);
        card3.setClickable(false);
        card4 = findViewById(R.id.Card4);
        card4.setOnClickListener(this);
        card4.setClickable(false);
        card5 = findViewById(R.id.Card5);
        card5.setOnClickListener(this);
        card5.setClickable(false);
        card6 = findViewById(R.id.Card6);
        card6.setOnClickListener(this);
        card6.setClickable(false);
        card7 = findViewById(R.id.Card7);
        card7.setOnClickListener(this);
        card7.setClickable(false);
        card8 = findViewById(R.id.Card8);
        card8.setOnClickListener(this);
        card8.setClickable(false);
        MiddleJackpot = findViewById(R.id.MiddleJackpot);
        MiddleJackpot.setOnClickListener(this);
        MiddleJackpot.setClickable(false);
        RightJackpot = findViewById(R.id.RigthJackpot);
        Jackpot = findViewById(R.id.Jackpot);
        Jackpot.setOnClickListener(this);
        Start = findViewById(R.id.Start);
        Start.setOnClickListener(this);
        declareWin = findViewById(R.id.DeclareWin);
        declareWin.setOnClickListener(this);
        take = findViewById(R.id.Take);
        take.setClickable(false);
        take.setVisibility(View.VISIBLE);
        take.setOnClickListener(this);
        take.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
        drop = findViewById(R.id.Drop);
        drop.setClickable(false);
        drop.setVisibility(View.VISIBLE);
        drop.setOnClickListener(this);
        drop.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));

        cardsRef = database.getReference("rooms/" + roomName + "/cards");
        cardsRef.push();

        messageRef = database.getReference("rooms/" + roomName + "/message");

        message = role + ": Start";
        messageRef.setValue(message);

        roomRef = database.getReference("rooms/" + roomName);
        //Start message for each of the roles
        if(role == "host"){
            textTurn.setText("Wait for opponent");
            Start.setClickable(false);
            Start.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
        }else{
            textTurn.setText("Wait for host to start the game");
        }
        roomRef.onDisconnect().removeValue();
        addRoomEventListener();
    }
    //get start jackpot from DB
    private void addRoomEventListener(){

        cardsRef.addValueEventListener((new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if(snapshot.getValue() != null){
                        cards.add(snapshot.getValue(Long.class));
                    }
                }
                System.out.println(cards);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }));
        messageRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Set action for each message
                if(role.equals("host")){
                    if(dataSnapshot.getValue(String.class).contains("guest: Start")){
                        Start.setClickable(true);
                        Start.setBackgroundTintList(getResources().getColorStateList(R.color.purple_700));
                        textTurn.setText("Its time for you to start the game");
                        textTurn.setTextColor(Color.RED);
                    } // - Host Starts the game
                    if(dataSnapshot.getValue(String.class).contains("Starting the game")){
                        try {
                            player1Cards.add(cards.get(0));
                            player2Cards.add(cards.get(1));
                            player1Cards.add(cards.get(2));
                            player2Cards.add(cards.get(3));
                            player1Cards.add(cards.get(4));
                            player2Cards.add(cards.get(5));
                            player1Cards.add(cards.get(6));
                            player2Cards.add(cards.get(7));
                            setImage(cards.get(0), card1);
                            setImage(cards.get(6), card4);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.out.println("Exception thrown  :" + e);
                        }
                        Start.setVisibility(View.INVISIBLE);
                        Jackpot.setClickable(true);
                        textTurn.setText("Its your turn");
                        declareWin.setVisibility(View.VISIBLE);
                        declareWin.setClickable(true);
                        new Handler().postDelayed(new Runnable(){
                            public void run() {
                                setImage(-1, card1);
                                setImage(-1, card4);
                            }
                        }, 1000*5);
                    } // - host stating the game
                    if(dataSnapshot.getValue(String.class).contains("host Press on Jackpot")){
                        RightJackpot.setVisibility(View.VISIBLE);
                        try {
                            setImage(cards.get(jackpotIndex), RightJackpot);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.out.println("Exception thrown  :" + e);
                        }
                        drop.setBackgroundTintList(getResources().getColorStateList(R.color.purple_500));
                        take.setBackgroundTintList(getResources().getColorStateList(R.color.purple_500));
                        Jackpot.setClickable(false);
                        MiddleJackpot.setClickable(false);
                        drop.setClickable(true);
                        take.setClickable(true);
                        try {
                            if(cards.get(jackpotIndex) == 10){//swap
                                swaphost = true;
                                RightJackpot.setVisibility(View.INVISIBLE);
                                setImage(10, MiddleJackpot);
                                card1.setClickable(true);
                                card2.setClickable(true);
                                card3.setClickable(true);
                                card4.setClickable(true);
                                drop.setClickable(false);
                                take.setClickable(false);
                                drop.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                                take.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                                Toast.makeText(MainActivity3.this, "Choose one of your cards", Toast.LENGTH_SHORT).show();
                            }
                            else if(cards.get(jackpotIndex) == 11){//peek
                                peekhost = true;
                                card1.setClickable(true);
                                card2.setClickable(true);
                                card3.setClickable(true);
                                card4.setClickable(true);
                                drop.setClickable(false);
                                take.setClickable(false);
                                drop.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                                take.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                                Toast.makeText(MainActivity3.this, "Choose one of your cards", Toast.LENGTH_SHORT).show();
                            }
                            else if(cards.get(jackpotIndex) == 12){//Take two
                                takeTwohost = true;
                                textTurn.setText("Take card from jackpot");
                                Jackpot.setClickable(true);
                                drop.setClickable(false);
                                take.setClickable(false);
                                drop.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                                take.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                                setImage(12, MiddleJackpot);
                                MiddleJackpot.setVisibility(View.VISIBLE);
                                RightJackpot.setVisibility(View.INVISIBLE);
                                messageRef.setValue("Taketwo");
                            }
                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.out.println("Exception thrown  :" + e);
                        }
                        jackpotIndex++;
                    }// host press Jackpot
                    if(dataSnapshot.getValue(String.class).contains("host Press on take")){
                        drop.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                        take.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                        drop.setClickable(false);
                        take.setClickable(false);
                        card1.setClickable(true);
                        card2.setClickable(true);
                        card3.setClickable(true);
                        card4.setClickable(true);
                        try {
                            currentCard = cards.get(jackpotIndex-1);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.out.println("Exception thrown  :" + e);
                        }

                        Toast.makeText(MainActivity3.this, "Choose one of your cards", Toast.LENGTH_SHORT).show();
                    }// host press on take
                    if(dataSnapshot.getValue(String.class).contains("host Press on card1")){
                        card1.setClickable(false);
                        card2.setClickable(false);
                        card3.setClickable(false);
                        card4.setClickable(false);
                        if(peekhost == true){
                            try {
                                setImage(player1Cards.get(0), card1);
                            } catch (ArrayIndexOutOfBoundsException e) {
                                System.out.println("Exception thrown  :" + e);
                            }
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card1);
                                }
                            }, 1000*5);
                            setImage(11, MiddleJackpot);
                            MiddleJackpot.setVisibility(View.VISIBLE);
                            RightJackpot.setVisibility(View.INVISIBLE);
                            peekhost = false;
                            textTurn.setText("Opponents turn");
                            textTurn.setTextColor(Color.BLACK);
                        }
                        else if(swaphost == true){
                            swap1 = true;
                            card5.setClickable(true);
                            card6.setClickable(true);
                            card7.setClickable(true);
                            card8.setClickable(true);
                            swaphost = false;
                            Toast.makeText(MainActivity3.this, "Choose one of your opponent cards", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            middlecard = player1Cards.get(0);
                            player1Cards.set(0, currentCard);
                            setImage(middlecard, MiddleJackpot);
                            MiddleJackpot.setVisibility(View.VISIBLE);
                            RightJackpot.setVisibility(View.INVISIBLE);


                            if(takeTwohost == true){
                                takeTwohost = false;
                                textTurn.setText("Take one more card");
                                Jackpot.setClickable(true);
                            }
                            else{
                                textTurn.setText("Opponents turn");
                                textTurn.setTextColor(Color.BLACK);
                                declareWin.setClickable(false);
                                declareWin.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                            }
                        }

                    }// host press on card 1
                    if(dataSnapshot.getValue(String.class).contains("host Press on card2")){
                        card1.setClickable(false);
                        card2.setClickable(false);
                        card3.setClickable(false);
                        card4.setClickable(false);
                        if(peekhost == true){
                            setImage(player1Cards.get(1), card2);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card2);
                                }
                            }, 1000*5);
                            setImage(11, MiddleJackpot);
                            MiddleJackpot.setVisibility(View.VISIBLE);
                            RightJackpot.setVisibility(View.INVISIBLE);
                            peekhost = false;
                            textTurn.setText("Opponents turn");
                            textTurn.setTextColor(Color.BLACK);
                            declareWin.setClickable(false);
                            declareWin.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                        }
                        else if(swaphost == true){
                            swap2 = true;
                            card5.setClickable(true);
                            card6.setClickable(true);
                            card7.setClickable(true);
                            card8.setClickable(true);
                            swaphost = false;
                            Toast.makeText(MainActivity3.this, "Choose one of your opponent cards", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            middlecard = player1Cards.get(1);
                            player1Cards.set(1, currentCard);
                            setImage(middlecard, MiddleJackpot);
                            MiddleJackpot.setVisibility(View.VISIBLE);
                            RightJackpot.setVisibility(View.INVISIBLE);
                            if(takeTwohost == true){
                                takeTwohost = false;
                                textTurn.setText("Take one more card");
                                Jackpot.setClickable(true);
                            }
                            else{
                                textTurn.setText("Opponents turn");
                                textTurn.setTextColor(Color.BLACK);
                                declareWin.setClickable(false);
                                declareWin.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                            }
                        }

                    }// host press on card2
                    if(dataSnapshot.getValue(String.class).contains("host Press on card3")){
                        card1.setClickable(false);
                        card2.setClickable(false);
                        card3.setClickable(false);
                        card4.setClickable(false);
                        if(peekhost == true){
                            setImage(player1Cards.get(2), card3);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card3);
                                }
                            }, 1000*5);
                            setImage(11, MiddleJackpot);
                            MiddleJackpot.setVisibility(View.VISIBLE);
                            RightJackpot.setVisibility(View.INVISIBLE);
                            peekhost = false;
                            textTurn.setText("Opponents turn");
                            textTurn.setTextColor(Color.BLACK);
                            declareWin.setClickable(false);
                            declareWin.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                        }
                        else if(swaphost == true){
                            swap3 = true;
                            card5.setClickable(true);
                            card6.setClickable(true);
                            card7.setClickable(true);
                            card8.setClickable(true);
                            swaphost = false;
                            Toast.makeText(MainActivity3.this, "Choose one of your opponent cards", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            middlecard = player1Cards.get(2);
                            player1Cards.set(2, currentCard);
                            setImage(middlecard, MiddleJackpot);
                            MiddleJackpot.setVisibility(View.VISIBLE);
                            RightJackpot.setVisibility(View.INVISIBLE);

                            if(takeTwohost == true){
                                takeTwohost = false;
                                textTurn.setText("Take one more card");
                                Jackpot.setClickable(true);
                            }
                            else{
                                textTurn.setText("Opponents turn");
                                textTurn.setTextColor(Color.BLACK);
                                declareWin.setClickable(false);
                                declareWin.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                            }
                        }

                    }//host press on card3
                    if(dataSnapshot.getValue(String.class).contains("host Press on card4")){
                        card1.setClickable(false);
                        card2.setClickable(false);
                        card3.setClickable(false);
                        card4.setClickable(false);
                        if(peekhost == true){
                            setImage(player1Cards.get(3), card4);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card4);
                                }
                            }, 1000*5);
                            setImage(11, MiddleJackpot);
                            MiddleJackpot.setVisibility(View.VISIBLE);
                            RightJackpot.setVisibility(View.INVISIBLE);
                            peekhost = false;
                            textTurn.setText("Opponents turn");
                            textTurn.setTextColor(Color.BLACK);
                            declareWin.setClickable(false);
                            declareWin.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                        }
                        else if(swaphost == true){
                            swap4 = true;
                            card5.setClickable(true);
                            card6.setClickable(true);
                            card7.setClickable(true);
                            card8.setClickable(true);
                            swaphost = false;
                            Toast.makeText(MainActivity3.this, "Choose one of your opponent cards", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            middlecard = player1Cards.get(3);
                            player1Cards.set(3, currentCard);
                            setImage(middlecard, MiddleJackpot);
                            MiddleJackpot.setVisibility(View.VISIBLE);
                            RightJackpot.setVisibility(View.INVISIBLE);

                            if(takeTwohost == true){
                                takeTwohost = false;
                                textTurn.setText("Take one more card");
                                Jackpot.setClickable(true);
                            }
                            else{
                                textTurn.setText("Opponents turn");
                                textTurn.setTextColor(Color.BLACK);
                                declareWin.setClickable(false);
                                declareWin.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                            }
                        }
                    }//host press on card4
                    if(dataSnapshot.getValue(String.class).contains("host Press on drop")){
                        drop.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                        take.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                        drop.setClickable(false);
                        take.setClickable(false);
                        try {
                            currentCard = cards.get(jackpotIndex-1);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.out.println("Exception thrown  :" + e);
                        }
                        middlecard = currentCard;
                        setImage(middlecard, MiddleJackpot);
                        MiddleJackpot.setVisibility(View.VISIBLE);
                        RightJackpot.setVisibility(View.INVISIBLE);
                        if(takeTwohost == true){
                            takeTwohost = false;
                            textTurn.setText("Take one more card");
                            Jackpot.setClickable(true);
                        }
                        else{
                            textTurn.setText("Opponents turn");
                            textTurn.setTextColor(Color.BLACK);
                            declareWin.setClickable(false);
                            declareWin.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                        }
                    }//host press on drop button
                    if(dataSnapshot.getValue(String.class).contains("host Press on MiddleJackpot")){
                        MiddleJackpot.setClickable(false);
                        Jackpot.setClickable(false);
                        currentCard = middlecard;
                        card1.setClickable(true);
                        card2.setClickable(true);
                        card3.setClickable(true);
                        card4.setClickable(true);
                        Toast.makeText(MainActivity3.this, "Choose one of your cards", Toast.LENGTH_SHORT).show();
                    }//host press on MiddleJackpot
                    if(dataSnapshot.getValue(String.class).contains("host Press on card5")){
                        card5.setClickable(false);
                        card6.setClickable(false);
                        card7.setClickable(false);
                        card8.setClickable(false);
                        if(swap1 == true){
                            setImage(player2Cards.get(0), card1);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card1);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(0);
                            player1Cards.set(0, player2Cards.get(0));
                            player2Cards.set(0, tmp);
                            swap1 = false;
                        }
                        else if(swap2 == true){
                            setImage(player2Cards.get(0), card2);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card2);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(1);
                            player1Cards.set(1, player2Cards.get(0));
                            player2Cards.set(0, tmp);
                            swap2 = false;
                        }
                        else if(swap3 == true){
                            setImage(player2Cards.get(0), card3);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card3);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(2);
                            player1Cards.set(2, player2Cards.get(0));
                            player2Cards.set(0, tmp);
                            swap3 = false;
                        }
                        else{
                            setImage(player2Cards.get(0), card4);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card4);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(3);
                            player1Cards.set(3, player2Cards.get(0));
                            player2Cards.set(0, tmp);
                            swap4 = false;
                        }
                        textTurn.setText("Opponents turn");
                        declareWin.setClickable(false);
                        declareWin.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                        textTurn.setTextColor(Color.BLACK);
                    }//host press on card5
                    if(dataSnapshot.getValue(String.class).contains("host Press on card6")){
                        card5.setClickable(false);
                        card6.setClickable(false);
                        card7.setClickable(false);
                        card8.setClickable(false);
                        if(swap1 == true){
                            setImage(player2Cards.get(1), card1);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card1);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(0);
                            player1Cards.set(0, player2Cards.get(1));
                            player2Cards.set(1, tmp);
                            swap1 = false;
                        }
                        else if(swap2 == true){
                            setImage(player2Cards.get(1), card2);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card2);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(1);
                            player1Cards.set(1, player2Cards.get(1));
                            player2Cards.set(1, tmp);
                            swap2 = false;
                        }
                        else if(swap3 == true){
                            setImage(player2Cards.get(1), card3);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card3);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(2);
                            player1Cards.set(2, player2Cards.get(1));
                            player2Cards.set(1, tmp);
                            swap3 = false;
                        }
                        else{
                            setImage(player2Cards.get(1), card4);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card4);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(3);
                            player1Cards.set(3, player2Cards.get(1));
                            player2Cards.set(1, tmp);
                            swap4 = false;
                        }
                        textTurn.setText("Opponents turn");
                        textTurn.setTextColor(Color.BLACK);
                        declareWin.setClickable(false);
                        declareWin.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                    }//host press on card6
                    if(dataSnapshot.getValue(String.class).contains("host Press on card7")){
                        card5.setClickable(false);
                        card6.setClickable(false);
                        card7.setClickable(false);
                        card8.setClickable(false);
                        if(swap1 == true){
                            setImage(player2Cards.get(2), card1);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card1);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(0);
                            player1Cards.set(0, player2Cards.get(2));
                            player2Cards.set(2, tmp);
                            swap1 = false;
                        }
                        else if(swap2 == true){
                            setImage(player2Cards.get(2), card2);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card2);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(1);
                            player1Cards.set(1, player2Cards.get(2));
                            player2Cards.set(2, tmp);
                            swap2 = false;
                        }
                        else if(swap3 == true){
                            setImage(player2Cards.get(2), card3);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card3);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(2);
                            player1Cards.set(2, player2Cards.get(2));
                            player2Cards.set(2, tmp);
                            swap3 = false;
                        }
                        else{
                            setImage(player2Cards.get(2), card4);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card4);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(3);
                            player1Cards.set(3, player2Cards.get(2));
                            player2Cards.set(2, tmp);
                            swap4 = false;
                        }
                        textTurn.setText("Opponents turn");
                        textTurn.setTextColor(Color.BLACK);
                        declareWin.setClickable(false);
                        declareWin.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                    }//host press on card7
                    if(dataSnapshot.getValue(String.class).contains("host Press on card8")){
                        card5.setClickable(false);
                        card6.setClickable(false);
                        card7.setClickable(false);
                        card8.setClickable(false);
                        if(swap1 == true){
                            setImage(player2Cards.get(3), card1);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card1);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(0);
                            player1Cards.set(0, player2Cards.get(3));
                            player2Cards.set(3, tmp);
                            swap1 = false;
                        }
                        else if(swap2 == true){
                            setImage(player2Cards.get(3), card2);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card2);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(1);
                            player1Cards.set(1, player2Cards.get(3));
                            player2Cards.set(3, tmp);
                            swap2 = false;
                        }
                        else if(swap3 == true){
                            setImage(player2Cards.get(3), card3);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card3);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(2);
                            player1Cards.set(2, player2Cards.get(3));
                            player2Cards.set(3, tmp);
                            swap3 = false;
                        }
                        else{
                            setImage(player2Cards.get(3), card4);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card4);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(3);
                            player1Cards.set(3, player2Cards.get(3));
                            player2Cards.set(3, tmp);
                            swap4 = false;
                        }
                        textTurn.setText("Opponents turn");
                        textTurn.setTextColor(Color.BLACK);
                        declareWin.setClickable(false);
                        declareWin.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                    }//host press on card8
                    if(dataSnapshot.getValue(String.class).contains("host Press on declareWin")){
                        long sum1 = player1Cards.get(0) + player1Cards.get(1) + player1Cards.get(2) + player1Cards.get(3);
                        long sum2 = player2Cards.get(0) + player2Cards.get(1) + player2Cards.get(2) + player2Cards.get(3);
                        setImage(player1Cards.get(0), card1);
                        setImage(player1Cards.get(1), card2);
                        setImage(player1Cards.get(2), card3);
                        setImage(player1Cards.get(3), card4);
                        setImage(player2Cards.get(0), card5);
                        setImage(player2Cards.get(1), card6);
                        setImage(player2Cards.get(2), card7);
                        setImage(player2Cards.get(3), card8);
                        if(sum1 < sum2){
                            AlertDialog alertDialog = new AlertDialog.Builder(context)
//set title
                                    .setTitle("END GAME")
//set message
                                    .setMessage("You won!!!")
//set positive button
                                    .setPositiveButton("EXIT", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            //set what would happen when positive button is clicked
                                            startActivity(new Intent(getApplicationContext(), MainActivity2.class));
                                        }
                                    })
                                    .show();
                        }
                        else if(sum1 == sum2){
                            AlertDialog alertDialog = new AlertDialog.Builder(context)
//set title
                                    .setTitle("END GAME")
//set message
                                    .setMessage("It's a tie")
//set positive button
                                    .setPositiveButton("EXIT", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            //set what would happen when positive button is clicked
                                            startActivity(new Intent(getApplicationContext(), MainActivity2.class));

                                        }
                                    })
                                    .show();
                        }
                        else{
                            AlertDialog alertDialog = new AlertDialog.Builder(context)
//set title
                                    .setTitle("END GAME")
//set message
                                    .setMessage("You lose!!!")
//set positive button
                                    .setPositiveButton("EXIT", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            //set what would happen when positive button is clicked
                                            startActivity(new Intent(getApplicationContext(), MainActivity2.class));
                                        }
                                    })
                                    .show();
                        }
                    }//host press on declareWin
                    if(dataSnapshot.getValue(String.class).contains("guest Press on card1")){


                        if(peekguest == true){
                            setImage(11, MiddleJackpot);
                            peekguest = false;
                        }
                        else if(swapguest == true){
                            swap1 = true;
                        }
                        else{
                            handleAnimation(card5);
                            middlecard = player2Cards.get(0);
                            player2Cards.set(0, currentCard);
                            setImage(middlecard, MiddleJackpot);
                        }
                        if(takeTwoguest == true){
                            takeTwoguest = false;
                        }
                        else{
                            textTurn.setText("It's your turn");
                            textTurn.setTextColor(Color.RED);
                            declareWin.setClickable(true);
                            declareWin.setBackgroundTintList(getResources().getColorStateList(R.color.purple_700));
                            MiddleJackpot.setVisibility(View.VISIBLE);
                            MiddleJackpot.setClickable(true);
                            Jackpot.setClickable(true);
                        }

                    }//guest press on card1
                    if(dataSnapshot.getValue(String.class).contains("guest Press on card2")){

                        if(peekguest == true){
                            setImage(11, MiddleJackpot);
                            peekguest = false;
                        }
                        else if(swapguest == true){
                            swap2 = true;
                        }
                        else{
                            handleAnimation(card6);
                            middlecard = player2Cards.get(1);
                            player2Cards.set(1, currentCard);
                            setImage(middlecard, MiddleJackpot);
                        }
                        if(takeTwoguest == true){
                            takeTwoguest = false;
                        }
                        else{
                            textTurn.setText("It's your turn");
                            textTurn.setTextColor(Color.RED);
                            declareWin.setClickable(true);
                            declareWin.setBackgroundTintList(getResources().getColorStateList(R.color.purple_700));
                            MiddleJackpot.setVisibility(View.VISIBLE);
                            MiddleJackpot.setClickable(true);
                            Jackpot.setClickable(true);
                        }
                    }//guest press on card2
                    if(dataSnapshot.getValue(String.class).contains("guest Press on card3")){
                        if(peekguest == true){
                            setImage(11, MiddleJackpot);
                            peekguest = false;
                        }
                        else if(swapguest == true){
                            swap3 = true;
                        }
                        else {
                            handleAnimation(card7);
                            middlecard = player2Cards.get(2);
                            player2Cards.set(2, currentCard);
                            setImage(middlecard, MiddleJackpot);
                        }

                        if(takeTwoguest == true){
                            takeTwoguest = false;
                        }
                        else{
                            textTurn.setText("It's your turn");
                            textTurn.setTextColor(Color.RED);
                            declareWin.setClickable(true);
                            declareWin.setBackgroundTintList(getResources().getColorStateList(R.color.purple_700));
                            MiddleJackpot.setVisibility(View.VISIBLE);
                            MiddleJackpot.setClickable(true);
                            Jackpot.setClickable(true);
                        }
                    }//guest press on card3
                    if(dataSnapshot.getValue(String.class).contains("guest Press on card4")){
                        if(peekguest == true){
                            setImage(11, MiddleJackpot);
                            peekguest = false;
                        }
                        else if(swapguest == true){
                            swap4 = true;
                        }
                        else{
                            handleAnimation(card8);
                            middlecard = player2Cards.get(3);
                            player2Cards.set(3, currentCard);
                            setImage(middlecard, MiddleJackpot);
                        }
                        if(takeTwoguest == true){
                            takeTwoguest = false;
                        }
                        else{
                            textTurn.setText("It's your turn");
                            textTurn.setTextColor(Color.RED);
                            declareWin.setClickable(true);
                            declareWin.setBackgroundTintList(getResources().getColorStateList(R.color.purple_700));
                            MiddleJackpot.setVisibility(View.VISIBLE);
                            MiddleJackpot.setClickable(true);
                            Jackpot.setClickable(true);
                        }
                    }//guest press on card4
                    if(dataSnapshot.getValue(String.class).contains("guest Press on card5")){
                        card5.setClickable(false);
                        card6.setClickable(false);
                        card7.setClickable(false);
                        card8.setClickable(false);
                        if(swap1 == true){
                            setImage(player2Cards.get(0), card1);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card1);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(0);
                            player1Cards.set(0, player2Cards.get(0));
                            player2Cards.set(0, tmp);
                            swap1 = false;
                        }
                        else if(swap2 == true){
                            setImage(player2Cards.get(0), card2);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card2);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(1);
                            player1Cards.set(1, player2Cards.get(0));
                            player2Cards.set(0, tmp);
                            swap2 = false;
                        }
                        else if(swap3 == true){
                            setImage(player2Cards.get(0), card3);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card3);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(2);
                            player1Cards.set(2, player2Cards.get(0));
                            player2Cards.set(0, tmp);
                            swap3 = false;
                        }
                        else{
                            setImage(player2Cards.get(0), card4);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card4);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(3);
                            player1Cards.set(3, player2Cards.get(0));
                            player2Cards.set(0, tmp);
                            swap4 = false;
                        }
                        setImage(10, MiddleJackpot);
                        Jackpot.setClickable(true);
                        textTurn.setText("It's your turn");
                        declareWin.setClickable(true);
                        declareWin.setBackgroundTintList(getResources().getColorStateList(R.color.purple_700));
                        textTurn.setTextColor(Color.RED);
                    }//guest press on card5
                    if(dataSnapshot.getValue(String.class).contains("guest Press on card6")){
                        card5.setClickable(false);
                        card6.setClickable(false);
                        card7.setClickable(false);
                        card8.setClickable(false);
                        if(swap1 == true){
                            setImage(player2Cards.get(1), card1);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card1);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(0);
                            player1Cards.set(0, player2Cards.get(1));
                            player2Cards.set(1, tmp);
                            swap1 = false;
                        }
                        else if(swap2 == true){
                            setImage(player2Cards.get(1), card2);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card2);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(1);
                            player1Cards.set(1, player2Cards.get(1));
                            player2Cards.set(1, tmp);
                            swap2 = false;
                        }
                        else if(swap3 == true){
                            setImage(player2Cards.get(1), card3);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card3);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(2);
                            player1Cards.set(2, player2Cards.get(1));
                            player2Cards.set(1, tmp);
                            swap3 = false;
                        }
                        else{
                            setImage(player2Cards.get(1), card4);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card4);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(3);
                            player1Cards.set(3, player2Cards.get(1));
                            player2Cards.set(1, tmp);
                            swap4 = false;
                        }
                        setImage(10, MiddleJackpot);
                        Jackpot.setClickable(true);
                        textTurn.setText("It's your turn");
                        declareWin.setClickable(true);
                        declareWin.setBackgroundTintList(getResources().getColorStateList(R.color.purple_700));
                        textTurn.setTextColor(Color.RED);
                    }//guest press on card6
                    if(dataSnapshot.getValue(String.class).contains("guest Press on card7")){
                        card5.setClickable(false);
                        card6.setClickable(false);
                        card7.setClickable(false);
                        card8.setClickable(false);
                        if(swap1 == true){
                            setImage(player2Cards.get(2), card1);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card1);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(0);
                            player1Cards.set(0, player2Cards.get(2));
                            player2Cards.set(2, tmp);
                            swap1 = false;
                        }
                        else if(swap2 == true){
                            setImage(player2Cards.get(2), card2);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card2);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(1);
                            player1Cards.set(1, player2Cards.get(2));
                            player2Cards.set(2, tmp);
                            swap2 = false;
                        }
                        else if(swap3 == true){
                            setImage(player2Cards.get(2), card3);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card3);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(2);
                            player1Cards.set(2, player2Cards.get(2));
                            player2Cards.set(2, tmp);
                            swap3 = false;
                        }
                        else{
                            setImage(player2Cards.get(2), card4);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card4);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(3);
                            player1Cards.set(3, player2Cards.get(2));
                            player2Cards.set(2, tmp);
                            swap4 = false;
                        }
                        textTurn.setText("It's your turn");
                        textTurn.setTextColor(Color.RED);
                        declareWin.setClickable(true);
                        declareWin.setBackgroundTintList(getResources().getColorStateList(R.color.purple_700));
                        setImage(10, MiddleJackpot);
                        Jackpot.setClickable(true);
                    }//guest press on card7
                    if(dataSnapshot.getValue(String.class).contains("guest Press on card8")){
                        card5.setClickable(false);
                        card6.setClickable(false);
                        card7.setClickable(false);
                        card8.setClickable(false);
                        if(swap1 == true){
                            setImage(player2Cards.get(3), card1);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card1);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(0);
                            player1Cards.set(0, player2Cards.get(3));
                            player2Cards.set(3, tmp);
                            swap1 = false;
                        }
                        else if(swap2 == true){
                            setImage(player2Cards.get(3), card2);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card2);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(1);
                            player1Cards.set(1, player2Cards.get(3));
                            player2Cards.set(3, tmp);
                            swap2 = false;
                        }
                        else if(swap3 == true){
                            setImage(player2Cards.get(3), card3);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card3);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(2);
                            player1Cards.set(2, player2Cards.get(3));
                            player2Cards.set(3, tmp);
                            swap3 = false;
                        }
                        else{
                            setImage(player2Cards.get(3), card4);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card4);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(3);
                            player1Cards.set(3, player2Cards.get(3));
                            player2Cards.set(3, tmp);
                            swap4 = false;
                        }
                        textTurn.setText("It's your turn");
                        textTurn.setTextColor(Color.RED);
                        setImage(10, MiddleJackpot);
                        Jackpot.setClickable(true);
                        declareWin.setClickable(true);
                        declareWin.setBackgroundTintList(getResources().getColorStateList(R.color.purple_700));
                    }//guest press on card8
                    if(dataSnapshot.getValue(String.class).contains("guest Press on drop")){
                        if(takeTwoguest == true){
                            takeTwoguest = false;
                        }
                        else{
                            MiddleJackpot.setClickable(true);
                            Jackpot.setClickable(true);
                            RightJackpot.setVisibility(View.INVISIBLE);
                            textTurn.setText("It's your turn");
                            textTurn.setTextColor(Color.RED);
                            declareWin.setClickable(true);
                            declareWin.setBackgroundTintList(getResources().getColorStateList(R.color.purple_700));
                        }
                        MiddleJackpot.setVisibility(View.VISIBLE);
                        try {
                            currentCard = cards.get(jackpotIndex-1);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.out.println("Exception thrown  :" + e);
                        }
                        middlecard = currentCard;
                        setImage(middlecard, MiddleJackpot);
                        MiddleJackpot.setVisibility(View.VISIBLE);
                    }//guest press on drop
                    if(dataSnapshot.getValue(String.class).contains("guest Press on Jackpot")){
                        try {
                            if(cards.get(jackpotIndex) == 11){
                                peekguest = true;
                            }
                            else if(cards.get(jackpotIndex) == 10){
                                swapguest = true;
                            }
                            else if(cards.get(jackpotIndex) == 12){
                                takeTwoguest = true;
                            }
                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.out.println("Exception thrown  :" + e);
                        }
                        jackpotIndex++;
                    }//guest press Jackpot
                    if(dataSnapshot.getValue(String.class).contains("guest Press on take")){
                        try {
                            currentCard = cards.get(jackpotIndex-1);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.out.println("Exception thrown  :" + e);
                        }
                    }////guest press on take
                    if(dataSnapshot.getValue(String.class).contains("guest Press on declareWin")){
                        long sum1 = player1Cards.get(0) + player1Cards.get(1) + player1Cards.get(2) + player1Cards.get(3);
                        long sum2 = player2Cards.get(0) + player2Cards.get(1) + player2Cards.get(2) + player2Cards.get(3);
                        setImage(player1Cards.get(0), card1);
                        setImage(player1Cards.get(1), card2);
                        setImage(player1Cards.get(2), card3);
                        setImage(player1Cards.get(3), card4);
                        setImage(player2Cards.get(0), card5);
                        setImage(player2Cards.get(1), card6);
                        setImage(player2Cards.get(2), card7);
                        setImage(player2Cards.get(3), card8);
                        if(sum1 < sum2){
                            AlertDialog alertDialog = new AlertDialog.Builder(context)
//set title
                                    .setTitle("END GAME")
//set message
                                    .setMessage("You won!!!")
//set positive button
                                    .setPositiveButton("EXIT", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            //set what would happen when positive button is clicked
                                            startActivity(new Intent(getApplicationContext(), MainActivity2.class));

                                        }
                                    })
                                    .show();
                        }
                        else if(sum1 == sum2){
                            AlertDialog alertDialog = new AlertDialog.Builder(context)
//set title
                                    .setTitle("END GAME")
//set message
                                    .setMessage("It's a tie")
//set positive button
                                    .setPositiveButton("EXIT", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            //set what would happen when positive button is clicked
                                            startActivity(new Intent(getApplicationContext(), MainActivity2.class));

                                        }
                                    })
                                    .show();
                        }
                        else{
                            AlertDialog alertDialog = new AlertDialog.Builder(context)
//set title
                                    .setTitle("END GAME")
//set message
                                    .setMessage("You lose!!!")
//set positive button
                                    .setPositiveButton("EXIT", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            //set what would happen when positive button is clicked
                                            startActivity(new Intent(getApplicationContext(), MainActivity2.class));

                                        }
                                    })
                                    .show();
                        }
                    }//guest press on DeclareWin
                } else {
                    if(dataSnapshot.getValue(String.class).contains("Start")){
                        Start.setVisibility(View.INVISIBLE);
                        declareWin.setVisibility(View.INVISIBLE);
                    } //END - set Guest can't Starts the game
                    if(dataSnapshot.getValue(String.class).contains("Starting the game")){
                        textTurn.setText("Opponent turn");
                        try {
                            player1Cards.add(cards.get(0));
                            player2Cards.add(cards.get(1));
                            player1Cards.add(cards.get(2));
                            player2Cards.add(cards.get(3));
                            player1Cards.add(cards.get(4));
                            player2Cards.add(cards.get(5));
                            player1Cards.add(cards.get(6));
                            player2Cards.add(cards.get(7));
                            setImage(cards.get(1), card1);
                            setImage(cards.get(7), card4);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.out.println("Exception thrown  :" + e);
                        }
                        Start.setVisibility(View.INVISIBLE);
                        new Handler().postDelayed(new Runnable(){
                            public void run() {
                                setImage(-1, card1);
                                setImage(-1, card4);
                            }
                        }, 1000*5);
                        declareWin.setVisibility(View.VISIBLE);
                        declareWin.setClickable(false);
                        declareWin.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                    } //End - guest stating the game
                    if(dataSnapshot.getValue(String.class).contains("guest Press on Jackpot")){
                        RightJackpot.setVisibility(View.VISIBLE);
                        try {
                            setImage(cards.get(jackpotIndex), RightJackpot);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.out.println("Exception thrown  :" + e);
                        }
                        drop.setBackgroundTintList(getResources().getColorStateList(R.color.purple_500));
                        take.setBackgroundTintList(getResources().getColorStateList(R.color.purple_500));
                        Jackpot.setClickable(false);
                        MiddleJackpot.setClickable(false);
                        drop.setClickable(true);
                        take.setClickable(true);
                        try {
                            if(cards.get(jackpotIndex) == 10){//swap
                                swapguest = true;
                                RightJackpot.setVisibility(View.INVISIBLE);
                                setImage(10, MiddleJackpot);
                                card1.setClickable(true);
                                card2.setClickable(true);
                                card3.setClickable(true);
                                card4.setClickable(true);
                                drop.setClickable(false);
                                take.setClickable(false);
                                drop.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                                take.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));


                                Toast.makeText(MainActivity3.this, "Choose one of your cards", Toast.LENGTH_SHORT).show();
                            }
                            else if(cards.get(jackpotIndex) == 11){//peek
                                peekguest = true;
                                card1.setClickable(true);
                                card2.setClickable(true);
                                card3.setClickable(true);
                                card4.setClickable(true);
                                drop.setClickable(false);
                                take.setClickable(false);
                                drop.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                                take.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                                Toast.makeText(MainActivity3.this, "Choose one of your cards", Toast.LENGTH_SHORT).show();
                            }
                            else if(cards.get(jackpotIndex) == 12){//Take two
                                takeTwoguest = true;
                                textTurn.setText("Take card from jackpot");
                                Jackpot.setClickable(true);
                                drop.setClickable(false);
                                take.setClickable(false);
                                drop.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                                take.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                                setImage(12, MiddleJackpot);
                                MiddleJackpot.setVisibility(View.VISIBLE);
                                RightJackpot.setVisibility(View.INVISIBLE);
                                messageRef.setValue("Taketwo");
                            }
                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.out.println("Exception thrown  :" + e);
                        }
                        jackpotIndex++;

                    }//guest press Jackpot
                    if(dataSnapshot.getValue(String.class).contains("guest Press on take")){
                        drop.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                        take.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                        drop.setClickable(false);
                        take.setClickable(false);
                        card1.setClickable(true);
                        card2.setClickable(true);
                        card3.setClickable(true);
                        card4.setClickable(true);
                        try {
                            currentCard = cards.get(jackpotIndex-1);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.out.println("Exception thrown  :" + e);
                        }


                        Toast.makeText(MainActivity3.this, "Choose one of your cards", Toast.LENGTH_SHORT).show();
                    }
                    if(dataSnapshot.getValue(String.class).contains("guest Press on drop")){
                        drop.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                        take.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                        drop.setClickable(false);
                        take.setClickable(false);
                        try {
                            currentCard = cards.get(jackpotIndex-1);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.out.println("Exception thrown  :" + e);
                        }

                        middlecard = currentCard;
                        setImage(middlecard, MiddleJackpot);
                        MiddleJackpot.setVisibility(View.VISIBLE);
                        RightJackpot.setVisibility(View.INVISIBLE);
                        if(takeTwoguest == true){
                            textTurn.setText("Take one more card");
                            Jackpot.setClickable(true);
                        }
                        else{
                            textTurn.setText("Opponents turn");
                            textTurn.setTextColor(Color.BLACK);
                            declareWin.setClickable(false);
                            declareWin.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                        }
                    }
                    if(dataSnapshot.getValue(String.class).contains("guest Press on card1")){
                        card1.setClickable(false);
                        card2.setClickable(false);
                        card3.setClickable(false);
                        card4.setClickable(false);
                        if(peekguest == true){
                            setImage(player2Cards.get(0), card1);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card1);
                                }
                            }, 1000*5);
                            setImage(11, MiddleJackpot);
                            MiddleJackpot.setVisibility(View.VISIBLE);
                            RightJackpot.setVisibility(View.INVISIBLE);
                            peekguest = false;
                            textTurn.setText("Opponents turn");
                            declareWin.setClickable(false);
                            declareWin.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                            textTurn.setTextColor(Color.BLACK);
                        }
                        else if(swapguest == true){
                            swapguest = false;
                            swap1 = true;
                            card5.setClickable(true);
                            card6.setClickable(true);
                            card7.setClickable(true);
                            card8.setClickable(true);
                            Toast.makeText(MainActivity3.this, "Choose one of your opponent cards", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            middlecard = player2Cards.get(0);
                            player2Cards.set(0, currentCard);
                            setImage(middlecard, MiddleJackpot);
                            MiddleJackpot.setVisibility(View.VISIBLE);
                            RightJackpot.setVisibility(View.INVISIBLE);
                            if(takeTwoguest == true){
                                textTurn.setText("Take one more card");
                                Jackpot.setClickable(true);
                                takeTwoguest = false;
                            }
                            else{
                                textTurn.setText("Opponents turn");
                                textTurn.setTextColor(Color.BLACK);
                                declareWin.setClickable(false);
                                declareWin.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                            }
                        }

                    }
                    if(dataSnapshot.getValue(String.class).contains("guest Press on card2")){
                        card1.setClickable(false);
                        card2.setClickable(false);
                        card3.setClickable(false);
                        card4.setClickable(false);
                        if(peekguest == true){
                            setImage(player2Cards.get(1), card2);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card2);
                                }
                            }, 1000*5);
                            setImage(11, MiddleJackpot);
                            MiddleJackpot.setVisibility(View.VISIBLE);
                            RightJackpot.setVisibility(View.INVISIBLE);
                            peekguest = false;
                            textTurn.setText("Opponents turn");
                            declareWin.setClickable(false);
                            declareWin.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                            textTurn.setTextColor(Color.BLACK);
                        }
                        else if(swapguest == true){
                            swapguest = false;
                            swap2 = true;
                            card5.setClickable(true);
                            card6.setClickable(true);
                            card7.setClickable(true);
                            card8.setClickable(true);
                            Toast.makeText(MainActivity3.this, "Choose one of your opponent cards", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            middlecard = player2Cards.get(1);
                            player2Cards.set(1, currentCard);
                            setImage(middlecard, MiddleJackpot);
                            MiddleJackpot.setVisibility(View.VISIBLE);
                            RightJackpot.setVisibility(View.INVISIBLE);
                            if(takeTwoguest == true){
                                textTurn.setText("Take one more card");
                                Jackpot.setClickable(true);
                                takeTwoguest = false;
                            }
                            else{
                                textTurn.setText("Opponents turn");
                                textTurn.setTextColor(Color.BLACK);
                                declareWin.setClickable(false);
                                declareWin.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                            }
                        }

                    }
                    if(dataSnapshot.getValue(String.class).contains("guest Press on card3")){
                        card1.setClickable(false);
                        card2.setClickable(false);
                        card3.setClickable(false);
                        card4.setClickable(false);
                        if(peekguest == true){
                            setImage(player2Cards.get(2), card3);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card3);
                                }
                            }, 1000*5);
                            setImage(11, MiddleJackpot);
                            MiddleJackpot.setVisibility(View.VISIBLE);
                            RightJackpot.setVisibility(View.INVISIBLE);
                            peekguest = false;
                            textTurn.setText("Opponents turn");
                            declareWin.setClickable(false);
                            declareWin.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                            textTurn.setTextColor(Color.BLACK);
                        }
                        else if(swapguest == true){
                            swapguest = false;
                            swap3 = true;
                            card5.setClickable(true);
                            card6.setClickable(true);
                            card7.setClickable(true);
                            card8.setClickable(true);
                            Toast.makeText(MainActivity3.this, "Choose one of your opponent cards", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            middlecard = player2Cards.get(2);
                            player2Cards.set(2, currentCard);
                            setImage(middlecard, MiddleJackpot);
                            MiddleJackpot.setVisibility(View.VISIBLE);
                            RightJackpot.setVisibility(View.INVISIBLE);
                            if(takeTwoguest == true){
                                textTurn.setText("Take one more card");
                                Jackpot.setClickable(true);
                                takeTwoguest = false;
                            }
                            else{
                                textTurn.setText("Opponents turn");
                                textTurn.setTextColor(Color.BLACK);
                                declareWin.setClickable(false);
                                declareWin.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                            }
                        }

                    }
                    if(dataSnapshot.getValue(String.class).contains("guest Press on card4")){
                        card1.setClickable(false);
                        card2.setClickable(false);
                        card3.setClickable(false);
                        card4.setClickable(false);
                        if(peekguest == true){
                            setImage(player2Cards.get(3), card4);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card4);
                                }
                            }, 1000*5);
                            setImage(11, MiddleJackpot);
                            MiddleJackpot.setVisibility(View.VISIBLE);
                            RightJackpot.setVisibility(View.INVISIBLE);
                            peekguest = false;
                            declareWin.setClickable(false);
                            declareWin.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                            textTurn.setText("Opponents turn");
                            textTurn.setTextColor(Color.BLACK);
                        }
                        else if(swapguest == true){
                            swapguest = false;
                            swap4 = true;
                            card5.setClickable(true);
                            card6.setClickable(true);
                            card7.setClickable(true);
                            card8.setClickable(true);
                            Toast.makeText(MainActivity3.this, "Choose one of your opponent cards", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            middlecard = player2Cards.get(3);
                            player2Cards.set(3, currentCard);
                            setImage(middlecard, MiddleJackpot);
                            MiddleJackpot.setVisibility(View.VISIBLE);
                            RightJackpot.setVisibility(View.INVISIBLE);
                            if(takeTwoguest == true){
                                textTurn.setText("Take one more card");
                                Jackpot.setClickable(true);
                                takeTwoguest = false;
                            }
                            else{
                                textTurn.setText("Opponents turn");
                                textTurn.setTextColor(Color.BLACK);
                                declareWin.setClickable(false);
                                declareWin.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                            }
                        }
                    }
                    if(dataSnapshot.getValue(String.class).contains("guest Press on card5")){
                        if(swap1 == true){
                            setImage(player1Cards.get(0), card1);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card1);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(0);
                            player1Cards.set(0, player2Cards.get(0));
                            player2Cards.set(0, tmp);
                            swap1 = false;
                        }
                        else if(swap2 == true){
                            setImage(player1Cards.get(0), card2);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card2);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(1);
                            player1Cards.set(1, player2Cards.get(0));
                            player2Cards.set(0, tmp);
                            swap2 = false;
                        }
                        else if(swap3 == true){
                            setImage(player1Cards.get(0), card3);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card3);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(2);
                            player1Cards.set(2, player2Cards.get(0));
                            player2Cards.set(0, tmp);
                            swap3 = false;
                        }
                        else{
                            setImage(player1Cards.get(0), card4);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card4);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(3);
                            player1Cards.set(3, player2Cards.get(0));
                            player2Cards.set(0, tmp);
                            swap4 = false;
                        }
                        setImage(10, MiddleJackpot);
                        Jackpot.setClickable(true);
                        textTurn.setText("Opponents turn");
                        textTurn.setTextColor(Color.BLACK);
                        declareWin.setClickable(false);
                        declareWin.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                    }
                    if(dataSnapshot.getValue(String.class).contains("guest Press on card6")){
                        card5.setClickable(false);
                        card6.setClickable(false);
                        card7.setClickable(false);
                        card8.setClickable(false);
                        if(swap1 == true){
                            setImage(player1Cards.get(1), card1);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card1);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(0);
                            player1Cards.set(0, player2Cards.get(1));
                            player2Cards.set(1, tmp);
                            swap1 = false;
                        }
                        else if(swap2 == true){
                            setImage(player1Cards.get(1), card2);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card2);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(1);
                            player1Cards.set(1, player2Cards.get(1));
                            player2Cards.set(1, tmp);
                            swap2 = false;
                        }
                        else if(swap3 == true){
                            setImage(player1Cards.get(1), card3);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card3);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(2);
                            player1Cards.set(2, player2Cards.get(1));
                            player2Cards.set(1, tmp);
                            swap3 = false;
                        }
                        else{
                            setImage(player1Cards.get(1), card4);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card4);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(3);
                            player1Cards.set(3, player2Cards.get(1));
                            player2Cards.set(1, tmp);
                            swap4 = false;
                        }
                        setImage(10, MiddleJackpot);
                        Jackpot.setClickable(true);
                        textTurn.setText("Opponents turn");
                        textTurn.setTextColor(Color.BLACK);
                        declareWin.setClickable(false);
                        declareWin.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                    }
                    if(dataSnapshot.getValue(String.class).contains("guest Press on card7")){
                        card5.setClickable(false);
                        card6.setClickable(false);
                        card7.setClickable(false);
                        card8.setClickable(false);
                        if(swap1 == true){
                            setImage(player1Cards.get(2), card1);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card1);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(0);
                            player1Cards.set(0, player2Cards.get(2));
                            player2Cards.set(2, tmp);
                            swap1 = false;
                        }
                        else if(swap2 == true){
                            setImage(player1Cards.get(2), card2);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card2);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(1);
                            player1Cards.set(1, player2Cards.get(2));
                            player2Cards.set(2, tmp);
                            swap2 = false;
                        }
                        else if(swap3 == true){
                            setImage(player1Cards.get(2), card3);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card3);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(2);
                            player1Cards.set(2, player2Cards.get(2));
                            player2Cards.set(2, tmp);
                            swap3 = false;
                        }
                        else{
                            setImage(player1Cards.get(2), card4);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card4);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(3);
                            player1Cards.set(3, player2Cards.get(2));
                            player2Cards.set(2, tmp);
                            swap4 = false;
                        }
                        textTurn.setText("Opponents turn");
                        textTurn.setTextColor(Color.BLACK);
                        setImage(10, MiddleJackpot);
                        declareWin.setClickable(false);
                        declareWin.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                        Jackpot.setClickable(true);
                    }
                    if(dataSnapshot.getValue(String.class).contains("guest Press on card8")){
                        card5.setClickable(false);
                        card6.setClickable(false);
                        card7.setClickable(false);
                        card8.setClickable(false);
                        if(swap1 == true){
                            setImage(player1Cards.get(3), card1);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card1);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(0);
                            player1Cards.set(0, player2Cards.get(3));
                            player2Cards.set(3, tmp);
                            swap1 = false;
                        }
                        else if(swap2 == true){
                            setImage(player1Cards.get(3), card2);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card2);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(1);
                            player1Cards.set(1, player2Cards.get(3));
                            player2Cards.set(3, tmp);
                            swap2 = false;
                        }
                        else if(swap3 == true){
                            setImage(player1Cards.get(3), card3);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card3);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(2);
                            player1Cards.set(2, player2Cards.get(3));
                            player2Cards.set(3, tmp);
                            swap3 = false;
                        }
                        else{
                            setImage(player1Cards.get(3), card4);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card4);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(3);
                            player1Cards.set(3, player2Cards.get(3));
                            player2Cards.set(3, tmp);
                            swap4 = false;
                        }
                        textTurn.setText("Opponents turn");
                        textTurn.setTextColor(Color.BLACK);
                        setImage(10, MiddleJackpot);
                        declareWin.setClickable(false);
                        declareWin.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                        Jackpot.setClickable(true);
                    }
                    if(dataSnapshot.getValue(String.class).contains("guest Press on MiddleJackpot")){
                        MiddleJackpot.setClickable(false);
                        Jackpot.setClickable(false);
                        currentCard = middlecard;
                        card1.setClickable(true);
                        card2.setClickable(true);
                        card3.setClickable(true);
                        card4.setClickable(true);
                        Toast.makeText(MainActivity3.this, "Choose one of your cards", Toast.LENGTH_SHORT).show();
                    }
                    if(dataSnapshot.getValue(String.class).contains("guest Press on declareWin")){
                        long sum1 = player1Cards.get(0) + player1Cards.get(1) + player1Cards.get(2) + player1Cards.get(3);
                        long sum2 = player2Cards.get(0) + player2Cards.get(1) + player2Cards.get(2) + player2Cards.get(3);
                        setImage(player2Cards.get(0), card1);
                        setImage(player2Cards.get(1), card2);
                        setImage(player2Cards.get(2), card3);
                        setImage(player2Cards.get(3), card4);
                        setImage(player1Cards.get(0), card5);
                        setImage(player1Cards.get(1), card6);
                        setImage(player1Cards.get(2), card7);
                        setImage(player1Cards.get(3), card8);
                        if(sum2 < sum1){
                            AlertDialog alertDialog = new AlertDialog.Builder(context)
//set title
                                    .setTitle("END GAME")
//set message
                                    .setMessage("You won!!!")
//set positive button
                                    .setPositiveButton("EXIT", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            //set what would happen when positive button is clicked
                                            startActivity(new Intent(getApplicationContext(), MainActivity2.class));

                                        }
                                    })
                                    .show();
                        }
                        else if(sum1 == sum2){
                            AlertDialog alertDialog = new AlertDialog.Builder(context)
//set title
                                    .setTitle("END GAME")
//set message
                                    .setMessage("It's a tie")
//set positive button
                                    .setPositiveButton("EXIT", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            //set what would happen when positive button is clicked
                                            startActivity(new Intent(getApplicationContext(), MainActivity2.class));
                                        }
                                    })
                                    .show();
                        }
                        else{
                            AlertDialog alertDialog = new AlertDialog.Builder(context)
//set title
                                    .setTitle("END GAME")
//set message
                                    .setMessage("You lose!!!")
//set positive button
                                    .setPositiveButton("EXIT", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            //set what would happen when positive button is clicked
                                            startActivity(new Intent(getApplicationContext(), MainActivity2.class));

                                        }
                                    })
                                    .show();
                        }
                    }
                    if(dataSnapshot.getValue(String.class).contains("host Press on Jackpot")){
                        try {
                            if(cards.get(jackpotIndex) == 11){
                                peekhost = true;
                            }
                            else if(cards.get(jackpotIndex) == 10){
                                swaphost = true;
                            }
                            else if(cards.get(jackpotIndex) == 12){
                                takeTwohost = true;
                            }
                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.out.println("Exception thrown  :" + e);
                        }
                        jackpotIndex++;
                    }//host press Jackpot
                    if(dataSnapshot.getValue(String.class).contains("host Press on card1")){
                        if(peekhost == true){
                            setImage(11, MiddleJackpot);
                            peekhost = false;
                            textTurn.setText("It's your turn");
                            textTurn.setTextColor(Color.RED);
                            declareWin.setClickable(true);
                            declareWin.setBackgroundTintList(getResources().getColorStateList(R.color.purple_700));
                            MiddleJackpot.setVisibility(View.VISIBLE);
                            MiddleJackpot.setClickable(true);
                            Jackpot.setClickable(true);
                        }
                        else if(swaphost == true){
                            swap1 = true;
                        }
                        else{
                            handleAnimation(card5);
                            middlecard = player1Cards.get(0);
                            player1Cards.set(0, currentCard);
                            setImage(middlecard, MiddleJackpot);
                            textTurn.setText("It's your turn");
                            declareWin.setClickable(true);
                            declareWin.setBackgroundTintList(getResources().getColorStateList(R.color.purple_700));
                            textTurn.setTextColor(Color.RED);
                            MiddleJackpot.setVisibility(View.VISIBLE);
                            MiddleJackpot.setClickable(true);
                            Jackpot.setClickable(true);
                        }

                    }
                    if(dataSnapshot.getValue(String.class).contains("host Press on card2")){
                        if(peekhost == true){
                            setImage(11, MiddleJackpot);
                            peekhost = false;
                            MiddleJackpot.setVisibility(View.VISIBLE);
                            MiddleJackpot.setClickable(true);
                            Jackpot.setClickable(true);
                            textTurn.setText("It's your turn");
                            declareWin.setClickable(true);
                            declareWin.setBackgroundTintList(getResources().getColorStateList(R.color.purple_700));
                            textTurn.setTextColor(Color.RED);
                        }
                        else if(swaphost == true){
                            swap2 = true;
                        }
                        else{
                            handleAnimation(card6);
                            middlecard = player1Cards.get(1);
                            player1Cards.set(1, currentCard);
                            setImage(middlecard, MiddleJackpot);
                            MiddleJackpot.setVisibility(View.VISIBLE);
                            MiddleJackpot.setClickable(true);
                            Jackpot.setClickable(true);
                            textTurn.setText("It's your turn");
                            declareWin.setClickable(true);
                            declareWin.setBackgroundTintList(getResources().getColorStateList(R.color.purple_700));
                            textTurn.setTextColor(Color.RED);
                        }

                    }
                    if(dataSnapshot.getValue(String.class).contains("host Press on card3")){
                        if(peekhost == true){
                            setImage(11, MiddleJackpot);
                            peekhost = false;
                            MiddleJackpot.setVisibility(View.VISIBLE);
                            MiddleJackpot.setClickable(true);
                            Jackpot.setClickable(true);
                            textTurn.setText("It's your turn");
                            declareWin.setClickable(true);
                            declareWin.setBackgroundTintList(getResources().getColorStateList(R.color.purple_700));
                            textTurn.setTextColor(Color.RED);
                        }
                        else if(swaphost == true){
                            swap3 = true;
                        }
                        else{
                            handleAnimation(card7);
                            middlecard = player1Cards.get(2);
                            player1Cards.set(2, currentCard);
                            setImage(middlecard, MiddleJackpot);
                            MiddleJackpot.setVisibility(View.VISIBLE);
                            MiddleJackpot.setClickable(true);
                            Jackpot.setClickable(true);
                            textTurn.setText("It's your turn");
                            declareWin.setClickable(true);
                            declareWin.setBackgroundTintList(getResources().getColorStateList(R.color.purple_700));
                            textTurn.setTextColor(Color.RED);
                        }

                    }
                    if(dataSnapshot.getValue(String.class).contains("host Press on card4")){
                        if(peekhost == true){
                            setImage(11, MiddleJackpot);
                            peekhost = false;
                            textTurn.setText("It's your turn");
                            declareWin.setClickable(true);
                            declareWin.setBackgroundTintList(getResources().getColorStateList(R.color.purple_700));
                            textTurn.setTextColor(Color.RED);
                            MiddleJackpot.setVisibility(View.VISIBLE);
                            MiddleJackpot.setClickable(true);
                            Jackpot.setClickable(true);
                        }
                        else if(swaphost == true){
                            swap4 = true;
                        }
                        else{
                            handleAnimation(card8);
                            middlecard = player1Cards.get(3);
                            player1Cards.set(3, currentCard);
                            setImage(middlecard, MiddleJackpot);
                            MiddleJackpot.setVisibility(View.VISIBLE);
                            MiddleJackpot.setClickable(true);
                            Jackpot.setClickable(true);
                            textTurn.setText("It's your turn");
                            declareWin.setClickable(true);
                            declareWin.setBackgroundTintList(getResources().getColorStateList(R.color.purple_700));
                            textTurn.setTextColor(Color.RED);
                        }

                    }
                    if(dataSnapshot.getValue(String.class).contains("host Press on card5")){
                        card5.setClickable(false);
                        card6.setClickable(false);
                        card7.setClickable(false);
                        card8.setClickable(false);
                        if(swap1 == true){
                            setImage(player1Cards.get(0), card1);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card1);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(0);
                            player1Cards.set(0, player2Cards.get(0));
                            player2Cards.set(0, tmp);
                            swap1 = false;
                        }
                        else if(swap2 == true){
                            setImage(player1Cards.get(0), card2);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card2);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(1);
                            player1Cards.set(1, player2Cards.get(0));
                            player2Cards.set(0, tmp);
                            swap2 = false;
                        }
                        else if(swap3 == true){
                            setImage(player1Cards.get(0), card3);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card3);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(2);
                            player1Cards.set(2, player2Cards.get(0));
                            player2Cards.set(0, tmp);
                            swap3 = false;
                        }
                        else{
                            setImage(player1Cards.get(0), card4);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card4);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(3);
                            player1Cards.set(3, player2Cards.get(0));
                            player2Cards.set(0, tmp);
                            swap4 = false;
                        }
                        setImage(10, MiddleJackpot);
                        Jackpot.setClickable(true);
                        textTurn.setText("It's your turn");
                        declareWin.setClickable(true);
                        declareWin.setBackgroundTintList(getResources().getColorStateList(R.color.purple_700));
                        textTurn.setTextColor(Color.RED);
                    }
                    if(dataSnapshot.getValue(String.class).contains("host Press on card6")){
                        card5.setClickable(false);
                        card6.setClickable(false);
                        card7.setClickable(false);
                        card8.setClickable(false);
                        if(swap1 == true){
                            setImage(player1Cards.get(1), card1);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card1);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(0);
                            player1Cards.set(0, player2Cards.get(1));
                            player2Cards.set(1, tmp);
                            swap1 = false;
                        }
                        else if(swap2 == true){
                            setImage(player1Cards.get(1), card2);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card2);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(1);
                            player1Cards.set(1, player2Cards.get(1));
                            player2Cards.set(1, tmp);
                            swap2 = false;
                        }
                        else if(swap3 == true){
                            setImage(player1Cards.get(1), card3);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card3);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(2);
                            player1Cards.set(2, player2Cards.get(1));
                            player2Cards.set(1, tmp);
                            swap3 = false;
                        }
                        else{
                            setImage(player1Cards.get(1), card4);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card4);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(3);
                            player1Cards.set(3, player2Cards.get(1));
                            player2Cards.set(1, tmp);
                            swap4 = false;
                        }
                        setImage(10, MiddleJackpot);
                        Jackpot.setClickable(true);
                        textTurn.setText("It's your turn");
                        textTurn.setTextColor(Color.RED);
                        declareWin.setClickable(true);
                        declareWin.setBackgroundTintList(getResources().getColorStateList(R.color.purple_700));
                    }
                    if(dataSnapshot.getValue(String.class).contains("host Press on card7")){
                        card5.setClickable(false);
                        card6.setClickable(false);
                        card7.setClickable(false);
                        card8.setClickable(false);
                        if(swap1 == true){
                            setImage(player1Cards.get(2), card1);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card1);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(0);
                            player1Cards.set(0, player2Cards.get(2));
                            player2Cards.set(2, tmp);
                            swap1 = false;
                        }
                        else if(swap2 == true){
                            setImage(player1Cards.get(2), card2);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card2);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(1);
                            player1Cards.set(1, player2Cards.get(2));
                            player2Cards.set(2, tmp);
                            swap2 = false;
                        }
                        else if(swap3 == true){
                            setImage(player1Cards.get(2), card3);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card3);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(2);
                            player1Cards.set(2, player2Cards.get(2));
                            player2Cards.set(2, tmp);
                            swap3 = false;
                        }
                        else{
                            setImage(player1Cards.get(2), card4);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card4);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(3);
                            player1Cards.set(3, player2Cards.get(2));
                            player2Cards.set(2, tmp);
                            swap4 = false;
                        }
                        textTurn.setText("It's your turn");
                        textTurn.setTextColor(Color.RED);
                        setImage(10, MiddleJackpot);
                        Jackpot.setClickable(true);
                        declareWin.setClickable(true);
                        declareWin.setBackgroundTintList(getResources().getColorStateList(R.color.purple_700));
                    }
                    if(dataSnapshot.getValue(String.class).contains("host Press on card8")){
                        card5.setClickable(false);
                        card6.setClickable(false);
                        card7.setClickable(false);
                        card8.setClickable(false);
                        if(swap1 == true){
                            setImage(player1Cards.get(3), card1);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card1);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(0);
                            player1Cards.set(0, player2Cards.get(3));
                            player2Cards.set(3, tmp);
                            swap1 = false;
                        }
                        else if(swap2 == true){
                            setImage(player1Cards.get(3), card2);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card2);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(1);
                            player1Cards.set(1, player2Cards.get(3));
                            player2Cards.set(3, tmp);
                            swap2 = false;
                        }
                        else if(swap3 == true){
                            setImage(player1Cards.get(3), card3);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card3);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(2);
                            player1Cards.set(2, player2Cards.get(3));
                            player2Cards.set(3, tmp);
                            swap3 = false;
                        }
                        else{
                            setImage(player1Cards.get(3), card4);
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    setImage(-1, card4);
                                }
                            }, 1000*5);
                            tmp = player1Cards.get(3);
                            player1Cards.set(3, player2Cards.get(3));
                            player2Cards.set(3, tmp);
                            swap4 = false;
                        }
                        textTurn.setText("It's your turn");
                        textTurn.setTextColor(Color.RED);
                        setImage(10, MiddleJackpot);
                        Jackpot.setClickable(true);
                        declareWin.setClickable(true);
                        declareWin.setBackgroundTintList(getResources().getColorStateList(R.color.purple_700));
                    }
                    if(dataSnapshot.getValue(String.class).contains("host Press on drop")){
                        MiddleJackpot.setVisibility(View.VISIBLE);
                        MiddleJackpot.setClickable(true);
                        Jackpot.setClickable(true);
                        try {
                            currentCard = cards.get(jackpotIndex-1);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.out.println("Exception thrown  :" + e);
                        }
                        middlecard = currentCard;
                        setImage(middlecard, MiddleJackpot);
                        MiddleJackpot.setVisibility(View.VISIBLE);
                        RightJackpot.setVisibility(View.INVISIBLE);
                        textTurn.setText("It's your turn");
                        textTurn.setTextColor(Color.RED);
                        declareWin.setClickable(true);
                        declareWin.setBackgroundTintList(getResources().getColorStateList(R.color.purple_700));
                    }
                    if(dataSnapshot.getValue(String.class).contains("host Press on declareWin")){
                        long sum1 = player1Cards.get(0) + player1Cards.get(1) + player1Cards.get(2) + player1Cards.get(3);
                        long sum2 = player2Cards.get(0) + player2Cards.get(1) + player2Cards.get(2) + player2Cards.get(3);
                        setImage(player2Cards.get(0), card1);
                        setImage(player2Cards.get(1), card2);
                        setImage(player2Cards.get(2), card3);
                        setImage(player2Cards.get(3), card4);
                        setImage(player1Cards.get(0), card5);
                        setImage(player1Cards.get(1), card6);
                        setImage(player1Cards.get(2), card7);
                        setImage(player1Cards.get(3), card8);
                        if(sum2 < sum1){
                            AlertDialog alertDialog = new AlertDialog.Builder(context)
//set title
                                    .setTitle("END GAME")
//set message
                                    .setMessage("You won!!!")
//set positive button
                                    .setPositiveButton("EXIT", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            //set what would happen when positive button is clicked
                                            startActivity(new Intent(getApplicationContext(), MainActivity2.class));

                                        }
                                    })
                                    .show();
                        }
                        else if(sum1 == sum2){
                            AlertDialog alertDialog = new AlertDialog.Builder(context)
//set title
                                    .setTitle("END GAME")
//set message
                                    .setMessage("It's a tie")
//set positive button
                                    .setPositiveButton("EXIT", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            //set what would happen when positive button is clicked
                                            startActivity(new Intent(getApplicationContext(), MainActivity2.class));

                                        }
                                    })
                                    .show();
                        }
                        else{
                            AlertDialog alertDialog = new AlertDialog.Builder(context)
//set title
                                    .setTitle("END GAME")
//set message
                                    .setMessage("You lose!!!")
//set positive button
                                    .setPositiveButton("EXIT", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            //set what would happen when positive button is clicked
                                            startActivity(new Intent(getApplicationContext(), MainActivity2.class));

                                        }
                                    })
                                    .show();
                        }
                    }
                    if(dataSnapshot.getValue(String.class).contains("host Press on take")){
                        try {
                            currentCard = cards.get(jackpotIndex-1);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.out.println("Exception thrown  :" + e);
                        }

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                messageRef.setValue(message);
            }
        });
    }


    @Override
    //set message for each click on button
    public void onClick(View v) {
        if (v==Start) {
            message = role + " Starting the game";
            messageRef.setValue(message);
        }

        if(v==Jackpot){
            message = role + " Press on Jackpot";
            messageRef.setValue(message);
        }

        if(v==drop){
            message = role + " Press on drop";
            messageRef.setValue(message);
        }

        if(v==take){
            message = role + " Press on take";
            messageRef.setValue(message);
        }


        if(v==card1){
            message = role + " Press on card1";
            messageRef.setValue(message);
        }

        if(v==card2){
            message = role + " Press on card2";
            messageRef.setValue(message);
        }

        if(v==card3){
            message = role + " Press on card3";
            messageRef.setValue(message);
        }

        if(v==card4){
            message = role + " Press on card4";
            messageRef.setValue(message);
        }

        if(v==card5){
            message = role + " Press on card5";
            messageRef.setValue(message);
        }

        if(v==card6){
            message = role + " Press on card6";
            messageRef.setValue(message);
        }

        if(v==card7){
            message = role + " Press on card7";
            messageRef.setValue(message);
        }

        if(v==card8){
            message = role + " Press on card8";
            messageRef.setValue(message);
        }

        if(v==MiddleJackpot){
            message = role + " Press on MiddleJackpot";
            messageRef.setValue(message);
        }

        if(v==declareWin){
            message = role + " Press on declareWin";
            messageRef.setValue(message);
        }
    }

    //set card images
    private void setImage(long card, ImageView image){
        switch ((int)card){
            case -1:
                image.setImageResource(R.drawable.untitled);
                break;
            case 0:
                image.setImageResource(R.drawable.card0);
                break;
            case 1:
                image.setImageResource(R.drawable.card1);
                break;
            case 2:
                image.setImageResource(R.drawable.card2);
                break;
            case 3:
                image.setImageResource(R.drawable.card3);
                break;
            case 4:
                image.setImageResource(R.drawable.card4);
                break;
            case 5:
                image.setImageResource(R.drawable.card5);
                break;
            case 6:
                image.setImageResource(R.drawable.card6);
                break;
            case 7:
                image.setImageResource(R.drawable.card7);
                break;
            case 8:
                image.setImageResource(R.drawable.card8);
                break;
            case 9:
                image.setImageResource(R.drawable.card9);
                break;
            case 10:
                image.setImageResource(R.drawable.card10);
                break;
            case 11:
                image.setImageResource(R.drawable.card11);
                break;
            case 12:
                image.setImageResource(R.drawable.card12);
                break;
        }
    }
    //set animation for photos
    public void handleAnimation(ImageView image){
        ObjectAnimator rotateAnimation = ObjectAnimator.ofFloat(image, "rotation", 0f, 360f);
        rotateAnimation.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(rotateAnimation);
        animatorSet.start();
    }
}