package las.bot.tennis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Service
public class BotUpdateHandler {

    private final MessageHandler messageHandler;
    private final UserService userService;

    public BotUpdateHandler(MessageHandler messageHandler, UserService userService) {
        this.messageHandler = messageHandler;
        this.userService = userService;
    }

    public void process(Update update) {
        log.debug("Событие: " + update);

        Message message = update.getMessage();
        if (message != null) {
            userService.createUserIfAbsent(message);
            messageHandler.process(message);
        }
    }
}
