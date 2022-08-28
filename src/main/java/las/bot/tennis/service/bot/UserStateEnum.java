package las.bot.tennis.service.bot;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum UserStateEnum {
    MAIN_MENU(0, "Главное меню", "Что надо сделать?"),
    CLIENTS_WORK_MENU(1, "Меню по работе с клиентами", "Что делать с клиентами?"),
    GET_CLIENT_STEP_1(2, "Поиск клиента", "Введите имя/фамилию/телефон/инфо клиента"),
    GET_CLIENT_STEP_2(3, "Выбор клиента из найденных", "Выберете клиента:"),
    CLIENT_WORK_MENU(4, "Меню по работе с конкретным клиентом", "Что делать с клиентом?"),
    CLIENTS_NOT_FOUND(5, "Не нашли ни одного клиента по запросу", "Клиенты отутствуют"),
    GROUPS_WORK_MENU(6, "Меню работы с группами", "Что делать с группами?"),
    NEW_GROUP_STEP_1(7, "Создаем новую группу", "Введите название группы"),
    GROUP_ALREADY_EXISTS(8, "Уже есть такая группа", "Группа уже существует"),
    ADD_CLIENT_TO_GROUP_STEP_1(9, "Выбор группы для добавления клиента", "Выберете группу или введите название для фильтрации"),
    ADD_CLIENT_TO_GROUP_STEP_2(10, "Ввод фильтра клиента для добавления в группу", "Введите имя/фамилию/телефон/инфо клиента"),
    ADD_CLIENT_TO_GROUP_STEP_3(11, "Выбор клиента для добавления в группу", "Выберете клиента:"),
    CLIENT_ADDED_TO_GROUP(12, "Клиент успешно добавлен в группу", "Что делаем дальше?"),
    ;

    private final int stateId;
    private final String description;
    private final String message;


    UserStateEnum(int stateId, String description, String message) {
        this.stateId = stateId;
        this.description = description;
        this.message = message;
    }

    public static UserStateEnum getById(int stateId) {
        return Arrays.stream(values()).filter(it -> it.getStateId() == stateId).findAny().orElse(null);
    }

}
