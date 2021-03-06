package com.conditionmgt.notificationservice.entity;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Condition implements Serializable {

    //private String conditionName;
    private Map<String, Object> conditionDetails = new LinkedHashMap<>();

    public void setConditionDetails(Map<String, Object> conditionDetails) {
        this.conditionDetails = conditionDetails;
    }

    @JsonAnyGetter
    public Map<String, Object> getConditionDetails() {
        return conditionDetails;
    }
    @JsonAnySetter
    public void setConditionDetails(String key,Object val) {
        this.conditionDetails.put(key, val);
    }

}
