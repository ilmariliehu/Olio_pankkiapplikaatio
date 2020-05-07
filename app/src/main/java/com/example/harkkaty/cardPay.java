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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class cardPay extends AppCompatActivity {

    Spinner cardSpin;
    TextView payLimit;
    TextView takeLimit;

    EditText sum;

    Button show;
    Button pay;
    Button take;
    Button back;
    //get access to bank and database
    Bank bank = Bank.getInstance();
    DatabaseReference accReff = FirebaseDatabase.getInstance().getReference().child("Accounts");
    DatabaseReference transReff = FirebaseDatabase.getInstance().getReference().child("Transactions");
    DatabaseReference cardReff = FirebaseDatabase.getInstance().getReference().child("Cards");

    private String usrname;
    private int count;
    private String chosenCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_pay);

        cardSpin = findViewById(R.id.spinner7);

        payLimit = findViewById(R.id.textView18);
        takeLimit = findViewById(R.id.textView19);

        sum = findViewById(R.id.editText17);

        show = findViewById(R.id.button19);
        pay = findViewById(R.id.button20);
        take = findViewById(R.id.button21);
        back = findViewById(R.id.button22);

        usrname = (String)getIntent().getSerializableExtra("usrname");

        //Checks if the user has any accounts

        accReff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                    String user = userSnapshot.child("user").getValue(String.class);
                    String card = userSnapshot.child("card").getValue(String.class);
                    if (usrname.equals(user)){
                        if (card.equals("true")) {
                            count = count + 1;
                        }
                    }
                }
                if (count == 0) {
                    Toast toast = Toast.makeText(cardPay.this, "Sinulla ei ole tilejä!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                } else {
                    bank.cardSpinner(usrname, cardSpin, cardPay.this);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //shows cards old information when clicked

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chosenCard = bank.cardSpinner(usrname, cardSpin, cardPay.this);
                cardReff.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            String acnbr = userSnapshot.child("accNbr").getValue(String.class);
                            String PL= String.valueOf(userSnapshot.child("payLimit").getValue(double.class));
                            String TL = String.valueOf(userSnapshot.child("takeLimit").getValue(double.class));

                            if (chosenCard.equals(acnbr)) {
                                payLimit.setText("Kortin maksuraja on: "+PL);
                                takeLimit.setText("Kortin nostoraja on: "+TL);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        //Checks if payment is Ok and execute it
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardReff.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            String acnbr = userSnapshot.child("accNbr").getValue(String.class);
                            String dead = userSnapshot.child("dead").getValue(String.class);
                            double PL= userSnapshot.child("payLimit").getValue(double.class);
                            if (chosenCard.equals(acnbr)) {
                                if (dead.equals("false")){
                                    final double sumP =  Double.parseDouble(sum.getText().toString());

                                    if (sumP <= PL) {

                                        //sets new money
                                        accReff.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                                    double money= (userSnapshot.child("money").getValue(double.class));
                                                    String acnbr = userSnapshot.child("accNumber").getValue(String.class);
                                                    if (chosenCard.equals(acnbr)) {
                                                        if (sumP <= money){
                                                            double newM = money-sumP;
                                                            userSnapshot.getRef().child("money").setValue(newM);

                                                            //Adds an transaction
                                                            transReff.push().setValue(new transactions(acnbr,
                                                                    "Korttimaksu suoritettu, arvo: " + sumP));


                                                            Toast toast = Toast.makeText(cardPay.this,"Korttimaksu suoritettu onnistuneesti!", Toast.LENGTH_LONG);
                                                            toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                                            toast.show();

                                                        }else{
                                                            Toast toast = Toast.makeText(cardPay.this,"Tilillä ei ole tarpeeksi rahaa!", Toast.LENGTH_LONG);
                                                            toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                                            toast.show();
                                                        }

                                                    }
                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });


                                    }

                                    else {
                                        Toast toast = Toast.makeText(cardPay.this, "Maksua ei voitu suorittaa, maksuraja on: "
                                                + PL, Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                        toast.show();
                                    }
                                } else {

                                    Toast toast = Toast.makeText(cardPay.this,"Kortti on kuoletettu! Maksu ei onnistunut", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                    toast.show();

                                }

                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        //Checks if the withdraw is Ok and execute it
        take.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cardReff.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            String acnbr = userSnapshot.child("accNbr").getValue(String.class);
                            String dead = userSnapshot.child("dead").getValue(String.class);
                            double TL= userSnapshot.child("takeLimit").getValue(double.class);

                            if (chosenCard.equals(acnbr)) {
                                if (dead.equals("false")){
                                    final double sumT =  Double.parseDouble(sum.getText().toString());
                                    if (sumT <= TL) {

                                        //sets new money
                                        accReff.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                                    double money= (userSnapshot.child("money").getValue(double.class));
                                                    String acnbr = userSnapshot.child("accNumber").getValue(String.class);
                                                    if (chosenCard.equals(acnbr)) {
                                                        if ((sumT <= money)) {
                                                            double newM = money - sumT;
                                                            userSnapshot.getRef().child("money").setValue(newM);

                                                            //Adds an transaction
                                                            transReff.push().setValue(new transactions(acnbr,
                                                                    "Kortilta nostettiin rahaa, arvo: " + sumT));

                                                            Toast toast = Toast.makeText(cardPay.this, "Korttinosto suoritettu onnistuneesti!", Toast.LENGTH_LONG);
                                                            toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                                            toast.show();
                                                        }else{
                                                            Toast toast = Toast.makeText(cardPay.this,"Tilillä ei ole tarpeeksi rahaa!", Toast.LENGTH_LONG);
                                                            toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                                            toast.show();
                                                        }
                                                    }
                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                            }
                                        });

                                    } else {
                                        Toast toast = Toast.makeText(cardPay.this, "Nostoa ei voitu suorittaa, nostoraja on: "
                                                + TL, Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                        toast.show();
                                    }
                                }

                                else {

                                    Toast toast = Toast.makeText(cardPay.this,"Kortti on kuoletettu! Nosto ei onnistunut", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                    toast.show();

                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

        });



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(cardPay.this, MainActivity.class);
                intent.putExtra("usrname",usrname);
                startActivity(intent);
            }
        });
    }
}
