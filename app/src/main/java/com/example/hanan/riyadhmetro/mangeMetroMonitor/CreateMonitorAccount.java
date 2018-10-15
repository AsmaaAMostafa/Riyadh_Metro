package com.example.hanan.riyadhmetro.mangeMetroMonitor;

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

import com.example.hanan.riyadhmetro.utility.DateDialogUtility;
import com.example.hanan.riyadhmetro.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static com.example.hanan.riyadhmetro.DatabaseName.COLLECTION_METRO_MONITOR;
import static com.example.hanan.riyadhmetro.SigninActivity.isEmailValid;
import static com.example.hanan.riyadhmetro.DatabaseName.BIRTH_DATE_FIELD;
import static com.example.hanan.riyadhmetro.DatabaseName.EMAIL_FIELD;
import static com.example.hanan.riyadhmetro.DatabaseName.NAME_FIELD;
import static com.example.hanan.riyadhmetro.DatabaseName.NATIONAL_ID_FIELD;
import static com.example.hanan.riyadhmetro.DatabaseName.PASSWORD_FIELD;


public class CreateMonitorAccount extends AppCompatActivity implements View.OnClickListener{

    private Button buttonRegister;



    private static final int ValidPasswordSize = 8;
    private static final int ValidNationalIdSize = 10;



    private EditText editTextFirst;
    private EditText editTextBirth;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextNationalid;
    private EditText editTextPasswordRepeat;
    private CheckBox checkBox;

    private TextView textViewTitle ;

