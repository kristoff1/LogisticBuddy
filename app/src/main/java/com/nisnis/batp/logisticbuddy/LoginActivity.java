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

    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        initializeFirebase();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username.setText("master@batp.com");
                password.setText("master");
                checkRole();
                mFirebaseAuth.signInWithEmailAndPassword(username.getText().toString(), password.getText().toString());
                startActivity(new Intent(LoginActivity.this, MenuActivity.class));
                finish();
            }
        });
    }

    private void checkRole() {
        switch (username.getText().toString()){
            case SessionHandler.DRIVER:
                SessionHandler.setSession(this, SessionHandler.DRIVER);
                break;
            case SessionHandler.CLIENT:
                SessionHandler.setSession(this, SessionHandler.CLIENT);
                break;
            case SessionHandler.SERVER:
                SessionHandler.setSession(this,SessionHandler.SERVER);
                break;
            default:
                SessionHandler.setSession(this, SessionHandler.MASTER);
                break;
        }
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
