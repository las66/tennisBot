package las.bot.tennis.service;

import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static las.bot.tennis.service.BotCommandsEnum.GO_TO_MAIN_MENU;
import static las.bot.tennis.service.BotCommandsEnum.WORK_WITH_CLIENTS;

@Getter
public enum UserStateEnum {
    MAIN_MENU(0,
            "Главное меню",
            "Что надо сделать?",
            keyboard_0()),
    CLIENT_WORK_MENU(1,
            "Меню по работе с клиентами",
            "Что сделать с клиентом?",
            keyboard_1());

    private final int stateId;
    private final String description;
    private final String message;
    private final ReplyKeyboardMarkup keyboard;


    UserStateEnum(int stateId, String description, String message, ReplyKeyboardMarkup keyboard) {
        this.stateId = stateId;
        this.description = description;
        this.message = message;
        this.keyboard = keyboard;
    }

    public static UserStateEnum getById(int stateId) {
        return Arrays.stream(values()).filter(it -> it.getStateId() == stateId).findAny().get();
    }

    private static ReplyKeyboardMarkup keyboard_0() {
        KeyboardRow row = new KeyboardRow();
        row.add(WORK_WITH_CLIENTS.getCommand());

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        keyboardRows.add(row);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setKeyboard(keyboardRows);

        return keyboardMarkup;
    }

    private static ReplyKeyboardMarkup keyboard_1() {
        KeyboardRow row = new KeyboardRow();
        row.add(GO_TO_MAIN_MENU.getCommand());

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        keyboardRows.add(row);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setKeyboard(keyboardRows);

        return keyboardMarkup;
    }

}
