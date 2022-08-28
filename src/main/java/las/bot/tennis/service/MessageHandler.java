package las.bot.tennis.service;

import las.bot.tennis.helper.KeyboardGenerator;
import las.bot.tennis.model.Group;
import las.bot.tennis.model.User;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

import static las.bot.tennis.service.BotCommandsEnum.GO_TO_MAIN_MENU;
import static las.bot.tennis.service.BotCommandsEnum.WRONG_COMMAND;
import static las.bot.tennis.service.UserStateEnum.*;

@Service
public class MessageHandler {

    private final UserService userService;
    private final GroupService groupService;
    private final SendMessageService sendMessageService;
    private final KeyboardGenerator keyboardGenerator;

    public MessageHandler(UserService userService,
                          GroupService groupService,
                          SendMessageService sendMessageService,
                          KeyboardGenerator keyboardGenerator) {
        this.userService = userService;
        this.groupService = groupService;
        this.sendMessageService = sendMessageService;
        this.keyboardGenerator = keyboardGenerator;
    }

    public void process(Message message) {
        BotCommandsEnum command = BotCommandsEnum.getByCommand(message.getText());

        if (command == GO_TO_MAIN_MENU) {
            userService.changeStateTo(message.getChatId(), MAIN_MENU);
            sendMessageService.sendStateMessage(message.getChatId());
            return;
        }

        User currentUser = userService.getUser(message.getChatId());
        UserStateEnum state = getById(currentUser.getState());

        switch (state) {
            case GET_CLIENT_STEP_1:
                List<User> users = userService.findUsers(message.getText());
                InlineKeyboardMarkup keyboard = null;
                if (users.isEmpty()) {
                    userService.changeStateTo(message.getChatId(), CLIENTS_NOT_FOUND);
                } else {
                    userService.changeStateTo(message.getChatId(), GET_CLIENT_STEP_2);
                    keyboard = keyboardGenerator.inlineUserKeyboard(users);
                }
                sendMessageService.sendStateMessage(message.getChatId(), keyboard);
                return;
            case NEW_GROUP_STEP_1:
                if (groupService.getGroup(message.getText()) != null) {
                    userService.changeStateTo(message.getChatId(), GROUP_ALREADY_EXISTS);
                } else {
                    groupService.createGroup(message);
                    userService.changeStateTo(message.getChatId(), MAIN_MENU);
                }
                sendMessageService.sendStateMessage(message.getChatId());
                return;
            case ADD_CLIENT_TO_GROUP_STEP_1:
                userService.changeStateTo(message.getChatId(), ADD_CLIENT_TO_GROUP_STEP_2);
                List<Group> groups = groupService.getAll(message.getText());
                sendMessageService.sendStateMessage(message.getChatId(), keyboardGenerator.inlineGroupKeyboard(groups));
                return;
            case ADD_CLIENT_TO_GROUP_STEP_2:
                users = userService.findUsers(message.getText());
                keyboard = null;
                if (users.isEmpty()) {
                    sendMessageService.sendMessage(message.getChatId(), CLIENTS_NOT_FOUND.getMessage());
                } else {
                    userService.changeStateTo(message.getChatId(), ADD_CLIENT_TO_GROUP_STEP_3, currentUser.getContext());
                    keyboard = keyboardGenerator.inlineUserKeyboard(users);
                }
                sendMessageService.sendStateMessage(message.getChatId(), keyboard);
                return;
        }

        if (command == WRONG_COMMAND) {
            sendMessageService.sendMessage(message.getChatId(), WRONG_COMMAND.getCommand());
        } else {
            userService.changeStateTo(message.getChatId(), command.getNextState());
        }

        sendMessageService.sendStateMessage(message.getChatId());
    }

}
