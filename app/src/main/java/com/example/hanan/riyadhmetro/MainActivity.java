package com.example.hanan.riyadhmetro;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.hanan.riyadhmetro.manageMetro.MetroListViewActivity;
import com.example.hanan.riyadhmetro.manageTicket.TicketListViewActivity;
import com.example.hanan.riyadhmetro.manageTrip.TripListViewActivity;
import com.example.hanan.riyadhmetro.manageUser.ViewUserAccountActivity;
import com.example.hanan.riyadhmetro.mangeMetroMonitor.MetroMonitorListViewActivity;
import com.example.hanan.riyadhmetro.mangeMetroMonitor.ViewMetroMonitorActivity;
import com.example.hanan.riyadhmetro.utility.PreferencesUtility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import static com.example.hanan.riyadhmetro.mangeMetroMonitor.MetroMonitorListAdpater.ID_KEY_INTENT;
import static com.example.hanan.riyadhmetro.mangeMetroMonitor.MetroMonitorListAdpater.METRO_MONITOR_KEY_INTENT;

public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mToggle;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore db ;
    private Map<String, Object> mMetroMonitor;
    private String mId;
    private ProgressDialog progressDialog;
    private Map<String, Object> mUserAccount;





    /**/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initElement();
        setNav();


        if(PreferencesUtility.getAuthority(this) == PreferencesUtility.MONITOR_AUTHORITY )
        {
            getDataFromDatabase();

        } else if(PreferencesUtility.getAuthority(this) == PreferencesUtility.USER_AUTHORITY )
        {
            getDataFromDatabaseForUser();
        }
        displayAdminSingin();
    }

    /**/
    private void initElement(){
        db = FirebaseFirestore.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);


    }


    /**/
    private void setNav(){

        //Set drawer nav
        mDrawer =  findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(
                this, mDrawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToggle.setDrawerIndicatorEnabled(true);

        NavigationView navigationView = findViewById(R.id.nav_view);
        changeNavMenuForMonitor(navigationView);

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

    }


    /**/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(mToggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);



    }


    /**
     *
     */

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.view_trip) {

            startActivity(new Intent(this,TripListViewActivity.class));
        }  else if (id == R.id.sing_out){
            singout();
        } else if (id == R.id.view_metro_monitor){
            startActivity(new Intent(this,MetroMonitorListViewActivity.class));

        }else if (id == R.id.view_account_Info){
            GoToViewAcountInfo();

        }else if (id == R.id.view_metro){
            startActivity(new Intent(this,MetroListViewActivity.class));
        }else if (id == R.id.view_ticket){
            startActivity(new Intent(this,TicketListViewActivity.class));
        }if (id == R.id.view_account){//////???
            GoToViewUserAcountInfo();
        }

        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**/
    private void GoToViewAcountInfo(){

        if(PreferencesUtility.getAuthority(this) == PreferencesUtility.MONITOR_AUTHORITY ){

            Class  viewMetroMonitoClass = ViewMetroMonitorActivity.class;
            Intent intent = new Intent(MainActivity.this,viewMetroMonitoClass);
            if(mMetroMonitor != null){
                intent.putExtra(METRO_MONITOR_KEY_INTENT,(HashMap) mMetroMonitor );
                intent.putExtra(ID_KEY_INTENT,mId);
                startActivity(intent);
            }
        }

    }
    /**/
    private void GoToViewUserAcountInfo(){

        if(PreferencesUtility.getAuthority(this) == PreferencesUtility.USER_AUTHORITY ){

            Class  ViewUserAccountClass = ViewUserAccountActivity.class;
            Intent intent = new Intent(MainActivity.this,ViewUserAccountClass);
            if(mUserAccount != null){
                intent.putExtra("user",(HashMap) mUserAccount );
                intent.putExtra(ID_KEY_INTENT,mId);
                startActivity(intent);
            }
        }

    }


    private void getDataFromDatabaseForUser(){

        String email = mFirebaseAuth.getCurrentUser().getEmail();

        progressDialog.setMessage("Loading ...");
        progressDialog.show();
        db.collection("User").whereEqualTo("Email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.getResult() != null){

                    getDate1(task);
                } else {

                }

            }
        });

    }

    private  void getDate1( Task<QuerySnapshot> task) {


        for (DocumentSnapshot document : task.getResult()) {

            Map<String, Object> user = document.getData();
            mUserAccount = user;
            mId = document.getId();
            progressDialog.dismiss();


        }


    }



    /**/
    private void getDataFromDatabase(){

        String email = mFirebaseAuth.getCurrentUser().getEmail();

        progressDialog.setMessage("Loading ...");
        progressDialog.show();
        db.collection("Metro_Monitor").whereEqualTo("Email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.getResult() != null){

                    getDate(task);
                } else {

                }

            }
        });

    }


    private  void getDate( Task<QuerySnapshot> task) {


        for (DocumentSnapshot document : task.getResult()) {

            Map<String, Object> metroMonitor = document.getData();
            mMetroMonitor = metroMonitor;
            mId = document.getId();
            progressDialog.dismiss();


        }


    }

    /**/
    @Override
    public void onBackPressed() {
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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

    private int checkIfSinginType() {

        Intent intent = getIntent();

        return intent.getIntExtra(SigninActivity.TYPE_OF_SINGUP,-1);
    }


    /**/
    private void displayAdminSingin() {

        TextView textView = findViewById(R.id.testText);
      int singinType = checkIfSinginType();

        switch (singinType){
            case SigninActivity.USER_ID:
                textView.setText("USER");
                break;
            case SigninActivity.ADMIN_ID:
                textView.setText("ADMIN");
                break;
            case SigninActivity.METRO_ID:
                textView.setText("METRO");
                break;


        }
    }
    /**/
    private void changeNavMenuForMonitor(NavigationView navigationView ){

        if(PreferencesUtility.getAuthority(this) == PreferencesUtility.MONITOR_AUTHORITY )
            navigationView.inflateMenu(R.menu.activity_main_drawer_monitor);

        else if(PreferencesUtility.getAuthority(this) == PreferencesUtility.ADMIN_AUTHORITY )
            navigationView.inflateMenu(R.menu.activity_main_drawer_admin);

        else if(PreferencesUtility.getAuthority(this) == PreferencesUtility.USER_AUTHORITY )
            navigationView.inflateMenu(R.menu.activity_main_drawer_user);



    }


}
