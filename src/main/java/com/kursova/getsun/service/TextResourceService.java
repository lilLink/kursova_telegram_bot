package com.kursova.getsun.service;

import com.kursova.getsun.model.enums.TextResourceKeys;

public interface TextResourceService {

    public String get(TextResourceKeys textResourceKey);

    public void cacheAllTextResources();

    public void clearCaches();
}
