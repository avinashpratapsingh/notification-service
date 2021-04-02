package com.conditionmgt.notificationservice.controller;

import com.conditionmgt.notificationservice.entity.Condition;
import com.conditionmgt.notificationservice.entity.Notification;
import com.conditionmgt.notificationservice.service.NotificationService;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notification/v1")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private KafkaTemplate <String,Notification > kafkaTemplate;
    private static final String TOPIC ="notification";

    @PostMapping(value = "/saveCondition",produces = { "application/json" })
    public String saveNotification(@RequestBody Condition condition){
        ObjectMapper mapper = new ObjectMapper();
        String response="";
        try {
            response= mapper.writeValueAsString(condition);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return response;
    }
    @GetMapping (value = "/getCondition",produces = { "application/json" })
    public String getCondition() throws JsonProcessingException {
        String jsn = "{ \"BloodSugar\" : { \"A1c Value\" : \"110\", \"Average Blood Glucose- mg/dl \" : \"100\", \"NewField\": \"value\" }, \"Hypertention\" : { \"systolic in mm Hg\" : \"120\", \"diastolic in mm Hg \" : \"80\"\n" +
                "\n" +
                "}\n" +
                "\n" +
                "}";


        HashMap<String, Object> map = new HashMap<String, Object>();

        ObjectMapper mapper = new ObjectMapper();
        try
        {
            //Convert Map to JSON
            map = mapper.readValue(jsn, new TypeReference<HashMap<String, Object>>(){});
        }
        catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mapper.writeValueAsString(map);
    }
    /*@PostMapping("/saveNotification")
    public Notification saveNotification(@RequestBody Notification notification){

        return notificationService.saveNotification(notification);
    }


    @GetMapping("/publish/{msg}")
    public String postMessage(@PathVariable("msg") final String msg){
        kafkaTemplate.send(TOPIC,msg);
        return "Published";
    }

    @RequestMapping("/getNotifications")
    public List<Notification> getNotifications(){
        return notificationService.getNotification();
    }*/


}
