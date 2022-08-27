package las.bot.tennis.service;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum BotCommandsEnum {
    GO_TO_MAIN_MENU("Главное меню"),
    WORK_WITH_CLIENTS("Клиенты"),
    GET_CLIENT("Найти клиента"),

    WRONG_COMMAND("Неизвестная команда \uD83E\uDD14");

    private final String command;

    BotCommandsEnum(String command) {
        this.command = command;
    }

    public static BotCommandsEnum getByCommand(String command) {
        return Arrays.stream(values())
                .filter(it -> it.getCommand().equals(command))
                .findAny()
                .orElse(WRONG_COMMAND);
    }

}
