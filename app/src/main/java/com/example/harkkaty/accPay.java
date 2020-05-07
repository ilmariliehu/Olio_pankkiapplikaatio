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

public class accPay extends AppCompatActivity {
    TextView Acc;
    TextView oldBlc;

    EditText withdraw;
    EditText toAcc;

    Button save;
    Button back;
    Button show;

    Spinner accSpinner;

    private String usrname;

    private String chosenAcc;
    private int count = 0;
    //get access to bank and database
    Bank bank = Bank.getInstance();
    DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Accounts");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acc_pay);

        Acc = findViewById(R.id.textView5);
        oldBlc = findViewById(R.id.textView7);

        withdraw = findViewById(R.id.editText10);
        toAcc = findViewById(R.id.editText16);

        save = findViewById(R.id.button3);
        back = findViewById(R.id.button4);
        show = findViewById(R.id.button18);

        accSpinner = findViewById(R.id.spinner6);

        usrname = (String) getIntent().getSerializableExtra("usrname");

        //Checks if the user has any accounts
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                    String user = userSnapshot.child("user").getValue(String.class);
                    if (usrname.equals(user)){
                        count = count + 1;
                    }
                }
                if (count == 0) {
                    Toast toast = Toast.makeText(accPay.this, "Sinulla ei ole tilejä!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                } else {
                    bank.AccSpinner(usrname, accSpinner, accPay.this);

                }
                show.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chosenAcc = bank.AccSpinner(usrname, accSpinner, accPay.this);
                        for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                            String acnbr = userSnapshot.child("accNumber").getValue(String.class);
                            if (chosenAcc.equals(acnbr)){
                                String blc = String.valueOf(userSnapshot.child("money").getValue(double.class));
                                String act = userSnapshot.child("accType").getValue(String.class);
                                oldBlc.setText(blc);
                                Acc.setText(act);
                            }
                        }
                    }
                });

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //Checks if user can transfer money and saves it
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {

                            String acNbr = userSnapshot.child("accNumber").getValue(String.class);
                            double money2 = userSnapshot.child("money").getValue(double.class);
                            String frozen = userSnapshot.child("frozen").getValue(String.class);

                            double money = Double.parseDouble(withdraw.getText().toString());
                            if (chosenAcc.equals(acNbr)) {
                                if (frozen.equals("true")) {
                                    Toast toast = Toast.makeText(accPay.this, "Tili on jäädytetty ja siirtoja ei voi suorittaa!", Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                    toast.show();

                                } else {
                                    //Checks if user have enough money
                                    if (money > money2) {
                                        Toast toast = Toast.makeText(accPay.this, "Tilillä ei ole tarpeeksi rahaa!", Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                        toast.show();
                                    } else {
                                        double nbr = money2 - money;
                                        System.out.println(nbr);
                                        userSnapshot.getRef().child("money").setValue(nbr);
                                        String blc = String.valueOf(money2 - money);
                                        oldBlc.setText(blc);

                                        //checks if receiver is this banks user
                                        //and adds money to his/her acct and also adds to new transaction to this acct

                                        for (DataSnapshot userSnapshot1 : dataSnapshot.getChildren()) {
                                            String acNbrn2 = userSnapshot1.child("accNumber").getValue(String.class);
                                            double money3 = userSnapshot1.child("money").getValue(double.class);
                                            if (acNbrn2.equals(toAcc.getText().toString())) {
                                                double nmbr3 = (money3 + money);
                                                userSnapshot1.getRef().child("money").setValue(nmbr3);

                                                DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("Transactions");
                                                reff.push().setValue(new transactions(toAcc.getText().toString().trim(), "Tililtä: " +
                                                        acNbr + "\nVastaanotettu rahaa: " + money));

                                            }

                                        }
                                        //Add an transaction to user

                                        DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("Transactions");
                                        reff.push().setValue(new transactions(acNbr, "Tililtä: " +
                                                acNbr + "\nSiirretty rahaa: " + money));


                                        Toast toast = Toast.makeText(accPay.this, "Siirto onnistui", Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                        toast.show();

                                    }
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
                Intent intent = new Intent(accPay.this, MainActivity.class);
                intent.putExtra("usrname",usrname);
                startActivity(intent);
            }
        });

    }
}
