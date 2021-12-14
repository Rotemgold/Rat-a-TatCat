package com.example.catproject;
/*
Final project
Author 1 - Rotem Reshef, ID - 308577188
Author 2 - Dor Hazout, ID - 313560328
 */
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity2 extends AppCompatActivity implements View.OnClickListener {

    ListView listView;
    Button buttonE;
    ImageView information;
    List<String> roomsList;
    String playerName = "";
    String roomName = "";

    ArrayList<Integer> cards;
    int[] card = new int[54];
    FirebaseDatabase database;
    DatabaseReference roomRef;
    DatabaseReference roomsRef;
    DatabaseReference cardsRef;
    DatabaseReference messageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
//connect to DB
        database = FirebaseDatabase.getInstance();

        SharedPreferences preferences = getSharedPreferences("PREFS", 0);
        playerName = preferences.getString("playerName","");
        roomName = playerName;

        listView = findViewById(R.id.listView);
        buttonE = (Button) findViewById(R.id.buttonE);

        information = findViewById(R.id.imageView);
        information.setOnClickListener(this);
        information.setClickable(true);



        messageRef = database.getReference("rooms/" + roomName + "/message");

        roomsList = new ArrayList<>();
        makeCardArray();
        //While click button create new room with player name and enter the room
        buttonE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonE.setEnabled(false);
                roomName = playerName;
                roomRef = database.getReference("rooms/" + roomName + "/player1");
                cardsRef = database.getReference("rooms/" + roomName + "/cards");
                addRoomEventListener();
                roomRef.setValue(playerName);
                cardsRef.setValue(cards);
            }
        });
        //get game information
        information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity4.class);
                startActivity(intent);
            }
        });

        //while choose room from the list log into the room and start the game
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                roomName = roomsList.get(position);
                roomRef = database.getReference("rooms/" + roomName + "/player2");
                addRoomEventListener();
                roomRef.setValue(playerName);
            }
        });



        addRoomsEventListener();
    }
    private void addRoomEventListener(){
        //set room list
        roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Create new room' set room name and set start jackpot
                buttonE.setText("CREATE ROOM");
                buttonE.setEnabled(true);
                if(snapshot.getKey() != "null") {
                    System.out.println("here");
                    Intent intent = new Intent(getApplicationContext(), MainActivity3.class);
                    intent.putExtra("roomName", roomName);
                    intent.putExtra("cards", cards);
                    roomsList.remove(roomName);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                buttonE.setText("CREATE ROOM");
                buttonE.setEnabled(true);
                Toast.makeText(MainActivity2.this,"Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addRoomsEventListener(){
        roomsRef = database.getReference("rooms");
        roomsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                roomsList.clear();
                Iterable<DataSnapshot> rooms = datasnapshot.getChildren();
                for(DataSnapshot snapshot : rooms){
                    if(snapshot.getKey() != "null"){
                        roomsList.add(snapshot.getKey());
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity2.this, android.R.layout.simple_list_item_1, roomsList);
                        listView.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v==information) {
            Intent intent = new Intent(getApplicationContext(), MainActivity4.class);
            startActivity(intent);
        }
    }
    //create jackpot
    private ArrayList<Integer> makeCardArray(){

        cards = new ArrayList<>();
        cards.add(0);
        cards.add(0);
        cards.add(0);
        cards.add(0);
        cards.add(1);
        cards.add(1);
        cards.add(1);
        cards.add(1);
        cards.add(2);
        cards.add(2);
        cards.add(2);
        cards.add(2);
        cards.add(3);
        cards.add(3);
        cards.add(3);
        cards.add(3);
        cards.add(4);
        cards.add(4);
        cards.add(4);
        cards.add(4);
        cards.add(5);
        cards.add(5);
        cards.add(5);
        cards.add(5);
        cards.add(6);
        cards.add(6);
        cards.add(6);
        cards.add(6);
        cards.add(7);
        cards.add(7);
        cards.add(7);
        cards.add(7);
        cards.add(8);
        cards.add(8);
        cards.add(8);
        cards.add(8);
        cards.add(9);
        cards.add(9);
        cards.add(9);
        cards.add(9);
        cards.add(9);
        cards.add(9);
        cards.add(9);
        cards.add(9);
        cards.add(9);
        cards.add(10);
        cards.add(10);
        cards.add(10);
        cards.add(11);
        cards.add(11);
        cards.add(11);
        cards.add(12);
        cards.add(12);
        cards.add(12);
        Collections.shuffle(cards);
        return cards;
    }
}