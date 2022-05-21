package com.aliyahatzoff.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.RequiresApi;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

public class OtpReceiver extends BroadcastReceiver {
    private static EditText edit_otp;
    public  SmsBroadcastReceiverListener smsBroadcastReceiverListener;
    public void setEdit_otp(EditText editText){
        edit_otp=editText;
    }

    public static EditText getEdit_otp() {
        return edit_otp;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction() == SmsRetriever.SMS_RETRIEVED_ACTION) {
            Bundle extras = intent.getExtras();
            Status smsRetrieverStatus = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
            switch (smsRetrieverStatus.getStatusCode()) {
                case CommonStatusCodes.SUCCESS:
                    Intent messageIntent = extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT);
                    smsBroadcastReceiverListener.onSuccess(messageIntent);
                    break;
                case CommonStatusCodes.TIMEOUT:
                    smsBroadcastReceiverListener.onFailure();
                    break;
            }
        }


           /* SmsMessage[] messages= Telephony.Sms.Intents.getMessagesFromIntent(intent);
            for ( SmsMessage smsMessage:messages){
                String otp=smsMessage.getMessageBody();
                Pattern pattern = Pattern.compile("(\\d{6})");
                Matcher matcher = pattern.matcher(otp);
                String val = "";
                if (matcher.find()) {
                    val = matcher.group(0);
                    if (null==val){
                        return;
                    }
                    edit_otp.setText(val);
                    Toast.makeText(context, val, Toast.LENGTH_SHORT).show();
                }
        }*/

    }
    public interface SmsBroadcastReceiverListener {
        void onSuccess(Intent intent);
        void onFailure();
    }
}
