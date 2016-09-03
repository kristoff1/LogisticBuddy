package com.nisnis.batp.logisticbuddy;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nisie on 9/3/16.
 */
public class ConfirmDialog extends DialogFragment {

    interface OnConfirmOrderListener{
        void onConfirm();
    }

    @BindView(R.id.btn_verify)
    Button verifyButton;

    @BindView(R.id.verify_code)
    EditText verifyCode;

    OnConfirmOrderListener listener;

    public static ConfirmDialog createInstance() {
        ConfirmDialog fragment = new ConfirmDialog();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(getFragmentLayout(), container, false);
    }

    private int getFragmentLayout() {
        return R.layout.confirm_dialog;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        initView(view);
        setViewListener();
    }

    private void initView(View view) {
        //// TODO: 9/3/16 SET ORDER DETAIL AND ITEMS
    }

    private void setViewListener() {
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyCode.setText("123456");
                if(verifyCode.getText().toString().equals("123456")){
                    Toast.makeText(getActivity(), "You have finished delivering this order!",Toast.LENGTH_SHORT).show();
                    listener.onConfirm();
                    dismiss();
                }else{
                    Toast.makeText(getActivity(), "Wrong Verification code!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        super.onResume();
    }

    public void setListener(OnConfirmOrderListener listener) {
        this.listener = listener;
    }
}
