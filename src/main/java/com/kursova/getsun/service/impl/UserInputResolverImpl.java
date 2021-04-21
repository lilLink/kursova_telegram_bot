package com.kursova.getsun.service.impl;

import com.kursova.getsun.model.User;
import com.kursova.getsun.model.UserConfig;
import com.kursova.getsun.model.enums.BotCommands;
import com.kursova.getsun.repository.UserConfigRepository;
import com.kursova.getsun.service.SunEventService;
import com.kursova.getsun.service.TextResourceService;
import com.kursova.getsun.service.UserCommandResolver;
import com.kursova.getsun.service.UserInputResolver;
import com.kursova.getsun.service.UserRepositoryService;
import com.kursova.getsun.utility.TimezoneMapper;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import static com.kursova.getsun.constant.STR_CONSTANT.SUNRISE_EVENT_NAME;
import static com.kursova.getsun.constant.STR_CONSTANT.SUNSET_EVENT_NAME;
import static com.kursova.getsun.model.enums.TextResourceKeys.CMD_NOT_AVAILABLE;
import static com.kursova.getsun.model.enums.TextResourceKeys.CMD_ONLY_SUPPORTED;
import static com.kursova.getsun.model.enums.TextResourceKeys.INITIAL_SETUP_FINISHED;
import static com.kursova.getsun.model.enums.TextResourceKeys.LOC_SETUP_2ND_ATTEMPT;
import static com.kursova.getsun.model.enums.TextResourceKeys.SMTH_WENT_WRONG;

@Service
@RequiredArgsConstructor
public class UserInputResolverImpl implements UserInputResolver {

    private final UserRepositoryService userRepositoryService;
    private final UserConfigRepository userConfigRepository;
    private final SunEventService sunEventService;
    private final UserCommandResolver userCommandResolver;
    private final TextResourceService textResourceService;

    @Override
    public String resolveCommand(Update update) {
        Long chatId = update.getMessage().getChatId();
        BotCommands command = Arrays.stream(BotCommands.values())
                .filter(curCmd -> curCmd.getCommand().equals(update.getMessage().getText()))
                .findFirst().orElse(BotCommands.NOT_EXISTING);
        switch (command) {
            case START: return userCommandResolver.resolveStart(update);
            case SUNRISE: return userCommandResolver.resolveSunEvent(chatId, SUNRISE_EVENT_NAME);
            case SUNSET: return userCommandResolver.resolveSunEvent(chatId, SUNSET_EVENT_NAME);
            case REMOVEME: return userCommandResolver.resolveRemoveme(update);
            case HELP: return userCommandResolver.resolveHelp(update);

            default: return textResourceService.get(CMD_NOT_AVAILABLE);
        }
    }

    @Override
    public String resolvePlainText(Update update) {
        return textResourceService.get(CMD_ONLY_SUPPORTED);
    }

    @Override
    public String resolveLocation(Update update) {
        long curChatId = update.getMessage().getChatId();
        Optional<User> user = userRepositoryService.findByChatId(curChatId);
        if (!user.isPresent()) {
            return textResourceService.get(SMTH_WENT_WRONG);
        }
        for (UserConfig userConfig : userConfigRepository.findAll()) {
            if (userConfig.getUser().getChatId() == curChatId && userConfig.isSetupFinished()) {
                return textResourceService.get(LOC_SETUP_2ND_ATTEMPT);
            }
        }
        UserConfig userConfig = new UserConfig();

        userConfig.setUser(user.orElseThrow(RuntimeException::new));
        userConfig.setLatitude(update.getMessage().getLocation().getLatitude().toString());
        userConfig.setLongitude(update.getMessage().getLocation().getLongitude().toString());
        userConfig.setTimezone(TimezoneMapper.latLngToTimezoneString(
                Double.parseDouble(userConfig.getLatitude()), Double.parseDouble(userConfig.getLongitude())));
        userConfig.setSetupFinished(true);
        userConfigRepository.save(userConfig);

        User curUser = user.orElseThrow(RuntimeException::new);

        String nextSunEventType = (sunEventService.getNearestSunEventType(curUser).equals(SUNRISE_EVENT_NAME)) ? SUNSET_EVENT_NAME : SUNRISE_EVENT_NAME;
        sunEventService.updateUserNextNotificationTime(curUser, nextSunEventType);

        return textResourceService.get(INITIAL_SETUP_FINISHED);
    }
}
