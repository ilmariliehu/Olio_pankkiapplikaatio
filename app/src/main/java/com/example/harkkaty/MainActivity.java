package com.example.harkkaty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout dl;
    private ActionBarDrawerToggle abdt;
    TextView hellotext;
    TextView accts;
    TextView accText;


    String usrname;
    // Access to database
    final DatabaseReference accReff = FirebaseDatabase.getInstance().getReference().child("Accounts");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usrname = (String)getIntent().getSerializableExtra("usrname");

        hellotext = findViewById(R.id.textView);
        accts = findViewById(R.id.textView27);
        accText = findViewById(R.id.textView29);

        accReff.addListenerForSingleValueEvent(new ValueEventListener() {
            String allAcc = "";
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                    String user = userSnapshot.child("user").getValue(String.class);
                    String acc = userSnapshot.child("accNumber").getValue(String.class);
                    String money = String.valueOf(userSnapshot.child("money").getValue(double.class));
                    if (user.equals(usrname)){
                        allAcc = allAcc+"Tilinumero: " + acc +"  ;  Saldo: "+ money +"€"+"\n*******************\n";
                    }
                }
                accts.setMovementMethod(new ScrollingMovementMethod());
                accts.setText(allAcc);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        hellotext.setText("Tervetuloa "+usrname);
        accText.setText("*******TILIT******");




        dl = (DrawerLayout)findViewById(R.id.dl);
        abdt = new ActionBarDrawerToggle(this, dl,R.string.Open, R.string.Close);
        abdt.setDrawerIndicatorEnabled(true);

        dl.addDrawerListener(abdt);
        abdt.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final NavigationView nav_view = (NavigationView) findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {


            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();


                if(id == R.id.Settings){
                    Intent intent = new Intent(MainActivity.this, Settings.class);
                    intent.putExtra("usrname",usrname);
                    startActivity(intent);
                }

                if(id == R.id.Cards){
                    Intent intent = new Intent(MainActivity.this, CreateCards.class);
                    intent.putExtra("usrname",usrname);
                    startActivity(intent);
                }

                if(id == R.id.Accounts){
                    Intent intent = new Intent(MainActivity.this, CreateAcc.class);
                    intent.putExtra("usrname",usrname);
                    startActivity(intent);
                }

                if(id == R.id.deposit){
                    Intent intent = new Intent(MainActivity.this, Deposit.class);
                    intent.putExtra("usrname",usrname);
                    startActivity(intent);

                }

                if(id == R.id.withdrawl){
                    Intent intent = new Intent(MainActivity.this, accPay.class);
                    intent.putExtra("usrname",usrname);
                    startActivity(intent);

                }

                if(id == R.id.payCard){
                    Intent intent = new Intent(MainActivity.this, cardPay.class);
                    intent.putExtra("usrname",usrname);
                    startActivity(intent);
                }

                if(id == R.id.Transactions){
                    Intent intent = new Intent(MainActivity.this, seeTrans.class);
                    intent.putExtra("usrname",usrname);
                    startActivity(intent);
                }

                if(id == R.id.Logout){
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivity(intent);
                }

                return true;
            }
        });

    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.dl);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return abdt.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);

    }
}
