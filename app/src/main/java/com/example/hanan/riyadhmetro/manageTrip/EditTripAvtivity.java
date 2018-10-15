package com.example.hanan.riyadhmetro.manageTrip;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.hanan.riyadhmetro.DatabaseName.COLLECTION_METRO_MONITOR;
import static com.example.hanan.riyadhmetro.DatabaseName.EMAIL_FIELD;
import static com.example.hanan.riyadhmetro.manageTrip.TripListAdpater.ID_KEY_INTENT;
import static com.example.hanan.riyadhmetro.manageTrip.TripListAdpater.TRIP_KEY_INTENT;

public class EditTripAvtivity extends AppCompatActivity implements View.OnClickListener{

    private EditText arrivalT,leavingT, GateNumber,date,Bseats,Aseats;
    private Spinner arrivalD, leavingD;
    private Button Add;
    private FirebaseFirestore db;

    private String arrivalDe ;
    private String leavingDe ;
    private String arrivalTi ;
    private String GateNum;
    private String Date ;
    private String mTripCode ;
//    private String Bookedseats ;
//    private String Availableseats;
    private String leavingTi ;
    private TextView title;
    private ProgressDialog progressDialog;
    private Spinner metro;
    private ArrayList<String> mMetroIds;
    private String mMetroId;




    private String mTripId;
    private Map<String, Object> mTrip;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);
        initElement();
        changeAddButtonToUpdate();
        getDateFromIntent();
    }
    /**/
    private void displaySpinner(Spinner spinner){

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.riyadh_stations, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

    }

    /**/
    private void setSpinnerValue(Spinner spinner, String stringValue){

        displaySpinner(spinner);
        ArrayAdapter myAdap = (ArrayAdapter) spinner.getAdapter(); //cast to an ArrayAdapter

        int spinnerPosition = myAdap.getPosition(stringValue);

        spinner.setSelection(spinnerPosition);
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
    private void setDateOnfields( Map<String, Object> trip){


        leavingT.setText(trip.get("Leaving time").toString());
        arrivalT.setText(trip.get("Arrival time").toString());

        setSpinnerValue(arrivalD,trip.get("Arrival destination").toString());
        setSpinnerValue(leavingD,trip.get("Leaving destination").toString());

        date.setText(trip.get("Date").toString());
//
//        Aseats.setText(trip.get("Available seats").toString());
//        Bseats.setText(trip.get("Booked seats").toString());
        GateNumber.setText(trip.get("Gate number").toString());

//        Tripcode = trip.get("Trip code").toString();


    }

    /**/
    private void getDateFromIntent() {

        Intent intent = getIntent();
        HashMap<String, String> t = (HashMap<String, String>)intent.getSerializableExtra(TRIP_KEY_INTENT);
        mTrip = (Map)t;
//        mTripId  = intent.getStringExtra(ID_KEY_INTENT);
        mMetroId = mTrip.get("metro_id").toString();
        mTripCode = mTrip.get("Trip code").toString();
        getTripId();
        setDateOnfields(mTrip);


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


    /**/
    private void UpdateTripUnsuccefully( Exception e){
        String error = e.getMessage();
        Toast.makeText(EditTripAvtivity.this,"Error: "+error,Toast.LENGTH_SHORT).show();

    }

    /**/
    private void UpdateTripSuccefully(){
        Toast.makeText(EditTripAvtivity.this,"The Trip has been Updated succefully!",Toast.LENGTH_SHORT).show();

    }
    /**/
    private void UpdateTrip(){

        progressDialog.show();
        Map<String,String> trip = getDataFromInput();
        // ff.collection
        db.collection("Trip").document(mTripId).set(trip).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                UpdateTripSuccefully();
                goToViewTrip();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                UpdateTripUnsuccefully(e);
            }
        });

    }

    private void goToViewTrip() {
        Context context = EditTripAvtivity.this;
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
        Date = date.getText().toString();
//        Bookedseats = Bseats.getText().toString();
//        Availableseats = Aseats.getText().toString();
        leavingTi = leavingT.getText().toString();

        setSpinnerValue(arrivalD,arrivalDe);
        setSpinnerValue(leavingD,leavingDe);




        if(checkEmptyInput(arrivalDe, leavingDe, arrivalTi, GateNum, Date, leavingTi))
        {

            AddingTOdb(trip);
        }

        return trip;
    }
    /**/
    public void AddingTOdb(Map<String, String> trip){

        trip.put("Arrival destination", arrivalDe);
        trip.put("Arrival time", arrivalTi);
        trip.put("Available seats",String.valueOf(mTrip.get("Available seats")));
        trip.put("Booked seats",String.valueOf(mTrip.get("Booked seats")));
        trip.put("Gate number", GateNum);
        trip.put("Date", Date);
        trip.put("Leaving destination", leavingDe);
        trip.put("Leaving time", leavingTi);
        trip.put("Trip code", String.valueOf(mTripCode));
        trip.put("metro_id",metro.getSelectedItem().toString());

    }
    /**/
    private void changeAddButtonToUpdate(){

        Add.setText("Update");
        title.setText("Edit Trip");
    }

    /**/
    @Override
    public void onClick(View view) {

        if(view == Add){
            UpdateTrip();
        }
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
//        Bseats = findViewById(R.id.Bseats);
//        Aseats = findViewById(R.id.Aseats);
        title = findViewById(R.id.title);

        metro = findViewById(R.id.spinnerMetro);
        mMetroIds = new ArrayList<>();


        Add = findViewById(R.id.Add);
        Add.setOnClickListener(this);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");

        showPickerDate();
        showPickerTime(leavingT);
        showPickerTime(arrivalT);

        getMonitorEmailList();


    }


    /**/
    private boolean checkEmptyInput(String arrivalDe, String leavingDe,String arrivalTi,String GateNum,String Date,String leavingTi){

        boolean notEmpty = true;




        if(TextUtils.isEmpty(arrivalTi)){
            arrivalT.setError("Please enter Arriving Time");
            //stopping the function execution further
            notEmpty = false;        }

        if(TextUtils.isEmpty(GateNum)){
            GateNumber.setError("Please enter Gate Number");
            //stopping the function execution further
            notEmpty = false;        }

        if(TextUtils.isEmpty(Date)){
            date.setError("Please enter Trip Date");
            //stopping the function execution further
            notEmpty = false;        }
//
//        if(TextUtils.isEmpty(Bookedseats)){
//
//            Bseats.setError("Please enter Booked Seats");
//            //stopping the function execution further
//            notEmpty = false;        }
//
//        if(TextUtils.isEmpty(Availableseats)){
//            Aseats.setError("Please enter Available Seats");
//            //stopping the function execution further
//            notEmpty = false;        }

        if(TextUtils.isEmpty(leavingTi)){
            leavingT.setError("Please enter Leaving Time");
            //stopping the function execution further
            notEmpty = false;        }

        return notEmpty ;

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


    /**/
    private void getMonitorEmailList(){

        progressDialog.setMessage("Loading ...");
        progressDialog.show();

        db.collection("Metro").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                int index = 0;
                int count = 0;

                for (DocumentSnapshot document : task.getResult()) {

                    Map<String, Object> metro = document.getData();
                    String metroId = metro.get("metro_id").toString();
                    mMetroIds.add(metroId);
                    if(mMetroId.equals(metroId))
                        index = count;
                        count ++;
                }
                displaySpinnerForMetroId(metro);
                metro.setSelection(index);
            }
        });



    }
}
