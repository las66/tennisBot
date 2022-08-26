package las.bot.tennis.service;

import las.bot.tennis.model.TennisBot;
import las.bot.tennis.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Service
public class SendMessageService {

    private final UserService userService;
    private final TennisBot bot;

    public SendMessageService(UserService userService, TennisBot bot) {
        this.userService = userService;
        this.bot = bot;
    }

    public void sendStateMessage(Long userId) {
        User user = userService.getUser(userId);
        UserStateEnum userStateEnum = UserStateEnum.getById(user.getState());
        SendMessage sendMessage = new SendMessage(userId.toString(), userStateEnum.getMessage());
        sendMessage.setReplyMarkup(userStateEnum.getKeyboard());
        try {
            bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщния: " + e.getMessage());
        }
    }

}
