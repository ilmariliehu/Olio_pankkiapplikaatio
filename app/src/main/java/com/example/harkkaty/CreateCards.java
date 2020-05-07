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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CreateCards extends AppCompatActivity {

    String usrname;

    Spinner spinner;

    EditText payLimit;
    EditText drawLimit;

    Button save;
    Button back;
    Button cardChange;
    //get access to bank and database
    Bank bank = Bank.getInstance();
    DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Accounts");
    DatabaseReference cardReff = FirebaseDatabase.getInstance().getReference().child("Cards");

    private String AccChs;
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_cards);

        payLimit = findViewById(R.id.editText8);
        drawLimit = findViewById(R.id.editText9);

        spinner = findViewById(R.id.spinner2);

        save = findViewById(R.id.button5);
        back = findViewById(R.id.button4);
        cardChange = findViewById(R.id.button14);

        usrname = (String)getIntent().getSerializableExtra("usrname");

        //Checks if there is accounts
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                    String user = userSnapshot.child("user").getValue(String.class);

                    if (usrname.equals(user)){
                        count = count + 1;
                    }
                }
                if(count != 0){
                    bank.AccSpinner(usrname, spinner, CreateCards.this);

                }else{
                    Toast toast = Toast.makeText(CreateCards.this, "Sinulta ei ole yhtään tiliä!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                }
                /*user selects an account
                 *checks if it is possible to create
                 * Create a card to chosen account
                 */
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AccChs = bank.AccSpinner(usrname,spinner, CreateCards.this);
                        for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                            String acnbr = userSnapshot.child("accNumber").getValue(String.class);
                            String card = userSnapshot.child("card").getValue(String.class);
                            String acType = userSnapshot.child("accType").getValue(String.class);
                            if (acnbr.equals(AccChs)) {
                                if (acType.equals("Säästötili")) {
                                    Toast toast = Toast.makeText(CreateCards.this, "Säästötilille ei voi luoda korttia!", Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                    toast.show();
                                } else if (card.equals("true")) {
                                    Toast toast = Toast.makeText(CreateCards.this, "Tilillä on jo kortti!", Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                    toast.show();
                                } else {
                                    final double limitP = Double.parseDouble(payLimit.getText().toString());
                                    final double limitD = Double.parseDouble(drawLimit.getText().toString());

                                    userSnapshot.getRef().child("card").setValue("true");
                                    //Creating a new card
                                    cardReff.push().setValue(new Cards(limitP, limitD, "false", AccChs));


                                    Toast toast = Toast.makeText(CreateCards.this, "Kortti luotu!", Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                    toast.show();
                                }


                            }
                        }

                    }
                });

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("error");

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateCards.this, MainActivity.class);
                intent.putExtra("usrname",usrname);
                startActivity(intent);
            }
        });
        cardChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateCards.this, cardCh.class);
                intent.putExtra("usrname",usrname);
                startActivity(intent);
            }
        });
    }
}
