package com.example.sagar.deliverybot;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private EditText mLoginUsername, mLoginPassword;
    private TextView mErrorText;
    private Button mUserLoginButton, mUserRegisterButton;
    private FirebaseAuth mAuth;
    private boolean isValid;
    private String errorMessage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
        }else {
            setContentView(R.layout.activity_main);
            mLoginUsername = (EditText) findViewById(R.id.login_username);
            mLoginPassword = (EditText) findViewById(R.id.login_password);
            mErrorText = (TextView) findViewById(R.id.login_error_text);
            mErrorText.setText("");
            mUserLoginButton = (Button) findViewById(R.id.login_submit);
            mUserLoginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loginUser();
                }
            });

            mUserRegisterButton = (Button) findViewById(R.id.register_submit);
            mUserRegisterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    registerUser();
                }
            });
        }
    }

    public void registerUser(){
        startActivity(new Intent(MainActivity.this, RegisterActivity.class));
    }

    public void loginUser(){
        isValid = true;
        String email = mLoginUsername.getText().toString();
        final String password = mLoginPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            isValid = false;
            errorMessage = "Enter email address!";
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            isValid = false;
            errorMessage = "Password: minimum 6 characters!";
        }

        if(isValid) {
            //authenticate user
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                // remove task.getException in production environment
                                mErrorText.setText("Authentication Failed");
                            } else {
                                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                startActivity(intent);
                                //finish();
                            }
                        }
                    });
        }
        else
            mErrorText.setText(errorMessage);
    }
}
