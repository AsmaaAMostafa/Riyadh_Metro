package com.example.hanan.riyadhmetro.manageUser;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanan.riyadhmetro.MainActivity;
import com.example.hanan.riyadhmetro.R;
import com.example.hanan.riyadhmetro.utility.DateDialogUtility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.example.hanan.riyadhmetro.DatabaseName.BIRTH_DATE_FIELD;
import static com.example.hanan.riyadhmetro.DatabaseName.COLLECTION_METRO_MONITOR;
import static com.example.hanan.riyadhmetro.DatabaseName.EMAIL_FIELD;
import static com.example.hanan.riyadhmetro.DatabaseName.NAME_FIELD;
import static com.example.hanan.riyadhmetro.DatabaseName.NATIONAL_ID_FIELD;
import static com.example.hanan.riyadhmetro.DatabaseName.PASSWORD_FIELD;
import static com.example.hanan.riyadhmetro.SigninActivity.isEmailValid;
import static com.example.hanan.riyadhmetro.manageTrip.TripListAdpater.ID_KEY_INTENT;

public class EditUserAccount extends AppCompatActivity implements View.OnClickListener {

    private Button buttonupdate;


    private static final int ValidPasswordSize = 8;
    private static final int ValidNationalIdSize = 10;


    private EditText editTextName;
    private EditText editTextBirth;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextNationalid;
    private CheckBox checkBox;
    private EditText editTextPasswordRepeat;

    private TextView textViewTitle;

    private HashMap<String, Object> userInput;
    private TextView textViewSignin;
    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseFirestore db;
    //private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private String mUserId;
    private ArrayList<Map<String, Object>> mTieckets;
    private String mUserEmail;



