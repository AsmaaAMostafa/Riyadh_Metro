package com.example.hanan.riyadhmetro.manageTrip;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.hanan.riyadhmetro.buyTicket.ViewTripAndBuyActivity;
import com.example.hanan.riyadhmetro.utility.PreferencesUtility;
import com.example.hanan.riyadhmetro.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.hanan.riyadhmetro.manageTrip.TripListAdpater.ID_KEY_INTENT;
import static com.example.hanan.riyadhmetro.manageTrip.TripListAdpater.TRIP_KEY_INTENT;

public class TripListViewActivity extends AppCompatActivity implements TripListAdpater.ListItemClickListener ,View.OnClickListener ,SearchView.OnQueryTextListener {


    private RecyclerView mTripList;
    private TripListAdpater mAdapter;
    private List<String> mIdList;
    private ProgressDialog progressDialog;
    private FloatingActionButton mFloatingActionButton;
    private FirebaseFirestore db ;
    private List<Map<String, Object>> mTrips;
    private static View emptyView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);

        initElement();
        viewRecyclerView();
        hideAddButtonForUser();

    }

    /**/
    private void initElement(){
        emptyView = findViewById(R.id.empty_view);

        mIdList = new ArrayList<>();
        mTrips = new ArrayList<Map<String,Object>>();
        progressDialog = new ProgressDialog(this);
        db = FirebaseFirestore.getInstance();
        mTripList = findViewById(R.id.rv);
        mFloatingActionButton = findViewById(R.id.addButton);

        mFloatingActionButton.setOnClickListener(this);
    }



    /**/
    public static View getEmptyView(){
        return emptyView;
    }


    /**/
    private void viewRecyclerView(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mTripList.setLayoutManager(layoutManager);
        mTripList.setHasFixedSize(true);
        getDataFromDatabase();




    }
    /**/
    private void initAdapter(){

        mAdapter = new TripListAdpater(mTrips,this,this,mIdList);
        mTripList.setAdapter(mAdapter);
        progressDialog.dismiss();
    }

    /**/
    private void getDataFromDatabase(){

        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        db.collection("Trip").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.getResult() != null){

                getDate(task);
                } else {

                    progressDialog.dismiss();
                    Toast.makeText(TripListViewActivity.this,"Error",Toast.LENGTH_LONG);
                }
                
            }
        });

    }

    private  void getDate( Task<QuerySnapshot> task) {

        boolean isEmpty = true;

        for (DocumentSnapshot document : task.getResult()) {

            Map<String, Object> trip = document.getData();
            mIdList.add(document.getId());
            mTrips.add(trip);

            if (trip.size() != 0)
                isEmpty = false;

        }

        if(!isEmpty ){


            emptyView.setVisibility(View.GONE);

            initAdapter();
        }else {

            progressDialog.dismiss();
            emptyView.setVisibility(View.VISIBLE);

        }
    }

    @Override
//    public void onListItemClick(int clickedItemIndex,List<Map<String, Object>> trips,List<String> idList) {
    public void onListItemClick(int clickedItemIndex,List<Map<String, Object>> trips) {

        HashMap<String, Object> trip =(HashMap) trips.get(clickedItemIndex);
//        String id = mIdList.get(clickedItemIndex);
        Context context = TripListViewActivity.this;
        Class tripClass;

//        Class t

        if(PreferencesUtility.getAuthority(this) == PreferencesUtility.MONITOR_AUTHORITY )
            tripClass = ViewTripActivity.class;
        else
            tripClass = ViewTripAndBuyActivity.class;

        Intent intent = new Intent(context,tripClass);
        intent.putExtra(TRIP_KEY_INTENT, trip);
//        intent.putExtra(ID_KEY_INTENT,id);

        startActivity(intent);
    }


    @Override
    public void onClick(View view) {

        if(view == mFloatingActionButton)
            goToAddTrip();
    }

    private void goToAddTrip() {

        Context context = TripListViewActivity.this;
        Class AddTripClass = AddTripActivity.class;
        Intent intent = new Intent(context,AddTripClass);
        startActivity(intent);
    }
    /**/
    @SuppressLint("RestrictedApi")
    public void hideAddButtonForUser(){


        if(PreferencesUtility.getAuthority(this) == PreferencesUtility.USER_AUTHORITY
                || PreferencesUtility.getAuthority(this) == PreferencesUtility.MONITOR_AUTHORITY ){
            mFloatingActionButton.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if ( TextUtils.isEmpty ( newText) ) {

            mAdapter.getFilter().filter("");
        } else {
            mAdapter.getFilter().filter(newText.toString());
        }


        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem m = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) m.getActionView();
        searchView.setOnQueryTextListener(TripListViewActivity.this);


        return true;
    }
}
