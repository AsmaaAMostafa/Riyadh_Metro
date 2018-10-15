package com.example.hanan.riyadhmetro.buyTicket;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanan.riyadhmetro.R;
import com.example.hanan.riyadhmetro.manageTicket.ViewTicketActivity;
import com.example.hanan.riyadhmetro.utility.PreferencesUtility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.hanan.riyadhmetro.manageTicket.TicketListAdpater.Ticket_KEY_INTENT;
import static com.example.hanan.riyadhmetro.manageTrip.TripListAdpater.ID_KEY_INTENT;
import static com.example.hanan.riyadhmetro.manageTrip.TripListAdpater.TRIP_KEY_INTENT;

public class ViewTripAndBuyActivity extends AppCompatActivity implements View.OnClickListener {

    private int mInteger;
    private TextView mTextViewGate;
    private TextView mTextViewBookedSeats;
    private TextView mTextViewPrice;

    private TextView mTextViewLeavingPlace;
    private TextView mTextViewArrivingPlace;
    private TextView mTextViewTripCode;
    private TextView mTextViewAvailableSeats;
    private TextView mTextViewTripDate;
    private TextView mTextViewArrivingTime;
    private TextView mTextViewLeavingTime;
    private ProgressDialog progressDialog;
    private Button mBuyButton;
    private List<Map<String, Object>> mtickets; // to send it to asmaa with intent
    private FirebaseFirestore db ;
    private Map<String, Object> trip;
    private String mTripId;
    private FirebaseAuth mFirebaseAuth;
    private String mTripCode ;


    Map<String, Object> map = new HashMap<String,Object>();//asmaa with intent



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buy_ticket);

        initElement();
        getTripForIntent();
        getTripId();
        checkOfpayment();

