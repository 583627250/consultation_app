package com.consultation.app.util;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;


public class ActivityList {

    private static ActivityList activityList;
    
    private Map<String, Activity> maps = new HashMap<String, Activity>();
    
    public static ActivityList getInstance(){
        if(null == activityList){
            activityList = new ActivityList();
        }
        return activityList;
    }
    
    public void setActivitys(String name, Activity activity){
        maps.put(name, activity);
    }
    
    public void closeActivity(String name){
        maps.get(name).setResult(Activity.RESULT_OK, new Intent());
        maps.get(name).finish();
        maps.remove(name);
    }
}
