package com.example.hanan.riyadhmetro.mangeMetroMonitor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.hanan.riyadhmetro.R;
import com.example.hanan.riyadhmetro.assign.AssignedMetroListViewActivity;
import com.example.hanan.riyadhmetro.manageTrip.TripListAdpater;
import com.example.hanan.riyadhmetro.manageUser.ChangeUserPassword;
import com.example.hanan.riyadhmetro.manageUser.ViewUserAccountActivity;
import com.example.hanan.riyadhmetro.utility.PreferencesUtility;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static com.example.hanan.riyadhmetro.DatabaseName.BIRTH_DATE_FIELD;
import static com.example.hanan.riyadhmetro.DatabaseName.EMAIL_FIELD;
import static com.example.hanan.riyadhmetro.DatabaseName.NAME_FIELD;
import static com.example.hanan.riyadhmetro.DatabaseName.NATIONAL_ID_FIELD;
import static com.example.hanan.riyadhmetro.mangeMetroMonitor.MetroMonitorListAdpater.ID_KEY_INTENT;
import static com.example.hanan.riyadhmetro.mangeMetroMonitor.MetroMonitorListAdpater.METRO_MONITOR_KEY_INTENT;


public class ViewMetroMonitorActivity extends AppCompatActivity implements View.OnClickListener{


    private TextView mTextViewName;
    private TextView mTextViewEmail;
    private TextView mTextViewBrithDate;

    private TextView mTextViewNationalid;
    private Button mUpdateButton;
    private TextView mMetro ;

    private ProgressDialog progressDialog;
    private Map<String, Object> mMetroMonitor;
    private String mId;
    private String mMointorEmail;
    private FirebaseFirestore db ;
    private TextView mPassword ;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.metro_monitor_view);

        initElement();
        viewMonitorInfo();
        hideUpdateIfAdmin();
    }

    /**/
    private void initElement() {

        db = FirebaseFirestore.getInstance();

        mTextViewName = findViewById(R.id.monitor_name);
        mTextViewBrithDate = findViewById(R.id.monitor_birth_date);
        mTextViewEmail = findViewById(R.id.monitor_email);
        mTextViewNationalid = findViewById(R.id.monitor_National_id);
        mMetro = findViewById(R.id.metro);
        mUpdateButton = findViewById(R.id.update_button);
        mMetro.setOnClickListener(this);
        mUpdateButton.setOnClickListener(this);


        mPassword = findViewById(R.id.passwordLabel);
        mPassword.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
    }
    /**/
    private void hideUpdateIfAdmin(){
        if(PreferencesUtility.getAuthority(this) == PreferencesUtility.ADMIN_AUTHORITY )
        mUpdateButton.setVisibility(View.GONE);
    }


    /**/
    private void viewMonitorInfo() {
        Intent intent = getIntent();
        HashMap<String, String> t = (HashMap<String, String>)intent.getSerializableExtra(METRO_MONITOR_KEY_INTENT);
        mMetroMonitor = (Map)t;
        mId  = intent.getStringExtra(TripListAdpater.ID_KEY_INTENT);
        setData(mMetroMonitor);

    }



    /**/
    private void setData( Map<String, Object> monitor){
        if(monitor != null ) {

            mTextViewName.setText(monitor.get(NAME_FIELD).toString());
            mTextViewBrithDate.setText(monitor.get(BIRTH_DATE_FIELD).toString());
            mTextViewEmail.setText(monitor.get(EMAIL_FIELD).toString());
            mTextViewNationalid.setText(monitor.get(NATIONAL_ID_FIELD).toString());
            mMointorEmail = monitor.get(EMAIL_FIELD).toString();
        }
        progressDialog.dismiss();


    }

    @Override
    public void onClick(View view) {


        if(view == mUpdateButton)
            GoToUpdateAcountInfo();
        if(view == mMetro ){
            goToAssignedMetro();
        }
        if(view == mPassword ){
            goToChangeUserPassword();
        }

    }

    private void goToAssignedMetro() {

        Intent intent = new Intent(ViewMetroMonitorActivity.this,AssignedMetroListViewActivity.class);
        intent.putExtra("email",mMointorEmail);
        intent.putExtra("isMointor",false);
        startActivity(intent);
    }

    /**/
    private void GoToUpdateAcountInfo(){

        if(PreferencesUtility.getAuthority(this) == PreferencesUtility.MONITOR_AUTHORITY ){

            Class  editMetroMonitoClass = EditMetroMonitorActivity.class;
            Intent intent = new Intent(ViewMetroMonitorActivity.this,editMetroMonitoClass);
            if(mMetroMonitor != null){
                intent.putExtra(METRO_MONITOR_KEY_INTENT,(HashMap) mMetroMonitor );
                intent.putExtra(ID_KEY_INTENT,mId);
                startActivity(intent);


            }

        }

    }

    private void goToChangeUserPassword() {
        Intent intent = new Intent(ViewMetroMonitorActivity.this,ChangeUserPassword.class);
        startActivity(intent);
    }

}
