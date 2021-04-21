package com.kursova.suncheck.service;

import com.kursova.suncheck.model.enums.TextResourceKeys;

public interface TextResourceService {

    public String get(TextResourceKeys textResourceKey);

    public void cacheAllTextResources();

    public void clearCaches();
}
