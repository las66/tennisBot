package las.bot.tennis.service;

import las.bot.tennis.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
public class TennisBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;

    public TennisBot(BotConfig botConfig) {
        this.botConfig = botConfig;
    }

    @Override
    public String getBotUsername() {
        return botConfig.getUsername();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.debug("Событие: " + update);
        Message message = update.getMessage();
        if (message != null) {
            Long chatId = message.getChat().getId();
            SendMessage sendMessage = new SendMessage(chatId.toString(), message.getText());
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                log.error("Ошибка при ответе: " + e.getMessage());
            }
        }
    }

}
