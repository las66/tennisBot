package las.bot.tennis.service;

import lombok.Getter;
import org.springframework.context.annotation.Lazy;

import java.util.Arrays;

import static las.bot.tennis.service.UserStateEnum.*;

@Getter
public enum BotCommandsEnum {
    GO_TO_MAIN_MENU("Главное меню", MAIN_MENU),
    WORK_WITH_CLIENTS("Клиенты", CLIENTS_WORK_MENU),
    WORK_WITH_GROUPS("Группы", GROUPS_WORK_MENU),
    GET_CLIENT("Найти клиента", GET_CLIENT_STEP_1),
    NEW_GROUP("Новая группа", NEW_GROUP_STEP_1),

    WRONG_COMMAND("Неизвестная команда \uD83E\uDD14", null);

    private final String command;
    private final UserStateEnum nextState;

    BotCommandsEnum(String command, UserStateEnum nextState) {
        this.command = command;
        this.nextState = nextState;
    }

    public static BotCommandsEnum getByCommand(String command) {
        return Arrays.stream(values())
                .filter(it -> it.getCommand().equals(command))
                .findAny()
                .orElse(WRONG_COMMAND);
    }

}