//        viewTicket();
    }


    private void initElement() {
        mtickets = new ArrayList<Map<String, Object>>();
        db = FirebaseFirestore.getInstance();
        mInteger = 1;
        mTextViewGate = findViewById(R.id.textViewGate);
        mTextViewBookedSeats = findViewById(R.id.textViewBookedSeats);
        mTextViewAvailableSeats=findViewById(R.id.textViewAvailableSeats);
        mTextViewPrice = findViewById(R.id.price);

        mTextViewLeavingPlace = findViewById(R.id.textViewLeavingPlace);
        mTextViewArrivingPlace = findViewById(R.id.textViewArrivingPlace);
        mTextViewTripCode = findViewById(R.id.textViewTripCode);

        mTextViewTripDate = findViewById(R.id.tripDateText);
        mTextViewArrivingTime = findViewById(R.id.arrivingTimeText);
        mTextViewLeavingTime = findViewById(R.id.leavingTimetext);
        mBuyButton = findViewById(R.id.buyButton);

        mFirebaseAuth = FirebaseAuth.getInstance();

        mBuyButton.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");


    }
    /**/
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


    private void displayVeiwTrip( Map<String, Object> trip){
       // if(trip.size() == 9 ) {

            mTextViewLeavingTime.setText(trip.get("Leaving time").toString());
            mTextViewArrivingTime.setText(trip.get("Arrival time").toString());
            mTextViewTripCode.setText(trip.get("Trip code").toString());

            mTextViewArrivingPlace.setText(trip.get("Arrival destination").toString());
            mTextViewLeavingPlace.setText(trip.get("Leaving destination").toString());
            mTextViewTripDate.setText(trip.get("Date").toString());

            mTextViewAvailableSeats.setText(trip.get("Available seats").toString());
            mTextViewBookedSeats.setText(trip.get("Booked seats").toString());
            //mTextViewPrice.setText(trip.get("Price").toString());
            mTextViewGate.setText(trip.get("Gate number").toString());

      //  }
        progressDialog.dismiss();


    }

    public void increaseInteger(View view) {
        if(mInteger < 9)
        mInteger = mInteger + 1;
        mTextViewPrice.setText(String.valueOf(mInteger*20));
        display(mInteger);
    }

    public void decreaseInteger(View view) {
        if(mInteger > 0)
            mInteger = mInteger - 1;
        int decm = mInteger;
        mTextViewPrice.setText(String.valueOf(decm*20));
        display(mInteger);
    }

    private void display(int number) {
        TextView displayInteger = (TextView) findViewById(R.id.integer_number);
        displayInteger.setText(" " + number);
    }

    private  void getTripForIntent(){
        Intent intent = getIntent();
        HashMap<String, String> t = (HashMap<String, String>)intent.getSerializableExtra(TRIP_KEY_INTENT);
//        mTripId  = intent.getStringExtra(ID_KEY_INTENT);
        trip = (Map)t;
        mTripCode = trip.get("Trip code").toString();

        displayVeiwTrip(trip);

    }
    public void checkAvailableTickets(){

        int available_seats = Integer.parseInt(trip.get("Available seats").toString());

        if(available_seats >= mInteger  ){


            if(PreferencesUtility.getAuthority(this) == PreferencesUtility.USER_AUTHORITY )
                goToPaymenet();

            else if(PreferencesUtility.getAuthority(this) == PreferencesUtility.ADMIN_AUTHORITY ){

                updateSeats(mInteger);

            }


        } else {

            displayUnavailableSeats();
        }
    }

    private void updateSeats( int numberOfBookedSeats) {
        progressDialog.show();

        if(trip != null) {

            trip.put("Available seats", Integer.parseInt(trip.get("Available seats").toString()) - numberOfBookedSeats);
            trip.put("Booked seats", Integer.parseInt(trip.get("Booked seats").toString()) + numberOfBookedSeats);

            if(mTripId != null) {

                db.collection("Trip").document(mTripId).set(trip).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        buyTicketSuccefully();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        buyTicketUnsuccefully(e);
                    }
                });

            }

        }
    }

    /**/
    private void buyTicketUnsuccefully( Exception e){

        progressDialog.dismiss();

        String error = e.getMessage();
        Toast.makeText(ViewTripAndBuyActivity.this,"Error: "+error,Toast.LENGTH_SHORT).show();

    }

    /**/
    private void buyTicketSuccefully(){


        if(PreferencesUtility.getAuthority(this) == PreferencesUtility.ADMIN_AUTHORITY )
            goToViewTicket();


    }

    private void goToPaymenet() {


        Class buymenetClass = BuyClass.class;
        Context context = ViewTripAndBuyActivity.this;
        int price = Integer.parseInt(mTextViewPrice.getText().toString());
        Intent intent = new Intent(context,buymenetClass);
        intent.putExtra("price",price);
        intent.putExtra(TRIP_KEY_INTENT, (HashMap)trip);
        intent.putExtra(ID_KEY_INTENT,mTripId);
        intent.putExtra("mInteger",mInteger);
        startActivity(intent);
    }
    /**/
    private void checkOfpayment(){

        Intent intent = getIntent();
        boolean isPaymentCorrectly = intent.getBooleanExtra("isPaymentCorrectly",false);
        int mInteger = intent.getIntExtra("mInteger",0);
        mTripId = intent.getStringExtra(ID_KEY_INTENT);

        if(isPaymentCorrectly){
//            getTripId();
            progressDialog.show();
            getIdOfUSerForAddTicket(mInteger);
            updateSeats(mInteger);


        }else {
            display(1);
        }
    }

    /**/
    /**/
    private void getIdOfUSerForAddTicket(final int mInteger){



        String email = mFirebaseAuth.getCurrentUser().getEmail();

        String id = String.valueOf(db.collection("User").whereEqualTo("Email", email).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        // ff.collection

                        for (DocumentSnapshot document : task.getResult()) {

                            String id = document.getId();
                            getTicketFromFireBase(id,mInteger);
                        }
                    }
                }));
    }

    /**/
    public void getTicketFromFireBase(final String id, final int mInteger) {

        db.collection("User").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                getTicket(task,mInteger);
            }
        });

    }
    /**/
    private void getTicket(Task<DocumentSnapshot> task,int mInteger){


        ArrayList<Map<String, Object>> mTickets = new ArrayList();
        ArrayList<Map<String, Object>> tickets = new ArrayList();



        tickets = (ArrayList) task.getResult().getData().get("tickets");
        if(tickets != null)
            mTickets = tickets;

        String userId = task.getResult().getId();

        Map<String,Object> ticket = craeteTicket();

        for(int i = 0 ; i <  mInteger ; i++)
            mTickets.add((HashMap)ticket);

        addTicket(mTickets,userId);




    }
    /**/

    private Map<String,Object> craeteTicket() {

        Map<String,Object> ticket = new HashMap<>();


        ticket.put("Leaving time",trip.get("Leaving time"));
        ticket.put("Arrival time",trip.get("Arrival time"));
        ticket.put("Trip code",trip.get("Trip code"));
        ticket.put("Arrival destination",trip.get("Arrival destination"));
        ticket.put("Date",trip.get("Date"));
        ticket.put("Leaving destination",trip.get("Leaving destination"));
        ticket.put("Gate number",trip.get("Gate number"));


        return ticket;

    }

    /**/
    private void addTicket(ArrayList<Map<String, Object>> tickets,String userId) {


        db.collection("User").document(userId).update("tickets", tickets).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                goToViewTicket();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                buyTicketUnsuccefully(e);

            }
        });
    }

    private void goToViewTicket() {
        Intent intent = new Intent(this, ViewTicketActivity.class);// Send intent to asmaa
        intent.putExtra(Ticket_KEY_INTENT, (HashMap)trip);
        startActivity(intent);
        progressDialog.dismiss();

    }

    private void displayUnavailableSeats() {
        Toast.makeText(ViewTripAndBuyActivity.this,"There are not enough seats",Toast.LENGTH_SHORT);
    }

    @Override
    public void onClick(View view) {

        if(view == mBuyButton){
            checkAvailableTickets();

        }
    }
}
