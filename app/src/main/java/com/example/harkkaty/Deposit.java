package com.example.harkkaty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Deposit extends AppCompatActivity {

    TextView Acc;
    TextView oldBlc;

    EditText deposit;

    Button save;
    Button back;
    Button show;
    String usrname;

    Spinner spinner;

    private String acNbr;
    private int count;

    Bank bank = Bank.getInstance();
    DatabaseReference accReff = FirebaseDatabase.getInstance().getReference().child("Accounts");
    DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("Transactions");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit);

        Acc = findViewById(R.id.textView5);
        oldBlc = findViewById(R.id.textView7);
        deposit = findViewById(R.id.editText10);
        save = findViewById(R.id.button3);
        back = findViewById(R.id.button4);
        show = findViewById(R.id.button6);

        spinner = findViewById(R.id.spinner8);

        usrname = (String) getIntent().getSerializableExtra("usrname");


        //checks is user has any accounts
        accReff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String user = userSnapshot.child("user").getValue(String.class);
                    if (usrname.equals(user)) {
                        count = count + 1;
                    }
                }
                if (count == 0) {
                    Toast toast = Toast.makeText(Deposit.this, "Sinulla ei ole tilejä!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                } else {
                    bank.AccSpinner(usrname, spinner, Deposit.this);

                }
            }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accReff.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        acNbr = bank.AccSpinner(usrname, spinner, Deposit.this);
                        for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                            String acNbr2 = userSnapshot.child("accNumber").getValue(String.class);
                            double money = userSnapshot.child("money").getValue(double.class);
                            String acType = userSnapshot.child("accType").getValue(String.class);
                            if (acNbr.equals(acNbr2)){
                                oldBlc.setText(String.valueOf(money));
                                Acc.setText(acType);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        System.out.println("Error");
                    }
                });

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accReff.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                            String acNbr2 = userSnapshot.child("accNumber").getValue(String.class);
                            double money = userSnapshot.child("money").getValue(double.class);
                            String acType = userSnapshot.child("accType").getValue(String.class);
                            if (acNbr.equals(acNbr2)){
                                double nmbr = Double.parseDouble(deposit.getText().toString());
                                double nmbr2 = (nmbr + money);
                                userSnapshot.getRef().child("money").setValue(nmbr2);

                                oldBlc.setText(String.valueOf(nmbr2));

                                //Add an transaction
                                reff.push().setValue(new transactions(acNbr,"Tilille: "+acNbr+
                                        "\ntalletettiin rahaa: " + nmbr));

                                Toast toast = Toast.makeText(Deposit.this, "Talletus onnistui", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                toast.show();

                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        System.out.println("Error");
                    }
                });
            }
        });



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Deposit.this, MainActivity.class);
                intent.putExtra("usrname",usrname);
                startActivity(intent);
            }
        });
    }
}
