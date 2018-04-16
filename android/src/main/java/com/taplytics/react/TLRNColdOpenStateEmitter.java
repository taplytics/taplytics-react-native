package com.taplytics.react;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.modules.core.DeviceEventManagerModule;

/**
 * Created by vic on 2018-04-16.
 */

class TLRNColdOpenStateEmitter {

    Object getAwaitingData() {
        return awaitingData;
    }

    void setAwaitingData(Object awaitingData) {
        this.awaitingData = awaitingData;
    }

    private Object awaitingData;

    void emit(String event, ReactContext context){
        context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(event, awaitingData);
        awaitingData = null;
    }

    private static TLRNColdOpenStateEmitter instance;

    static TLRNColdOpenStateEmitter getInstance(){
        if(instance == null){
            instance=new TLRNColdOpenStateEmitter();
        }
        return instance;
    }

}
