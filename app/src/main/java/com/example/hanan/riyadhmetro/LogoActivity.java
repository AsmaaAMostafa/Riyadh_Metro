package com.example.hanan.riyadhmetro;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LogoActivity extends AppCompatActivity implements View.OnClickListener{


    private Button mUserSigninButton;
    private Button mAdmoinSigninButton;

    private TextView mSignupTextView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logo);


        initElement();
    }

    /**/
    private void initElement() {

        mUserSigninButton = findViewById(R.id.buttonSigninUser);
        mAdmoinSigninButton = findViewById(R.id.buttonSigninAdmin);
        mSignupTextView  =  findViewById(R.id.textViewSignUp);


        mSignupTextView.setOnClickListener(this);
        mAdmoinSigninButton.setOnClickListener(this);
        mUserSigninButton.setOnClickListener(this);

    }

    /**/
    @Override
    public void onClick(View view) {

        if(view == mUserSigninButton){

            goToSingin();

        }else if (view == mAdmoinSigninButton){

            goToSingin();

        }else if (view == mSignupTextView){
            goToSingup();


        }

    }

    /**/
    private void goToSingin() {


        Context context = LogoActivity.this;
        Class singinClass = SinginActivity.class;

        Intent intent = new Intent(context,singinClass);
        startActivity(intent);

    }

    /**/
    private void goToSingup() {

        Context context = LogoActivity.this;
        Class singupClass = SingupActivity.class;

        Intent intent = new Intent(context,singupClass);
        startActivity(intent);

    }


}
