package com.kursova.getsun.controller;

import com.kursova.getsun.service.TextResourceService;
import com.kursova.getsun.service.UserInputResolver;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import lombok.RequiredArgsConstructor;

import static com.kursova.getsun.model.enums.TextResourceKeys.MSG_TYPE_NOT_SUP;

@Component
@RequiredArgsConstructor
public class InputController implements ApiController {

    private final UserInputResolver userInputResolver;
    private final TextResourceService textResourceService;

    @Override
    public String handle(Update update) {
        Message message = update.getMessage();
        if (message.isCommand()) {
            return userInputResolver.resolveCommand(update);
        } else if (message.getText() != null) {
            return userInputResolver.resolvePlainText(update);
        } else if (message.getLocation() != null) {
            return userInputResolver.resolveLocation(update);
        } else {
            return textResourceService.get(MSG_TYPE_NOT_SUP);
        }
    }
}
