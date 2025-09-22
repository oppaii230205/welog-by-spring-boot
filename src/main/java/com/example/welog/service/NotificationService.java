package com.example.welog.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.welog.dto.NotificationResponseDto;
import com.example.welog.model.Notification;
import com.example.welog.repository.NotificationRepository;
import com.example.welog.utils.ResponseDtoMapper;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<NotificationResponseDto> getNotificationsForUser(Long userId) {
        List<Notification> notifications = notificationRepository.findByRecipientId(userId);
        return notifications.stream()
                .map(ResponseDtoMapper::mapToNotificationResponseDto)
                .toList();
    }

    public void markAllNotificationsAsRead(Long userId) {
        List<Notification> notifications = notificationRepository.findByRecipientId(userId);
        for (Notification notification : notifications) {
            notification.setRead(true);
        }
        notificationRepository.saveAll(notifications);
    }

    public void markNotificationAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + notificationId));
        notification.setRead(true);
        notificationRepository.save(notification);
    }
}
