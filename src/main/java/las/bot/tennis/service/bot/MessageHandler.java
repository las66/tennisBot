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
        Long currentUserId = message.getChatId();
        User currentUser = userService.getUser(currentUserId);
        UserStateEnum state = getById(currentUser.getContext().getState());
        userContextService.setMenuMessageId(currentUser.getChatId(), null);
        String data = message.getText();
        if (data.startsWith(BotCommandsEnum.START.getCommand())) {
            sendMessageService.sendMessage(currentUser.getChatId(), "Тут будет приветственное сообщение"); //todo
            return;
        }
        switch (state) {
            case GET_CLIENT_STEP_1:
                List<User> users = userService.findUsers(data);
                InlineKeyboardMarkup keyboard = null;
                if (users.isEmpty()) {
                    sendMessageService.sendMessage(currentUserId, "Клиенты по данному фильтру не найдены");
                } else {
                    userContextService.setState(currentUserId, GET_CLIENT_STEP_2);
                    keyboard = keyboardGenerator.inlineUserKeyboard(users);
                }
                sendMessageService.sendStateMessage(currentUserId, keyboard);
                break;
            case NEW_GROUP_STEP_1:
                if (groupService.getGroup(data) != null) {
                    userContextService.setState(currentUserId, GROUP_ALREADY_EXISTS);
                } else {
                    groupService.createGroup(message);
                    userContextService.setState(currentUserId, MAIN_MENU);
                }
                sendMessageService.sendStateMessage(currentUserId);
                break;
            case ADD_CLIENT_TO_GROUP_STEP_1:
            case SEND_MESSAGE_TO_GROUP_MENU:
            case DELETE_GROUP_STEP_1:
            case RENAME_GROUP_STEP_1:
            case LIST_GROUP_STEP_1:
            case DELETE_CLIENT_FROM_GROUP_STEP_1:
                List<Group> groups = groupService.getAll(data);
                sendMessageService.sendStateMessage(currentUserId, keyboardGenerator.inlineGroupKeyboard(groups));
                break;
            case ADD_CLIENT_TO_GROUP_STEP_2:
                users = userService.findUsers(data);
                keyboard = null;
                if (users.isEmpty()) {
                    sendMessageService.sendMessage(currentUserId, "Клиенты по данному фильтру не найдены");
                } else {
                    userContextService.setState(currentUserId, ADD_CLIENT_TO_GROUP_STEP_3);
                    keyboard = keyboardGenerator.inlineUserKeyboard(users);
                }
                sendMessageService.sendStateMessage(currentUserId, keyboard);
                break;
            case MESSAGE_FOR_GROUP:
                userContextService.setState(currentUserId, MAIN_MENU);
                sendMessageService.sendMessageToGroup(data, currentUser.getContext().getTargetUserGroup());
                String infoMessage = "Группе " + currentUser.getContext().getTargetUserGroup() + " успешно отправлено сообщение:\n" + data;
                sendMessageService.sendMessage(currentUser.getChatId(), infoMessage);
                sendMessageService.sendStateMessage(currentUserId);
                break;
            case RENAME_GROUP_STEP_2:
                if (groupService.getGroup(data) != null) {
                    sendMessageService.sendMessage(currentUserId, GROUP_ALREADY_EXISTS.getMessage());
                } else {
                    groupService.renameGroup(currentUser.getChatId(), currentUser.getContext().getTargetUserGroup(), data);
                    userContextService.setState(currentUserId, MAIN_MENU);
                }
                sendMessageService.sendStateMessage(currentUserId);
                break;
            case CHANGE_CLIENT_STEP_1:
            case CHANGE_CLIENT_STEP_2:
                users = userService.findUsers(data);
                keyboard = null;
                if (users.isEmpty()) {
                    sendMessageService.sendMessage(currentUserId, "Клиенты по данному фильтру не найдены");
                } else {
                    userContextService.setState(currentUserId, CHANGE_CLIENT_STEP_2);
                    keyboard = keyboardGenerator.inlineUserKeyboard(users);
                }
                sendMessageService.sendStateMessage(currentUserId, keyboard);
                break;
            case CHANGE_CLIENT_STEP_3_1:
                userContextService.setState(currentUserId, CHANGE_CLIENT_STEP_3);
                userService.setName(currentUser.getContext().getTargetUserId(), data);
                String clientInfo = userService.getUser(currentUser.getContext().getTargetUserId()).toLongString();
                sendMessageService.sendMessage(currentUserId, clientInfo);
                sendMessageService.sendStateMessage(currentUserId);
                break;
            case CHANGE_CLIENT_STEP_3_2:
                userContextService.setState(currentUserId, CHANGE_CLIENT_STEP_3);
                userService.setPhone(currentUser.getContext().getTargetUserId(), data);
                clientInfo = userService.getUser(currentUser.getContext().getTargetUserId()).toLongString();
                sendMessageService.sendMessage(currentUserId, clientInfo);
                sendMessageService.sendStateMessage(currentUserId);
                break;
            case CHANGE_CLIENT_STEP_3_3:
                userContextService.setState(currentUserId, CHANGE_CLIENT_STEP_3);
                userService.setDescription(currentUser.getContext().getTargetUserId(), data);
                clientInfo = userService.getUser(currentUser.getContext().getTargetUserId()).toLongString();
                sendMessageService.sendMessage(currentUserId, clientInfo);
                sendMessageService.sendStateMessage(currentUserId);
                break;
            default:
                sendMessageService.sendMessage(currentUser.getChatId(), WRONG_COMMAND.getCommand());
                sendMessageService.sendStateMessage(currentUserId);
                break;
        }
    }

}
