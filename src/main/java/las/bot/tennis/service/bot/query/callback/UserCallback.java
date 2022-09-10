package las.bot.tennis.service.bot.query.callback;

import las.bot.tennis.model.User;
import las.bot.tennis.service.bot.CommandStateService;
import las.bot.tennis.service.bot.SendMessageService;
import las.bot.tennis.service.bot.UserStateEnum;
import las.bot.tennis.service.database.GroupService;
import las.bot.tennis.service.database.UserContextService;
import las.bot.tennis.service.database.UserService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import static java.lang.Long.parseLong;

@Service
public class UserCallback {

    private final UserService userService;
    private final UserContextService userContextService;
    private final SendMessageService sendMessageService;
    private final CommandStateService commandStateService;
    private final GroupService groupService;

    public UserCallback(UserService userService,
                        UserContextService userContextService,
                        SendMessageService sendMessageService,
                        CommandStateService commandStateService,
                        GroupService groupService) {
        this.userService = userService;
        this.userContextService = userContextService;
        this.sendMessageService = sendMessageService;
        this.commandStateService = commandStateService;
        this.groupService = groupService;
    }

    public void process(CallbackQuery callbackQuery) {
        String clientId = callbackQuery.getData().substring(CallbackPrefixEnum.LENGTH);
        User client = userService.getUser(clientId);
        Long currentUserId = callbackQuery.getMessage().getChatId();
        User currentUser = userService.getUser(currentUserId);
        UserStateEnum state = UserStateEnum.getById(currentUser.getContext().getState());

        switch (state) {
            case ADD_CLIENT_TO_GROUP_STEP_3:
                userService.addToGroup(currentUserId, clientId, groupService.getGroup(currentUser.getContext().getTargetUserGroup()));
                break;

            case DELETE_CLIENT_FROM_GROUP_STEP_2:
                userService.deleteFromGroup(currentUserId, clientId, currentUser.getContext().getTargetUserGroup());
                break;

            case GET_CLIENT_STEP_2:
            case CHANGE_CLIENT_STEP_2:
            case SEND_MESSAGE_TO_CLIENT_STEP_2:
            case DELETE_CLIENT_STEP_2:
                userContextService.setTargetUserId(currentUserId, parseLong(clientId));
                sendMessageService.sendMessage(currentUserId, client.toLongString());
                break;
        }
        userContextService.setState(currentUserId, commandStateService.getNextState(state));
    }

}
