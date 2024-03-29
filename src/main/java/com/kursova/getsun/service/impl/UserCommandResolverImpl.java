package com.kursova.getsun.service.impl;

import com.kursova.getsun.constant.PATH_CONSTANT;
import com.kursova.getsun.model.UserConfig;
import com.kursova.getsun.model.UserSetupStepsBO;
import com.kursova.getsun.model.UserSetupStepsBO.UserSetupSteps;
import com.kursova.getsun.model.enums.TextResourceKeys;
import com.kursova.getsun.repository.UserConfigRepository;
import com.kursova.getsun.service.SunEventService;
import com.kursova.getsun.service.TextResourceService;
import com.kursova.getsun.service.UserCommandResolver;
import com.kursova.getsun.service.UserService;
import com.kursova.getsun.utility.ResourceUtil;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.kursova.getsun.constant.INTEGER_CONSTANT.REMOVEME_EXPIRE_SEC;
import static com.kursova.getsun.constant.STR_CONSTANT.SUNRISE_EVENT_NAME;
import static com.kursova.getsun.model.enums.TextResourceKeys.BOT_NOT_CONF_YET;
import static com.kursova.getsun.model.enums.TextResourceKeys.LOCATION_SETUP;
import static com.kursova.getsun.model.enums.TextResourceKeys.REMOVEME;
import static com.kursova.getsun.model.enums.TextResourceKeys.START_2ND_TIME;
import static com.kursova.getsun.model.enums.TextResourceKeys.SUNRISE_NOTIF_FORMAT;
import static com.kursova.getsun.model.enums.TextResourceKeys.SUNSET_NOTIF_FORMAT;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCommandResolverImpl implements UserCommandResolver {

    private final UserService userService;
    private final UserSetupStepsBO userSetupStepsBO;
    private final UserConfigRepository userConfigRepository;
    private final SunEventService sunEventService;
    private final TextResourceService textResourceService;
    private final ResourceUtil resourceUtil;

    @Override
    public String resolveStart(Update update) {
        if (userService.checkIfUserRegistered(update.getMessage().getChatId())) {
            return textResourceService.get(START_2ND_TIME);
        }
        userService.register(update);
        List<UserSetupSteps> userSetupStepsList = userSetupStepsBO.getUserSetupStepsList();
        boolean isPresent = false;
        for (UserSetupSteps setupSteps : userSetupStepsList) {
            if (setupSteps.getChatId() == (update.getMessage().getChatId())) {
                isPresent = true;
                setupSteps.setWaitingForLocation(true);
            }
        }
        if (!isPresent) {
            UserSetupSteps userSetupSteps = new UserSetupSteps();
            userSetupSteps.setChatId(update.getMessage().getChatId());
            userSetupSteps.setWaitingForLocation(true);
            userSetupStepsList.add(userSetupSteps);
        }
        return textResourceService.get(LOCATION_SETUP);
    }

    @Override
    public String resolveSunEvent(Long chatId, String eventName) {
        if (!userService.checkIfUserRegistered(chatId)) {
            return textResourceService.get(BOT_NOT_CONF_YET);
        }
        UserConfig userConfig = userConfigRepository.findAll().stream()
                .filter(x -> x.getUser().getChatId() == chatId)
                .findFirst().orElse(null);
        if (userConfig == null || !userConfig.isSetupFinished()) {
            return textResourceService.get(BOT_NOT_CONF_YET);
        }
        String sunEventTime = sunEventService.getSunEventTimeByUser(userConfig.getUser(), eventName);
        TextResourceKeys responseFormatTRK = (SUNRISE_EVENT_NAME.equals(eventName)) ? SUNRISE_NOTIF_FORMAT : SUNSET_NOTIF_FORMAT;
        String sunEventEmojiURKEncoded = (SUNRISE_EVENT_NAME.equals(eventName))
                ? textResourceService.get(TextResourceKeys.SUNRISE_EMOJI_URL_ENCODED)
                : textResourceService.get(TextResourceKeys.SUNSET_EMOJI_URL_ENCODED);
        String sunEventEmoji = URLDecoder.decode(sunEventEmojiURKEncoded);

        return String.format(textResourceService.get(responseFormatTRK), sunEventEmoji, sunEventTime);
    }

    @Override
    public String resolveRemoveme(Update update) {
        Long chatId = update.getMessage().getChatId();
        UserSetupSteps userSetupSteps = null;
        for (UserSetupSteps setupSteps : userSetupStepsBO.getUserSetupStepsList()) {
            if (setupSteps.getChatId() == update.getMessage().getChatId()) {
                userSetupSteps = setupSteps;
            }
        }
        if (userSetupSteps == null) {
            userSetupSteps = new UserSetupSteps(chatId);
            userSetupStepsBO.getUserSetupStepsList().add(userSetupSteps);
        }
        boolean removeMeExpired = ChronoUnit.SECONDS
                .between(userSetupSteps.getRemovemeSentTime(), LocalDateTime.now()) > REMOVEME_EXPIRE_SEC;
        if (removeMeExpired) {
            // Setting removeme command sent time
            userSetupSteps.setRemovemeSentTime(LocalDateTime.now());
            return textResourceService.get(TextResourceKeys.REMOVEME_CONFIRM);
        }
        userService.remove(update);
        userSetupStepsBO.removeByChatId(chatId);

        return textResourceService.get(REMOVEME);
    }

    @Override
    public String resolveHelp(Update update) {
        return String.format(textResourceService.get(TextResourceKeys.HELP_CMD),
                             resourceUtil.readResourceFileLines(PATH_CONSTANT.BOT_COMMANDS));
    }
}
