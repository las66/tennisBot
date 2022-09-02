package las.bot.tennis.service.bot;

import las.bot.tennis.model.Group;
import las.bot.tennis.model.User;
import las.bot.tennis.service.database.GroupService;
import las.bot.tennis.service.database.UserContextService;
import las.bot.tennis.service.database.UserService;
import las.bot.tennis.service.helper.KeyboardGenerator;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

import static las.bot.tennis.service.bot.BotCommandsEnum.WRONG_COMMAND;
import static las.bot.tennis.service.bot.UserStateEnum.*;

@Service
public class MessageHandler {

    private final UserService userService;
    private final UserContextService userContextService;
    private final GroupService groupService;
    private final SendMessageService sendMessageService;
    private final KeyboardGenerator keyboardGenerator;

    public MessageHandler(UserService userService,
                          UserContextService userContextService,
                          GroupService groupService,
                          SendMessageService sendMessageService,
                          KeyboardGenerator keyboardGenerator) {
        this.userService = userService;
        this.userContextService = userContextService;
        this.groupService = groupService;
        this.sendMessageService = sendMessageService;
        this.keyboardGenerator = keyboardGenerator;
    }

    public void process(Message message) {
        User currentUser = userService.getUser(message.getChatId());
        UserStateEnum state = getById(currentUser.getContext().getState());
        userContextService.setMenuMessageId(currentUser.getChatId(), null);
        if (message.getText().startsWith(BotCommandsEnum.START.getCommand())) {
            sendMessageService.sendMessage(currentUser.getChatId(), "Тут будет приветственное сообщение"); //todo
            return;
        }
        switch (state) {
            case GET_CLIENT_STEP_1:
                List<User> users = userService.findUsers(message.getText());
                InlineKeyboardMarkup keyboard = null;
                if (users.isEmpty()) {
                    userContextService.setState(message.getChatId(), CLIENTS_NOT_FOUND);
                } else {
                    userContextService.setState(message.getChatId(), GET_CLIENT_STEP_2);
                    keyboard = keyboardGenerator.inlineUserKeyboard(users);
                }
                sendMessageService.sendStateMessage(message.getChatId(), keyboard);
                break;
            case NEW_GROUP_STEP_1:
                if (groupService.getGroup(message.getText()) != null) {
                    userContextService.setState(message.getChatId(), GROUP_ALREADY_EXISTS);
                } else {
                    groupService.createGroup(message);
                    userContextService.setState(message.getChatId(), MAIN_MENU);
                }
                sendMessageService.sendStateMessage(message.getChatId());
                break;
            case ADD_CLIENT_TO_GROUP_STEP_1:
            case SEND_MESSAGE_TO_GROUP_MENU:
            case DELETE_GROUP_STEP_1:
            case RENAME_GROUP_STEP_1:
                List<Group> groups = groupService.getAll(message.getText());
                sendMessageService.sendStateMessage(message.getChatId(), keyboardGenerator.inlineGroupKeyboard(groups));
                break;
            case ADD_CLIENT_TO_GROUP_STEP_2:
                users = userService.findUsers(message.getText());
                keyboard = null;
                if (users.isEmpty()) {
                    sendMessageService.sendMessage(message.getChatId(), CLIENTS_NOT_FOUND.getMessage());
                } else {
                    userContextService.setState(message.getChatId(), ADD_CLIENT_TO_GROUP_STEP_3);
                    keyboard = keyboardGenerator.inlineUserKeyboard(users);
                }
                sendMessageService.sendStateMessage(message.getChatId(), keyboard);
                break;
            case MESSAGE_FOR_GROUP:
                userContextService.setState(message.getChatId(), MAIN_MENU);
                sendMessageService.sendMessageToGroup(message.getText(), currentUser.getContext().getUserGroup());
                String infoMessage = "Группе " + currentUser.getContext().getUserGroup() + " успешно отправлено сообщение:\n" + message.getText();
                sendMessageService.sendMessage(currentUser.getChatId(), infoMessage);
                sendMessageService.sendStateMessage(message.getChatId());
                break;
            case RENAME_GROUP_STEP_2:
                if (groupService.getGroup(message.getText()) != null) {
                    sendMessageService.sendMessage(message.getChatId(), GROUP_ALREADY_EXISTS.getMessage());
                } else {
                    groupService.renameGroup(currentUser.getChatId(), currentUser.getContext().getUserGroup(), message.getText());
                    userContextService.setState(message.getChatId(), MAIN_MENU);
                }
                sendMessageService.sendStateMessage(message.getChatId());
                break;

            default:
                sendMessageService.sendMessage(currentUser.getChatId(), WRONG_COMMAND.getCommand());
                sendMessageService.sendStateMessage(message.getChatId());
                break;
        }
    }

}
