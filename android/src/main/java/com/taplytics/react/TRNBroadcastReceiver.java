package com.taplytics.react;

import android.content.Context;
import android.content.Intent;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.taplytics.sdk.TLGcmBroadcastReceiver;

/**
 * Created by Adam on 2017-01-30.
 */

public class TRNBroadcastReceiver extends TLGcmBroadcastReceiver {
    @Override
    public void pushOpened(Context context, Intent intent) {
        WritableMap data = Arguments.createMap();
        data.putString("value", intent.getExtras().getString("custom_keys"));
        if (TaplyticsReactModule.getInstance() != null) {
            TaplyticsReactModule.getInstance().sendEvent("pushOpened", data);
        }
        super.pushOpened(context, intent);
    }
    @Override
    public void pushDismissed(Context context, Intent intent) {
        WritableMap data = Arguments.createMap();
        data.putString("value", intent.getExtras().getString("custom_keys"));
        if (TaplyticsReactModule.getInstance() != null) {
            TaplyticsReactModule.getInstance().sendEvent("pushDismissed", data);
        }
    }
    @Override
    public void pushReceived(Context context, Intent intent) {
        WritableMap data = Arguments.createMap();
        data.putString("value", intent.getExtras().getString("custom_keys"));
        if (TaplyticsReactModule.getInstance() != null) {
            TaplyticsReactModule.getInstance().sendEvent("pushReceived", data);
        }
    }
}
