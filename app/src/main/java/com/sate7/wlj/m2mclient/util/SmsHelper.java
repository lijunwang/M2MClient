package com.sate7.wlj.m2mclient.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class SmsHelper {
    private static final String TAG = "SmsHelper";
    public static final String SMS_SEND_SUCCESS = "com.wlj.sms.send.ok";//send success
    public static final String SMS_DELIVERED_SUCCESS = "com.wlj.sms.deliver.ok";//deliver success

    public static void sendSms(Context context, String number, String content) {
        Log.d(TAG, "sendSms ... " + number + "," + content);
        Intent sendIntent = new Intent(SMS_SEND_SUCCESS);
        Intent deliveryIntent = new Intent(SMS_DELIVERED_SUCCESS);
        sendIntent.putExtra("phoneNumber", number);
        PendingIntent sendPI = PendingIntent.getBroadcast(context, 0, sendIntent, 0);
        PendingIntent deliveryPendingIntent = PendingIntent.getBroadcast(context, 0, deliveryIntent, 0);
        SmsManager smsManager = SmsManager.getDefault();
        if (content.length() >= 70) {
            ArrayList<String> msgs = smsManager.divideMessage(content);
            ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();
            ArrayList<PendingIntent> deliveryIntents = new ArrayList<PendingIntent>();
            for (int i = 0; i < msgs.size(); i++) {
                sentIntents.add(sendPI);
                deliveryIntents.add(deliveryPendingIntent);
            }
            smsManager.sendMultipartTextMessage(number, null, msgs, sentIntents, deliveryIntents);
        } else {
            smsManager.sendTextMessage(number, null, content, sendPI, deliveryPendingIntent);
        }
    }

    public static void rebootBySms(Context context, String number) {
        sendSms(context, number, "restart");
    }
}
