
package com.reactlibrary;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableMap;
import com.taplytics.sdk.CodeBlockListener;
import com.taplytics.sdk.Taplytics;
import com.taplytics.sdk.TaplyticsNewSessionListener;
import com.taplytics.sdk.TaplyticsRunningExperimentsListener;
import com.taplytics.sdk.TaplyticsVar;
import com.taplytics.sdk.TaplyticsVarListener;

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
  
  @ReactMethod
  public void newSyncVariable(String name, Object defaultValue, Callback callback) {
      TaplyticsVar var = new TaplyticsVar<>(name, defaultValue);
      callback.invoke(var.get());
  }

  @ReactMethod
  public void newAsyncVariable(String name, Object defaultValue, final Callback callback) {
      TaplyticsVar var = new TaplyticsVar<>(name, defaultValue, new TaplyticsVarListener() {
          @Override
          public void variableUpdated(Object o) {
              callback.invoke((String) o);
          }
      });
  }

  @ReactMethod
  public void logEvent(String name, Number number, ReadableMap object) {
      Taplytics.logEvent(name, number);
  }

  @ReactMethod
  public void logRevenue(String name, Number number, ReadableMap object) {
      Taplytics.logRevenue(name, number);
  }

  @ReactMethod
  public void runCodeBlock(String name, final Callback callback) {
      Taplytics.runCodeBlock(name, new CodeBlockListener() {
          @Override
          public void run() {
              callback.invoke();
          }
      });
  }

  @ReactMethod
  public void taplyticsLoadedListener(final Callback callback) {
      Taplytics.getRunningExperimentsAndVariations(new TaplyticsRunningExperimentsListener() {
          @Override
          public void runningExperimentsAndVariation(Map<String, String> map) {
              callback.invoke(map);
          }
      });
  }

  @ReactMethod
  public void startNewSession(final Callback callback) {
      Taplytics.startNewSession(new TaplyticsNewSessionListener() {
          @Override
          public void onNewSession() {
              callback.invoke();
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
}