package com.example.hanan.riyadhmetro;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SingupActivity extends AppCompatActivity implements View.OnClickListener {

    //defining view objects
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonSignup;

    private final String TAG = "SingupActivity";

    private TextView textViewSignin;

    private ProgressDialog progressDialog;


    //defining firebaseauth object
    private FirebaseAuth firebaseAuth;

    /**/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);

        initElement();
        checkIfSingin();
    }

    /**/
    private void checkIfSingin(){

        //if getCurrentUser does not returns null
        if(firebaseAuth.getCurrentUser() != null){
            //that means user is already logged in
            //so close this activity
            finish();

            //and open profile activity
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }


    }

    /**/
    private void initElement(){
        //initializing firebase auth object

        firebaseAuth = FirebaseAuth.getInstance();

        //initializing views
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        textViewSignin = findViewById(R.id.textViewSignin);

        buttonSignup = findViewById(R.id.buttonSignup);

        progressDialog = new ProgressDialog(this);

        //attaching listener to button
        buttonSignup.setOnClickListener(this);
        textViewSignin.setOnClickListener(this);
    }

    /**/
    private void registerUser(){

        //getting email and password from edit texts
        String email = editTextEmail.getText().toString().trim();
        String password  = editTextPassword.getText().toString().trim();

        if(checkEmptyInput(email,password))
            singUp(email,password);


    }

    /**/
    private boolean checkEmptyInput(String email, String password){

        boolean notEmpty = true;


        //checking if email and passwords are empty
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_LONG).show();
            notEmpty = false;
        }

        if(!isEmailValid(email)){
            editTextEmail.setError("Please enter email correctly");
            notEmpty = false;

        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_LONG).show();
            notEmpty = false;
        }

        return notEmpty;

    }

    /**/
    private void singUp(String email, String password){
        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();

        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if(task.isSuccessful()){

                            singinSuccessfully();
                        } else{
                            //display some message here
                            singinUnsuccessfully(task);
                        }

                        progressDialog.dismiss();
                    }
                });

    }


    /**/
    private void singinSuccessfully(){
        finish();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));

    }
    /**/
    private void singinUnsuccessfully(Task<AuthResult> task){


        editTextEmail.setError(String.valueOf(task.getException().getMessage()));


    }


    /**/
    @Override
    public void onClick(View view) {

        if(view == buttonSignup){
            registerUser();
        }

        if(view == textViewSignin){
            goToSingin();
        }

    }

    /**/
    private void goToSingin() {

        Context context = SingupActivity.this;
        Class singinClass = SinginActivity.class;
        Intent intent = new Intent(context,singinClass);
        startActivity(intent);

    }

    /*comment*/
    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}