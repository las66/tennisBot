package las.bot.tennis.service.bot.query.callback;

public enum CallbackPrefixEnum {
    ANSW("Ответ на опрос"),
    CLSP("Закрыть опрос"),
    GRUP("Выбор группы"),
    MENU("Меню"),
    POLL("Выбор опроса"),
    USER("Выбор клиента"),
    ;

    public static final int LENGTH = 4;

    CallbackPrefixEnum(String description) {
    }

}
