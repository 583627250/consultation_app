package com.consultation.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 请求参数封装类
 * @create-time 2012-3-13 下午12:32:17
 */
public class CaseParams {

    private Map<String, String> parameters=new HashMap<String, String>();

    private List<String> keys=new ArrayList<String>();

    public CaseParams() {

    }

    public void add(String key, String value) {
        if(this.keys.contains(key)) {
            this.parameters.put(key, value);
        } else {
            this.keys.add(key);
            this.parameters.put(key, value);
        }
    }

    public void remove(String key) {
        keys.remove(key);
        this.parameters.remove(key);
    }

    public String getValue(String key) {
        String rlt=this.parameters.get(key);
        return rlt;
    }

    public String getValue(int location) {
        String key=this.keys.get(location);
        String rlt=this.parameters.get(key);
        return rlt;
    }

    public String getKey(int location) {
        if(location >= 0 && location < this.keys.size()) {
            return this.keys.get(location);
        }
        return "";
    }

    public int size() {
        return keys.size();
    }

    public void clear() {
        this.keys.clear();
        this.parameters.clear();
    }
    
    public Map<String, String> getParameters() {
        return parameters;
    }
    
    public void setParameters(Map<String, String> mParameters) {
        this.parameters=mParameters;
    }
    
    public List<String> getKeys() {
        return keys;
    }
    
    public void setKeys(List<String> mKeys) {
        this.keys=mKeys;
    }
}
