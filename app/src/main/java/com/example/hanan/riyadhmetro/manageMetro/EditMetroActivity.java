package com.example.hanan.riyadhmetro.manageMetro;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanan.riyadhmetro.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import static com.example.hanan.riyadhmetro.DatabaseName.COLLECTION_METRO;
import static com.example.hanan.riyadhmetro.DatabaseName.COLLECTION_METRO_MONITOR;
import static com.example.hanan.riyadhmetro.DatabaseName.EMAIL_FIELD;
import static com.example.hanan.riyadhmetro.DatabaseName.LATITUDE_FIELD;
import static com.example.hanan.riyadhmetro.DatabaseName.LONGITUDE_FIELD;
import static com.example.hanan.riyadhmetro.DatabaseName.METRO_ID_FIELD;
import static com.example.hanan.riyadhmetro.DatabaseName.METRO_STATUS_FIELD;
import static com.example.hanan.riyadhmetro.DatabaseName.NUMBER_OF_SEATS_FIELD;
import static com.example.hanan.riyadhmetro.manageMetro.MetroListAdpater.ID_KEY_INTENT;
import static com.example.hanan.riyadhmetro.manageMetro.MetroListAdpater.Metro_KEY_INTENT;

public class EditMetroActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText metroId, seatsNumber,latitude,longitude,monitore;
    private Spinner metroStatus;
    private Button Add;
    private FirebaseFirestore db;
    private String metroID,seatNumber,stutus,monitor,latitudei,longitudei;
    private TextView title;

    private String mMetroId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_metro);
        initElement();
        changeAddButtonToUpdate();
        getDateFromIntent();
        displayEdit();
    }
    /**/
    private void displaySpinner(Spinner spinner){

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.metro_status, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

    }

    /**/
    private void displayEdit(){

        metroId.setFocusableInTouchMode(false);
        seatsNumber.setFocusableInTouchMode(false);
        latitude.setVisibility(View.VISIBLE);
        longitude.setVisibility(View.VISIBLE);

    }

    private void setSpinnerValue(Spinner spinner, String stringValue){

        displaySpinner(spinner);
        ArrayAdapter myAdap = (ArrayAdapter) spinner.getAdapter(); //cast to an ArrayAdapter

        int spinnerPosition = myAdap.getPosition(stringValue);

        spinner.setSelection(spinnerPosition);
    }


    /**/
    private void setDateOnfields( Map<String, Object> metro){


        metroId.setText(metro.get(METRO_ID_FIELD).toString());
        seatsNumber.setText(metro.get(NUMBER_OF_SEATS_FIELD).toString());

       setSpinnerValue(metroStatus,metro.get(METRO_STATUS_FIELD).toString());

        latitude.setText(metro.get(LATITUDE_FIELD).toString());


        longitude.setText(metro.get(LONGITUDE_FIELD).toString());


    }

    /**/
    private void getDateFromIntent() {

        Intent intent = getIntent();
        HashMap<String, String> m= (HashMap<String, String>)intent.getSerializableExtra(Metro_KEY_INTENT);
        Map<String, Object> metro = (Map)m;
//        mMetroId  = intent.getStringExtra(ID_KEY_INTENT);
        setDateOnfields(metro);


    }


    /**/
    /**/
    private void getMetroId(){


        db.collection(COLLECTION_METRO).whereEqualTo("metro_id", metroID).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        for (DocumentSnapshot document : task.getResult()) {

                            mMetroId = document.getId();

                        }
                    }
                });

    }

    /**/
    private void UpdateMetroUnsuccefully( Exception e){

        String error = e.getMessage();
        Toast.makeText(EditMetroActivity.this,"Error: "+error,Toast.LENGTH_SHORT).show();

    }

    /**/
    private void UpdateMetroSuccefully(){

        Toast.makeText(EditMetroActivity.this,"The Metro has been Updated succefully!",Toast.LENGTH_SHORT).show();

    }
    /**/
    private void UpdateMetro(){

        Map<String,String> metro = getDataFromInput();

        if(mMetroId != null) {
            db.collection(COLLECTION_METRO).document(mMetroId).set(metro).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    UpdateMetroSuccefully();
                    goToViewMetro();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    UpdateMetroUnsuccefully(e);
                }
            });

        }

    }

    private void goToViewMetro() {

        Context context = EditMetroActivity.this;
        Class viewMetroClass = MetroListViewActivity.class;

        Intent intent = new Intent(context, viewMetroClass);
        startActivity(intent);
    }




    private Map<String, String> getDataFromInput(){

        Map<String,String> metro=new HashMap<>();

        stutus = metroStatus.getSelectedItem().toString();
        metroID = metroId.getText().toString();
        seatNumber = seatsNumber.getText().toString();
        latitudei = latitude.getText().toString();
        longitudei = longitude.getText().toString();


        setSpinnerValue(metroStatus,stutus);




        if(checkEmptyInput(metroID, seatNumber, latitudei, longitudei,monitor ))
        {

            AddingTOdb(metro);
        }

        return metro;
    }
    /**/
    public void AddingTOdb(Map<String, String> metro){


        metro.put(METRO_ID_FIELD, metroID);
        metro.put(LATITUDE_FIELD, latitudei);
        metro.put(LONGITUDE_FIELD, longitudei);
        metro.put(NUMBER_OF_SEATS_FIELD, seatNumber);
        metro.put(METRO_STATUS_FIELD, stutus);

    }
    /**/
    private void changeAddButtonToUpdate(){

        Add.setText("Update");
        title.setText("Edit Metro");
    }

    /**/
    @Override
    public void onClick(View view) {

        if(view == Add){
            getMetroId();
            UpdateMetro();

        }
    }

    /**/
    private void initElement() {
        db = FirebaseFirestore.getInstance();
       metroStatus = findViewById(R.id.spinnerMetroStatus);
        metroId = findViewById(R.id.metroId);
        seatsNumber = findViewById(R.id.seatsNumber);
        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);


        title = findViewById(R.id.title);

        Add = findViewById(R.id.Add);
        Add.setOnClickListener(this);


    }


    /**/
    private boolean checkEmptyInput (String metroID,String seatNumber, String latitudei,String longitudei,String monitor ) {

        boolean notEmpty = true;


        if (TextUtils.isEmpty(metroID)) {
            metroId.setError("Please enter Metro ID");
            //stopping the function execution further
            notEmpty = false;
        }

        if (TextUtils.isEmpty(seatNumber)) {
            seatsNumber.setError("Please enter seats Number");
            //stopping the function execution further
            notEmpty = false;
        }

        if (TextUtils.isEmpty(latitudei)) {

            latitude.setError("Please enter latitude");
            //stopping the function execution further
            notEmpty = false;
        }

        if (TextUtils.isEmpty(longitudei)) {
            longitude.setError("Please enter longitude");
            //stopping the function execution further
            notEmpty = false;
        }

        return notEmpty;

    }
}

