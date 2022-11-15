package las.bot.tennis.service.bot.query.callback;

import las.bot.tennis.model.Group;
import las.bot.tennis.model.User;
import las.bot.tennis.service.bot.CommandStateService;
import las.bot.tennis.service.bot.SendMessageService;
import las.bot.tennis.service.bot.UserStateEnum;
import las.bot.tennis.service.database.GroupService;
import las.bot.tennis.service.database.PollService;
import las.bot.tennis.service.database.UserContextService;
import las.bot.tennis.service.database.UserService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Service
public class GroupCallback {

    private final UserService userService;
    private final GroupService groupService;
    private final UserContextService userContextService;
    private final SendMessageService sendMessageService;
    private final CommandStateService commandStateService;
    private final PollService pollService;

    public GroupCallback(UserService userService,
                         GroupService groupService,
                         UserContextService userContextService,
                         SendMessageService sendMessageService,
                         CommandStateService commandStateService,
                         PollService pollService) {
        this.userService = userService;
        this.groupService = groupService;
        this.userContextService = userContextService;
        this.sendMessageService = sendMessageService;
        this.commandStateService = commandStateService;
        this.pollService = pollService;
    }

    public void process(CallbackQuery callbackQuery) {
        String groupName = callbackQuery.getData().substring(CallbackPrefixEnum.LENGTH);
        Group group = groupService.getGroup(groupName);
        Long currentUserId = callbackQuery.getMessage().getChatId();
        User currentUser = userService.getUser(currentUserId);
        UserStateEnum state = UserStateEnum.getById(currentUser.getContext().getState());
        Long targetUserId = currentUser.getContext().getTargetUserId();

        switch (state) {
            case ADD_CLIENT_TO_GROUP_STEP_1:
                userContextService.setTargetUserGroup(currentUserId, groupName);
                userService.addToGroup(currentUserId, targetUserId, group);
                break;
            case SEND_MESSAGE_TO_GROUP_MENU:
            case RENAME_GROUP_STEP_1:
                userContextService.setTargetUserGroup(currentUserId, groupName);
                sendMessageService.sendMessage(currentUserId, group.toShortString());
                break;
            case DELETE_CLIENT_FROM_GROUP_STEP_1:
                userContextService.setTargetUserGroup(currentUserId, groupName);
                userService.deleteFromGroup(targetUserId, group);
                break;
            case LIST_GROUP_STEP_1:
                sendMessageService.sendMessage(currentUserId, group.toLongString());
                break;
            case DELETE_GROUP_STEP_1:
                groupService.deleteGroup(currentUserId, groupName);
                break;
            case SEND_POLL_STEP_1:
                pollService.createNewNextMonthPoll(currentUserId, groupName);
                break;
        }
        userContextService.setState(currentUserId, commandStateService.getNextState(state));
    }

}
