package com.taplytics.react;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by vic on 2019-01-15.
 *
 * An event queue that can store events which happened while the reactContext is null.
 *
 * This queue gets flushed the moment reactContext is not null.
 *
 * At the moment, we will only be firing off pushOpen events, but this can be used for various things in the future.
 */
class TaplyticsReactEventQueueManager {

    private static TaplyticsReactEventQueueManager instance;
    private Queue<TaplyticsReactEvent> eventQueue;

    private TaplyticsReactEventQueueManager(){
        eventQueue = new LinkedList<>();
    }

    static TaplyticsReactEventQueueManager getInstance(){
        if (instance == null){
            instance = new TaplyticsReactEventQueueManager();
        }
        return instance;
    }

    void addToQueue(TaplyticsReactEvent e){
        eventQueue.add(e);
    }

     void flushQueue(){
         for(TaplyticsReactEvent e : eventQueue){
            eventQueue.remove(e);
            if(TaplyticsReactModule.getInstance() != null) {
                TaplyticsReactModule.getInstance().sendEvent(e.name, e.data);
            }
        }
    }


}
