package las.bot.tennis.service.bot;

import las.bot.tennis.model.Group;
import las.bot.tennis.model.TennisBot;
import las.bot.tennis.model.User;
import las.bot.tennis.service.database.GroupService;
import las.bot.tennis.service.database.UserContextService;
import las.bot.tennis.service.database.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static las.bot.tennis.service.bot.BotCommandsEnum.GO_TO_MAIN_MENU;
import static las.bot.tennis.service.bot.BotCommandsEnum.WRONG_COMMAND;
import static las.bot.tennis.service.bot.UserStateEnum.*;

@Slf4j
@Service
public class CallbackQueryHandler {

    private final UserService userService;
    private final UserContextService userContextService;
    private final CommandStateService commandStateService;
    private final GroupService groupService;
    private final SendMessageService sendMessageService;
    private final TennisBot bot;

    public CallbackQueryHandler(UserService userService,
                                UserContextService userContextService,
                                CommandStateService commandStateService,
                                GroupService groupService,
                                SendMessageService sendMessageService,
                                TennisBot bot) {
        this.userService = userService;
        this.userContextService = userContextService;
        this.commandStateService = commandStateService;
        this.groupService = groupService;
        this.sendMessageService = sendMessageService;
        this.bot = bot;
    }

    public void process(CallbackQuery callbackQuery) {
        Long currentUserId = callbackQuery.getMessage().getChatId();
        User currentUser = userService.getUser(currentUserId);
        String data = callbackQuery.getData();
        if (GO_TO_MAIN_MENU.name().equals(data)) {
            userContextService.setState(currentUserId, commandStateService.getNextState(GO_TO_MAIN_MENU));
        } else {
            UserStateEnum state = UserStateEnum.getById(currentUser.getContext().getState());
            switch (state) {
                case GET_CLIENT_STEP_2:
                    userContextService.setState(currentUserId, CLIENT_WORK_MENU);
                    String clientInfo = userService.getUser(data).toShortString();
                    sendMessageService.sendMessage(currentUserId, clientInfo);
                    break;
                case ADD_CLIENT_TO_GROUP_STEP_1:
                    userContextService.setState(currentUserId, ADD_CLIENT_TO_GROUP_STEP_2);
                    userContextService.setUserGroup(currentUserId, data);
                    String groupInfo = groupService.getGroup(data).toShortString();
                    sendMessageService.sendMessage(currentUserId, groupInfo);
                    break;
                case ADD_CLIENT_TO_GROUP_STEP_3:
                    userService.addToGroup(currentUserId, data, groupService.getGroup(currentUser.getContext().getUserGroup()));
                    userContextService.setState(currentUserId, CLIENT_ADDED_TO_GROUP);
                    break;
                case SEND_MESSAGE_TO_GROUP_MENU:
                    userContextService.setState(currentUserId, MESSAGE_FOR_GROUP);
                    userContextService.setUserGroup(currentUserId, data);
                    groupInfo = groupService.getGroup(data).toShortString();
                    sendMessageService.sendMessage(currentUserId, groupInfo);
                    break;
                case DELETE_GROUP_STEP_1:
                    groupService.deleteGroup(currentUserId, data);
                    userContextService.setState(currentUserId, GROUPS_WORK_MENU);
                    break;
                case RENAME_GROUP_STEP_1:
                    Group group = groupService.getGroup(data);
                    groupInfo = group.toShortString();
                    sendMessageService.sendMessage(currentUserId, "Выбрана группа " + groupInfo);
                    userContextService.setState(currentUserId, RENAME_GROUP_STEP_2);
                    userContextService.setUserGroup(currentUserId, group.getName());
                    break;
                case LIST_GROUP_STEP_1:
                    group = groupService.getGroup(data);
                    groupInfo = group.toLongString();
                    sendMessageService.sendMessage(currentUserId, groupInfo);
                    userContextService.setState(currentUserId, GROUPS_WORK_MENU);
                    break;
                default:
                    if (WRONG_COMMAND.name().equals(data)) {
                        sendMessageService.sendMessage(currentUserId, WRONG_COMMAND.getCommand());
                    } else {
                        userContextService.setState(currentUserId, commandStateService.getNextState(BotCommandsEnum.valueOf(data)));
                    }
                    break;
            }
        }
        sendMessageService.sendStateMessage(currentUserId);

        try {
            bot.execute(new AnswerCallbackQuery(callbackQuery.getId()));
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке AnswerCallbackQuery", e);
        }

    }

}
