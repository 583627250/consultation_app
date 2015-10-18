package com.consultation.app.util;

import com.consultation.app.listener.ConsultationCallbackHandler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CaseCountBroadcastReceiver extends BroadcastReceiver {

    private static ConsultationCallbackHandler callbackHandler;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(callbackHandler != null){
            callbackHandler.onSuccess(intent.getStringExtra("type"), intent.getIntExtra("count", 0));
        }
    }

    public static void setHander(ConsultationCallbackHandler handler) {
        callbackHandler=handler;
    }
}
