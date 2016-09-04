package com.nisnis.batp.logisticbuddy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MenuActivity extends AppCompatActivity {

    @BindView(R.id.btn_start_driving)
    Button startDriving;

    @BindView(R.id.btn_create_order)
    Button createOrder;

    @BindView(R.id.logout)
    Button logout;

    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initializeFirebase();
        initializeMenu(this);

    }

    private void initializeMenu(Context context) {
        switch (SessionHandler.getSession(context)){
            case SessionHandler.DRIVER:
                startDriving.setVisibility(View.VISIBLE);
                createOrder.setVisibility(View.GONE);
                break;
            case SessionHandler.CLIENT:
                startDriving.setVisibility(View.GONE);
                createOrder.setVisibility(View.VISIBLE);
                break;
            case SessionHandler.SERVER:
                startDriving.setVisibility(View.GONE);
                createOrder.setVisibility(View.GONE);
                break;
            default:
                startDriving.setVisibility(View.VISIBLE);
                createOrder.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void initializeFirebase() {

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        } else {
            initView();
        }

    }

    private void initView() {
        startDriving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuActivity.this, DriverActivity.class));
            }
        });
        createOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuActivity.this, CreateOrderActivity.class));
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFirebaseAuth.signOut();
                startActivity(new Intent(MenuActivity.this, LoginActivity.class));
            }
        });
    }

}
