package com.example.harkkaty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Settings extends AppCompatActivity {
    String usrname;

    EditText newName;
    EditText newAddrs;
    EditText newEmail;

    TextView oldName;
    TextView oldAddrs;
    TextView oldEmail;
    TextView username;

    Button save;
    Button back;
    Button passChange;

    // Access to bank class and database
    Bank bank = Bank.getInstance();
    final DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        usrname = (String) getIntent().getSerializableExtra("usrname");
        username = findViewById(R.id.TextView2);
        username.setText(usrname);

        save = findViewById(R.id.button);
        back = findViewById(R.id.button4);
        passChange = findViewById(R.id.button7);

        oldName = findViewById(R.id.TextView3);
        oldAddrs = findViewById(R.id.TextView4);
        oldEmail = findViewById(R.id.TextView5);

        newName = findViewById(R.id.editText6);
        newAddrs = findViewById(R.id.editText3);
        newEmail = findViewById(R.id.editText5);

        //Prints old information
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                    String user = userSnapshot.child("username").getValue(String.class);
                    String name = userSnapshot.child("name").getValue(String.class);
                    String adr = userSnapshot.child("address").getValue(String.class);
                    String email = userSnapshot.child("email").getValue(String.class);

                    if (usrname.equals(user)){
                        oldName.setText(name);
                        oldAddrs.setText(adr);
                        oldEmail.setText(email);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("error");

            }
        });

        //Checking if there sis any modifications and if so -> update
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {


                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            String user = userSnapshot.child("username").getValue(String.class);

                            if (usrname.equals(user)) {

                                if (!newName.getText().toString().equals("")) {
                                    userSnapshot.getRef().child("name").setValue(newName.getText().toString());
                                    oldName.setText(newName.getText().toString());
                                }
                                if (!newAddrs.getText().toString().equals("")) {
                                    userSnapshot.getRef().child("address").setValue(newAddrs.getText().toString());
                                    oldAddrs.setText(newAddrs.getText().toString());
                                }
                                if (!newEmail.getText().toString().equals("")) {
                                    userSnapshot.getRef().child("email").setValue(newEmail.getText().toString());
                                    oldEmail.setText(newEmail.getText().toString());
                                }
                                Toast toast = Toast.makeText(Settings.this, "Tiedot päivitetty!", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                toast.show();

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        System.out.println("error");
                    }
                });
            }
        });


        passChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.this, passChange.class);
                intent.putExtra("usrname",usrname);
                startActivity(intent);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.this, MainActivity.class);
                intent.putExtra("usrname",usrname);
                startActivity(intent);
            }
        });
    }
}
