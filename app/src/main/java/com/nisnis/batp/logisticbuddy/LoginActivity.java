package com.nisnis.batp.logisticbuddy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.username)
    EditText username;

    @BindView(R.id.password)
    EditText password;

    @BindView(R.id.btn_login)
    Button login;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        initializeFirebase();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username.setText("nisietest1@gmail.com");
                password.setText("nisienisie");

                mFirebaseAuth.signInWithEmailAndPassword(username.getText().toString(), password.getText().toString());
                startActivity(new Intent(LoginActivity.this, MenuActivity.class));
                finish();
            }
        });
    }

    private void initializeFirebase() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser != null) {
            startActivity(new Intent(LoginActivity.this, MenuActivity.class));
            finish();
        }
    }
}
