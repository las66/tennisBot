package las.bot.tennis.service.bot;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum UserStateEnum {
    MAIN_MENU(0, "Главное меню", "Что надо сделать?"),
    CLIENTS_WORK_MENU(1, "Меню по работе с клиентами", "Что делать с клиентами?"),
    GET_CLIENT_STEP_1(2, "Поиск клиента", "Введите имя/фамилию/телефон/инфо клиента"),
    GET_CLIENT_STEP_2(3, "Выбор клиента из найденных", "Выберете клиента:"),
    GROUPS_WORK_MENU(6, "Меню работы с группами", "Что делать с группами?"),
    NEW_GROUP_STEP_1(7, "Создаем новую группу", "Введите название группы"),
    GROUP_ALREADY_EXISTS(8, "Уже есть такая группа", "Группа уже существует"),
    ADD_CLIENT_TO_GROUP_STEP_1(9, "Выбор группы для добавления клиента", "Выберете группу или введите название для фильтрации"),
    ADD_CLIENT_TO_GROUP_STEP_2(10, "Ввод фильтра клиента для добавления в группу", "Введите имя/фамилию/телефон/инфо клиента"),
    ADD_CLIENT_TO_GROUP_STEP_3(11, "Выбор клиента для добавления в группу", "Выберете клиента:"),
    CLIENT_ADDED_TO_GROUP(12, "После попытки добавить клиента в группу", "Что делаем дальше?"),
    SEND_MESSAGE_MENU(13, "Выбор кому отправить сообщение группа/клиент", "Кому отправляем?"),
    SEND_MESSAGE_TO_CLIENT_MENU(14, "Выбираем клиента для сообщения", "Введите имя/фамилию/телефон/инфо клиента"),
    SEND_MESSAGE_TO_GROUP_MENU(15, "Выбираем группу для сообщения", "Выберете группу или введите название для фильтрации"),
    MESSAGE_FOR_GROUP(16, "Пишем сообщение для отправку в группу", "Введите сообщение для группы:"),
    DELETE_GROUP_STEP_1(17, "Выбираем группу для удаления", "Выберете группу или введите название для фильтрации"),
    RENAME_GROUP_STEP_1(18, "Выбираем группу для переименования", "Выберете группу или введите название для фильтрации"),
    RENAME_GROUP_STEP_2(19, "Вводим новое название для группы", "Введите новое название:"),
    LIST_GROUP_STEP_1(20, "Выбираем группу для просмотра", "Выберете группу или введите название для фильтрации"),
    DELETE_CLIENT_FROM_GROUP_STEP_1(21, "Выбор группы для удаления клиента", "Выберете группу или введите название для фильтрации"),
    DELETE_CLIENT_FROM_GROUP_STEP_2(22, "Выбор клиента, которого из группы удаляем", "Выберете клиента:"),
    CHANGE_CLIENT_STEP_1(23, "Ввод фильтра для поиска клиента для изменения", "Введите имя/фамилию/телефон/инфо клиента"),
    CHANGE_CLIENT_STEP_2(24, "Выбор клиента для изменения", "Выберете клиента:"),
    CHANGE_CLIENT_STEP_3(25, "Выбор параметра, который будем менять у клиента", "Что меняем?"),
    CHANGE_CLIENT_STEP_3_1(26, "Ввод нового имени клиента", "Введите ФИО"),
    CHANGE_CLIENT_STEP_3_2(27, "Ввод нового телефона клиента", "Введите номер телефона"),
    CHANGE_CLIENT_STEP_3_3(28, "Ввод нового описания клиента", "Введите заметку"),
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
