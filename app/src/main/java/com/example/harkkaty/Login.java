package com.example.harkkaty;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Login extends AppCompatActivity {

    EditText username;
    EditText password;

    TextView register;

    ProgressBar progressBar;

    Button login;
   // Button createusr;

    // Access to bank class and database
    //Bank bank = new Bank().getInstance();
    DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("Users");

    String rng;
    int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = findViewById(R.id.Button);
        //createusr = findViewById(R.id.Button2);

        register = findViewById(R.id.textView30);

        progressBar = findViewById(R.id.progressBar);

        username = findViewById(R.id.EditText);
        password = findViewById(R.id.EditText2);


        //Create new user
        register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(Login.this, Createusr.class);
                startActivity(intent);
            }
        });

        //If username, password and pin are correct -> user logs in
        login.setOnClickListener(new View.OnClickListener() {




            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                FirebaseApp.initializeApp(Login.this);

                final String username2 = username.getText().toString().trim();
                final String password2 = password.getText().toString().trim();

                //Checks if user is admin.
                if (username2.equals("Admin") & password2.equals("admin")){
                    progressBar.setVisibility(View.GONE);
                    count = count + 1;
                    Intent intent = new Intent(Login.this, AdminMain.class);
                    startActivity(intent);
                }else {
                    reff.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                String username1 = userSnapshot.child("username").getValue(String.class);
                                String password1 = userSnapshot.child("password").getValue(String.class);
                                String salt = userSnapshot.child("salt").getValue(String.class);

                                //Retrieve the user's salt and hash from the database.
                                String hash = validPass(password2, salt);

                                //Compare the hash of the given password with the hash from the database.
                                if (username2.equals(username1) & hash.equals(password1)) {
                                    progressBar.setVisibility(View.GONE);
                                    count = count + 1;
                                    //Creating random number for pin
                                    final Random randomnbr = new Random();
                                    rng = String.valueOf(randomnbr.nextInt(899999) + 100000);
                                    //Creating pop up to verify pin
                                    //Checks if the pin is correct
                                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(Login.this);
                                    alertDialog.setTitle("PIN vahvistus");
                                    alertDialog.setMessage(rng);

                                    final EditText input = new EditText(Login.this);
                                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.MATCH_PARENT);
                                    input.setLayoutParams(lp);
                                    alertDialog.setView(input);
                                    alertDialog.setIcon(R.drawable.ic_security_black_24dp);
                                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String PIN = input.getText().toString();
                                            if (PIN.equals(rng)) {

                                                Intent intent = new Intent(Login.this, MainActivity.class);
                                                String usrname = username.getText().toString();
                                                intent.putExtra("usrname", usrname);
                                                startActivity(intent);
                                            } else {
                                                Toast toast = Toast.makeText(Login.this, "PIN koodi väärin!", Toast.LENGTH_SHORT);
                                                toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                                toast.show();
                                            }
                                        }
                                    });
                                    alertDialog.setNegativeButton("Peruuta", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                                    alertDialog.show();
                                }

                            }
                            if (count == 0) {
                                progressBar.setVisibility(View.GONE);
                                Toast toast = Toast.makeText(Login.this, "Tarkista käyttäjänimi tai salasana!", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                toast.show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

            }

        });

    }

    //Prepend the salt to the given password and hash it using the same hash function.
    private static String validPass(String passwordToHash, String salt) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(salt.getBytes());
            //Get the hash's bytes
            byte[] bytes = md.digest(passwordToHash.getBytes());
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            //Get complete hashed password in hex format
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }


}

