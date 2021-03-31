package com.conditionmgt.notificationservice.repo;

import com.conditionmgt.notificationservice.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepo extends JpaRepository<Notification,Integer> {
}
