package com.example.harkkaty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminMain extends AppCompatActivity {
    DatabaseReference userReff = FirebaseDatabase.getInstance().getReference().child("Users");
    DatabaseReference accReff = FirebaseDatabase.getInstance().getReference().child("Accounts");
    DatabaseReference transReff = FirebaseDatabase.getInstance().getReference().child("Transactions");
    DatabaseReference cardReff = FirebaseDatabase.getInstance().getReference().child("Cards");

    Spinner userSpin;
    String Chosenuser;
    Button deleteUsr;
    Button accUsr;
    Button chUsr;
    Button cardusr;
    Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        userSpin = findViewById(R.id.spinner10);
        deleteUsr = findViewById(R.id.button25);
        accUsr = findViewById(R.id.button26);
        chUsr = findViewById(R.id.button27);
        cardusr = findViewById(R.id.button28);
        logout = findViewById(R.id.button30);


        //Create spinner for users
        final List<String> users = new ArrayList<>();
        users.add("Valitse käyttäjä");
        userReff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                    String username1 = userSnapshot.child("username").getValue(String.class);
                    users.add(username1);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        ArrayAdapter<String> Users = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, users);

        Users.setDropDownViewResource(android.R.layout.simple_spinner_item);
        userSpin.setAdapter(Users);

        userSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).equals("Valitse käyttäjä")){
                }else{
                    Chosenuser = parent.getItemAtPosition(position).toString();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



        //Deletes user and all user's data what is in database
        deleteUsr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userReff.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                            String username1 = userSnapshot.child("username").getValue(String.class);
                            if (Chosenuser.equals(username1)){
                                accReff.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                                            String user = userSnapshot.child("user").getValue(String.class);
                                            final String accNbr = userSnapshot.child("accNumber").getValue(String.class);
                                            if (Chosenuser.equals(user)){
                                                transReff.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                                                            String accNbr2 = userSnapshot.child("aNmbr").getValue(String.class);
                                                            if (accNbr.equals(accNbr2)){
                                                                userSnapshot.getRef().removeValue();
                                                            }
                                                        }
                                                    }
                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    }
                                                });
                                                cardReff.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                                                            String accNbr3 = userSnapshot.child("accNbr").getValue(String.class);
                                                            if (accNbr.equals(accNbr3)){
                                                                userSnapshot.getRef().removeValue();
                                                            }
                                                        }
                                                    }
                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    }
                                                });

                                                userSnapshot.getRef().removeValue();
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                userSnapshot.getRef().removeValue();

                            }
                        }
                        Toast toast = Toast.makeText(AdminMain.this, "Käyttäjä poistettu onnistuneesti!", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                        toast.show();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        //Opens new activity where admin can change chosen user's account information
        chUsr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMain.this, AdminUser.class);
                intent.putExtra("usrname",Chosenuser);
                startActivity(intent);
            }
        });

        //Opens new activity where admin can change user's user information
        accUsr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMain.this, AdminAcc.class);
                intent.putExtra("usrname",Chosenuser);
                startActivity(intent);
            }
        });

        //Opens new activity where admin can change user's card information
        cardusr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMain.this, AdminCard.class);
                intent.putExtra("usrname",Chosenuser);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMain.this, Login.class);
                startActivity(intent);
            }
        });
    }
}
