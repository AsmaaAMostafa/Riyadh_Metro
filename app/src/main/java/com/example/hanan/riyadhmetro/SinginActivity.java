package com.example.hanan.riyadhmetro;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SinginActivity extends AppCompatActivity implements View.OnClickListener {


    //defining views
    private Button buttonSignIn;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignup;


    //firebase auth object
    private FirebaseAuth firebaseAuth;

    //progress dialog
    private ProgressDialog progressDialog;


    /**/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singin);

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
        //getting firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        //initializing views
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonSignIn =  findViewById(R.id.buttonSignin);
        textViewSignup = findViewById(R.id.textViewSignUp);


        progressDialog = new ProgressDialog(this);

        //attaching click listener
        buttonSignIn.setOnClickListener(this);
        textViewSignup.setOnClickListener(this);
    }

    /**/
    private boolean checkEmptyInput(String email, String password){

        boolean notEmpty = true;

        //checking if email and passwords are empty
        if(TextUtils.isEmpty(email)){
            editTextEmail.setError("Please enter email");
            notEmpty = false;
        }
        if(!isEmailValid(email)){
            editTextEmail.setError("Please enter email correctly");
            notEmpty = false;

        }

        if(TextUtils.isEmpty(password)){
            editTextPassword.setError("Please enter password");
            notEmpty = false;
        }

        return notEmpty ;

    }




    /**/
    private void userSingin(){

        String email = editTextEmail.getText().toString().trim();
        String password  = editTextPassword.getText().toString().trim();

        if(checkEmptyInput(email,password))
            singin(email,password);


    }

    /**/
    private void singin(String email, String password) {
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        //logging in the user
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        //if the task is successfull
                        if(task.isSuccessful()){
                            singinSuccessfully();
                        }
                        else {

                             singinUnsuccessfully(task);

                        }

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
        if(view == buttonSignIn){
            userSingin();
        }

        if(view == textViewSignup){

            goToSingup();
        }
    }

    /**/
    private void goToSingup() {

        Context context = SinginActivity.this;
        Class singupClass = SingupActivity.class;

        Intent intent = new Intent(context,singupClass);
        startActivity(intent);

    }

    /**/
    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}