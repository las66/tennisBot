package las.bot.tennis.helper;

import las.bot.tennis.model.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;

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

}
