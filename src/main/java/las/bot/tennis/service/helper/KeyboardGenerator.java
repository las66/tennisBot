package las.bot.tennis.service.helper;

import las.bot.tennis.model.*;
import las.bot.tennis.service.bot.BotCommandsEnum;
import las.bot.tennis.service.bot.UserStateEnum;
import las.bot.tennis.service.database.GroupService;
import las.bot.tennis.service.database.PollService;
import las.bot.tennis.service.database.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static las.bot.tennis.service.bot.BotCommandsEnum.*;
import static las.bot.tennis.service.bot.UserStateEnum.MAIN_MENU;
import static las.bot.tennis.service.bot.query.callback.CallbackPrefixEnum.*;

@Component
public class KeyboardGenerator {

    private final GroupService groupService;
    private final UserService userService;
    private final PollService pollService;

    public KeyboardGenerator(@Lazy GroupService groupService,
                             @Lazy UserService userService,
                             @Lazy PollService pollService) {
        this.groupService = groupService;
        this.userService = userService;
        this.pollService = pollService;
    }

    public InlineKeyboardMarkup inlineUsersKeyboard(List<User> users) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (User user : users) {
            keyboard.add(getInlineUserRow(user));
        }

        return new InlineKeyboardMarkupWithMenuButton(keyboard);
    }

    private List<InlineKeyboardButton> getInlineUserRow(Long userId) {
        return getInlineUserRow(userService.getUser(userId));
    }

    private List<InlineKeyboardButton> getInlineUserRow(User user) {
        InlineKeyboardButton userButton = new InlineKeyboardButton(user.toOneLineString());
        userButton.setCallbackData(USER.name() + user.getChatId().toString());
        return singletonList(userButton);
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
        UserStateEnum state = UserStateEnum.getById(user.getContext().getState());
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        switch (state) {
            case MAIN_MENU:
                keyboard = asList(
                        asList(toButton(SEND_MESSAGE), toButton(POLL_MENU)),
                        asList(toButton(WORK_WITH_GROUPS), toButton(WORK_WITH_CLIENTS))
                );
                break;
            case GET_CLIENT_STEP_1:
                Long targetUserId = user.getContext().getTargetUserId();
                if (targetUserId != null) {
                    keyboard.add(getInlineUserRow(targetUserId));
                }
                int newClientAmount = userService.getNewClients().size();
                if (newClientAmount != 0) {
                    String text = NEW_CLIENTS.getCommand() + " (" + newClientAmount + ")";
                    keyboard.add(asList(toButton(NEW_CLIENTS, text)));
                }
                break;
            case GROUPS_WORK_MENU:
            case GROUP_ALREADY_EXISTS:
                keyboard = asList(
                        asList(toButton(LIST_GROUP), toButton(RENAME_GROUP)),
                        asList(toButton(NEW_GROUP), toButton(DELETE_GROUP)),
                        asList(toButton(ADD_CLIENT_TO_GROUP), toButton(DELETE_CLIENT_FROM_GROUP))
                );
                break;
            case CLIENT_ADDED_TO_GROUP:
                break;
            case SEND_MESSAGE_MENU:
                keyboard = asList(
                        asList(toButton(SEND_MESSAGE_TO_GROUP), toButton(SEND_MESSAGE_TO_CLIENT))
                );
                break;
            case DELETE_CLIENT_MENU:
                keyboard = asList(
                        asList(toButton(DELETE_CLIENT_YES), toButton(DELETE_CLIENT_NO))
                );
                break;
            case POLL_WORK_MENU:
                keyboard = asList(
                        asList(toButton(NEW_POLL), toButton(ACTIVE_POLLS))
                );
                break;
            case NEW_POLL_MENU:
                keyboard = asList(
                        asList(toButton(NEXT_MONTH_POLL))
                );
                break;
            case CLIENT_WORK_MENU:
                keyboard.add(asList(toButton(CHANGE_CLIENT_NAME), toButton(CHANGE_CLIENT_PHONE), toButton(CHANGE_CLIENT_DESCRIPTION)));
                if (userService.getUser(user.getContext().getTargetUserId()).isNewClient()) {
                    keyboard.add(asList(toButton(CONFIRM_CLIENT)));
                }
                keyboard.add(asList(toButton(ADD_CLIENT_TO_GROUP_2), toButton(DELETE_CLIENT_FROM_GROUP_2)));
                keyboard.add(asList(toButton(DELETE_CLIENT)));
                break;
            case ADD_CLIENT_TO_GROUP_STEP_1:
            case SEND_MESSAGE_TO_GROUP_MENU:
            case DELETE_GROUP_STEP_1:
            case RENAME_GROUP_STEP_1:
            case LIST_GROUP_STEP_1:
            case SEND_POLL_STEP_1:
                return inlineGroupKeyboard(groupService.getAll());
            case DELETE_CLIENT_FROM_GROUP_STEP_1:
                return inlineGroupKeyboard(new ArrayList<>(user.getGroups()));
            case DELETE_CLIENT_FROM_GROUP_STEP_2:
                return inlineUsersKeyboard(groupService.getGroup(user.getContext().getTargetUserGroup()).getUsers());
            case GET_ACTIVE_POLL_RESULT:
                return pollListKeyboard(pollService.getAllActive());
            case NEW_CLIENTS_LIST:
                return inlineUsersKeyboard(userService.getNewClients());
        }

        return state == MAIN_MENU
                ? new InlineKeyboardMarkup(keyboard)
                : new InlineKeyboardMarkupWithMenuButton(keyboard);
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

    private InlineKeyboardButton toButton(BotCommandsEnum command) {
        return toButton(command, command.getCommand());
    }

    private InlineKeyboardButton toButton(BotCommandsEnum command, String text) {
        InlineKeyboardButton button = new InlineKeyboardButton(text);
        button.setCallbackData(MENU.name() + command.name());
        return button;
    }

}
