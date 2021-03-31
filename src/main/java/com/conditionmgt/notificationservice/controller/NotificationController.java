package com.conditionmgt.notificationservice.controller;

import com.conditionmgt.notificationservice.entity.Condition;
import com.conditionmgt.notificationservice.entity.Notification;
import com.conditionmgt.notificationservice.service.NotificationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notification/v1")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private KafkaTemplate <String,Notification > kafkaTemplate;
    private static final String TOPIC ="notification";

    @PostMapping("/saveCondition")
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
