package com.future.demo.android.sip.pjsip;

import com.alibaba.fastjson.JSONObject;

/**
 *
 */
public abstract class Event {
    /**
     * 事件额外数据存储
     */
    private JSONObject extra;

    /**
     *
     * @param key
     * @param object
     */
    public synchronized void put(String key, Object object){
        if(extra==null){
            extra=new JSONObject();
        }
        extra.put(key,object);
    }

    /**
     *
     * @param key
     * @return
     */
    public synchronized Object get(String key){
        if(extra==null){
            return null;
        }
        return extra.get(key);
    }
}