    private HashMap<String,Object> userInput;
    private TextView textViewSignin;
    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseFirestore db ;
    //private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

// do not forget change it
        initElement();
        displayCreateAccountForMonitorView();

    }

    /**/
    private void initElement(){

        firebaseAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        db = FirebaseFirestore.getInstance();


        user = firebaseAuth.getCurrentUser();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");

        buttonRegister = findViewById(R.id.buttonSignup);

        editTextFirst = findViewById(R.id.editTextName);
        editTextBirth = findViewById(R.id.editTextBirthDate);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextNationalid = findViewById(R.id.editTextNationalId);
        editTextPasswordRepeat = findViewById(R.id.editTextRepeatPassword);

        checkBox = findViewById(R.id.checkBox);

        textViewTitle = findViewById(R.id.textViewTitle);
        textViewSignin = findViewById(R.id.textViewSignin);
        buttonRegister.setOnClickListener(this);
      //  textViewSignin.setOnClickListener(this);

        userInput = new HashMap<>();
        showPickerDate();
    }

    /**/
    private void displayNotUniqeEmail() {

        editTextEmail.setError("Email is already chosen");
        progressDialog.dismiss();
    }

    /**/
    public void isUniqueEmail() {

        progressDialog.show();

        String email = editTextEmail.getText().toString();
        db.collection(COLLECTION_METRO_MONITOR).whereEqualTo("Email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.getResult() != null){

                            getDate(task);
                        }
                    }
                });

    }


    private  void getDate( Task<QuerySnapshot> task) {

        int size ;

        size = task.getResult().size();


        if(size == 0){
            createMonitorAccount();

        }
        else
        {
            displayNotUniqeEmail();
        }
    }


    /**/
    private void displayCreateAccountForMonitorView(){

        checkBox.setVisibility(View.GONE);
        textViewSignin.setVisibility(View.GONE);
        textViewTitle.setText("Create Metro Monitor Account");
        buttonRegister.setText("Create ");
    }



    /**/
    private boolean checkOfEmtiyInput(HashMap<String,Object> userInput) {

        boolean isValid = true;

        /*
         * NAME
         * */
        if(TextUtils.isEmpty(userInput.get(NAME_FIELD).toString())){
            //firstName is empty
            editTextFirst.setError("Please enter The Name");
            editTextFirst.setHint("name should not contain number");
            //stopping the function execution further
            isValid = false;
        }


        if(!isVaildName(userInput.get(NAME_FIELD).toString())){
            //firstName is empty
            editTextFirst.setError("Please enter valid  Name");
            //stopping the function execution further
            isValid = false;
        }


        /*
         * Birth date
         * */
        if(TextUtils.isEmpty(userInput.get(BIRTH_DATE_FIELD).toString())){
            //firstName is empty
            editTextBirth.setError("Please enter your Birth date");
            //stopping the function execution further
            isValid = false;
        }


        if(!isValidDate(userInput.get(BIRTH_DATE_FIELD).toString())){            //firstName is empty
            editTextBirth.setError("Please enter valid date");
            //stopping the function execution further
            isValid = false;
        }

        /*
         * Email
         * */
        if(TextUtils.isEmpty(userInput.get(EMAIL_FIELD).toString())){
            //Email is empty
            editTextEmail.setError("Please enter Email");
            //stopping the function execution further
            isValid = false;
        }

        if(!isEmailValid(userInput.get(EMAIL_FIELD).toString())){

            editTextEmail.setError("Please enter valid Email");
            //stopping the function execution further
            isValid = false;

        }


        /*
         * NATIONAL_ID
         * */
        if(TextUtils.isEmpty(userInput.get(NATIONAL_ID_FIELD).toString())){
            //Nationalid is empty
            editTextNationalid.setError("Please enter National ID");
            //stopping the function execution further
            isValid = false;
        }
        if(userInput.get(NATIONAL_ID_FIELD).toString().length() < ValidNationalIdSize){
            //Password is empty
            editTextNationalid.setError("Please enter valid National ID");
            //stopping the function execution further
            isValid = false;
        }


        /*
         * PASSWORD
         * */
        if(TextUtils.isEmpty(userInput.get(PASSWORD_FIELD).toString())){
            //Password is empty
            editTextPassword.setError("Please enter Password");
            //stopping the function execution further
            isValid = false;
        }

        else if(userInput.get(PASSWORD_FIELD).toString().length() < ValidPasswordSize){
            //Password is empty
            editTextPassword.setError("The Password is too short");
            //stopping the function execution further
            isValid = false;
        }
        if(!userInput.get(PASSWORD_FIELD).toString().equals(editTextPasswordRepeat.getText().toString())){

            editTextPasswordRepeat.setError("The Password not match");
            isValid = false;


        }

        /**/
        if(!checkBox.isChecked()){

            checkBox.setError("You Should agree");

            isValid = false;


        }



        return isValid;
    }
    /**/
    private void createMonitorAccount(){



        HashMap<String,Object> userInput = getInputFromUser();



        if(checkOfEmtiyInput(userInput)  ){

            progressDialog.setMessage("Registering Monitor...");
            progressDialog.show();
            SingUp(userInput);


        }
        /*else {


        }*/

    }
    /**/
    private void SingUp(final HashMap<String,Object> userInput){

        firebaseAuth.createUserWithEmailAndPassword(userInput.get(EMAIL_FIELD).toString(), userInput.get(PASSWORD_FIELD).toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            SingUpSuccessfully(userInput.get(PASSWORD_FIELD).toString(),getApplicationContext());
                        } else {

                            SingUpUnsuccessfully(task);
                        }
                    }
                });
    }

    /**/
    private void SingUpSuccessfully(String email,Context context){
        Toast.makeText(CreateMonitorAccount.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
        SaveUserInfo(userInput);
        goToHome();

        progressDialog.dismiss();

    }

    /**/
    private void SingUpUnsuccessfully(Task<AuthResult> task){
        Toast.makeText(CreateMonitorAccount.this, "Could not register,please try again", Toast.LENGTH_SHORT).show();
        editTextEmail.setError(String.valueOf(task.getException().getMessage()));
        progressDialog.dismiss();

    }
    /**/
    private void SaveUserInfo(HashMap<String,Object> userInput){

        String emil = userInput.get(EMAIL_FIELD).toString();
        String collectionName = "Metro_Monitor";


        db.collection(collectionName).add(userInput).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            public void onSuccess(DocumentReference documentReference) {
                SaveSuccessfully();
            }

        }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {

                SaveUnsuccessfully(e);
            }
        });

    }
    /**/
    private void SaveSuccessfully(){
//Toast.makeText(Create_Monitor_Account.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
    }

    /**/
    private void goToHome() {

        Context context = CreateMonitorAccount.this;
        Class homeClass = MetroMonitorListViewActivity.class; // where to go
        Intent intent = new Intent(context,homeClass);
        startActivity(intent);

    }

    /**/
    private void SaveUnsuccessfully(Exception e){

        String error = e.getMessage();
        Toast.makeText(CreateMonitorAccount.this, "Error" + error, Toast.LENGTH_SHORT).show();
    }

    /**/
    @Override
    public void onClick(View view) {

        if(view == buttonRegister){
            isUniqueEmail();

        }
    }

    /**/
    private void showPickerDate() {

        editTextBirth.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            public void onFocusChange(View view, boolean hasfocus){
                if(hasfocus){
                    DateDialogUtility dialog= new DateDialogUtility(view);
                    FragmentTransaction ft =getFragmentManager().beginTransaction();
                    dialog.show(ft, "DatePicker");

                }
            }

        });
    }
    public HashMap<String,Object> getInputFromUser() {


        String name = editTextFirst.getText().toString().trim();
        String birth = editTextBirth.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String nationalId = editTextNationalid.getText().toString().trim();

        userInput.put(NAME_FIELD,name);
        userInput.put(BIRTH_DATE_FIELD,birth);
        userInput.put(EMAIL_FIELD,email);
        userInput.put(PASSWORD_FIELD,password);
        userInput.put(NATIONAL_ID_FIELD,nationalId);

        return userInput;
    }

    /**/
    private boolean isVaildName(String name){

        return !(name.matches(".\\d+."));

    }
    /**/
    public boolean isValidDate(String date)
    {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d/m/yyyy");

        Date testDate ;


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

}

