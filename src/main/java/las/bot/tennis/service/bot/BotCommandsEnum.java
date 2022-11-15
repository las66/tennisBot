package las.bot.tennis.service.bot;

import lombok.Getter;

@Getter
public enum BotCommandsEnum {
    START("/start"),
    GO_TO_MAIN_MENU("⬅️ Главное меню"),
    WORK_WITH_CLIENTS("Клиенты"),
    WORK_WITH_GROUPS("Группы"),
    GET_CLIENT("Инфо"),
    NEW_GROUP("Новая"),
    RENAME_GROUP("Переименовать"),
    DELETE_GROUP("Удалить"),
    LIST_GROUP("Список"),
    ADD_CLIENT_TO_GROUP("Добавить клиента"),
    ADD_CLIENT_TO_GROUP_2("Добавить в группу"),
    DELETE_CLIENT_FROM_GROUP("Убрать клиента"),
    DELETE_CLIENT_FROM_GROUP_2("Убрать из группы"),
    NEW_CLIENTS("Новые клиенты"),
    SEND_MESSAGE("Отправить сообщение"),
    SEND_MESSAGE_TO_GROUP("Группе"),
    SEND_MESSAGE_TO_CLIENT("Клиенту"),
    CHANGE_CLIENT_NAME("ФИО"),
    CHANGE_CLIENT_PHONE("Телефон"),
    CHANGE_CLIENT_DESCRIPTION("Заметка"),
    DELETE_CLIENT("Удалить"),
    DELETE_CLIENT_YES("Да, удалить"),
    DELETE_CLIENT_NO("НЕТ"),
    CONFIRM_CLIENT("Утвердить"),
    POLL_MENU("Опросы"),
    NEW_POLL("Новый"),
    ACTIVE_POLLS("Активные"),
    NEXT_MONTH_POLL("Опрос на следующий месяц"),
    CLOSE_POLL("Закрыть опрос"),

    WRONG_COMMAND("Неизвестная команда \uD83E\uDD37\u200D♀️");

    private final String command;

    BotCommandsEnum(String command) {
        this.command = command;
    }

}
