package com.example.harkkaty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextWatcher;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class passChange extends AppCompatActivity {

    EditText oldPass;
    EditText newPass;
    EditText newPass2;

    TextView info;

    String usrname;

    Button save;
    Button back;

    // Access to bank class and database
    Bank bank = Bank.getInstance();
    final DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_change);

        oldPass = findViewById(R.id.editText11);
        newPass = findViewById(R.id.editText12);
        newPass2 = findViewById(R.id.editText13);

        info = findViewById(R.id.textView10);

        save = findViewById(R.id.button8);
        back = findViewById(R.id.button9);

        usrname = (String)getIntent().getSerializableExtra("usrname");

        //Gets the right user information
        //Retrieve the user's salt and hash from the database.
        //Compare the hash of the given password with the hash from the database.
        //Checks if the old password is correct and if the new passwords are correct
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                        for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                            String user = userSnapshot.child("username").getValue(String.class);
                            if (usrname.equals(user)) {

                                String pass = userSnapshot.child("password").getValue(String.class);
                                String salt = userSnapshot.child("salt").getValue(String.class);
                                System.out.println(pass);
                                String hash = getSecurePassword(oldPass.getText().toString(), salt);
                                System.out.println(hash);

                                if (hash.equals(pass)) {
                                    if (newPass.getText().toString().equals(newPass2.getText().toString())) {
                                        if (newPass.getText().toString().length() < 12 && isValidPassword(newPass.getText().toString()) == false) {

                                            info.setText("Salasanan täytyy sisältää vähintään yhden numeron, erikoismerkin, ison ja pienen kirjaimen ja sen on oltava vähintään 12 merkkiä pitkä.");
                                        } else {
                                            //Create new salt and hash the new password and store both to the database
                                            String newSalt = null;
                                            try {
                                                newSalt = getSalt();
                                            } catch (NoSuchAlgorithmException e) {
                                                e.printStackTrace();
                                            } catch (NoSuchProviderException e) {
                                                e.printStackTrace();
                                            }
                                            String Pass = newPass.getText().toString();
                                            String securePass = getSecurePassword(Pass, newSalt);

                                            userSnapshot.getRef().child("password").setValue(securePass);
                                            userSnapshot.getRef().child("salt").setValue(newSalt);

                                            Toast toast = Toast.makeText(passChange.this, "Salasana vaihdettu onnistuneesti!", Toast.LENGTH_SHORT);
                                            toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                            toast.show();
                                        }

                                    } else {
                                        Toast toast = Toast.makeText(passChange.this, "Uusi salasana ei täsmää!", Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                        toast.show();
                                    }

                                } else {
                                    Toast toast = Toast.makeText(passChange.this, "Vanha salasana väärin", Toast.LENGTH_SHORT);
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
                Intent intent = new Intent(passChange.this, Settings.class);
                intent.putExtra("usrname",usrname);
                startActivity(intent);
            }
        });
    }
    //Checks if the password contains a number, lower case, capital letter, special character,
    public static boolean isValidPassword(final String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Za-z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }


    //Prepend the salt to the given password and hash it using the same hash function.
    private static String getSecurePassword(String passwordToHash, String salt) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(salt.getBytes());
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

