package com.aliyahatzoff.Activities;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.aliyahatzoff.Api.ApiClient;
import com.aliyahatzoff.Api.ApiInterface;
import com.aliyahatzoff.Api.Common;
import com.aliyahatzoff.Models.MessageOtpModel;
import com.aliyahatzoff.Models.User;
import com.aliyahatzoff.R;
import com.aliyahatzoff.utils.Functions;
import com.aliyahatzoff.utils.Sharedhelper;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    LinearLayout buttonContinue;
    private EditText editTextMobile, editTextName;
    ImageView otpname, person, phone;
    private static final int av = 0;
    private CallbackManager mCallbackManager;
    TwitterAuthClient mTwitterAuthClient;
    EditText mobile;
    String twitterEmail = "";
    private static final int RC_SIGN_IN = 123;
    private static final String TAG = "Tag";
    ActivityResultLauncher launcher;

    FirebaseAuth mAuth;
    LinearLayout twitter_login_button, loginbutton;
    private static final String EMAIL = "email";
    private FirebaseAuth.AuthStateListener mAuthListener;
    GoogleSignInClient mGoogleSignInClient;
    private int PERMISSION_CALLBACK_CONSTANT = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Twitter.initialize(this);
/*         mTwitterAuthClient= new TwitterAuthClient();
        TwitterAuthConfig mTwitterAuthConfig = new TwitterAuthConfig(getString(R.string.com_twitter_sdk_android_CONSUMER_KEY),
                getString(R.string.com_twitter_sdk_android_CONSUMER_SECRET));
        TwitterConfig twitterConfig = new TwitterConfig.Builder(this)
                .twitterAuthConfig(mTwitterAuthConfig)
                .build();
        Twitter.initialize(twitterConfig);*/
        // FacebookSdk.setApplicationId(getString(R.string.facebook_app_id));
        // FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        loginbutton=findViewById(R.id.loginbutton);
        twitter_login_button=findViewById(R.id.twitter_login_button);
        Permissions();

        mobile = findViewById(R.id.mobile);
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {

                        if (result.getResultCode() == RC_SIGN_IN) {
                            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                            try {
                                // Google Sign In was successful, authenticate with Firebase
                                GoogleSignInAccount account = task.getResult(ApiException.class);
                                firebaseAuthWithGoogle(account);
                            } catch (ApiException e) {
                                // Google Sign In failed, update UI appropriately
                                Toast.makeText(LoginActivity.this, "Google sign in failed" + e, Toast.LENGTH_SHORT).show();
                                Log.w(TAG, "Google sign in failed", e);
                                // ...
                            }
                        } else {

                            mCallbackManager.onActivityResult(result.getResultCode(), result.getResultCode(), result.getData());
                        }
                    }
                });
        findViewById(R.id.signup).setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.login).setOnClickListener(v -> {
            if (mobile.getText().toString().length() != 10) {
                mobile.setError("Invalid Mobile Number");
                mobile.requestFocus();
                return;
            }
            requestOtp();
        });


        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("848089208318-1cpaod8akimunvgo17367ukvps0bml9n.apps.googleusercontent.com")
                .requestEmail()
                .build();
        //intialize mgooglesigninclient
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //initialize
        mAuth = FirebaseAuth.getInstance();

        twitter_login_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //  System.out.println("Aa")
                signIn();
            }
        });






        mCallbackManager = CallbackManager.Factory.create();
        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginbutton.setEnabled(false);
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile"));
                LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(TAG, "facebook:onSuccess:" + loginResult);
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "facebook:onCancel");
                        // ...
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(TAG, "facebook:onError", error);
                        // ...
                    }
                });
            }
        });
