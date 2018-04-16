package com.taplytics.react;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.modules.core.DeviceEventManagerModule;

/**
 * Created by vic on 2018-04-16.
 *
 * Temporary solution for cold open triggering on android.
 *
 * Should never be hit / used in any other case.
 */
class TLRNEventEmitter {

    /**
     * Data that is to be sent to android's open listeners.
     */
    private Object awaitingData;

    Object getAwaitingData() {
        return awaitingData;
    }

    void setAwaitingData(Object awaitingData) {
        this.awaitingData = awaitingData;
    }


    /**
     * Emit the event data to the react context now that its available.
     */
    void emit(String event, ReactContext context){
        context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(event, awaitingData);
        awaitingData = null;
    }

    private static TLRNEventEmitter instance;

    static TLRNEventEmitter getInstance(){
        if(instance == null){
            instance=new TLRNEventEmitter();
        }
        return instance;
    }

}
