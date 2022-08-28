package las.bot.tennis.service.bot;

import las.bot.tennis.model.Group;
import las.bot.tennis.service.helper.PermissionHandler;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.EMPTY_LIST;
import static las.bot.tennis.service.bot.UserStateEnum.*;
import static las.bot.tennis.service.database.GroupService.ADMIN_GROUP;
import static las.bot.tennis.service.database.GroupService.ALL_CLIENTS_GROUP;

@Getter
public enum BotCommandsEnum {
    START("/start", MAIN_MENU, asList(ALL_CLIENTS_GROUP)),
    GO_TO_MAIN_MENU("Главное меню", MAIN_MENU, asList(ALL_CLIENTS_GROUP)),
    WORK_WITH_CLIENTS("Клиенты", CLIENTS_WORK_MENU, asList(ADMIN_GROUP)),
    WORK_WITH_GROUPS("Группы", GROUPS_WORK_MENU, asList(ADMIN_GROUP)),
    GET_CLIENT("Найти клиента", GET_CLIENT_STEP_1, asList(ADMIN_GROUP)),
    NEW_GROUP("Новая группа", NEW_GROUP_STEP_1, asList(ADMIN_GROUP)),
    ADD_CLIENT_TO_GROUP("Добавить клиента в группу", ADD_CLIENT_TO_GROUP_STEP_1, asList(ADMIN_GROUP)),

    WRONG_COMMAND("Неизвестная команда \uD83E\uDD14", null, EMPTY_LIST);

    private final String command;
    private final UserStateEnum nextState;
    private final List<String> allowedGroups;

    BotCommandsEnum(String command, UserStateEnum nextState, List<String> allowedGroups) {
        this.command = command;
        this.nextState = nextState;
        this.allowedGroups = allowedGroups;
    }

    public static BotCommandsEnum getByCommand(String command, Set<Group> groups) {
        return Arrays.stream(values())
                .filter(it -> it.getCommand().equals(command))
                .filter(it -> PermissionHandler.hasPermission(it, groups))
                .findAny()
                .orElse(WRONG_COMMAND);
    }

}
