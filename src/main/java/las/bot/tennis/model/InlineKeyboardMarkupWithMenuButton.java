package las.bot.tennis.model;

import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static las.bot.tennis.service.bot.BotCommandsEnum.GO_TO_MAIN_MENU;
import static las.bot.tennis.service.bot.query.callback.CallbackPrefixEnum.MENU;

@NoArgsConstructor
public class InlineKeyboardMarkupWithMenuButton extends InlineKeyboardMarkup {

    public InlineKeyboardMarkupWithMenuButton(List<List<InlineKeyboardButton>> keyboard) {
        List<InlineKeyboardButton> menuRow = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton(GO_TO_MAIN_MENU.getCommand());
        button.setCallbackData(MENU.name() + GO_TO_MAIN_MENU.name());
        menuRow.add(button);
        keyboard.add(menuRow);
        this.setKeyboard(keyboard);
    }

}
