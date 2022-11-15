package las.bot.tennis.service.bot.query.callback;

import las.bot.tennis.model.User;
import las.bot.tennis.service.bot.CommandStateService;
import las.bot.tennis.service.bot.UserStateEnum;
import las.bot.tennis.service.database.GroupService;
import las.bot.tennis.service.database.UserContextService;
import las.bot.tennis.service.database.UserService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Service
public class UserCallback {

    private final UserService userService;
    private final UserContextService userContextService;
    private final CommandStateService commandStateService;
    private final GroupService groupService;

    public UserCallback(UserService userService,
                        UserContextService userContextService,
                        CommandStateService commandStateService,
                        GroupService groupService) {
        this.userService = userService;
        this.userContextService = userContextService;
        this.commandStateService = commandStateService;
        this.groupService = groupService;
    }

    public void process(CallbackQuery callbackQuery) {
        Long targetClientId = Long.parseLong(callbackQuery.getData().substring(CallbackPrefixEnum.LENGTH));
        Long currentUserId = callbackQuery.getMessage().getChatId();
        User currentUser = userService.getUser(currentUserId);
        userContextService.setTargetUserId(currentUserId, targetClientId);
        UserStateEnum state = UserStateEnum.getById(currentUser.getContext().getState());

        switch (state) {
            case ADD_CLIENT_TO_GROUP_STEP_3:
                userService.addToGroup(currentUserId, targetClientId, groupService.getGroup(currentUser.getContext().getTargetUserGroup()));
                break;

            case DELETE_CLIENT_FROM_GROUP_STEP_2:
                userService.deleteFromGroup(targetClientId, groupService.getGroup(currentUser.getContext().getTargetUserGroup()));
                break;
        }
        userContextService.setState(currentUserId, commandStateService.getNextState(state));
    }

}
