package las.bot.tennis.service.helper;

public enum Month {

    Январь("январе"),
    Февраль("феврале"),
    Март("марте"),
    Апрель("апреле"),
    Май("мае"),
    Июнь("июне"),
    Июль("июле"),
    Август("августе"),
    Сентябрь("сентябре"),
    Октябрь("октябре"),
    Ноябрь("ноябре"),
    Декабрь("декабре");

    private final String accusative;

    Month(String accusative) {
        this.accusative = accusative;
    }

    public String getAccusative() {
        return accusative;
    }

}
