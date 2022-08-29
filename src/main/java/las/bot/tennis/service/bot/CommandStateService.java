package las.bot.tennis.service.bot;

import las.bot.tennis.model.User;
import las.bot.tennis.service.helper.PermissionHandler;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.EMPTY_LIST;
import static las.bot.tennis.service.bot.BotCommandsEnum.*;
import static las.bot.tennis.service.bot.UserStateEnum.*;

@Service
public class CommandStateService {

    public UserStateEnum getNextState(BotCommandsEnum command) {
        switch (command) {
            case START:
            case GO_TO_MAIN_MENU:
                return MAIN_MENU;
            case WORK_WITH_CLIENTS:
                return CLIENTS_WORK_MENU;
            case WORK_WITH_GROUPS:
                return GROUPS_WORK_MENU;
            case GET_CLIENT:
                return GET_CLIENT_STEP_1;
            case NEW_GROUP:
                return NEW_GROUP_STEP_1;
            case ADD_CLIENT_TO_GROUP:
                return ADD_CLIENT_TO_GROUP_STEP_1;
        }
        return null;
    }

    public List<List<BotCommandsEnum>> getKeyboardSkeleton(UserStateEnum state) {
        switch (state) {
            case MAIN_MENU:
                return (asList(
                        asList(WORK_WITH_CLIENTS),
                        asList(WORK_WITH_GROUPS)
                ));
            case CLIENTS_WORK_MENU:
            case CLIENTS_NOT_FOUND:
                return (asList(
                        asList(GET_CLIENT),
                        asList(ADD_CLIENT_TO_GROUP),
                        asList(GO_TO_MAIN_MENU)
                ));
            case CLIENT_WORK_MENU:
                return (asList(
                        asList(GO_TO_MAIN_MENU)
                ));
            case GROUPS_WORK_MENU:
            case GROUP_ALREADY_EXISTS:
                return (asList(
                        asList(NEW_GROUP),
                        asList(ADD_CLIENT_TO_GROUP),
                        asList(GO_TO_MAIN_MENU)
                ));
            case CLIENT_ADDED_TO_GROUP:
                return (asList(
                        asList(GO_TO_MAIN_MENU)
                ));
            default:
                return EMPTY_LIST;
        }
    }

    public BotCommandsEnum getCommand(String command, User user) {
        List<BotCommandsEnum> commands = getKeyboardSkeleton(UserStateEnum.getById(user.getState())).stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
        commands.add(GO_TO_MAIN_MENU);
        return Arrays.stream(BotCommandsEnum.values())
                .filter(it -> it.getCommand().equals(command))
                .filter(commands::contains)
                .filter(it -> PermissionHandler.hasPermission(it, user.getGroups()))
                .findAny()
                .orElse(WRONG_COMMAND);
    }

}
