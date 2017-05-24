package com.example.sagar.deliverybot;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText mNickname, mEmail, mPassword;
    private Button mSubmitButton;
    private boolean isValid = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mNickname = (EditText) findViewById(R.id.nickname);
        mEmail = (EditText) findViewById(R.id.register_email);
        mPassword = (EditText) findViewById(R.id.password);
        mSubmitButton = (Button) findViewById(R.id.submit);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isValid = true;
                createAccount();
            }
        });
    }

    public void createAccount(){
        String toastMessage = "";
        if(mPassword.getText().toString() == null || mPassword.getText().toString().equals("") || mPassword.getText().toString().length() < 3){
            toastMessage = getResources().getString(R.string.password_error);
            isValid = false;
        }
        if(mEmail.getText().toString() == null || mEmail.getText().toString().equals("")){
            toastMessage = getResources().getString(R.string.email_error);
            isValid = false;
        }
        if(mNickname.getText().toString() == null || mNickname.getText().toString().equals("") || mNickname.getText().toString().length() == 0){
            toastMessage = getResources().getString(R.string.nickname_error);
            isValid = false;
        }

        if(isValid){
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference firebaseDatabaseReference = firebaseDatabase.getReference();
            firebaseDatabaseReference.child("deliverybot").child("drivers").orderByKey().equalTo(mNickname.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.hasChildren())
                        registerUser();
                    else
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.username_exists), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });

        }else{
            Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_LONG).show();
        }
    }

    public void registerUser(){
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();

        //create user
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Toast.makeText(RegisterActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                        if (!task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, getResources().getString(R.string.profile_created_error_message),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                            DatabaseReference firebaseDatabaseReference = firebaseDatabase.getReference();
                            firebaseDatabaseReference.child("deliverybot/drivers/" + mNickname.getText().toString().trim())
                                    .setValue(mEmail.getText().toString().trim());
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.profile_created_message), Toast.LENGTH_LONG).show();
                            startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                            finish();
                        }
                    }
                });
    }
}
