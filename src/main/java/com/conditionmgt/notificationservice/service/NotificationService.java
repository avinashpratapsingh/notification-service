package com.conditionmgt.notificationservice.service;

import com.conditionmgt.notificationservice.entity.Notification;
import com.conditionmgt.notificationservice.repo.NotificationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepo notificationRepo;

    public List<Notification> getNotification(){
        return notificationRepo.findAll();
    }

    public Notification saveNotification(Notification notification) {
        return notificationRepo.save(notification);
    }
}