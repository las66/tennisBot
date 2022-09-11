package las.bot.tennis.service.helper;

import las.bot.tennis.model.*;
import las.bot.tennis.service.bot.BotCommandsEnum;
import las.bot.tennis.service.bot.CommandStateService;
import las.bot.tennis.service.bot.UserStateEnum;
import las.bot.tennis.service.database.GroupService;
import las.bot.tennis.service.database.PollService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;
import static las.bot.tennis.service.bot.BotCommandsEnum.CLOSE_POLL;
import static las.bot.tennis.service.bot.UserStateEnum.MAIN_MENU;
import static las.bot.tennis.service.bot.query.callback.CallbackPrefixEnum.*;

@Component
public class KeyboardGenerator {

    private final GroupService groupService;
    private final PollService pollService;
    private final CommandStateService commandStateService;

    public KeyboardGenerator(@Lazy GroupService groupService, @Lazy PollService pollService, CommandStateService commandStateService) {
        this.groupService = groupService;
        this.pollService = pollService;
        this.commandStateService = commandStateService;
    }

    public InlineKeyboardMarkup getInlineKeyboard(List<List<BotCommandsEnum>> keyboardSkeleton, boolean isMainMenu) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (List<BotCommandsEnum> rowSkeleton : keyboardSkeleton) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (BotCommandsEnum command : rowSkeleton) {
                InlineKeyboardButton button = new InlineKeyboardButton(command.getCommand());
                button.setCallbackData(MENU.name() + command.name());
                row.add(button);
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
            InlineKeyboardButton userButton = new InlineKeyboardButton(user.toOneLineString());
            userButton.setCallbackData(USER.name() + user.getChatId().toString());
            keyboard.add(singletonList(userButton));
        }

        return new InlineKeyboardMarkupWithMenuButton(keyboard);
    }

    public InlineKeyboardMarkup inlineGroupKeyboard(List<Group> groups) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (Group group : groups) {
            InlineKeyboardButton groupButton = new InlineKeyboardButton(group.getName());
            groupButton.setCallbackData(GRUP.name() + group.getName());
            keyboard.add(singletonList(groupButton));
        }

        return new InlineKeyboardMarkupWithMenuButton(keyboard);
    }

    public InlineKeyboardMarkup getKeyboardByState(User user) {
        UserStateEnum userStateEnum = UserStateEnum.getById(user.getContext().getState());
        List<List<BotCommandsEnum>> keyboardSkeleton = commandStateService.getKeyboardSkeleton(userStateEnum);
        if (keyboardSkeleton.size() != 0) {
            return getInlineKeyboard(keyboardSkeleton, userStateEnum == MAIN_MENU);
        }

        switch (userStateEnum) {
            case ADD_CLIENT_TO_GROUP_STEP_1:
            case SEND_MESSAGE_TO_GROUP_MENU:
            case DELETE_GROUP_STEP_1:
            case RENAME_GROUP_STEP_1:
            case LIST_GROUP_STEP_1:
            case DELETE_CLIENT_FROM_GROUP_STEP_1:
            case SEND_POLL_STEP_1:
                return inlineGroupKeyboard(groupService.getAll());
            case DELETE_CLIENT_FROM_GROUP_STEP_2:
                return inlineUserKeyboard(groupService.getGroup(user.getContext().getTargetUserGroup()).getUsers());
            case GET_ACTIVE_POLL_RESULT:
                return pollListKeyboard(pollService.getAllActive());
            default:
                return new InlineKeyboardMarkupWithMenuButton(new ArrayList<>());
        }
    }

    private InlineKeyboardMarkup pollListKeyboard(List<Poll> polls) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (Poll poll : polls) {
            InlineKeyboardButton pollButton = new InlineKeyboardButton("(" + poll.getForGroup() + ") " + poll.getPollText());
            pollButton.setCallbackData(POLL.name() + poll.getId().toString());
            keyboard.add(singletonList(pollButton));
        }

        return new InlineKeyboardMarkupWithMenuButton(keyboard);
    }

    public InlineKeyboardMarkup pullKeyboard(Poll poll) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (PollAnswer answer : poll.getAnswers()) {
            InlineKeyboardButton answerButton = new InlineKeyboardButton(answer.getAnswerText());
            answerButton.setCallbackData(ANSW.name() + answer.getId());
            keyboard.add(singletonList(answerButton));
        }

        return new InlineKeyboardMarkup(keyboard);
    }

    public InlineKeyboardMarkup closePollKeyboard(String pollId) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        InlineKeyboardButton closeButton = new InlineKeyboardButton(CLOSE_POLL.getCommand());
        closeButton.setCallbackData(CLSP.name() + pollId);
        keyboard.add(singletonList(closeButton));

        return new InlineKeyboardMarkup(keyboard);
    }

}
