package las.bot.tennis.service.helper;

import las.bot.tennis.model.Group;
import las.bot.tennis.service.bot.BotCommandsEnum;

import java.util.Set;

import static las.bot.tennis.service.database.GroupService.ALL_CLIENTS_GROUP;

public class PermissionHandler {

    public static boolean hasPermission(BotCommandsEnum command, Set<Group> groups) {
        if (command.getAllowedGroups().contains(ALL_CLIENTS_GROUP)) {
            return true;
        }
        for (Group group : groups) {
            if (command.getAllowedGroups().contains(group.getName())) {
                return true;
            }
        }
        return false;
    }

}
