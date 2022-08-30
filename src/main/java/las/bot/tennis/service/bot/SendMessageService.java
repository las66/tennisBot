package las.bot.tennis.service.bot;

import las.bot.tennis.model.Group;
import las.bot.tennis.model.TennisBot;
import las.bot.tennis.model.User;
import las.bot.tennis.service.database.GroupService;
import las.bot.tennis.service.database.UserService;
import las.bot.tennis.service.helper.KeyboardGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Service
public class SendMessageService {

    private final UserService userService;
    private final GroupService groupService;
    private final TennisBot bot;
    private final KeyboardGenerator keyboardGenerator;

    public SendMessageService(UserService userService, GroupService groupService, TennisBot bot, @Lazy KeyboardGenerator keyboardGenerator) {
        this.userService = userService;
        this.groupService = groupService;
        this.bot = bot;
        this.keyboardGenerator = keyboardGenerator;
    }

    public void sendStateMessage(Long userId) {
        sendStateMessage(userId, null);
    }

    public void sendStateMessage(Long userId, ReplyKeyboard keyboard) {
        User user = userService.getUser(userId);
        UserStateEnum userStateEnum = UserStateEnum.getById(user.getContext().getState());
        SendMessage sendMessage = new SendMessage(userId.toString(), userStateEnum.getMessage());
        sendMessage.setReplyMarkup(keyboard == null ? keyboardGenerator.getKeyboardByState(userStateEnum, user.getGroups()) : keyboard);
        sendMessage(sendMessage);
    }

    public void sendMessage(Long userId, String message) {
        sendMessage(new SendMessage(userId.toString(), message));
    }

    public void sendMessage(SendMessage sendMessage) {
        try {
            log.debug("Отправка сообщения: " + sendMessage);
            bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения: " + e.getMessage());
        }
    }

    public void sendMessageToGroup(String message, String groupName) {
        Group group = groupService.getGroup(groupName);
        for (User user : group.getUsers()) {
            sendMessage(user.getChatId(), message);
        }
    }
}
