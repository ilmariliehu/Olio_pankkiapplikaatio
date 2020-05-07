package com.example.harkkaty;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import static com.example.harkkaty.passChange.isValidPassword;


public class Createusr extends AppCompatActivity {
    EditText username;
    EditText password;
    EditText Name;
    EditText addrs;
    EditText Email;

    TextView info;

    Button save;
    Button back;

    //get access to bank and database
    Bank bank = new Bank().getInstance();
    DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createusr);

        username = findViewById(R.id.editText);
        password = findViewById(R.id.editText2);
        Name = findViewById(R.id.editText6);
        addrs = findViewById(R.id.editText3);
        Email = findViewById(R.id.editText5);

        info = findViewById(R.id.textView31);

        save = findViewById(R.id.button);
        back = findViewById(R.id.button2);

        //Creating a new user and adding it to the database
        save.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                //checks if the password is strong enough
                if (password.getText().toString().length() < 12 && isValidPassword(password.getText().toString()) == false) {
                    info.setText("Salasanan täytyy sisältää vähintään yhden numeron, erikoismerkin, ison ja pienen kirjaimen ja sen on oltava vähintään 12 merkkiä pitkä.");
                } else {

                    /*
                     *Creating a random salt
                     *Prepend the salt to the password when hashing
                     *Store the salt and hash to the user's database
                     */
                    String salt = null;
                    try {
                        salt = getSalt();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (NoSuchProviderException e) {
                        e.printStackTrace();
                    }
                    String Pass = password.getText().toString();
                    String securePass = getSecurePassword(Pass, salt);

                    //Adding users to database
                    reff.push().setValue(new Users(username.getText().toString(), securePass, Name.getText().toString(), addrs.getText().toString(), Email.getText().toString(), salt));


                    Toast toast = Toast.makeText(Createusr.this, "Käyttäjä luotu", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Createusr.this, Login.class);
                startActivity(intent);
            }
        });

    }


    //Creates a hash
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
