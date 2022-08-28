package las.bot.tennis.model;

import las.bot.tennis.config.BotConfig;
import las.bot.tennis.service.bot.BotUpdateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
public class TennisBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final BotUpdateHandler botUpdateHandler;

    public TennisBot(BotConfig botConfig, @Lazy BotUpdateHandler botUpdateHandler) {
        this.botConfig = botConfig;
        this.botUpdateHandler = botUpdateHandler;
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
        botUpdateHandler.process(update);
    }

}
