package com.example.hanan.riyadhmetro.manageTicket;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.hanan.riyadhmetro.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static com.example.hanan.riyadhmetro.manageTicket.TicketListAdpater.Ticket_KEY_INTENT;

public class ViewTicketActivity extends AppCompatActivity {

    private TextView mTextViewGate;
    private TextView mTextViewLeavingPlace;
    private TextView mTextViewArrivingPlace;
    private TextView mTextViewTripCode;

    private TextView mTextViewTripDate;
    private TextView mTextViewArrivingTime;
    private TextView mTextViewLeavingTime;

    private ProgressDialog progressDialog;

    private FirebaseFirestore db ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.ticket_view);
        initElement();
        viewTicket();
    }

    private void initElement() {

        db = FirebaseFirestore.getInstance();

        mTextViewGate = findViewById(R.id.textViewGate);
        mTextViewLeavingPlace = findViewById(R.id.textViewLeavingPlace);
        mTextViewArrivingPlace = findViewById(R.id.textViewArrivingPlace);
        mTextViewTripCode = findViewById(R.id.textViewTripCode);
        mTextViewTripDate = findViewById(R.id.dateText);
        mTextViewArrivingTime = findViewById(R.id.arrivingTimeText);
        mTextViewLeavingTime = findViewById(R.id.leavingTimetext);

        progressDialog = new ProgressDialog(this);
    }
    /**/
    private void viewTicket() {
        Intent intent = getIntent();
        HashMap<String, String> t = (HashMap<String, String>) intent.getSerializableExtra(Ticket_KEY_INTENT);

        Map<String, Object> ticket = (Map)t;
        setData(ticket);
    }
    /**/
    private void setData( Map<String, Object> tickets){

        if(tickets != null && tickets.size() != 0 ) {




            mTextViewLeavingTime.setText(tickets.get("Leaving time").toString());
            mTextViewArrivingTime.setText(tickets.get("Arrival time").toString());
            mTextViewTripCode.setText(tickets.get("Trip code").toString());

            mTextViewArrivingPlace.setText(tickets.get("Arrival destination").toString());
            mTextViewLeavingPlace.setText(tickets.get("Leaving destination").toString());
            mTextViewTripDate.setText(tickets.get("Date").toString());
            mTextViewGate.setText(tickets.get("Gate number").toString());
            // in herer i must put the photo

        }
        progressDialog.dismiss();


    }
}

