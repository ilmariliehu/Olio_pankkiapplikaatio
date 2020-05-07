package com.example.harkkaty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class accChange extends AppCompatActivity {

    String usrname;

    TextView accType;
    TextView accFro;

    Spinner spinnerAcc;
    Spinner spinnerType;

    Button save;
    Button back;
    Button show;

    Switch isFrozen;
    private int count = 0;

    private String AccChs;
    private String newTypeChs;

    //get access to bank and database
    Bank bank = Bank.getInstance();
    final DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Accounts");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acc_change);

        accType = findViewById(R.id.textView12);
        accFro = findViewById(R.id.textView13);

        spinnerAcc = findViewById(R.id.spinner3);
        spinnerType = findViewById(R.id.spinner4);

        save = findViewById(R.id.button11);
        back = findViewById(R.id.button12);
        show = findViewById(R.id.button13);

        isFrozen = findViewById(R.id.switch2);


        usrname = (String)getIntent().getSerializableExtra("usrname");

        //Checks if there is accounts
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            String actype;
            String frozen;
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                    String user = userSnapshot.child("user").getValue(String.class);

                    if (usrname.equals(user)){
                        count = count + 1;
                    }
                }
                if(count != 0){
                    bank.AccSpinner(usrname, spinnerAcc, accChange.this);
                    bank.AccTypeSpin(spinnerType, accChange.this);
                }else{
                    Toast toast = Toast.makeText(accChange.this, "Sinulta ei ole yhtään tiliä!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                }
                show.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AccChs = bank.AccSpinner(usrname,spinnerAcc, accChange.this);
                        for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                            String acnbr = userSnapshot.child("accNumber").getValue(String.class);
                            if (AccChs.equals(acnbr)){
                                actype = userSnapshot.child("accType").getValue(String.class);
                                frozen = userSnapshot.child("frozen").getValue(String.class);

                                accType.setText("Tilin tyyppi on: "+actype);
                                accFro.setText("Tili on jäädytetty: "+ frozen);

                            }
                        }

                    }
                });
                //Saves the new information and checks if user can make the change
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //AccChs = bank.AccSpinner(usrname,spinnerAcc, accChange.this);
                        newTypeChs = bank.AccTypeSpin(spinnerType, accChange.this);
                        for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                            String acnbr = userSnapshot.child("accNumber").getValue(String.class);
                            String card = userSnapshot.child("card").getValue(String.class);
                            if (AccChs.equals(acnbr)){
                                if (isFrozen.isChecked()){
                                    userSnapshot.getRef().child("frozen").setValue("true");
                                    accFro.setText("Tili on jäädytetty: "+ "true");
                                }else{
                                    userSnapshot.getRef().child("frozen").setValue("false");
                                    accFro.setText("Tili on jäädytetty: "+ "false");
                                }
                                if (card.equals("true") && newTypeChs.equals("Säästötili")) {
                                    Toast toast = Toast.makeText(accChange.this, "Tilillä on kortti, eikä voida vaihtaa säästötiliksi!", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                    toast.show();

                                }else{
                                    userSnapshot.getRef().child("accType").setValue(newTypeChs);
                                    accType.setText("Tilin tyyppi on: "+newTypeChs);
                                    Toast toast = Toast.makeText(accChange.this, "Tallennus onnistui!", Toast.LENGTH_LONG);
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
                Intent intent = new Intent(accChange.this, CreateAcc.class);
                intent.putExtra("usrname",usrname);
                startActivity(intent);
            }
        });
    }
}
