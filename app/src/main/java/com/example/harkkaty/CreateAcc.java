package com.example.harkkaty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateAcc extends AppCompatActivity {

    String usrname;

    EditText accnbr;
    EditText money;

    Button save;
    Button back;
    Button accChange;

    // Access to bank class and database
    DatabaseReference accReff;
    Bank bank = Bank.getInstance();

    Spinner spintype;
    private String accTypeChosen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_acc);

        accnbr = findViewById(R.id.editText4);
        money = findViewById(R.id.editText7);
        spintype = findViewById(R.id.spinner);

        save = findViewById(R.id.button3);
        back = findViewById(R.id.button4);
        accChange = findViewById(R.id.button10);

        usrname = (String)getIntent().getSerializableExtra("usrname");

        bank.AccTypeSpin(spintype,this);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accTypeChosen = bank.AccTypeSpin(spintype,CreateAcc.this);


                double nmbr = Double.parseDouble(money.getText().toString());

                //Adding accounts to database
                accReff = FirebaseDatabase.getInstance().getReference().child("Accounts");
                accReff.push().setValue(new Accounts(usrname, accnbr.getText().toString(), accTypeChosen, nmbr, "false", "false"));


                Toast toast = Toast.makeText(CreateAcc.this, "Tili luotu", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateAcc.this, MainActivity.class);
                intent.putExtra("usrname",usrname);
                startActivity(intent);
            }
        });

        accChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateAcc.this, accChange.class);
                intent.putExtra("usrname",usrname);
                startActivity(intent);
            }
        });




    }
}
