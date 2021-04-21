package com.kursova.suncheck.service;

import com.kursova.suncheck.model.User;

public interface SunEventService {

    String getSunEventTimeByUser(User user, String event);

    void updateUserNextNotificationTime(User curUser, String previousSunEventType);

    String getNearestSunEventType(User curUser);
}
