package com.sate7.wlj.m2mclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.sate7.wlj.m2mclient.databinding.ActivityMainBinding;
import com.sate7.wlj.m2mclient.util.SmsHelper;

import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {
    private ActivityMainBinding binding;
    private final String TAG = "MainActivity";
    private final int SMS_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        registerSms();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Log.d(TAG, "onPermissionsGranted ... " + requestCode + "," + perms);
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Log.d(TAG, "onPermissionsDenied ... " + requestCode + "," + perms);
    }

    @Override
    public void onRationaleAccepted(int requestCode) {
        Log.d(TAG, "onRationaleAccepted ... " + requestCode);
    }

    @Override
    public void onRationaleDenied(int requestCode) {
        Log.d(TAG, "onRationaleDenied ... " + requestCode);
    }

    public void onClick(View view) {
        if (binding.reboot == view && !TextUtils.isEmpty(binding.etNumber.getText())) {
            EasyPermissions.requestPermissions(
                    new PermissionRequest.Builder(this, SMS_REQUEST_CODE, new String[]{Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS})
                            .setRationale(R.string.permission_rationale)
                            .setPositiveButtonText(R.string.permission_ok)
                            .setNegativeButtonText(R.string.permission_cancel)
                            .build());
            SmsHelper.rebootBySms(this, binding.etNumber.getText().toString().trim());
        } else {
            showToast("请输入电话号码 ...");
        }
    }

    private void showToast(String msg) {
        Toast.makeText(this, "" + msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterSms();
    }

    private SmsSendStateReceiver smsReceiver;
    private IntentFilter mSmsFilter = new IntentFilter();

    private void registerSms() {
        mSmsFilter.addAction(SmsHelper.SMS_SEND_SUCCESS);
        mSmsFilter.addAction(SmsHelper.SMS_DELIVERED_SUCCESS);
        registerReceiver(smsReceiver, mSmsFilter);
    }

    private void unRegisterSms() {
        unregisterReceiver(smsReceiver);
    }

    private class SmsSendStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive ... " + intent.getAction());
            if (SmsHelper.SMS_SEND_SUCCESS.equals(intent.getAction())) {
                Toast.makeText(context, "send success ... ", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
