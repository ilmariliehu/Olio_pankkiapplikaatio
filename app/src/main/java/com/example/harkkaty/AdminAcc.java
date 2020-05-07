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

public class AdminAcc extends AppCompatActivity {
    String usrname;
    TextView accType;
    TextView accFro;

    Spinner spinnerAcc;
    Spinner spinnerType;

    Button save;
    Button back;
    Button show;
    Button delete;

    Switch isFrozen;
    int count = 0;

    private String AccChs;
    private String newTypeChs;
    //get access to bank and database
    Bank bank = Bank.getInstance();
    final DatabaseReference accReff = FirebaseDatabase.getInstance().getReference().child("Accounts");
    final DatabaseReference transReff = FirebaseDatabase.getInstance().getReference().child("Transactions");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_acc);

        usrname = (String)getIntent().getSerializableExtra("usrname");
        accType = findViewById(R.id.TypeText);
        accFro = findViewById(R.id.FrozenText);

        spinnerAcc = findViewById(R.id.accSpinner);
        spinnerType = findViewById(R.id.typeSpinner);

        save = findViewById(R.id.SaveButton);
        back = findViewById(R.id.BackButton);
        show = findViewById(R.id.ShowButton);
        delete = findViewById(R.id.button29);

        isFrozen = findViewById(R.id.FrozenSwitch);

        //Checks if there is accounts
        accReff.addListenerForSingleValueEvent(new ValueEventListener() {
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
                    bank.AccSpinner(usrname, spinnerAcc, AdminAcc.this);
                    bank.AccTypeSpin(spinnerType, AdminAcc.this);
                }else{
                    Toast toast = Toast.makeText(AdminAcc.this, "Käyttäjällä ei ole yhtään tiliä!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                }
                show.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AccChs = bank.AccSpinner(usrname,spinnerAcc, AdminAcc.this);
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
                //Deletes user's account and transactions from the acc
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        accReff.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                                    String accNbr = userSnapshot.child("accNumber").getValue(String.class);
                                    if (AccChs.equals(accNbr)){
                                        transReff.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                                                    String accNbr2 = userSnapshot.child("aNmbr").getValue(String.class);
                                                    if (AccChs.equals(accNbr2)){
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
                                Toast toast = Toast.makeText(AdminAcc.this, "Käyttäjän tili poistettu oonistuunesti!", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                toast.show();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });

                //Saves the new information and checks if user can make the change
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //AccChs = bank.AccSpinner(usrname,spinnerAcc, accChange.this);
                        newTypeChs = bank.AccTypeSpin(spinnerType, AdminAcc.this);
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
                                //
                                if (card.equals("true") && newTypeChs.equals("Säästötili")) {
                                    Toast toast = Toast.makeText(AdminAcc.this, "Tilillä on kortti, eikä voida vaihtaa säästötiliksi!", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                    toast.show();

                                }else{
                                    userSnapshot.getRef().child("accType").setValue(newTypeChs);
                                    accType.setText("Tilin tyyppi on: "+newTypeChs);
                                    Toast toast = Toast.makeText(AdminAcc.this, "Tallennus onnistui!", Toast.LENGTH_LONG);
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
                Intent intent = new Intent(AdminAcc.this, AdminMain.class);
                startActivity(intent);
            }
        });
    }
}
