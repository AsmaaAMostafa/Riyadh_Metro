package com.example.hanan.riyadhmetro.manageMetro;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.hanan.riyadhmetro.R;
import com.example.hanan.riyadhmetro.assign.AssignedMetroListViewActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static com.example.hanan.riyadhmetro.DatabaseName.LATITUDE_FIELD;
import static com.example.hanan.riyadhmetro.DatabaseName.LONGITUDE_FIELD;
import static com.example.hanan.riyadhmetro.DatabaseName.METRO_ID_FIELD;
import static com.example.hanan.riyadhmetro.DatabaseName.METRO_STATUS_FIELD;
import static com.example.hanan.riyadhmetro.DatabaseName.NUMBER_OF_SEATS_FIELD;
import static com.example.hanan.riyadhmetro.manageMetro.MetroListAdpater.Metro_KEY_INTENT;

public class ViewMetroActivity extends AppCompatActivity implements View.OnClickListener{


    private TextView mTextViewMetroId;
    private TextView mTextViewnumberSeats;

    private TextView mTextViewStatus;
    private TextView mTextViewlatitude;
    private TextView mTextViewlongitude;
    private TextView mMonitor;

    private ProgressDialog progressDialog;

    private FirebaseFirestore db ;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_metro);

        initElement();
        viewMetro();
    }

    /**/
    private void initElement() {

        db = FirebaseFirestore.getInstance();
        mTextViewMetroId =findViewById(R.id.textViewMetroID);

        mMonitor = findViewById(R.id.monitor);
        mTextViewnumberSeats = findViewById(R.id.textViewnumberSeats);
        mTextViewStatus = findViewById(R.id.textViewStatus);

        mTextViewlatitude = findViewById(R.id.textViewlatitude);
        mTextViewlongitude = findViewById(R.id.textViewlongitude );

        progressDialog = new ProgressDialog(this);

        mMonitor.setOnClickListener(this);
    }


    /**/
    private void viewMetro() {
        Intent intent = getIntent();
        HashMap<String, String> m = (HashMap<String, String>)intent.getSerializableExtra(Metro_KEY_INTENT);
        Map<String, Object> metro= (Map)m;
        setData(metro);

    }



    /**/
    private void setData( Map<String, Object> metro){
        if(metro.size() >= 5) {

            mTextViewMetroId.setText(metro.get(METRO_ID_FIELD).toString());
            mTextViewnumberSeats.setText(metro.get(NUMBER_OF_SEATS_FIELD).toString());
            mTextViewStatus.setText(metro.get(METRO_STATUS_FIELD).toString());

            mTextViewlatitude.setText(metro.get(LATITUDE_FIELD).toString());
            mTextViewlongitude.setText(metro.get(LONGITUDE_FIELD).toString());

        }
        progressDialog.dismiss();


    }

    @Override
    public void onClick(View view) {
        if(view == mMonitor)
            goToAssignedMonitor();
    }


    private void goToAssignedMonitor() {

        String metroId = mTextViewMetroId.getText().toString();
        Intent intent = new Intent(ViewMetroActivity.this,AssignedMetroListViewActivity.class);
        intent.putExtra("isMointor",true);
        intent.putExtra("metro_id",metroId);
        startActivity(intent);
    }

}
