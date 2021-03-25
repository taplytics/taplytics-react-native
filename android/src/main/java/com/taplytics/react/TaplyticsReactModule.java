package com.taplytics.react;

import android.util.Log;

import androidx.annotation.Nullable;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.taplytics.sdk.CodeBlockListener;
import com.taplytics.sdk.SessionInfoRetrievedListener;
import com.taplytics.sdk.Taplytics;
import com.taplytics.sdk.TaplyticsNewSessionListener;
import com.taplytics.sdk.TaplyticsResetUserListener;
import com.taplytics.sdk.TaplyticsRunningExperimentsListener;
import com.taplytics.sdk.TaplyticsRunningFeatureFlagsListener;
import com.taplytics.sdk.TaplyticsSetUserAttributesListener;
import com.taplytics.sdk.TaplyticsVar;
import com.taplytics.sdk.TaplyticsVarListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.taplytics.react.TaplyticsReactHelper.convertJsonToMap;
import static com.taplytics.react.TaplyticsReactHelper.convertMapToJson;
import static com.taplytics.react.TaplyticsReactHelper.getWritableMap;


public class TaplyticsReactModule extends ReactContextBaseJavaModule implements ReactInstanceManager.ReactInstanceEventListener {

    private static final String LOG_TAG_NAME = "TaplyticsReact";
    private static final String EVENT_VALUE_NAME = "value";
    private static final String ASYNC_VARIABLE_EVENT_VALUE_ID = "id";
    private static final String ASYNC_VARIABLE_EVENT_NAME = "asyncVariable";
    private static TaplyticsReactModule instance;
    private final ReactApplicationContext reactContext;

