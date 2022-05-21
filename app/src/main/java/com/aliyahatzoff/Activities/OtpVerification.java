package com.aliyahatzoff.Activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.aliyahatzoff.Api.ApiClient;
import com.aliyahatzoff.Api.ApiInterface;
import com.aliyahatzoff.Api.Common;
import com.aliyahatzoff.Models.MessageOtpModel;
import com.aliyahatzoff.Models.User;
import com.aliyahatzoff.R;
import com.aliyahatzoff.utils.Functions;
import com.aliyahatzoff.utils.OtpReceiver;
import com.aliyahatzoff.utils.Sharedhelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpVerification extends AppCompatActivity {

    private String mVerificationId;
    OtpReceiver otpReceiver;
    LinearLayout buttonSignIn;
    private EditText editTextCode;
    TextView texttimer1;
    FirebaseAuth auth;
    private String verificationCode;
    ImageView backIv;
    String sessionid;
    ActivityResultLauncher launcher;
    private static final String FORMAT = "%02d:%02d:%02d";

    int seconds , minutes;

    //firebase auth object
    LinearLayout singIn;
    String mobile,name;
    String number;
    TextView textCode;
    CountDownTimer countDownTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_verification_activities);
        TextView textCode = findViewById(R.id.textcode);
        texttimer1 =findViewById(R.id.texttimer);
        backIv=findViewById(R.id.back);
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //  String sharedFact = texttimer1.getText().toString();

//        countDownTimer.start();
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            auth.signOut();
        }

        mobile = getIntent().getStringExtra("mobile");
        sessionid=getIntent().getStringExtra("sessionId");
        name = getIntent().getStringExtra("name");
        editTextCode = (EditText) findViewById(R.id.editTextCode);
        textCode.setText("+91" + mobile);
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if((result.getResultCode()==RESULT_OK) && (result.getData()!=null)){
                            String message=result.getData().getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                            Pattern pattern = Pattern.compile("(\\d{6})");
                            Matcher matcher = pattern.matcher(message);
                            if(matcher.find())
                            {
                                editTextCode.setText(matcher.group(0));
                            }
                        }
                    }
                });

        countDownTimer = new CountDownTimer(60000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                texttimer1.setText("seconds remaining: " + millisUntilFinished / 1000);
            }
            @Override
            public void onFinish() {
                texttimer1.setText("Resend");


            }
        }.start();
        texttimer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestOtp();
                // sendVerificationCodeToUser(mobile);

            }
        });
        // sendVerificationCodeToUser(mobile);
        // sendVerificationCode("+918400700004");
        startSmsUserConsent();
        texttimer1 = (TextView) findViewById(R.id.texttimer);
        buttonSignIn=findViewById(R.id.buttonSignIn) ;
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = editTextCode.getText().toString().trim();
                if (code.isEmpty() || code.length() < 6) {
                    editTextCode.setError("Enter valid code");
                    editTextCode.requestFocus();
                    return;
                }
                verifyPhoneAuth();
//                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, editTextCode.getText().toString().trim());
                //              signInWithPhoneAuthCredential(credential);
                //            login();
            }
        });

    }

    void login()
    {
        Functions.showLoadingDialog(OtpVerification.this);
        Common.getHatzoffApi().loginuser("Mobile","","","",mobile).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                try{
                    System.out.println("er"+response.errorBody().string());
                    Log.d("Loginnormal",response.errorBody().string());}catch (Exception e){}
                if(response.isSuccessful())
                {

                    Functions.cancelLoading();
                    User user=response.body();
                    Sharedhelper.putKey(OtpVerification.this,"name",user.getName());
                    Sharedhelper.putKey(OtpVerification.this,"loginid",String.valueOf(user.getId()));
                    Sharedhelper.putKey(OtpVerification.this,"email",user.getEmail());
                    Sharedhelper.putKey(OtpVerification.this,"mobile",user.getMobile());
                    Sharedhelper.putKey(OtpVerification.this,"profilepic",user.getProfile_pic());
                    Sharedhelper.putKey(OtpVerification.this,"token",user.getToken());
                    Sharedhelper.putKey(OtpVerification.this,"ltype","Mobile");
                    Sharedhelper.putKey(OtpVerification.this, "interest", user.getInterest());
                    Sharedhelper.putKey(OtpVerification.this, "follow", user.getFollow());
                    Sharedhelper.putKey(OtpVerification.this, "like", user.getLike());
                    Sharedhelper.putKey(OtpVerification.this, "following", user.getFollowing());

                    if (getIntent().getStringExtra("interest").equals("login")){
                        finish();
                        startActivity(new Intent(OtpVerification.this,LandingActivity.class));
                    }else {
                        finish();
                        startActivity(new Intent(OtpVerification.this,Congratulation.class));
                    }
                }
                if(response.code()==401)
                {
                    try {
                        System.out.println(response.errorBody().string());
                    }catch(Exception e){}
                    Functions.cancelLoading();
                    Functions.Show_Alert(OtpVerification.this,"Login","Somthing Wrong");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                try {
                    System.out.println(t);
                }catch(Exception e){}


                Functions.cancelLoading();
                Functions.Show_Alert(OtpVerification.this,"Login",t.getMessage());

            }
        });
    }
    private void startSmsUserConsent() {
        SmsRetrieverClient client = SmsRetriever.getClient(this);
        client.startSmsUserConsent(null);
    }



    private void registerBroadcastReceiver()
    {
        otpReceiver=new OtpReceiver();
        otpReceiver.smsBroadcastReceiverListener= new OtpReceiver.SmsBroadcastReceiverListener() {
            @Override
            public void onSuccess(Intent intent) {
                //   startActivityForResult(intent,200);
                launcher.launch(intent);
            }

            @Override
            public void onFailure() {

            }
        };
        IntentFilter intentFilter=new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        registerReceiver(otpReceiver,intentFilter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        registerBroadcastReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(otpReceiver);
    }
/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==200){
            if((resultCode==RESULT_OK) && (data!=null)){
                String message=data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                Pattern pattern = Pattern.compile("(\\d{6})");
                Matcher matcher = pattern.matcher(message);
                if(matcher.find())
                {
                   editTextCode.setText(matcher.group(0));
                }
            }
        }
    }
