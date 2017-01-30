
package com.reactlibrary;

import android.support.annotation.Nullable;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.taplytics.sdk.CodeBlockListener;
import com.taplytics.sdk.Taplytics;
import com.taplytics.sdk.TaplyticsNewSessionListener;
import com.taplytics.sdk.TaplyticsPushTokenListener;
import com.taplytics.sdk.TaplyticsResetUserListener;
import com.taplytics.sdk.TaplyticsRunningExperimentsListener;
import com.taplytics.sdk.TaplyticsVar;
import com.taplytics.sdk.TaplyticsVarListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class TaplyticsReactModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    private static TaplyticsReactModule instance;
    private final String tagName = "TaplyticsReact";
    public TaplyticsReactModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        TaplyticsReactModule.instance = this;
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
    public void logEvent(String name, Float number, String object) {
        try {
            JSONObject data = new JSONObject(object);
            Taplytics.logEvent(name, number, data);
        } catch (JSONException e) {
            Log.e("Taplytics", e.getMessage());
        }
    }

    @ReactMethod
    public void logRevenue(String name, Float number, String object) {
        try {
            JSONObject data = new JSONObject(object);
            Taplytics.logRevenue(name, number, data);
        } catch (JSONException e) {
            Log.e("Taplytics", e.getMessage());
        }

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
    public void setTaplyticsPushTokenListener(final Promise callback) {
        Taplytics.setTaplyticsPushTokenListener(new TaplyticsPushTokenListener() {
            @Override
            public void pushTokenReceived(String s) {
                callback.resolve(s);
            }
        });
    }

    @ReactMethod
    public void propertiesLoadedCallback(final Promise callback) {
        Taplytics.getRunningExperimentsAndVariations(new TaplyticsRunningExperimentsListener() {
            @Override
            public void runningExperimentsAndVariation(Map<String, String> map) {
                callback.resolve(null);
            }
        });
    }

    @ReactMethod
    public void getRunningExperimentsAndVariations(final Promise callback) {
        Taplytics.getRunningExperimentsAndVariations(new TaplyticsRunningExperimentsListener() {
            @Override
            public void runningExperimentsAndVariation(Map<String, String> map) {
                callback.resolve(map);
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
        });
    }

    @ReactMethod
    public void setTaplyticsNewSessionListener(final Callback callback) {
        Taplytics.setTaplyticsNewSessionListener(new TaplyticsNewSessionListener() {
            @Override
            public void onNewSession() {
                callback.invoke();
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
}