    public TaplyticsReactModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        TaplyticsReactModule.instance = this;
        setupReactListener(reactContext);
    }

    static TaplyticsReactModule getInstance() {
        return TaplyticsReactModule.instance;
    }

    private void setupReactListener(ReactApplicationContext reactContext) {
        try {
            if (reactContext.getApplicationContext() instanceof ReactApplication) {
                ((ReactApplication) reactContext.getApplicationContext()).getReactNativeHost().getReactInstanceManager().addReactInstanceEventListener(this);
            }
        } catch (Throwable t) {
            //For reasons unknown this is being used in a non-react app?
            Log.w("Taplytics", "Cannot access ReactNativeHost or ReactInstanceManager");
        }
    }

    @Override
    public String getName() {
        return "Taplytics";
    }

    void sendEvent(String eventName, @Nullable WritableMap params) {
        this.reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
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
    public void _newSyncObject(String name, ReadableMap defaultValue, Promise callback) {
        try {
            TaplyticsVar var = new TaplyticsVar<>(name, convertMapToJson(defaultValue));
            callback.resolve(convertJsonToMap((JSONObject) var.get()));
        } catch (JSONException e) {
            callback.reject(LOG_TAG_NAME, e.getMessage());
        }
    }

    @ReactMethod
    public void _newSyncNumber(String name, Float defaultValue, Promise callback) {
        TaplyticsVar var = new TaplyticsVar<>(name, defaultValue);
        callback.resolve(var.get());
    }

    @ReactMethod
    public void _newAsyncString(final String name, String defaultValue, final Integer callbackID) {
        new TaplyticsVar<>(name, defaultValue, new TaplyticsVarListener() {
            @Override
            public void variableUpdated(Object o) {
                WritableMap params = Arguments.createMap();
                params.putString(EVENT_VALUE_NAME, (String) o);
                params.putInt(ASYNC_VARIABLE_EVENT_VALUE_ID, callbackID);
                sendEvent(ASYNC_VARIABLE_EVENT_NAME, params);
            }
        });
    }

    @ReactMethod
    public void _newAsyncBool(final String name, Boolean defaultValue, final Integer callbackID) {
        new TaplyticsVar<>(name, defaultValue, new TaplyticsVarListener() {
            @Override
            public void variableUpdated(Object o) {
                WritableMap params = Arguments.createMap();
                params.putBoolean(EVENT_VALUE_NAME, (Boolean) o);
                params.putInt(ASYNC_VARIABLE_EVENT_VALUE_ID, callbackID);
                sendEvent(ASYNC_VARIABLE_EVENT_NAME, params);
            }
        });
    }

    @ReactMethod
    public void _newAsyncNumber(final String name, Double defaultValue, final Integer callbackID) {
        new TaplyticsVar<>(name, defaultValue, new TaplyticsVarListener() {
            @Override
            public void variableUpdated(Object o) {
                try {
                    WritableMap params = Arguments.createMap();
                    if (o instanceof Double) {
                        params.putDouble(EVENT_VALUE_NAME, (Double) o);
                    } else if (o instanceof Integer) {
                        params.putInt(EVENT_VALUE_NAME, (Integer) o);
                    } else {
                        return;
                    }
                    params.putInt(ASYNC_VARIABLE_EVENT_VALUE_ID, callbackID);
                    sendEvent(ASYNC_VARIABLE_EVENT_NAME, params);
                } catch (Exception e) {
                    Log.e(LOG_TAG_NAME, e.getMessage());
                }
            }
        });
    }

    @ReactMethod
    public void _newAsyncObject(final String name, ReadableMap defaultValue, final Integer callbackID) {
        try {
            new TaplyticsVar<>(name, convertMapToJson(defaultValue), new TaplyticsVarListener() {
                @Override
                public void variableUpdated(Object o) {
                    WritableMap params = Arguments.createMap();
                    try {
                        params.putMap(EVENT_VALUE_NAME, convertJsonToMap((JSONObject) o));
                    } catch (JSONException e) {
                        Log.e(LOG_TAG_NAME, e.getMessage());
                    }
                    params.putInt(ASYNC_VARIABLE_EVENT_VALUE_ID, callbackID);
                    sendEvent(ASYNC_VARIABLE_EVENT_NAME, params);
                }
            });
        } catch (JSONException e) {
            Log.e(LOG_TAG_NAME, e.getMessage());
        }
    }

    @ReactMethod
    public void _logEvent(String name, Float number, ReadableMap object) {
        try {
            Taplytics.logEvent(name, number, convertMapToJson(object));
        } catch (JSONException e) {
            Log.e("Taplytics", e.getMessage());
        }
    }

    @ReactMethod
    public void _logRevenue(String name, Float number, ReadableMap object) {
        try {
            Taplytics.logRevenue(name, number, convertMapToJson((object)));
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
    public void _runCodeBlock(String name, final Callback callback) {
        Taplytics.runCodeBlock(name, new CodeBlockListener() {
            @Override
            public void run() {
                callback.invoke();
            }
        });
    }

    @ReactMethod
    public void _resetAppUser(final Promise callback) {
        TaplyticsResetUserListener listener = new TaplyticsResetUserListener() {
            @Override
            public void finishedResettingUser() {
                callback.resolve(null);
            }
        };

        Taplytics.resetAppUser(listener);
    }

    @ReactMethod
    public void _propertiesLoadedCallback() {
        Taplytics.getRunningExperimentsAndVariations(new TaplyticsRunningExperimentsListener() {
            @Override
            public void runningExperimentsAndVariation(Map<String, String> map) {
                WritableMap params = Arguments.createMap();
                params.putBoolean("loaded", true);
                sendEvent("propertiesLoadedCallback", params);
            }
        });
    }

    @ReactMethod
    public void _getRunningExperimentsAndVariations(final Promise callback) {
        Taplytics.getRunningExperimentsAndVariations(new TaplyticsRunningExperimentsListener() {
            @Override
            public void runningExperimentsAndVariation(Map<String, String> map) {
                WritableMap resultData = getWritableMap(map);
                callback.resolve(resultData);
            }
        });
    }

    @ReactMethod
    public void _getRunningFeatureFlags(final Promise callback) {
        Taplytics.getRunningFeatureFlags(new TaplyticsRunningFeatureFlagsListener() {
            @Override
            public void runningFeatureFlags(Map<String, String> map) {
                WritableMap resultData = getWritableMap(map);
                callback.resolve(resultData);
            }
        });
    }

    @ReactMethod
    public void _startNewSession(final Promise callback) {
        Taplytics.startNewSession(new TaplyticsNewSessionListener() {
            @Override
            public void onNewSession() {
                callback.resolve(null);
            }

            @Override
            public void onError() {
                callback.reject(LOG_TAG_NAME, "Starting New Session");
            }
        });
    }

    @ReactMethod
    public void _setTaplyticsNewSessionListener() {
        Taplytics.setTaplyticsNewSessionListener(new TaplyticsNewSessionListener() {
            @Override
            public void onNewSession() {
                WritableMap params = Arguments.createMap();
                params.putBoolean("loaded", true);
                sendEvent("newSession", params);
            }

            @Override
            public void onError() {
            }
        });
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
            Log.e(LOG_TAG_NAME, e.getMessage());
        }
    }

    @ReactMethod
    public void _getSessionInfo(final Promise callback) {
        Taplytics.getSessionInfo(new SessionInfoRetrievedListener() {
            @Override
            public void sessionInfoRetrieved(HashMap hashMap) {
                WritableMap resultData = new WritableNativeMap();
                if (hashMap.containsKey("session_id")) {
                    resultData.putString("session_id", (String) hashMap.get("session_id"));
                }
                if (hashMap.containsKey("appUser_id")) {
                    resultData.putString("appUser_id", (String) hashMap.get("appUser_id"));
                }

                callback.resolve(resultData);
            }

            @Override
            public void onError(HashMap hashMap) {
                callback.reject(LOG_TAG_NAME, "Getting Session Info");
            }
        });
    }

    @Override
    public void onReactContextInitialized(ReactContext context) {
        //Flush queue te moment react context is initialized
        TaplyticsReactEventQueueManager.getInstance().flushQueue();
    }
}