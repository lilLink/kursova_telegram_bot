package com.kursova.suncheck.service.impl;

import com.kursova.suncheck.service.ApplicationService;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    @Override
    public void checkEnvVarsPresence() {
        /*final List<String> requiredVars = asList(STR_CONSTANT.BOT_USERNAME_ENV_VAR, STR_CONSTANT.BOT_TOKEN_ENV_VAR);
        final List<String> extraVars = Collections.singletonList(STR_CONSTANT.SERVER_URL_ENV_VAR);
        // Handling required env vars
        List<String> absentReqVars = requiredVars.stream().filter(reqVar -> System.getenv().get(reqVar) == null).collect(Collectors.toList());
        if (!absentReqVars.isEmpty()) {
            absentReqVars.forEach(x -> log.error(String.format("Required environment variable missing: %s", x)));
            throw new RuntimeException("System can't be initialized without missing required variables");
        }
        // Handling extra env vars
        extraVars.stream().filter(extraVar -> System.getenv().get(extraVar) == null)
                          .forEach(x -> log.warn(String.format("Extra environment variable missing: %s", x)));*/
    }
}
