package com.aliyahatzoff.Activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.aliyahatzoff.Api.Common;
import com.aliyahatzoff.Models.UserInfoModel;
import com.aliyahatzoff.R;
import com.aliyahatzoff.utils.Functions;
import com.aliyahatzoff.utils.Sharedhelper;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {
    FrameLayout verify_mobile;
    EditText mobile_number,et_name,et_email,et_userid,et_bio;
    LinearLayout birth;
    TextView dob_et;
    String  secret, access, bucket, imageFilePath;
    ImageView profilepic;
    AmazonS3Client s3Client;
    GeneratePresignedUrlRequest request;
    URL objectURL;

    final Calendar myCalendar = Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        et_name=findViewById(R.id.et_name);
        et_email=findViewById(R.id.et_email);
        et_userid=findViewById(R.id.userid);
        et_bio=findViewById(R.id.et_bio);
        mobile_number=findViewById(R.id.mobile_number);
        dob_et=findViewById(R.id.dob_et);
        profilepic=findViewById(R.id.profilepic);
        secret = getResources().getString(R.string.s3_secret);
        access = getResources().getString(R.string.s3_access_key);
        bucket = getResources().getString(R.string.s3_bucket);
        BasicAWSCredentials credentials = new BasicAWSCredentials(access, secret);
        s3Client = new AmazonS3Client(credentials);
        s3Client.setRegion(Region.getRegion("ap-south-1"));


        findViewById(R.id.save).setOnClickListener(this::onClick);
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        dob_et.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(EditProfileActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        if (Sharedhelper.getKey(EditProfileActivity.this, "profilepic").contains("http"))
            imageFilePath = Sharedhelper.getKey(EditProfileActivity.this, "profilepic");
        else {
            // imageFilePath = ApiClient.image_URL + "profile/" + Sharedhelper.getKey(getContext(), "profilepic");
          //  request = new GeneratePresignedUrlRequest(bucket,Sharedhelper.getKey(EditProfileActivity.this, "profilepic") );
           // objectURL = s3Client.generatePresignedUrl(request);

            imageFilePath = getResources().getString(R.string.cloudfront)+"/"+Sharedhelper.getKey(EditProfileActivity.this, "profilepic") ;

        }
        try {
            Picasso.get().
                    load(imageFilePath)
                    .resize(150, 150)
                    .placeholder(getResources().getDrawable(R.drawable.profile_image_placeholder))
                    .into(profilepic);

        } catch (Exception e) {

        }

    }
    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dob_et.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.save:
                updateInfo(et_name.getText().toString(),et_email.getText().toString(),mobile_number.getText().toString(),
                        dob_et.getText().toString(),et_bio.getText().toString(),et_userid.getText().toString());
                break;
        }
    }

    private void updateInfo(String name, String email, String mobile, String dob,String bio,String userid) {
        Functions.showLoadingDialog(this);
        Common.getHatzoffApi().updateprofile(name,email,mobile,dob,bio,userid).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    System.out.println(response.errorBody().string());
                }catch(Exception e){}
                if (response.isSuccessful()){

                    Functions.cancelLoading();
                    Functions.Show_Alert(EditProfileActivity.this,"Edit PRofile","Profilr Updated");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.cancelLoading();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        getuserinfo();
    }
    private void getuserinfo() {
        Functions.showLoadingDialog(this);
        Common.getHatzoffApi().userInfo().enqueue(new Callback<UserInfoModel>() {
            @Override
            public void onResponse(Call<UserInfoModel> call, Response<UserInfoModel> response) {
                try{
                    System.out.println(response.errorBody().string());
                }catch(Exception e){}
                if (response.isSuccessful()){
                    Functions.cancelLoading();
                    et_name.setText(response.body().getName());
                    mobile_number.setText(response.body().getMobile());
                    et_email.setText(response.body().getEmail());
                    et_userid.setText(response.body().getUserid());
                    et_bio.setText(response.body().getBio());
                    dob_et.setText(response.body().getBirthday());
                }
            }

            @Override
            public void onFailure(Call<UserInfoModel> call, Throwable t) {
                Functions.cancelLoading();
            }
        });
    }

    public void onBackPressed(View view) {
        super.onBackPressed();
        finish();
    }
}