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
import static las.bot.tennis.service.database.GroupService.ADMIN_GROUP;

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
        String data = message.getText();
        if (data.startsWith(BotCommandsEnum.START.getCommand())) {
            sendMessageService.sendMessage(currentUserId, "Тут будет приветственное сообщение"); //todo
            return;
        }

        User currentUser = userService.getUser(currentUserId);
        boolean currentUserIsAdmin = currentUser.getGroups().stream().map(Group::getName).anyMatch(name -> name.equals(ADMIN_GROUP));
        if (!currentUserIsAdmin) {
            sendMessageService.sendMessage(currentUserId, WRONG_COMMAND.getCommand());
            return;
        }
        userContextService.setMenuMessageId(currentUserId, null);
        UserStateEnum state = getById(currentUser.getContext().getState());
        switch (state) {
            case GET_CLIENT_STEP_1:
            case GET_CLIENT_STEP_2:
                List<User> users = userService.findUsers(data);
                InlineKeyboardMarkup keyboard = null;
                if (users.isEmpty()) {
                    sendMessageService.sendMessage(currentUserId, "Клиенты по данному фильтру не найдены");
                } else {
                    userContextService.setState(currentUserId, GET_CLIENT_STEP_2);
                    keyboard = keyboardGenerator.inlineUsersKeyboard(users);
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
            case SEND_POLL_STEP_1:
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
                    keyboard = keyboardGenerator.inlineUsersKeyboard(users);
                }
                sendMessageService.sendStateMessage(currentUserId, keyboard);
                break;
            case MESSAGE_FOR_GROUP:
                userContextService.setState(currentUserId, MAIN_MENU);
                sendMessageService.sendMessageToGroup(data, currentUser.getContext().getTargetUserGroup());
                String infoMessage = "Группе " + currentUser.getContext().getTargetUserGroup() + " успешно отправлено сообщение:\n" + data;
                sendMessageService.sendMessage(currentUserId, infoMessage);
                sendMessageService.sendStateMessage(currentUserId);
                break;
            case RENAME_GROUP_STEP_2:
                if (groupService.getGroup(data) != null) {
                    sendMessageService.sendMessage(currentUserId, GROUP_ALREADY_EXISTS.getMessage());
                } else {
                    groupService.renameGroup(currentUserId, currentUser.getContext().getTargetUserGroup(), data);
                    userContextService.setState(currentUserId, MAIN_MENU);
                }
                sendMessageService.sendStateMessage(currentUserId);
                break;
            case CHANGE_CLIENT_NAME:
                userService.setName(currentUser.getContext().getTargetUserId(), data);
                userContextService.setState(currentUserId, CLIENT_WORK_MENU);
                sendMessageService.sendStateMessage(currentUserId);
                break;
            case CHANGE_CLIENT_PHONE:
                userService.setPhone(currentUser.getContext().getTargetUserId(), data);
                userContextService.setState(currentUserId, CLIENT_WORK_MENU);
                sendMessageService.sendStateMessage(currentUserId);
                break;
            case CHANGE_CLIENT_DESC:
                userService.setDescription(currentUser.getContext().getTargetUserId(), data);
                userContextService.setState(currentUserId, CLIENT_WORK_MENU);
                sendMessageService.sendStateMessage(currentUserId);
                break;
            case SEND_MESSAGE_TO_CLIENT_STEP_1:
            case SEND_MESSAGE_TO_CLIENT_STEP_2:
                users = userService.findUsers(data);
                keyboard = null;
                if (users.isEmpty()) {
                    sendMessageService.sendMessage(currentUserId, "Клиенты по данному фильтру не найдены");
                } else {
                    userContextService.setState(currentUserId, SEND_MESSAGE_TO_CLIENT_STEP_2);
                    keyboard = keyboardGenerator.inlineUsersKeyboard(users);
                }
                sendMessageService.sendStateMessage(currentUserId, keyboard);
                break;
            case SEND_MESSAGE_TO_CLIENT_STEP_3:
                Long targetUserId = currentUser.getContext().getTargetUserId();
                sendMessageService.sendMessage(targetUserId, data);
                String userInfo = userService.getUser(targetUserId).toOneLineString();
                sendMessageService.sendMessage(currentUserId, "Сообщение отправлено пользователю " + userInfo);
                userContextService.setState(currentUserId, MAIN_MENU);
                sendMessageService.sendStateMessage(currentUserId);
                break;
            default:
                sendMessageService.sendMessage(currentUserId, WRONG_COMMAND.getCommand());
                sendMessageService.sendStateMessage(currentUserId);
                break;
        }
    }

}
