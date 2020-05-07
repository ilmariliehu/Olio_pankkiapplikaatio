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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

public class AdminUser extends AppCompatActivity {

    String usrname;

    EditText newName;
    EditText newAddrs;
    EditText newEmail;
    EditText newPass;

    TextView oldName;
    TextView oldAddrs;
    TextView oldEmail;

    Button save;
    Button back;
    //get access to bank and database
    Bank bank = Bank.getInstance();
    final DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user);
        usrname = (String) getIntent().getSerializableExtra("usrname");

        save = findViewById(R.id.save);
        back = findViewById(R.id.back);

        oldName = findViewById(R.id.oldName);
        oldAddrs = findViewById(R.id.oldAdrs);
        oldEmail = findViewById(R.id.oldEmail);
        newPass = findViewById(R.id.newPass);

        newName = findViewById(R.id.newName);
        newAddrs = findViewById(R.id.newAdr);
        newEmail = findViewById(R.id.newEmail);

        //Prints user's old information
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
                                if (!newPass.getText().toString().equals("")) {
                                    String salt = null;
                                    try {
                                        salt = getSalt();
                                    } catch (NoSuchAlgorithmException e) {
                                        e.printStackTrace();
                                    } catch (NoSuchProviderException e) {
                                        e.printStackTrace();
                                    }
                                    String Pass = newPass.getText().toString();
                                    String securePass = getSecurePassword(Pass, salt);
                                    userSnapshot.getRef().child("password").setValue(securePass);
                                    userSnapshot.getRef().child("salt").setValue(salt);
                                }
                                Toast toast = Toast.makeText(AdminUser.this, "Tiedot päivitetty!", Toast.LENGTH_LONG);
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
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminUser.this, AdminMain.class);
                startActivity(intent);
            }
        });
    }

    private static String getSecurePassword(String passwordToHash, String  salt) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(salt.getBytes());

            System.out.println(salt);

            //Get the hash's bytes
            byte[] bytes = md.digest(passwordToHash.getBytes());
            //This bytes[] has bytes in decimal format
            //Convert it to hexadecimal format
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

    //Generate a random salt for each user
    private static String  getSalt() throws NoSuchAlgorithmException, NoSuchProviderException {
        String salt = null;

        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] bytes = new byte[16];
        //Get a random salt
        sr.nextBytes(bytes);

        StringBuilder sb = new StringBuilder();
        for(int i=0; i< bytes.length ;i++)
        {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        //Get salt in hex format
        salt = sb.toString();

        return salt;
    }
}
