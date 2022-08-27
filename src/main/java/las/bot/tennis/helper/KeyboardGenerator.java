package las.bot.tennis.helper;

import las.bot.tennis.model.User;
import las.bot.tennis.service.UserStateEnum;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static las.bot.tennis.service.BotCommandsEnum.*;

public class KeyboardGenerator {

    public static ReplyKeyboardMarkup replyKeyboard(List<List<String>> buttons) {
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        for (List<String> button : buttons) {
            KeyboardRow row = new KeyboardRow();
            for (String s : button) {
                row.add(s);
            }
            keyboardRows.add(row);
        }

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(keyboardRows);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
    }

    public static InlineKeyboardMarkup inlineKeyboard(List<User> users) {
        List<List<InlineKeyboardButton>> keyb = new ArrayList<>();

        for (User user : users) {
            InlineKeyboardButton userButton = new InlineKeyboardButton(user.getName() + " " + user.getPhone() + " " + user.getDescription());
            userButton.setCallbackData(user.getChatId().toString());
            keyb.add(singletonList(userButton));
        }

        return new InlineKeyboardMarkup(keyb);
    }


    public static ReplyKeyboard getKeyboardByState(UserStateEnum userStateEnum) {
        switch (userStateEnum) {
            case MAIN_MENU:
                return replyKeyboard(asList(
                        asList(WORK_WITH_CLIENTS.getCommand()),
                        asList(WORK_WITH_GROUPS.getCommand())
                ));
            case CLIENTS_WORK_MENU:
            case CLIENTS_NOT_FOUND:
                return replyKeyboard(asList(
                        asList(GET_CLIENT.getCommand()),
                        asList(GO_TO_MAIN_MENU.getCommand())
                ));
            case CLIENT_WORK_MENU:
                return replyKeyboard(asList(
                        asList(GO_TO_MAIN_MENU.getCommand())
                ));
            case GROUPS_WORK_MENU:
            case GROUP_ALREADY_EXISTS:
                return replyKeyboard(asList(
                        asList(NEW_GROUP.getCommand()),
                        asList(GO_TO_MAIN_MENU.getCommand())
                ));
            default:
                return null;
        }
    }
}
