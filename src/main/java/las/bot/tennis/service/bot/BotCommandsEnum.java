package las.bot.tennis.service.bot;

import lombok.Getter;

@Getter
public enum BotCommandsEnum {
    START("/start"),
    GO_TO_MAIN_MENU("⬅️ Главное меню"),
    WORK_WITH_CLIENTS("Клиенты"),
    WORK_WITH_GROUPS("Группы"),
    GET_CLIENT("Найти клиента"),
    NEW_GROUP("Новая"),
    RENAME_GROUP("Переименовать"),
    DELETE_GROUP("Удалить"),
    ADD_CLIENT_TO_GROUP("Добавить клиента в группу"),
    SEND_MESSAGE("Отправить сообщение"),
    SEND_MESSAGE_TO_GROUP("Группе"),
    SEND_MESSAGE_TO_CLIENT("Клиенту"),

    WRONG_COMMAND("Неизвестная команда \uD83E\uDD37\u200D♀️");

    private final String command;

    BotCommandsEnum(String command) {
        this.command = command;
    }

}
