package las.bot.tennis.service;

import las.bot.tennis.helper.KeyboardGenerator;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.Arrays;

import static java.util.Arrays.asList;
import static las.bot.tennis.service.BotCommandsEnum.*;

@Getter
public enum UserStateEnum {
    MAIN_MENU(0,
            "Главное меню",
            "Что надо сделать?",
            keyboard_0()),
    CLIENTS_WORK_MENU(1,
            "Меню по работе с клиентами",
            "Что сделать с клиентом?",
            keyboard_1()),
    GET_CLIENT_STEP_1(2,
            "Поиск клиента",
            "Введите имя/фамилию/телефон/инфо клиента",
            null),
    GET_CLIENT_STEP_2(3,
            "Выбор клиента из найденных",
            "Выберете клиента:",
            null),
    CLIENT_WORK_MENU(4,
            "Меню по работе с конкретным клиентом",
            "Что делать с клиентом?",
            keyboard_4()),
    CLIENTS_NOT_FOUND(5,
            "Не нашли ни одного клиента по запросу",
            "Клиенты отутствуют",
            keyboard_5()),
    ;

    private final int stateId;
    private final String description;
    private final String message;
    private final ReplyKeyboard keyboard;


    UserStateEnum(int stateId, String description, String message, ReplyKeyboard keyboard) {
        this.stateId = stateId;
        this.description = description;
        this.message = message;
        this.keyboard = keyboard;
    }

    public static UserStateEnum getById(int stateId) {
        return Arrays.stream(values()).filter(it -> it.getStateId() == stateId).findAny().orElse(null);
    }

    private static ReplyKeyboardMarkup keyboard_0() {
        return KeyboardGenerator.replyKeyboard(asList(
                asList(WORK_WITH_CLIENTS.getCommand())
        ));
    }

    private static ReplyKeyboardMarkup keyboard_1() {
        return KeyboardGenerator.replyKeyboard(asList(
                asList(GET_CLIENT.getCommand()),
                asList(GO_TO_MAIN_MENU.getCommand())
        ));
    }

    private static ReplyKeyboardMarkup keyboard_4() {
        return KeyboardGenerator.replyKeyboard(asList(
                asList(GO_TO_MAIN_MENU.getCommand())
        ));
    }

    private static ReplyKeyboardMarkup keyboard_5() {
        return KeyboardGenerator.replyKeyboard(asList(
                asList(GET_CLIENT.getCommand()),
                asList(GO_TO_MAIN_MENU.getCommand())
        ));
    }

}
