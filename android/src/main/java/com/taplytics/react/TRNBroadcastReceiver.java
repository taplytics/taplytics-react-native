package com.taplytics.react;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.taplytics.sdk.TLGcmBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import static com.taplytics.react.TaplyticsReactHelper.convertJsonToMap;

/**
 * Created by Adam Wootton on 2017-01-30.
 */

public class TRNBroadcastReceiver extends TLGcmBroadcastReceiver {
    private static final String LOG_TAG_NAME = "TaplyticsBR";

    @Override
    public void pushOpened(Context context, Intent intent) {
        if (TaplyticsReactModule.getInstance() != null) {
            TaplyticsReactModule.getInstance().sendEvent("pushOpened", getIntentData(intent));
        } else {
            //Add to the queue to be sent off right when the react context is available.
            TaplyticsReactEventQueueManager.getInstance().addToQueue(new TaplyticsReactEvent("pushOpened", getIntentData(intent)));
        }
        super.pushOpened(context, intent);
    }

    @Override
    public void pushDismissed(Context context, Intent intent) {
        if (TaplyticsReactModule.getInstance() != null) {
            TaplyticsReactModule.getInstance().sendEvent("pushDismissed", getIntentData(intent));
        }
    }

    @Override
    public void pushReceived(Context context, Intent intent) {
        if (TaplyticsReactModule.getInstance() != null) {
            TaplyticsReactModule.getInstance().sendEvent("pushReceived", getIntentData(intent));
        }
    }

    private WritableMap getIntentData(Intent intent) {
        WritableMap data = Arguments.createMap();
        JSONObject dataObject = new JSONObject();
        try {
            if (intent.hasExtra("tl_id")) {
                dataObject.put("tl_id", intent.getExtras().getString("tl_id"));
            }
            if (intent.hasExtra("custom_keys")) {
                dataObject.put("custom_keys", new JSONObject(intent.getExtras().getString("custom_keys")));
            }
        } catch (Throwable ignored) {
            //Ignore the bad JSON for now. Log here as you please.
        }
        try {
            data.putMap("value", convertJsonToMap(dataObject));
        } catch (JSONException e) {
            Log.e(LOG_TAG_NAME, e.getMessage());
        }
        return data;
    }
}
