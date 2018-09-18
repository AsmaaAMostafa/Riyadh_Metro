package com.example.hanan.riyadhmetro;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;

    /**/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();

    }


    /**/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    /**/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.singout:
                singout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**/
    private void singout() {

        mFirebaseAuth.signOut();
        //closing activity
        finish();
        Context context = MainActivity.this;
        Class logoClass = LogoActivity.class;

        //starting login activity
        startActivity(new Intent(context, logoClass));
    }

}
