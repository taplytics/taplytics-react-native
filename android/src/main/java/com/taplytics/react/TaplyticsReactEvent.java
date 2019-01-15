package com.taplytics.react;

import com.facebook.react.bridge.WritableMap;

 class TaplyticsReactEvent {
        String name;
        WritableMap data;
        TaplyticsReactEvent(String name, WritableMap data) {
            this.name = name;
            this.data = data;
        }
    }