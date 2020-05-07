package com.example.harkkaty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminCard extends AppCompatActivity {
    //get access to bank and database
    Bank bank = Bank.getInstance();
    DatabaseReference cardReff = FirebaseDatabase.getInstance().getReference().child("Cards");
    DatabaseReference accReff = FirebaseDatabase.getInstance().getReference().child("Accounts");

    String usrname;

    TextView oldPay;
    TextView oldTake;
    TextView oldDead;

    EditText newPay;
    EditText newTake;

    Button save;
    Button back;
    Button show;
    Button delete;


    Switch switchDead;
    String chosenCard;

    Spinner spinnerCards;
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_card);

        oldPay = findViewById(R.id.oldPayL);
        oldTake = findViewById(R.id.oldTakeL);
        oldDead = findViewById(R.id.oldDead);

        newPay = findViewById(R.id.newPayL);
        newTake = findViewById(R.id.newTakeL);

        switchDead = findViewById(R.id.deadSwitch);

        save = findViewById(R.id.saveButton);
        back = findViewById(R.id.backButton);
        show = findViewById(R.id.showButton);
        delete = findViewById(R.id.deleteCard);

        spinnerCards = findViewById(R.id.spincard);

        usrname = (String)getIntent().getSerializableExtra("usrname");

        //Checks if user has any cards
        accReff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                    String user = userSnapshot.child("user").getValue(String.class);
                    String card = userSnapshot.child("card").getValue(String.class);
                    if (usrname.equals(user)){
                        if (card.equals("true")){
                            count = count + 1;
                        }
                    }
                }
                if(count != 0){
                    bank.cardSpinner(usrname, spinnerCards, AdminCard.this);
                }else{
                    Toast toast = Toast.makeText(AdminCard.this, "Käyttäjällä ei ole yhtään pankkikorttia!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("error");

            }
        });

        //Shows cards old information
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardReff.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        chosenCard = bank.cardSpinner(usrname, spinnerCards, AdminCard.this);
                        for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                            String acnbr = userSnapshot.child("accNbr").getValue(String.class);

                            System.out.println(acnbr);
                            System.out.println(chosenCard);

                            if (chosenCard.equals(acnbr)){
                                String PL = String.valueOf(userSnapshot.child("payLimit").getValue(double.class));
                                String TK = String.valueOf(userSnapshot.child("takeLimit").getValue(double.class));
                                String dead = userSnapshot.child("dead").getValue(String.class);
                                oldPay.setText("Kortin maksuraja: "+PL);
                                oldTake.setText("Kortin nostoraja: "+TK);
                                oldDead.setText("Kortti on kuolletettu: "+dead);

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

        });

        //Checking if there sis any modifications and if so -> update
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cardReff.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                            String acnbr = userSnapshot.child("accNbr").getValue(String.class);

                            if (chosenCard.equals(acnbr)) {
                                if (!newTake.getText().toString().equals("")) {
                                    double limitT = Double.parseDouble(newTake.getText().toString());
                                    userSnapshot.getRef().child("takeLimit").setValue(limitT);

                                    oldTake.setText("Kortin nostoraja: " + limitT);
                                }
                                if (!newPay.getText().toString().equals("")) {
                                    double limitP =  Double.parseDouble(newPay.getText().toString());
                                    userSnapshot.getRef().child("payLimit").setValue(limitP);

                                    oldPay.setText("Kortin maksuraja: "+limitP);
                                }
                                if (switchDead.isChecked()){
                                    userSnapshot.getRef().child("dead").setValue("true");
                                    oldDead.setText("true");
                                }else{
                                    userSnapshot.getRef().child("dead").setValue("false");
                                    oldDead.setText("false");
                                }
                                Toast toast = Toast.makeText(AdminCard.this, "Tiedot päivitetty!", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                toast.show();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
        //Deletes user's card
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardReff.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                            String accNbr3 = userSnapshot.child("accNbr").getValue(String.class);
                            if (chosenCard.equals(accNbr3)){
                                userSnapshot.getRef().removeValue();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
                Toast toast = Toast.makeText(AdminCard.this, "Kortti poistettu onnistuneesti!", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCard.this, AdminMain.class);
                startActivity(intent);
            }
        });


    }
}