/*
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    this.getPackageName(),                  //Insert your own package name.
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            System.out.println("msg"+e.getMessage());

        } catch (NoSuchAlgorithmException e) {
            System.out.println("msg2"+e.getMessage());

        }
        byte[] sha1 = {
                0x44, (byte)0xB8, (byte)0xB6, 0x0A, (byte)0xF8, 0x0D, (byte)0x90, (byte)0xC7, 0x6E, 0x28, (byte)0xC5, (byte)0x16, (byte)0x19, (byte)0x57, (byte)0xAC, 0x7B, (byte)0x5C, (byte)0x16, (byte)0x32, (byte)0x6F
        };
        Log.e("keyhash==", Base64.encodeToString(sha1, Base64.NO_WRAP));*/
    }

    public void signIn() {
        //Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        //startActivityForResult(signInIntent, RC_SIGN_IN);
        launcher.launch(signInIntent);
    }
    /*
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);


            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            if (requestCode == RC_SIGN_IN) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account);
                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    Toast.makeText(this, "Google sign in failed" + e, Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Google sign in failed", e);
                    // ...
                }
            } else {
                mCallbackManager.onActivityResult(requestCode, resultCode, data);
            }
        }
    */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                Functions.showLoadingDialog(LoginActivity.this);
                                gettoken(user.getDisplayName(), user.getEmail(), "Google", user.getUid(), user.getPhotoUrl().toString(),
                                        Sharedhelper.getKey(LoginActivity.this, "ftoken"));



                            }
                            //Intent intent = new Intent(LoginActivity.this, LandingActivity.class);
                            // startActivity(intent);

                            // Sign in success, update UI with the signed-in user's information
                            //  Toast.makeText(LoginActivity.this, "signInWithCredential:success", Toast.LENGTH_SHORT).show();
                            //Log.d(TAG, "signInWithCredential:success");

                            //FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void Permissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_CALLBACK_CONSTANT);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            mAuth.signOut();
        }
    }


    void gettoken(String name, String email, String logintype, String fid, String profilepic, String token) {
        Common.getHatzoffApi().social_signUp(name, email, logintype, fid, profilepic, token).enqueue(new retrofit2.Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                try {
                    Functions.cancelLoading();
                    System.out.println(response.errorBody().string());
                } catch (Exception e) {
                }
                if (response.isSuccessful()) {
                    Functions.cancelLoading();
                    User user = response.body();
                    // System.out.println(user.getToken());

                    Sharedhelper.putKey(LoginActivity.this, "name", user.getName());
                    Sharedhelper.putKey(LoginActivity.this, "loginid", String.valueOf(user.getId()));
                    Sharedhelper.putKey(LoginActivity.this, "email", user.getEmail());
                    Sharedhelper.putKey(LoginActivity.this, "mobile", user.getMobile());
                    Sharedhelper.putKey(LoginActivity.this, "profilepic", user.getProfile_pic());
                    Sharedhelper.putKey(LoginActivity.this, "token", user.getToken());

                    Sharedhelper.putKey(LoginActivity.this, "ltype", logintype);
                    Sharedhelper.putKey(LoginActivity.this, "interest", user.getInterest());
                    Sharedhelper.putKey(LoginActivity.this, "like", user.getLike());
                    Sharedhelper.putKey(LoginActivity.this, "follow", user.getFollow());
                    Sharedhelper.putKey(LoginActivity.this, "following", user.getFollowing());
                    finish();
                    startActivity(new Intent(LoginActivity.this, LandingActivity.class));

                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Functions.cancelLoading();

            }


        });
    }

 /*   private void signInToFirebaseWithTwitterSession(TwitterSession session) {
        AuthCredential credential = TwitterAuthProvider.getCredential(session.getAuthToken().token,
                session.getAuthToken().secret);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Auth firebase twitter failed", Toast.LENGTH_LONG).show();
                        } else {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                Functions.showLoadingDialog(LoginActivity.this);
                                gettoken(user.getDisplayName(), user.getEmail(), "Twitter", user.getUid(), user.getPhotoUrl().toString(),
                                        Sharedhelper.getKey(LoginActivity.this, "ftoken"));
                            }
                        }
                    }
                });
    }*/

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                Functions.showLoadingDialog(LoginActivity.this);

                                gettoken(user.getDisplayName(), user.getEmail(), "Facebook", user.getUid(), user.getPhotoUrl().toString(),
                                        Sharedhelper.getKey(LoginActivity.this, "ftoken"));


                            }
                        }
                        else {
                            // If sign-in fails, a message will display to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Functions.cancelLoading();
    }
    private void requestOtp() {
        ApiInterface apiInterface= ApiClient.getRetrofitsms().create(ApiInterface.class);
        apiInterface.requestOTPSMS(mobile.getText().toString()).enqueue(new Callback<MessageOtpModel>() {
            @Override
            public void onResponse(Call<MessageOtpModel> call, Response<MessageOtpModel> response) {
                if (response.isSuccessful()){
                    String sessionId=response.body().getDetails();
                    Intent intent=new Intent(LoginActivity.this,OtpVerification.class);
                    intent.putExtra("mobile",mobile.getText().toString());
                    intent.putExtra("sessionId",sessionId);
                    intent.putExtra("interest","login");

                    startActivity(intent);


                }
            }
            @Override
            public void onFailure(Call<MessageOtpModel> call, Throwable t) {

            }
        });
    }
}