
package com.taplytics.react;

import androidx.annotation.Nullable;
import android.util.Log;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.taplytics.sdk.CodeBlockListener;
import com.taplytics.sdk.SessionInfoRetrievedListener;
import com.taplytics.sdk.Taplytics;
import com.taplytics.sdk.TaplyticsExperimentsUpdatedListener;
import com.taplytics.sdk.TaplyticsNewSessionListener;
import com.taplytics.sdk.TaplyticsPushSubscriptionChangedListener;
import com.taplytics.sdk.TaplyticsPushTokenListener;
import com.taplytics.sdk.TaplyticsResetUserListener;
import com.taplytics.sdk.TaplyticsRunningExperimentsListener;
import com.taplytics.sdk.TaplyticsRunningFeatureFlagsListener;
import com.taplytics.sdk.TaplyticsVar;
import com.taplytics.sdk.TaplyticsVarListener;
import com.taplytics.sdk.TaplyticsSetUserAttributesListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;


public class TaplyticsReactModule extends ReactContextBaseJavaModule implements ReactInstanceManager.ReactInstanceEventListener {

    private final ReactApplicationContext reactContext;
    private static TaplyticsReactModule instance;
    private final String tagName = "TaplyticsReact";

    public TaplyticsReactModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        TaplyticsReactModule.instance = this;
        setupReactListener(reactContext);
    }

    private void setupReactListener(ReactApplicationContext reactContext) {
        try {
            if (reactContext.getApplicationContext() instanceof ReactApplication) {
                ((ReactApplication) reactContext.getApplicationContext()).getReactNativeHost().getReactInstanceManager().addReactInstanceEventListener(this);
            }
        } catch (Throwable t){
            //For reasons unknown this is being used in a non-react app?
            Log.w("Taplytics", "Cannot access ReactNativeHost or ReactInstanceManager");
        }
    }

    
    public WritableMap getWritableMap(Map<String, String> map) {
        WritableMap writeMap = Arguments.createMap();
        if (map.isEmpty()) {
            return writeMap;
        }
        try {
            Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                writeMap.putString((String) pair.getKey(), (String) pair.getValue());
                it.remove(); // avoids a ConcurrentModificationException
            }
        } catch (Throwable e) {
            writeMap = Arguments.createMap();
        }
        return writeMap;
    }

    @Override
    public String getName() {
        return "Taplytics";
    }

    void sendEvent(String eventName, @Nullable WritableMap params) {
        this.reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
    }

    static TaplyticsReactModule getInstance() {
        return TaplyticsReactModule.instance;
    }

    @ReactMethod
    public void _newSyncString(String name, String defaultValue, Promise callback) {
        TaplyticsVar var = new TaplyticsVar<>(name, defaultValue);
        callback.resolve(var.get());
    }

    @ReactMethod
    public void _newSyncBool(String name, Boolean defaultValue, Promise callback) {
        TaplyticsVar var = new TaplyticsVar<>(name, defaultValue);
        callback.resolve(var.get());
    }

    @ReactMethod
    public void _newSyncObject(String name, String defaultValue, Promise callback) {
        try {
            JSONObject object = new JSONObject(defaultValue);
            TaplyticsVar var = new TaplyticsVar<>(name, object);
            callback.resolve(((JSONObject) var.get()).toString());
        } catch (JSONException e) {
            callback.reject(tagName, e.getMessage());
        }
    }

    @ReactMethod
    public void _newSyncNumber(String name, Float defaultValue, Promise callback) {
        TaplyticsVar var = new TaplyticsVar<>(name, defaultValue);
        callback.resolve(var.get());
    }

    @ReactMethod
    public void _newAsyncString(final String name, String defaultValue) {
        TaplyticsVar var = new TaplyticsVar<>(name, defaultValue, new TaplyticsVarListener() {
            @Override
            public void variableUpdated(Object o) {
                WritableMap params = Arguments.createMap();
                params.putString("value", (String) o);
                sendEvent(name, params);
            }
        });
    }

    @ReactMethod
    public void _newAsyncBool(final String name, Boolean defaultValue) {
        TaplyticsVar var = new TaplyticsVar<>(name, defaultValue, new TaplyticsVarListener() {
            @Override
            public void variableUpdated(Object o) {
                WritableMap params = Arguments.createMap();
                params.putBoolean("value", (Boolean) o);
                sendEvent(name, params);
            }
        });
    }

    @ReactMethod
    public void _newAsyncNumber(final String name, Double defaultValue) {
        TaplyticsVar var = new TaplyticsVar<>(name, defaultValue, new TaplyticsVarListener() {
            @Override
            public void variableUpdated(Object o) {
                WritableMap params = Arguments.createMap();
                params.putDouble("value", (Double) o);
                sendEvent(name, params);
            }
        });
    }

    @ReactMethod
    public void _newAsyncObject(final String name, String defaultValue) {
        try {
            JSONObject object = new JSONObject(defaultValue);
            TaplyticsVar var = new TaplyticsVar<>(name, object, new TaplyticsVarListener() {
                @Override
                public void variableUpdated(Object o) {
                    WritableMap params = Arguments.createMap();
                    params.putString("value", ((JSONObject) o).toString());
                    sendEvent(name, params);
                }
            });
        } catch (JSONException e) {
            Log.e(tagName, e.getMessage());
        }
    }

    @ReactMethod
    public void _logEvent(String name, Float number, String object) {
        try {
            JSONObject data = new JSONObject(object);
            Taplytics.logEvent(name, number, data);
        } catch (JSONException e) {
            Log.e("Taplytics", e.getMessage());
        }
    }

    @ReactMethod
    public void _logRevenue(String name, Float number, String object) {
        try {
            JSONObject data = new JSONObject(object);
            Taplytics.logRevenue(name, number, data);
        } catch (JSONException e) {
            Log.e("Taplytics", e.getMessage());
        }

    }

    @ReactMethod
    public void _featureFlagEnabled(String key, Promise callback) {
        boolean isEnabled = Taplytics.featureFlagEnabled(key);
        callback.resolve(isEnabled);
    }


    @ReactMethod
    public void codeBlock(String name, final Callback callback) {
        Taplytics.runCodeBlock(name, new CodeBlockListener() {
            @Override
            public void run() {
                callback.invoke();
            }
        });
    }

    @ReactMethod
    public void runCodeBlock(String name, final Callback callback) {
        codeBlock(name, callback);
    }

    @ReactMethod
    public void runCodeBlockSync(String name, final Callback callback) {
        Taplytics.runCodeBlockSync(name, new CodeBlockListener() {
            @Override
            public void run() {
                callback.invoke();
            }
        });
    }

    @ReactMethod
    public void resetAppUser(final Promise callback) {
        TaplyticsResetUserListener listener = new TaplyticsResetUserListener() {
            @Override
            public void finishedResettingUser() {
                callback.resolve(null);
            }
        };

        Taplytics.resetAppUser(listener);
    }

    @ReactMethod
    public void setTaplyticsPushTokenListener(final Callback callback) {
        Taplytics.setTaplyticsPushTokenListener(new TaplyticsPushTokenListener() {
            @Override
            public void pushTokenReceived(String s) {
                callback.invoke(s);
            }
        });
    }

    @ReactMethod
    public void _propertiesLoadedCallback(final Callback callback) {
        Taplytics.getRunningExperimentsAndVariations(new TaplyticsRunningExperimentsListener() {
            @Override
            public void runningExperimentsAndVariation(Map<String, String> map) {
                callback.invoke();
            }
        });
    }

    @ReactMethod
    public void getRunningExperimentsAndVariations(final Promise callback) {
        Taplytics.getRunningExperimentsAndVariations(new TaplyticsRunningExperimentsListener() {
            @Override
            public void runningExperimentsAndVariation(Map<String, String> map) {
                WritableMap resultData = getWritableMap(map);
                callback.resolve(resultData);
            }
        });
    }

    @ReactMethod
    public void getRunningFeatureFlags(final Promise callback) {
        Taplytics.getRunningFeatureFlags(new TaplyticsRunningFeatureFlagsListener() {
            @Override
            public void runningFeatureFlags(Map<String, String> map) {
                WritableMap resultData = getWritableMap(map);
                callback.resolve(resultData);
            }
        });
    }

    @ReactMethod
    public void startNewSession(final Promise callback) {
        Taplytics.startNewSession(new TaplyticsNewSessionListener() {
            @Override
            public void onNewSession() {
                callback.resolve(null);
            }
            @Override
            public void onError() {
                callback.reject(tagName,"Starting New Session");
            }
        });
    }

    @ReactMethod
    public void _setTaplyticsNewSessionListener() {
        Taplytics.setTaplyticsNewSessionListener(new TaplyticsNewSessionListener() {
            @Override
            public void onNewSession() {
                WritableMap params = Arguments.createMap();
                params.putBoolean("value", true);
                sendEvent("newSession", params);
            }
            @Override
            public void onError() { }
        });
    }

    @ReactMethod
    public void _setTaplyticsExperimentsUpdatedListener() {
        Taplytics.setTaplyticsExperimentsUpdatedListener(new TaplyticsExperimentsUpdatedListener() {
            @Override
            public void onExperimentUpdate() {
                WritableMap params = Arguments.createMap();
                params.putBoolean("value", true);
                sendEvent("experimentsUpdated", params);
            }
        });
    }

    @ReactMethod
    public void _setUserAttributes(final String attributes) {
        try {
            JSONObject jsonAttributes = new JSONObject(attributes);
            Taplytics.setUserAttributes(jsonAttributes);
        } catch (JSONException e) {
            Log.e(tagName, e.getMessage());
        }
    }

    @ReactMethod
    public void _setUserAttributes(final String attributes, final Promise promise) {
        try {
            JSONObject jsonAttributes = new JSONObject(attributes);
            Taplytics.setUserAttributes(jsonAttributes, new TaplyticsSetUserAttributesListener() {
                @Override
                public void finishedSettingUserAttributes() {
                    promise.resolve(null);
                }
            });
        } catch (JSONException e) {
            Log.e(tagName, e.getMessage());
        }
    }

    @ReactMethod
    public void getSessionInfo(final Promise callback) {
        Taplytics.getSessionInfo(new SessionInfoRetrievedListener() {
            @Override
            public void sessionInfoRetrieved(HashMap hashMap) {
                WritableMap resultData = new WritableNativeMap();
                if(hashMap.containsKey("session_id")){
                    resultData.putString("session_id", (String) hashMap.get("session_id"));
                }
                if(hashMap.containsKey("appUser_id")){
                    resultData.putString("appUser_id", (String) hashMap.get("appUser_id"));
                }

                callback.resolve(resultData);
            }
            @Override
            public void onError(HashMap hashMap) {
                callback.reject(tagName,"Getting Session Info");
            }
        });
    }

    @ReactMethod
    public void deviceLink(final String link) {
        Taplytics.deviceLink(link);
    }

    @ReactMethod
    public void showMenu() {
        Taplytics.showMenu();
    }

    @ReactMethod
    public void setPushSubscriptionEnabled(final boolean enabled, final Promise callback) {
        Taplytics.setPushSubscriptionEnabled(enabled, new TaplyticsPushSubscriptionChangedListener() {
            @Override
            public void success() {
                callback.resolve(null);
            }

            @Override
            public void failure() {
                callback.reject(tagName, "Failed to set push subscription enabled status");
            }
        });
    }

    @Override
    public void onReactContextInitialized(ReactContext context) {
        //Flush queue te moment react context is initialized
        TaplyticsReactEventQueueManager.getInstance().flushQueue();
    }
}