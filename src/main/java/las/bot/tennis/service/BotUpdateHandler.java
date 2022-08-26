package las.bot.tennis.service;

import las.bot.tennis.model.User;
import las.bot.tennis.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

@Slf4j
@Service
public class BotUpdateHandler {

    private final TelegramLongPollingBot bot;
    private final UserRepository userRepository;

    public BotUpdateHandler(TelegramLongPollingBot bot, UserRepository userRepository) {
        this.bot = bot;
        this.userRepository = userRepository;
    }

    public void handleUpdate(Update update) {
        log.debug("Событие: " + update);
        Message message = update.getMessage();
        if (message != null) {
            Optional<User> userOptional = userRepository.findById(message.getChatId());
            User user;
            if (userOptional.isPresent()) {
                user = userOptional.get();
                user.setMessageCount(user.getMessageCount() + 1);
            } else {
                user = new User(message.getChatId(), 1);
            }
            userRepository.save(user);

            Long chatId = message.getChat().getId();
            SendMessage sendMessage = new SendMessage(chatId.toString(), user.getMessageCount() + ") " + message.getText());
            try {
                bot.execute(sendMessage);
            } catch (TelegramApiException e) {
                log.error("Ошибка при ответе: " + e.getMessage());
            }
        }
    }
}
