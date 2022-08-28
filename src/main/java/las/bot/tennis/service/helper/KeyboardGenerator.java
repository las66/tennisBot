package las.bot.tennis.service.helper;

import las.bot.tennis.model.Group;
import las.bot.tennis.model.User;
import las.bot.tennis.service.bot.BotCommandsEnum;
import las.bot.tennis.service.bot.UserStateEnum;
import las.bot.tennis.service.database.GroupService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static las.bot.tennis.service.bot.BotCommandsEnum.*;
import static las.bot.tennis.service.helper.PermissionHandler.hasPermission;

@Component
public class KeyboardGenerator {

    private final GroupService groupService;

    public KeyboardGenerator(@Lazy GroupService groupService) {
        this.groupService = groupService;
    }

    public ReplyKeyboardMarkup replyKeyboard(List<List<BotCommandsEnum>> buttons, Set<Group> groups) {
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        for (List<BotCommandsEnum> button : buttons) {
            KeyboardRow row = new KeyboardRow();
            for (BotCommandsEnum command : button) {
                if (hasPermission(command, groups)) {
                    row.add(command.getCommand());
                }
            }
            if (!row.isEmpty()) {
                keyboardRows.add(row);
            }
        }

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(keyboardRows);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
    }

    public InlineKeyboardMarkup inlineUserKeyboard(List<User> users) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (User user : users) {
            InlineKeyboardButton userButton = new InlineKeyboardButton(user.getName() + " " + user.getPhone() + " " + user.getDescription());
            userButton.setCallbackData(user.getChatId().toString());
            keyboard.add(singletonList(userButton));
        }

        return new InlineKeyboardMarkup(keyboard);
    }

    public InlineKeyboardMarkup inlineGroupKeyboard(List<Group> groups) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (Group group : groups) {
            InlineKeyboardButton groupButton = new InlineKeyboardButton(group.getName());
            groupButton.setCallbackData(group.getName());
            keyboard.add(singletonList(groupButton));
        }

        return new InlineKeyboardMarkup(keyboard);
    }


    public ReplyKeyboard getKeyboardByState(UserStateEnum userStateEnum, Set<Group> groups) {
        switch (userStateEnum) {
            case MAIN_MENU:
                return replyKeyboard(asList(
                        asList(WORK_WITH_CLIENTS),
                        asList(WORK_WITH_GROUPS)
                ), groups);
            case CLIENTS_WORK_MENU:
            case CLIENTS_NOT_FOUND:
                return replyKeyboard(asList(
                        asList(GET_CLIENT),
                        asList(ADD_CLIENT_TO_GROUP),
                        asList(GO_TO_MAIN_MENU)
                ), groups);
            case CLIENT_WORK_MENU:
                return replyKeyboard(asList(
                        asList(GO_TO_MAIN_MENU)
                ), groups);
            case GROUPS_WORK_MENU:
            case GROUP_ALREADY_EXISTS:
                return replyKeyboard(asList(
                        asList(NEW_GROUP),
                        asList(ADD_CLIENT_TO_GROUP),
                        asList(GO_TO_MAIN_MENU)
                ), groups);
            case ADD_CLIENT_TO_GROUP_STEP_1:
                return inlineGroupKeyboard(groupService.getAll());
            case CLIENT_ADDED_TO_GROUP:
                return replyKeyboard(asList(
                        asList(GO_TO_MAIN_MENU)
                ), groups);
            default:
                return null;
        }
    }
}
