package com.example.hanan.riyadhmetro.manageTrip;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.hanan.riyadhmetro.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import static com.example.hanan.riyadhmetro.manageTrip.TripListAdpater.TRIP_KEY_INTENT;

public class ViewTripActivity extends AppCompatActivity {


    private TextView mTextViewGate;
    private TextView mTextViewBookedSeats;
    private TextView mTextViewAvailableSeats;

    private TextView mTextViewLeavingPlace;
    private TextView mTextViewArrivingPlace;
    private TextView mTextViewTripCode;

    private TextView mTextViewTripDate;
    private TextView mTextViewArrivingTime;
    private TextView mTextViewLeavingTime;

    private ProgressDialog progressDialog;
    private String mTripCode ;
    private String mTripId;
    private FirebaseFirestore db ;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_trip);

        initElement();
        viewTrip();
    }

    /**/
    private void getTripId(){


        db.collection("Trip").whereEqualTo("Trip code", mTripCode).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        for (DocumentSnapshot document : task.getResult()) {

                            mTripId = document.getId();

                        }
                    }
                });

    }


    /**/
    private void initElement() {

        db = FirebaseFirestore.getInstance();

        mTextViewGate = findViewById(R.id.textViewGate);
        mTextViewBookedSeats = findViewById(R.id.textViewBookedSeats);
        mTextViewAvailableSeats = findViewById(R.id.textViewAvailableSeats);

        mTextViewLeavingPlace = findViewById(R.id.textViewLeavingPlace);
        mTextViewArrivingPlace = findViewById(R.id.textViewArrivingPlace);
        mTextViewTripCode = findViewById(R.id.textViewTripCode);

        mTextViewTripDate = findViewById(R.id.tripDateText);
        mTextViewArrivingTime = findViewById(R.id.arrivingTimeText);
        mTextViewLeavingTime = findViewById(R.id.leavingTimetext);

        progressDialog = new ProgressDialog(this);
    }


    /**/
    private void viewTrip() {
        Intent intent = getIntent();
        HashMap<String, String> t = (HashMap<String, String>)intent.getSerializableExtra(TRIP_KEY_INTENT);
        Map<String, Object> trip = (Map)t;
        mTripCode = trip.get("Trip code").toString();
        getTripId();
        setData(trip);

    }



    /**/
    private void setData( Map<String, Object> trip){
        if(trip.size() >= 9 ) {

            mTextViewLeavingTime.setText(trip.get("Leaving time").toString());
            mTextViewArrivingTime.setText(trip.get("Arrival time").toString());
            mTextViewTripCode.setText(trip.get("Trip code").toString());

            mTextViewArrivingPlace.setText(trip.get("Arrival destination").toString());
            mTextViewLeavingPlace.setText(trip.get("Leaving destination").toString());
            mTextViewTripDate.setText(trip.get("Date").toString());

            mTextViewAvailableSeats.setText(trip.get("Available seats").toString());
            mTextViewBookedSeats.setText(trip.get("Booked seats").toString());
            mTextViewGate.setText(trip.get("Gate number").toString());

        }
        progressDialog.dismiss();


    }

}
