package las.bot.tennis.service.helper;

import las.bot.tennis.model.Group;
import las.bot.tennis.service.bot.BotCommandsEnum;

import java.util.List;
import java.util.Set;

import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.singletonList;
import static las.bot.tennis.service.database.GroupService.ADMIN_GROUP;
import static las.bot.tennis.service.database.GroupService.ALL_CLIENTS_GROUP;

public class PermissionHandler {

    public static boolean hasPermission(BotCommandsEnum command, Set<Group> groups) {
        if (getAllowedGroups(command).contains(ALL_CLIENTS_GROUP)) {
            return true;
        }
        for (Group group : groups) {
            if (getAllowedGroups(command).contains(group.getName())) {
                return true;
            }
        }
        return false;
    }

    public static List<String> getAllowedGroups(BotCommandsEnum command) {
        switch (command) {
            case START:
            case GO_TO_MAIN_MENU:
                return singletonList(ALL_CLIENTS_GROUP);

            case WORK_WITH_CLIENTS:
            case WORK_WITH_GROUPS:
            case GET_CLIENT:
            case NEW_GROUP:
            case RENAME_GROUP:
            case DELETE_GROUP:
            case LIST_GROUP:
            case ADD_CLIENT_TO_GROUP:
            case ADD_CLIENT_TO_GROUP_2:
            case DELETE_CLIENT_FROM_GROUP:
            case DELETE_CLIENT_FROM_GROUP_2:
            case SEND_MESSAGE:
            case SEND_MESSAGE_TO_GROUP:
            case SEND_MESSAGE_TO_CLIENT:
            case CHANGE_CLIENT:
            case CHANGE_CLIENT_NAME:
            case CHANGE_CLIENT_PHONE:
            case CHANGE_CLIENT_DESCRIPTION:
            case DELETE_CLIENT:
            case DELETE_CLIENT_YES:
            case DELETE_CLIENT_NO:
                return singletonList(ADMIN_GROUP);

            case WRONG_COMMAND:
            default:
                return EMPTY_LIST;
        }
    }

}
