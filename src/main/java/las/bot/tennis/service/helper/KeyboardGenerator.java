package las.bot.tennis.service.helper;

import las.bot.tennis.model.Group;
import las.bot.tennis.model.InlineKeyboardMarkupWithMenuButton;
import las.bot.tennis.model.User;
import las.bot.tennis.service.bot.BotCommandsEnum;
import las.bot.tennis.service.bot.CommandStateService;
import las.bot.tennis.service.bot.UserStateEnum;
import las.bot.tennis.service.database.GroupService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.singletonList;
import static las.bot.tennis.service.bot.UserStateEnum.MAIN_MENU;
import static las.bot.tennis.service.helper.PermissionHandler.hasPermission;

@Component
public class KeyboardGenerator {

    private final GroupService groupService;
    private final CommandStateService commandStateService;

    public KeyboardGenerator(@Lazy GroupService groupService, CommandStateService commandStateService) {
        this.groupService = groupService;
        this.commandStateService = commandStateService;
    }

    public InlineKeyboardMarkup getInlineKeyboard(List<List<BotCommandsEnum>> keyboardSkeleton, Set<Group> groups, boolean isMainMenu) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (List<BotCommandsEnum> rowSkeleton : keyboardSkeleton) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (BotCommandsEnum command : rowSkeleton) {
                if (hasPermission(command, groups)) {
                    InlineKeyboardButton button = new InlineKeyboardButton(command.getCommand());
                    button.setCallbackData(command.name());
                    row.add(button);
                }
            }
            if (!row.isEmpty()) {
                keyboard.add(row);
            }
        }

        if (isMainMenu) {
            return new InlineKeyboardMarkup(keyboard);
        } else {
            return new InlineKeyboardMarkupWithMenuButton(keyboard);
        }
    }

    public InlineKeyboardMarkup inlineUserKeyboard(List<User> users) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (User user : users) {
            InlineKeyboardButton userButton = new InlineKeyboardButton(user.getName() + " " + user.getPhone() + " " + user.getDescription());
            userButton.setCallbackData(user.getChatId().toString());
            keyboard.add(singletonList(userButton));
        }

        return new InlineKeyboardMarkupWithMenuButton(keyboard);
    }

    public InlineKeyboardMarkup inlineGroupKeyboard(List<Group> groups) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (Group group : groups) {
            InlineKeyboardButton groupButton = new InlineKeyboardButton(group.getName());
            groupButton.setCallbackData(group.getName());
            keyboard.add(singletonList(groupButton));
        }

        return new InlineKeyboardMarkupWithMenuButton(keyboard);
    }


    public InlineKeyboardMarkup getKeyboardByState(UserStateEnum userStateEnum, Set<Group> groups) {
        List<List<BotCommandsEnum>> keyboardSkeleton = commandStateService.getKeyboardSkeleton(userStateEnum);
        if (keyboardSkeleton.size() != 0) {
            return getInlineKeyboard(keyboardSkeleton, groups, userStateEnum == MAIN_MENU);
        }

        switch (userStateEnum) {
            case ADD_CLIENT_TO_GROUP_STEP_1:
            case SEND_MESSAGE_TO_GROUP_MENU:
            case DELETE_GROUP_STEP_1:
            case RENAME_GROUP_STEP_1:
            case LIST_GROUP_STEP_1:
                return inlineGroupKeyboard(groupService.getAll());

            default:
                return new InlineKeyboardMarkupWithMenuButton(new ArrayList<>());
        }
    }

}
