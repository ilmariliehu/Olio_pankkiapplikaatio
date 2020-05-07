package com.example.harkkaty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.TestLooperManager;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class seeTrans extends AppCompatActivity {
    // Access to bank class and database
    Bank bank = Bank.getInstance();
    final DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Accounts");
    final DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("Transactions");

    Spinner accSpinner;
    TextView acc;
    TextView blc;
    TextView trans;

    Button back;
    Button show;

    String usrname;

    int count;
    String acnbr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_trans);

        accSpinner = findViewById(R.id.spinner9);

        acc = findViewById(R.id.textView25);
        blc = findViewById(R.id.textView23);
        trans = findViewById(R.id.textView26);

        back = findViewById(R.id.button23);
        show = findViewById(R.id.button24);

        usrname = (String) getIntent().getSerializableExtra("usrname");

        //Checks if the user has any accounts or transactions
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
                    Toast toast = Toast.makeText(seeTrans.this, "Sinulla ei ole tilejä!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                } else {
                    bank.AccSpinner(usrname, accSpinner, seeTrans.this);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acnbr = bank.AccSpinner(usrname, accSpinner, seeTrans.this);
                mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                            String userAC = userSnapshot.child("accNumber").getValue(String.class);
                            String money = String.valueOf(userSnapshot.child("money").getValue(double.class));
                            if (acnbr.equals(userAC)){

                                blc.setText(money);
                                acc.setText(userAC);

                                //will loop also transactions
                                reff.addListenerForSingleValueEvent(new ValueEventListener() {
                                    String allTrans = "*****TILITAPAHTUMAT*****\n\n";
                                    @Override
                                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                        for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                                            String aNmbr = userSnapshot.child("aNmbr").getValue(String.class);
                                            String note = userSnapshot.child("note").getValue(String.class);
                                            if (acnbr.equals(aNmbr)){
                                                //System.out.println("Tapahtuma " + note);
                                                allTrans = allTrans + note+ "\n\n************************\n\n";
                                            }
                                        }
                                        //using scrollingmethod because there might be many transactions
                                        trans.setMovementMethod(new ScrollingMovementMethod());
                                        trans.setText(allTrans);
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
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
                Intent intent = new Intent(seeTrans.this, MainActivity.class);
                intent.putExtra("usrname",usrname);
                startActivity(intent);
            }
        });
    }
}
