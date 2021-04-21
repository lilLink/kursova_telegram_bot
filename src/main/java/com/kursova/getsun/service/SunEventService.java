package com.kursova.getsun.service;

import com.kursova.getsun.model.User;

public interface SunEventService {

    String getSunEventTimeByUser(User user, String event);

    void updateUserNextNotificationTime(User curUser, String previousSunEventType);

    String getNearestSunEventType(User curUser);
}
