package com.example.harkkaty;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Bank extends AppCompatActivity {

    private String ChosenAcc;
    private String accNumber;
    private String AccType;
    private String Numberacc;
    private int count = 0;

    private static Bank bank = new Bank();

    public static Bank getInstance() {
        return bank;
    }


    //Spinner for accounts
    public String  AccSpinner (final String username, Spinner spinner, Context context){

        //Adds users account from the database
        FirebaseApp.initializeApp(Bank.this);
        final DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Accounts");

        final List<String> accts = new ArrayList<>();
        accts.add("Valitse tili");

        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                    String username1 = userSnapshot.child("user").getValue(String.class);
                    String accNumber = userSnapshot.child("accNumber").getValue(String.class);

                    if (username.equals(username1)){
                        accts.add(accNumber);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        ArrayAdapter<String> accs = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, accts);

        accs.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(accs);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).equals("Valitse tili")){
                }else{
                    ChosenAcc = parent.getItemAtPosition(position).toString();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //Returns which account is been selected
        return ChosenAcc;
    }



    //Spinner for choosing account type
    public String AccTypeSpin(Spinner spinner, Context context){

        final List<String> accts = new ArrayList<>();
        accts.add("Valitse tilin tyyppi");
        accts.add("Normaalitili");
        accts.add("Säästötili");

        ArrayAdapter<String> accs = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, accts);
        accs.setDropDownViewResource(android.R.layout.simple_spinner_item);

        spinner.setAdapter(accs);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).equals("Valitse tilin tyyppi")){

                }else{
                    AccType = parent.getItemAtPosition(position).toString();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if(AccType == null){
            return "";
        }else{
            //Returns which type is chosen
            return AccType;
        }
    }

    //Spinner for cards
    public String cardSpinner (final String username, Spinner spin, Context context){
        final List<String> cards = new ArrayList<>();
        cards.add("Valitse kortin tili");

        //Gets users cards from database
        FirebaseApp.initializeApp(Bank.this);
        final DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Accounts");

        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                    String username1 = userSnapshot.child("user").getValue(String.class);
                    String card1 = userSnapshot.child("card").getValue(String.class);
                    String accNumber = userSnapshot.child("accNumber").getValue(String.class);

                    if (username.equals(username1)){
                        if (card1.equals("true")){
                            cards.add(accNumber);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        ArrayAdapter<String> Card = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, cards);
        Card.setDropDownViewResource(android.R.layout.simple_spinner_item);

        spin.setAdapter(Card);

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (parent.getItemAtPosition(position).equals("Valitse kortin tili")) {
                }
                else {

                    final String chosenCard = parent.getItemAtPosition(position).toString();

                    final DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Accounts");

                    mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                                count = count + 1;
                                accNumber = userSnapshot.child("accNumber").getValue(String.class);
                                if (chosenCard.equals(accNumber)){
                                   Numberacc = accNumber;
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return Numberacc;

    }


}