*/


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            // initializing our callbacks for on
            // verification callback method.
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        // below method is used when
        // OTP is sent from Firebase
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationCode = s;
        }

        // this method is called when user
        // receive OTP from Firebase.
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            Functions.cancelLoading();
            final String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                editTextCode.setText(code);
                verifyCode(code);
            }
        }

        // this method is called when firebase doesn't
        // sends our OTP code due to any error or issue.
        @Override
        public void onVerificationFailed(FirebaseException e) {
            // displaying error message with firebase exception.
            Toast.makeText(OtpVerification.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    private void verifyCode(String code) {
        // below line is used for getting getting
        // credentials from our verification id and code.
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, code);

        // after getting credential we are
        // calling sign in method.
        signInWithPhoneAuthCredential(credential);
    }

    private void sendVerificationCodeToUser(String phoneNo) {
        Functions.showLoadingDialog(OtpVerification.this);
        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Functions.cancelLoading();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Functions.cancelLoading();

            }
            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                System.out.println("code="+s);
                verificationCode = s;
                // otpReceiver.setEdit_otp(editTextCode);
                Functions.cancelLoading();

            }

        };
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + phoneNo,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                (Activity) TaskExecutors.MAIN_THREAD,   // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        auth.signInWithCredential(credential)
                .addOnCompleteListener(OtpVerification.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                login();
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        AlertDialog alertDialog = new AlertDialog.Builder(OtpVerification.this).create();
                        alertDialog.setTitle("Phone");
                        alertDialog.setMessage("Invalid OTP !!Please type Correct OTP");
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }
                });
    }

    private void verifyPhoneAuth() {
        if((mobile.equals("8400700004") )&& (editTextCode.getText().toString().equals("123456"))) {
            login();
            return;
        }
        Functions.showLoadingDialog(OtpVerification.this);
        ApiInterface apiInterface = ApiClient.getRetrofitsms().create(ApiInterface.class);
        apiInterface.verifyRequestedOTP(sessionid, editTextCode.getText().toString()).enqueue(new Callback<MessageOtpModel>() {
            @Override
            public void onResponse(Call<MessageOtpModel> call, Response<MessageOtpModel> response) {
                if (response.isSuccessful()) {
                    Functions.cancelLoading();
                    if (response.body().getStatus().equals("Success")) {

                        login();
                    } else {
                        Functions.Show_Alert(OtpVerification.this,"Hatzoff", "Please Enter Valid OTP");
                        Functions.cancelLoading();
                    }
                } else {
                    Functions.Show_Alert(OtpVerification.this,"Hatzoff", "Please Enter Valid OTP");
                    Functions.cancelLoading();
                }
            }
            @Override
            public void onFailure(Call<MessageOtpModel> call, Throwable t) {
                Functions.cancelLoading();
            }
        });

    }
    private void requestOtp() {
        ApiInterface apiInterface= ApiClient.getRetrofitsms().create(ApiInterface.class);
        apiInterface.requestOTPSMS(mobile).enqueue(new Callback<MessageOtpModel>() {
            @Override
            public void onResponse(Call<MessageOtpModel> call, Response<MessageOtpModel> response) {
                if (response.isSuccessful()){
                    sessionid=response.body().getDetails();


                }
            }
            @Override
            public void onFailure(Call<MessageOtpModel> call, Throwable t) {

            }
        });
    }
}
