package com.aliyahatzoff.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.aliyahatzoff.Api.Common;
import com.aliyahatzoff.R;
import com.aliyahatzoff.utils.Sharedhelper;
import com.razorpay.Checkout;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComptitionForm extends AppCompatActivity  {
TextView next,price;
ImageView back;
EditText user_name,intrest_et,email_et,mobile_et;
TextView dob_et;

    final Calendar myCalendar = Calendar.getInstance();
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comptition_form);
        next=findViewById(R.id.next);
        back=findViewById(R.id.back);
        price=findViewById(R.id.price);
        user_name=findViewById(R.id.user_name);
        intrest_et=findViewById(R.id.intrest_et);
        email_et=findViewById(R.id.email_et);
        mobile_et=findViewById(R.id.mobile_et);
        dob_et=findViewById(R.id.dob_et);
        user_name.setText(Sharedhelper.getKey(getApplicationContext(),"name"));
        price.setText("Rs."+" "+getIntent().getStringExtra("pricing"));
        intrest_et.setText(getIntent().getStringExtra("intrest"));
        email_et.setText(Sharedhelper.getKey(getApplicationContext(),"email"));
        mobile_et.setText(Sharedhelper.getKey(getApplicationContext(),"mobile"));
        next.setOnClickListener(this::onClick);
        back.setOnClickListener(this::onClick);
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
                new DatePickerDialog(ComptitionForm.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dob_et.setText(sdf.format(myCalendar.getTime()));
    }
    public void onPaymentSuccess(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
        updateToDatabse();
    }


    private void updateToDatabse() {
        Common.getHatzoffApi().participateForm(intrest_et.getText().toString(),Double.parseDouble(getIntent().getStringExtra("pricing"))).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    Dialog dialog=new Dialog(ComptitionForm.this);
                    dialog.setContentView(R.layout.alert_hats);
                    dialog.show();
                    dialog.setCancelable(false);
                    TextView exit=dialog.findViewById(R.id.exit);
                    exit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(ComptitionForm.this,LandingActivity.class));
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.next:
                if (user_name.getText().toString().isEmpty()){
                    user_name.requestFocus();
                    user_name.setError("Participator name can't be blank");
                    return;
                }else if (email_et.getText().toString().isEmpty()){
                    email_et.requestFocus();
                    email_et.setError("Invalid email");
                    return;
                }else if (mobile_et.getText().toString().isEmpty()){
                    mobile_et.requestFocus();
                    mobile_et.setError("Invalid mobile");
                    return;
                }else if (dob_et.getText().toString().isEmpty()){
                    dob_et.requestFocus();
                    dob_et.setError("Invalid DOB");
                    return;
                }else if (intrest_et.getText().toString().isEmpty()){
                    intrest_et.requestFocus();
                    intrest_et.setError("Invalid Interest Area");
                    return;
                }
                startPayment();
                break;

                case R.id.back:
                finish();
                break;
        }
    }
    private void startPayment() {
        final Activity activity = this;
        final Checkout co = new Checkout();
        try {
            String amnt=getIntent().getStringExtra("pricing");
            double total = Double.parseDouble(amnt);
            JSONObject options = new JSONObject();
            options.put("name", "Hatzoff");
            options.put("description", "Comptition Payment");
            options.put("currency", "INR");
            options.put("amount", total*100);
            JSONObject preFill = new JSONObject();
            preFill.put("email", Sharedhelper.getKey(getApplicationContext(), "email"));
            preFill.put("contact", Sharedhelper.getKey(getApplicationContext(), "mobile"));
            preFill.put("name", Sharedhelper.getKey(getApplicationContext(), "name"));
            options.put("prefill", preFill);
            co.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }
}