    private String mMetroMonitorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        initElement();
        hidePassword();
        getDateFromIntent();
        getUserId();
        displayEditView();


    }

    /**/
    private void displayEditView() {

        editTextEmail.setFocusableInTouchMode(false);
        checkBox.setVisibility(View.GONE);
        textViewSignin.setVisibility(View.GONE);
        buttonupdate.setText("Update");
        textViewTitle.setText("Update Account Information");
    }

    /**/
    private void getDateFromIntent() {

        Intent intent = getIntent();
        HashMap<String, String> t = (HashMap<String, String>) intent.getSerializableExtra("user");//?????idk?
        Map<String, Object> user = (Map) t;
        mUserEmail = user.get("Email").toString();
//        mMetroMonitorId = intent.getStringExtra(ID_KEY_INTENT);
        setDateOnfields(user);


    }
    /**/

    /**/
    private void setDateOnfields(Map<String, Object> user) {

        if (user != null) {
            editTextName.setText(user.get(NAME_FIELD).toString());
            editTextEmail.setText(user.get(EMAIL_FIELD).toString());
            editTextPassword.setText(user.get(PASSWORD_FIELD).toString());

            editTextBirth.setText(user.get(BIRTH_DATE_FIELD).toString());
            editTextNationalid.setText(user.get(NATIONAL_ID_FIELD).toString());
        }


    }

    /**/
    private void initElement() {

        firebaseAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        db = FirebaseFirestore.getInstance();


        user = firebaseAuth.getCurrentUser();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");

        buttonupdate = findViewById(R.id.buttonSignup);

        editTextName = findViewById(R.id.editTextName);
        editTextBirth = findViewById(R.id.editTextBirthDate);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextNationalid = findViewById(R.id.editTextNationalId);
        editTextPasswordRepeat = findViewById(R.id.editTextRepeatPassword);

        textViewTitle = findViewById(R.id.textViewTitle);
        checkBox = findViewById(R.id.checkBox);

        textViewSignin = findViewById(R.id.textViewSignin);
        buttonupdate.setOnClickListener(this);

        userInput = new HashMap<>();
        showPickerDate();

    }

    /**/
    private void hidePassword() {

        editTextPassword.setVisibility(View.GONE);
        editTextPasswordRepeat.setVisibility(View.GONE);

    }


    /**/
    private boolean checkOfEmtiyInput(HashMap<String, Object> userInput) {

        boolean isValid = true;

        /*
         * NAME
         * */
        if (TextUtils.isEmpty(userInput.get(NAME_FIELD).toString())) {
            //firstName is empty
            editTextName.setError("Please enter The Name");
            //stopping the function execution further
            isValid = false;
        }


        if (!isVaildName(userInput.get(NAME_FIELD).toString())) {
            //firstName is empty
            editTextName.setError("Please enter valid  Name");
            //stopping the function execution further
            isValid = false;
        }


        /*
         * Birth date
         * */
        if (TextUtils.isEmpty(userInput.get(BIRTH_DATE_FIELD).toString())) {
            //firstName is empty
            editTextBirth.setError("Please enter your Birth date");
            //stopping the function execution further
            isValid = false;
        }


        if (!isValidDate(userInput.get(BIRTH_DATE_FIELD).toString())) {            //firstName is empty
            editTextBirth.setError("Please enter valid date");
            //stopping the function execution further
            isValid = false;
        }

        /*
         * Email
         * */
        if (TextUtils.isEmpty(userInput.get(EMAIL_FIELD).toString())) {
            //Email is empty
            editTextEmail.setError("Please enter Email");
            //stopping the function execution further
            isValid = false;
        }

        if (!isEmailValid(userInput.get(EMAIL_FIELD).toString())) {

            editTextEmail.setError("Please enter valid Email");
            //stopping the function execution further
            isValid = false;

        }


        /*
         * NATIONAL_ID
         * */
        if (TextUtils.isEmpty(userInput.get(NATIONAL_ID_FIELD).toString())) {
            //Nationalid is empty
            editTextNationalid.setError("Please enter National ID");
            //stopping the function execution further
            isValid = false;
        }
        if (userInput.get(NATIONAL_ID_FIELD).toString().length() < ValidNationalIdSize) {
            //Password is empty
            editTextNationalid.setError("Please enter valid National ID");
            //stopping the function execution further
            isValid = false;
        }



        /**/
        if (!checkBox.isChecked()) {

            checkBox.setError("You Should agree");

            isValid = false;


        }


        return isValid;
    }

    /**/
    private void UpdateUserInfo() {

        progressDialog.setMessage("Loading ...");
        progressDialog.show();

        HashMap<String, Object> userInput = getInputFromUser();


        if (checkOfEmtiyInput(userInput)) {

            edit(userInput);


        } else {

            progressDialog.dismiss();

        }

    }


    /**/
    private void edit(HashMap<String, Object> userInput) {

        String emil = userInput.get(EMAIL_FIELD).toString();
        String collectionName = "User";

        if(mUserId != null) {
            db.collection(collectionName).document(mUserId).set(userInput).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    updateSuccessfully();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    updateUnsuccessfully(e);
                }
            });
        }
    }

    /**/
    private void updateSuccessfully() {
        Toast.makeText(EditUserAccount.this, "Updated Successfully", Toast.LENGTH_SHORT).show();

        goToHome();

    }

    /**/
    private void goToHome() {

        Context context = EditUserAccount.this;
        Class homeClass = MainActivity.class;
        Intent intent = new Intent(context, homeClass);
        startActivity(intent);

    }

    /**/
    private void updateUnsuccessfully(Exception e) {

        String error = e.getMessage();
        Toast.makeText(EditUserAccount.this, "Error" + error, Toast.LENGTH_SHORT).show();
    }

    /**/
    @Override
    public void onClick(View view) {

        if (view == buttonupdate) {
            getTieckets();

        }


    }

    /**/
    private void showPickerDate() {

        editTextBirth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View view, boolean hasfocus) {
                if (hasfocus) {
                    DateDialogUtility dialog = new DateDialogUtility(view);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    dialog.show(ft, "DatePicker");

                }
            }

        });
    }

    /**/
    public HashMap<String, Object> getInputFromUser() {


        String name = editTextName.getText().toString().trim();
        String birth = editTextBirth.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String nationalId = editTextNationalid.getText().toString().trim();

        userInput.put(NAME_FIELD, name);
        userInput.put(BIRTH_DATE_FIELD, birth);
        userInput.put(EMAIL_FIELD, email);
        userInput.put(PASSWORD_FIELD, password);
        userInput.put(NATIONAL_ID_FIELD, nationalId);
        if(mTieckets != null)
            userInput.put("tickets", mTieckets);


        return userInput;
    }

    /**/
    private boolean isVaildName(String name) {

        return !(name.matches(".*\\d+.*"));

    }

    /**/
    public boolean isValidDate(String date) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d/m/yyyy");

        Date testDate;


        try {
            testDate = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            return false;
        }

        if (!simpleDateFormat.format(testDate).equals(date)) {
            return false;
        }


        return true;

    }

    /**/
    public void getTieckets() {

        if(mUserId != null) {
            db.collection("User").document(mUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    getAssignedMetro(task);
                    UpdateUserInfo();

                }
            });

        }
    }

    /**/
    private void getAssignedMetro(Task<DocumentSnapshot> task) {

        mTieckets = (ArrayList) task.getResult().getData().get("tickets");



    }

    /**/
    private void getUserId(){


        db.collection("User").whereEqualTo("Email", mUserEmail).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        for (DocumentSnapshot document : task.getResult()) {

                            mUserId = document.getId();

                        }
                    }
                });



    }
}

