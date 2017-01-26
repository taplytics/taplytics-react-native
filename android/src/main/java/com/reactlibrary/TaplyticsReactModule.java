
package com.reactlibrary;

import android.support.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.taplytics.sdk.CodeBlockListener;
import com.taplytics.sdk.Taplytics;
import com.taplytics.sdk.TaplyticsNewSessionListener;
import com.taplytics.sdk.TaplyticsRunningExperimentsListener;
import com.taplytics.sdk.TaplyticsVar;
import com.taplytics.sdk.TaplyticsVarListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class TaplyticsReactModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  public TaplyticsReactModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "Taplytics";
  }

  private void sendEvent(String eventName, @Nullable WritableMap params) {
      this.reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
  }

  @ReactMethod
  public void _newStringSyncVariable(String name, String defaultValue, Promise callback) {
      TaplyticsVar var = new TaplyticsVar<>(name, defaultValue);
      callback.resolve(var.get());
  }

    @ReactMethod
    public void _newBoolSyncVariable(String name, Boolean defaultValue, Promise callback) {
        TaplyticsVar var = new TaplyticsVar<>(name, defaultValue);
        callback.resolve(var.get());
    }

    @ReactMethod
    public void _newObjectSyncVariable(String name, String defaultValue, Promise callback) {
        try {
            JSONObject object = new JSONObject(defaultValue);
            TaplyticsVar var = new TaplyticsVar<>(name, object);
            callback.resolve(((JSONObject)var.get()).toString());
        } catch(JSONException e) {
            callback.resolve(e.getMessage());
        }
    }

    @ReactMethod
    public void _newNumberSyncVariable(String name, Float defaultValue, Promise callback) {
        TaplyticsVar var = new TaplyticsVar<>(name, defaultValue);
        callback.resolve(var.get());
    }

  @ReactMethod
  public void _newAsyncVariable(final String name, String defaultValue) {
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
  public void logEvent(String name, Float number, ReadableMap object) {
      Taplytics.logEvent(name, number);
  }

  @ReactMethod
  public void logRevenue(String name, Float number, ReadableMap object) {
      Taplytics.logRevenue(name, number);
  }

  @ReactMethod
  public void runCodeBlock(String name, final Promise callback) {
      Taplytics.runCodeBlock(name, new CodeBlockListener() {
          @Override
          public void run() {
              callback.resolve(null);
          }
      });
  }

  @ReactMethod
  public void taplyticsLoadedListener(final Promise callback) {
      Taplytics.getRunningExperimentsAndVariations(new TaplyticsRunningExperimentsListener() {
          @Override
          public void runningExperimentsAndVariation(Map<String, String> map) {
              callback.resolve(null);
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
  public void setTaplyticsNewSessionListener(final Promise callback) {
      Taplytics.setTaplyticsNewSessionListener(new TaplyticsNewSessionListener() {
          @Override
          public void onNewSession() {
              callback.resolve(null);
          }
      });
  }
}