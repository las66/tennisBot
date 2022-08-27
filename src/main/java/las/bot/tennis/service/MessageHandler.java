package las.bot.tennis.service;

import las.bot.tennis.helper.KeyboardGenerator;
import las.bot.tennis.model.User;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

import static las.bot.tennis.service.BotCommandsEnum.GO_TO_MAIN_MENU;
import static las.bot.tennis.service.UserStateEnum.*;

@Service
public class MessageHandler {

    private final UserService userService;
    private final SendMessageService sendMessageService;

    public MessageHandler(UserService userService, SendMessageService sendMessageService) {
        this.userService = userService;
        this.sendMessageService = sendMessageService;
    }

    public void process(Message message) {
        BotCommandsEnum command = BotCommandsEnum.getByCommand(message.getText());
        if (command == GO_TO_MAIN_MENU) {
            userService.changeStateTo(message.getChatId(), MAIN_MENU);
            sendMessageService.sendStateMessage(message.getChatId());
            return;
        }

        User user = userService.getUser(message.getChatId());
        UserStateEnum state = getById(user.getState());

        switch (state) {
            case GET_CLIENT_STEP_1:
                List<User> users = userService.findUsers(message.getText());
                InlineKeyboardMarkup keyboard = null;
                if (users.isEmpty()) {
                    userService.changeStateTo(message.getChatId(), CLIENTS_NOT_FOUND);
                } else {
                    userService.changeStateTo(message.getChatId(), GET_CLIENT_STEP_2);
                    keyboard = KeyboardGenerator.inlineKeyboard(users);
                }
                sendMessageService.sendStateMessage(message.getChatId(), keyboard);
                return;
        }


        switch (command) {
            case WORK_WITH_CLIENTS:
                userService.changeStateTo(message.getChatId(), CLIENTS_WORK_MENU);
                break;
            case GET_CLIENT:
                userService.changeStateTo(message.getChatId(), GET_CLIENT_STEP_1);
                break;
        }
        sendMessageService.sendStateMessage(message.getChatId());
    }

}
