package com.nisnis.batp.logisticbuddy;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nisnis.batp.logisticbuddy.model.ItemData;
import com.nisnis.batp.logisticbuddy.model.MapData;

import java.util.ArrayList;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateOrderActivity extends AppCompatActivity {

    private static final String ORDER_TABLE = "order";
    @BindView(R.id.recipient)
    EditText recipient;

    @BindView(R.id.address)
    EditText address;

    @BindView(R.id.phone)
    EditText phone;

    @BindView(R.id.btn_submit)
    Button submitButton;

    @BindView(R.id.verify_code)
    EditText verifyCode;

    @BindView(R.id.verify_code_layout)
    View verifyCodeLayout;

    DatabaseReference mFirebaseDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);
        ButterKnife.bind(this);

        initFirebase();
        initView();

    }

    private void initFirebase() {
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    private void initView() {
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initFakeData();
                sendDataToFirebase(getParam());

            }
        });
    }

    private MapData getParam() {

        ArrayList<ItemData> listItem = new ArrayList<>();
        listItem.add(new ItemData("Barang 6"));
        listItem.add(new ItemData("Barang 7"));

        MapData mapData = new MapData();
        mapData.setAddress(address.getText().toString());
        mapData.setRecipient(recipient.getText().toString());
        mapData.setPhone(phone.getText().toString());
        mapData.setVerifyCode(verifyCode.getText().toString());
        mapData.setItem(listItem);
        mapData.setPosition(new LatLng(-6.131138,106.824011));

        return mapData;
    }

    private void sendDataToFirebase(MapData param) {
        mFirebaseDatabaseReference.child(ORDER_TABLE)
                .push()
                .setValue(param)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finish();
            }
        });
    }

    private void initFakeData() {
        recipient.setText("Nisie");
        address.setText("Jalan 4 no 4");
        phone.setText("08999991149");

        Random rnd = new Random();
        int n = 100000 + rnd.nextInt(999999);
        verifyCode.setText(String.valueOf(n));
        verifyCodeLayout.setVisibility(View.VISIBLE);

    }
}
