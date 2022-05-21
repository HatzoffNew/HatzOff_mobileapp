package com.aliyahatzoff.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.aliyahatzoff.Api.ApiClient;
import com.aliyahatzoff.Api.ApiInterface;
import com.aliyahatzoff.Api.Common;
import com.aliyahatzoff.Models.MessageOtpModel;
import com.aliyahatzoff.R;
import com.aliyahatzoff.utils.Functions;
import com.facebook.login.LoginManager;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {
    LinearLayout buttonContinue;
    private EditText editTextMobile, editTextName;
    private static final int av = 0;
    String mobile,name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        buttonContinue = findViewById(R.id.buttonContinue);
        editTextMobile = (EditText) findViewById(R.id.mobile);
        editTextName = (EditText) findViewById(R.id.name);
        LoginManager.getInstance().logOut();
        buttonContinue.setOnClickListener(v12 -> {
             mobile = editTextMobile.getText().toString().trim();
             name = editTextName.getText().toString().trim();


            if (name.isEmpty()) {
                editTextName.setError("Enter Your Name");
                editTextName.requestFocus();
                return;
            }
            if (mobile.isEmpty() || mobile.length() < 10) {
                editTextMobile.setError("Enter your number");
                editTextMobile.requestFocus();
                return;
            }
            Functions.showLoadingDialog(SignupActivity.this);
            Common.getHatzoffApi().createuser(editTextName.getText().toString(),
                    editTextMobile.getText().toString(),"Mobile").enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try{
                        Log.d("Login",response.errorBody().string());}catch (Exception e){}
                    if(response.isSuccessful())
                    {
                        requestOtp();
                    }
                    if(response.code()==401)
                    {
                        Functions.cancelLoading();
                        Functions.Show_Alert(SignupActivity.this,"Signup","Mobile Number Already Exist");
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Functions.cancelLoading();
                    Functions.Show_Alert(SignupActivity.this,"Signup","Somthing Wrong");

                }
            });


        });

    }

    private void requestOtp() {
        ApiInterface apiInterface= ApiClient.getRetrofitsms().create(ApiInterface.class);
        apiInterface.requestOTPSMS(mobile).enqueue(new Callback<MessageOtpModel>() {
            @Override
            public void onResponse(Call<MessageOtpModel> call, Response<MessageOtpModel> response) {
                if (response.isSuccessful()){
                    String sessionId=response.body().getDetails();

                    Intent intent = new Intent(SignupActivity.this, OtpVerification.class);
                    intent.putExtra("mobile", mobile);
                    intent.putExtra("name", name);
                    intent.putExtra("sessionId",sessionId);
                    intent.putExtra("interest", "signup");
                    startActivity(intent);






                }
            }
            @Override
            public void onFailure(Call<MessageOtpModel> call, Throwable t) {

            }
        });
    }
}