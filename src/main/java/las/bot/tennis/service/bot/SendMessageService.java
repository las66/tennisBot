package las.bot.tennis.service.bot;

import las.bot.tennis.model.Group;
import las.bot.tennis.model.TennisBot;
import las.bot.tennis.model.User;
import las.bot.tennis.service.database.GroupService;
import las.bot.tennis.service.database.UserContextService;
import las.bot.tennis.service.database.UserService;
import las.bot.tennis.service.helper.KeyboardGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static java.lang.Long.parseLong;

@Slf4j
@Service
public class SendMessageService {

    private final UserService userService;
    private final UserContextService userContextService;
    private final GroupService groupService;
    private final TennisBot bot;
    private final KeyboardGenerator keyboardGenerator;

    public SendMessageService(UserService userService,
                              UserContextService userContextService,
                              GroupService groupService,
                              TennisBot bot,
                              @Lazy KeyboardGenerator keyboardGenerator) {
        this.userService = userService;
        this.userContextService = userContextService;
        this.groupService = groupService;
        this.bot = bot;
        this.keyboardGenerator = keyboardGenerator;
    }

    public void sendStateMessage(Long userId) {
        sendStateMessage(userId, null);
    }

    public void sendStateMessage(Long userId, InlineKeyboardMarkup keyboard) {
        User user = userService.getUser(userId);
        UserStateEnum userStateEnum = UserStateEnum.getById(user.getContext().getState());
        InlineKeyboardMarkup newKeyboard = keyboard == null ? keyboardGenerator.getKeyboardByState(userStateEnum, user.getGroups()) : keyboard;
        if (user.getContext().getMenuMessageId() == null) {
            SendMessage sendMessage = new SendMessage(userId.toString(), userStateEnum.getMessage());
            sendMessage.setReplyMarkup(newKeyboard);
            userContextService.setMenuMessageId(userId, sendMessage(sendMessage));
        } else {
            editMenuMessage(user, newKeyboard);
        }
    }

    public void editMenuMessage(User user, InlineKeyboardMarkup newKeyboard) {
        EditMessageText editMessageText = new EditMessageText(UserStateEnum.getById(user.getContext().getState()).getMessage());
        editMessageText.setChatId(user.getChatId());
        editMessageText.setMessageId(user.getContext().getMenuMessageId());
        editMessageText.setReplyMarkup(newKeyboard);
        try {
            log.debug("Редактирование сообщения: " + editMessageText);
            bot.execute(editMessageText);
        } catch (TelegramApiException e) {
            log.error("Ошибка при редактировании сообщения", e);
        }
    }

    public void sendMessage(Long userId, String message) {
        sendMessage(new SendMessage(userId.toString(), message));
    }

    public Integer sendMessage(SendMessage sendMessage) {
        try {
            log.debug("Отправка сообщения: " + sendMessage);
            Message message = bot.execute(sendMessage);
            userContextService.setMenuMessageId(parseLong(sendMessage.getChatId()), null);
            return message.getMessageId();
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения", e);
        }
        return null;
    }

    public void sendMessageToGroup(String message, String groupName) {
        Group group = groupService.getGroup(groupName);
        for (User user : group.getUsers()) {
            sendMessage(user.getChatId(), message);
        }
    }
}
