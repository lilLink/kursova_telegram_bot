package com.kursova.suncheck.service;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface BotErrorHandler {

    void handleSendingMessageException(long chatId, String message, TelegramApiException e);
}
