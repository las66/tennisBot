package las.bot.tennis.service.bot;

import las.bot.tennis.service.bot.query.callback.CallbackQueryHandler;
import las.bot.tennis.service.database.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Service
public class BotUpdateHandler {

    private final MessageHandler messageHandler;
    private final CallbackQueryHandler callbackQueryHandler;
    private final UserService userService;

    public BotUpdateHandler(MessageHandler messageHandler, CallbackQueryHandler callbackQueryHandler, UserService userService) {
        this.messageHandler = messageHandler;
        this.callbackQueryHandler = callbackQueryHandler;
        this.userService = userService;
    }

    public void process(Update update) {
        log.debug("Событие: " + update);
        if (update.hasCallbackQuery()) {
            callbackQueryHandler.process(update.getCallbackQuery());
        } else {
            Message message = update.getMessage();
            if (message != null) {
                userService.createUserIfAbsent(message);
                messageHandler.process(message);
            } else {
                log.debug("Неизвестный апдейт:" + update);
            }
        }
    }

}
