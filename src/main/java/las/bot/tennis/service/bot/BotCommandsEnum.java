package las.bot.tennis.service.bot;

import lombok.Getter;

@Getter
public enum BotCommandsEnum {
    START("/start"),
    GO_TO_MAIN_MENU("Главное меню"),
    WORK_WITH_CLIENTS("Клиенты"),
    WORK_WITH_GROUPS("Группы"),
    GET_CLIENT("Найти клиента"),
    NEW_GROUP("Новая группа"),
    ADD_CLIENT_TO_GROUP("Добавить клиента в группу"),

    WRONG_COMMAND("Неизвестная команда \uD83E\uDD14");

    private final String command;

    BotCommandsEnum(String command) {
        this.command = command;
    }

}
