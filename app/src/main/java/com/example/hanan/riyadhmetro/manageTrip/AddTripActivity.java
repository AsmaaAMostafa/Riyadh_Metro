package com.example.hanan.riyadhmetro.manageTrip;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanan.riyadhmetro.utility.DateDialogUtility;
import com.example.hanan.riyadhmetro.R;
import com.example.hanan.riyadhmetro.utility.TimeDialogUtility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.example.hanan.riyadhmetro.assign.AssignedMonitorToMetroActivity.convertTime;

public class AddTripActivity  extends AppCompatActivity implements View.OnClickListener {

    static final private String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    final private Random rng = new SecureRandom();
    private static final long MINIMUM_TIME_MILLISECONDS = 900000;


    private EditText arrivalT,leavingT, GateNumber,date,Bseats,Aseats;
    private TextInputLayout arrivingLabel ;
    private Spinner arrivalD, leavingD;
    private Button Add;
    private FirebaseFirestore db;
    private String arrivalDe ;
    private String leavingDe ;
    private String arrivalTi ;
    private String GateNum;
    private String  dateStr ;
    private StringBuilder Tripcode ;
    private String leavingTi ;
    private Spinner metro;
    private ArrayList<String> mMetroIds;
    private Map<String,Object> mMetroSeats ;
    private ProgressDialog progressDialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);


        initElement();






    }
    /**/
    private void displaySpinner(Spinner spinner){

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.riyadh_stations, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

    }


    /**/
    private void showPickerDate() {

        date.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            public void onFocusChange(View view, boolean hasfocus){
                if(hasfocus){
                    DateDialogUtility dialog= new DateDialogUtility(view);
                    FragmentTransaction ft =getFragmentManager().beginTransaction();
                    dialog.show(ft, "DatePicker");

                }
            }

        });
    }
    /**/
    private void showPickerTime(EditText time) {

        TimeDialogUtility fromTime = new TimeDialogUtility(time, this);

    }


        /**/
    private void addTripUnsuccefully( Exception e){

        String error = e.getMessage();
        Toast.makeText(AddTripActivity.this,"Error: "+error,Toast.LENGTH_SHORT).show();

    }

    /**/
    private void addTripSuccefully(){

        Toast.makeText(AddTripActivity.this,"The Trip has been added succefully!",Toast.LENGTH_SHORT).show();

    }

    /**/
    private void AddTrip(){


            Map<String,String> trip = getDataFromInput();

            if(trip != null) {
                // ff.collection
                db.collection("Trip").add(trip)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                addTripSuccefully();
                                goToViewTrip();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                addTripUnsuccefully(e);
                            }
                        });
            }

    }
    /**/
    private void goToViewTrip() {

        Context context = AddTripActivity.this;
        Class viewTripsClass = TripListViewActivity.class;
        Intent intent = new Intent(context,viewTripsClass);
        startActivity(intent);
    }



    private Map<String, String> getDataFromInput(){

        Map<String,String> trip=new HashMap<>();

        arrivalDe = arrivalD.getSelectedItem().toString();
        leavingDe = leavingD.getSelectedItem().toString();
        arrivalTi = arrivalT.getText().toString();
        GateNum = GateNumber.getText().toString();
         dateStr = date.getText().toString();
        Tripcode = randomUUID(16,4,'a');
        leavingTi = leavingT.getText().toString();

        if(checkEmptyInput(arrivalTi, GateNum,  dateStr, leavingTi,arrivalD,leavingD))
        {

            AddingTOdb(trip);
            return trip;

        }

        return null;
    }
    public void AddingTOdb(Map<String, String> trip){

        trip.put("Arrival destination", arrivalDe);
        trip.put("Arrival time", arrivalTi);
        trip.put("Available seats", (String) mMetroSeats.get(getMetroID()));
        trip.put("Booked seats", "0");
        trip.put("Gate number", GateNum);
        trip.put("Date",  dateStr);
        trip.put("Leaving destination", leavingDe);
        trip.put("Leaving time", leavingTi);
        trip.put("Trip code", String.valueOf(Tripcode));
        trip.put("metro_id",metro.getSelectedItem().toString());

    }

    /**/
    private void initElement() {

        db = FirebaseFirestore.getInstance();
        arrivalD = findViewById(R.id.spinnerArrivingDestination);
        leavingD = findViewById(R.id.spinnerLeavingDestination);
        arrivalT = findViewById(R.id.arrivalT);
        leavingT = findViewById(R.id.leavingT);
        GateNumber = findViewById(R.id.GateNumber);
        date = findViewById(R.id.date);

        arrivingLabel = findViewById(R.id.ArrivingLabel);
        displaySpinner(arrivalD);
        displaySpinner(leavingD);


        mMetroIds = new ArrayList<>();
        mMetroSeats = new HashMap<>();
        metro = findViewById(R.id.spinnerMetro);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");

        Add = findViewById(R.id.Add);
        Add.setOnClickListener(this);

        showPickerDate();
        showPickerTime(leavingT);
        showPickerTime(arrivalT);

        getMonitorEmailList();

    }

    @Override
    public void onClick(View view) {
        if(view == Add){
            AddTrip();
        }


    }

    char randomChar(){
        return ALPHABET.charAt(rng.nextInt(ALPHABET.length()));
    }



    public StringBuilder randomUUID(int length,int spacing,char spacerChar){
        StringBuilder sb = new StringBuilder();
        int spacer = 0;
        while(length > 0){
            if(spacer == spacing){
                sb.append(spacerChar);
                spacer = 0;
            }
            length--;
            spacer++;
            sb.append(randomChar());
        }
        return sb;
    }



    private boolean checkEmptyInput(String arrivalTi,String GateNum,String dateSTR,String leavingTi,Spinner arrivalPlace,Spinner leavingPlace){

        boolean notEmpty = true;




        if(TextUtils.isEmpty(arrivalTi)){
            arrivalT.setError("Please enter Arriving Time");
            //stopping the function execution further
            notEmpty = false;        }

        if(TextUtils.isEmpty(GateNum)){
            GateNumber.setError("Please enter Gate Number");
            //stopping the function execution further
            notEmpty = false;        }

        if(TextUtils.isEmpty(dateStr)){
            date.setError("Please enter Trip Date");
            //stopping the function execution further
            notEmpty = false;        }


        if(TextUtils.isEmpty(leavingTi)){
            leavingT.setError("Please enter Leaving Time");
            //stopping the function execution further
            notEmpty = false;        }

        if(!(TextUtils.isEmpty(arrivalTi)&&TextUtils.isEmpty(leavingTi))){
            if(!isMoreThenMinimumValue(dateSTR,leavingTi,arrivalTi)){
            arrivalT.setError("The Minimum between leaving time and arrival time should be at least 15 min ");
            notEmpty = false;
            }
        }

        if(!isDifferentStation(arrivalPlace,leavingPlace)){
            arrivingLabel.setError("The stations should be different ");

            notEmpty = false;
        }


        return notEmpty ;

    }
    private boolean isMoreThenMinimumValue(String date, String leaving,String arrival){


        Date leavingDate = convertTime(date,leaving);
        Date arrivalDate = convertTime(date,arrival);

        long leaveMLS = leavingDate.getTime();
        long minimumArrivalTimeMLS = leaveMLS + MINIMUM_TIME_MILLISECONDS;


        Date minimumArrivalTime = new Date(minimumArrivalTimeMLS);

        if(arrivalDate.before(minimumArrivalTime))
            return false;

        return true;

    }

    /**/
    private boolean isDifferentStation(Spinner arrivalPlace,Spinner leavingPlace){

        String arrivalPlaceStr = arrivalPlace.getSelectedItem().toString();
        String leavingPlaceStr = leavingPlace.getSelectedItem().toString();

        if(arrivalPlaceStr.equals(leavingPlaceStr))
            return false;

        return true;


    }

    /**/
    private String getMetroID(){

        return metro.getSelectedItem().toString();
    }


    /**/
    private void displaySpinnerForMetroId(final Spinner spinner) {


        // Initializing an ArrayAdapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item,mMetroIds);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                metro.setSelection(i,true);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        progressDialog.dismiss();

    }


    /**/
    private void getMonitorEmailList(){

        progressDialog.setMessage("Loading ...");
        progressDialog.show();

        db.collection("Metro").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                for (DocumentSnapshot document : task.getResult()) {

                    Map<String, Object> metro = document.getData();
                    String email = metro.get("metro_id").toString();
                    String seats = metro.get("number_of_seats").toString();
                    mMetroIds.add(email);
                    mMetroSeats.put(email,seats);

                }
                displaySpinnerForMetroId(metro);
                metro.setSelection(0);
            }
        });



    }

}
