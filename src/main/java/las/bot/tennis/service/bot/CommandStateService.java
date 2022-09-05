package las.bot.tennis.service.bot;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static las.bot.tennis.service.bot.BotCommandsEnum.*;
import static las.bot.tennis.service.bot.UserStateEnum.*;

@Service
public class CommandStateService {

    public UserStateEnum getNextState(BotCommandsEnum command) {
        switch (command) {
            case START:
            case GO_TO_MAIN_MENU:
                return MAIN_MENU;
            case WORK_WITH_CLIENTS:
                return CLIENTS_WORK_MENU;
            case WORK_WITH_GROUPS:
                return GROUPS_WORK_MENU;
            case GET_CLIENT:
                return GET_CLIENT_STEP_1;
            case CHANGE_CLIENT:
                return CHANGE_CLIENT_STEP_1;
            case NEW_GROUP:
                return NEW_GROUP_STEP_1;
            case RENAME_GROUP:
                return RENAME_GROUP_STEP_1;
            case DELETE_GROUP:
                return DELETE_GROUP_STEP_1;
            case LIST_GROUP:
                return LIST_GROUP_STEP_1;
            case DELETE_CLIENT_FROM_GROUP:
            case DELETE_CLIENT_FROM_GROUP_2:
                return DELETE_CLIENT_FROM_GROUP_STEP_1;
            case ADD_CLIENT_TO_GROUP:
            case ADD_CLIENT_TO_GROUP_2:
                return ADD_CLIENT_TO_GROUP_STEP_1;
            case SEND_MESSAGE:
                return SEND_MESSAGE_MENU;
            case SEND_MESSAGE_TO_CLIENT:
                return SEND_MESSAGE_TO_CLIENT_STEP_1;
            case SEND_MESSAGE_TO_GROUP:
                return SEND_MESSAGE_TO_GROUP_MENU;
            case CHANGE_CLIENT_NAME:
                return CHANGE_CLIENT_STEP_3_1;
            case CHANGE_CLIENT_PHONE:
                return CHANGE_CLIENT_STEP_3_2;
            case CHANGE_CLIENT_DESCRIPTION:
                return CHANGE_CLIENT_STEP_3_3;
            case DELETE_CLIENT:
                return DELETE_CLIENT_STEP_1;
            case ACTIVE_POLLS:
                return GET_ACTIVE_POLL_RESULT;
            case POLL_MENU:
                return POLL_WORK_MENU;
            case NEW_POLL:
                return NEW_POLL_MENU;
            case NEXT_MONTH_POLL:
                return SEND_POLL_STEP_1;
        }
        return null;
    }

    public List<List<BotCommandsEnum>> getKeyboardSkeleton(UserStateEnum state) {
        switch (state) {
            case MAIN_MENU:
                return (asList(
                        asList(SEND_MESSAGE, POLL_MENU),
                        asList(WORK_WITH_GROUPS, WORK_WITH_CLIENTS)
                ));
            case CLIENTS_WORK_MENU:
                return (asList(
                        asList(GET_CLIENT, CHANGE_CLIENT, DELETE_CLIENT),
                        asList(ADD_CLIENT_TO_GROUP_2, DELETE_CLIENT_FROM_GROUP_2)
                ));
            case GROUPS_WORK_MENU:
            case GROUP_ALREADY_EXISTS:
                return (asList(
                        asList(LIST_GROUP, RENAME_GROUP),
                        asList(NEW_GROUP, DELETE_GROUP),
                        asList(ADD_CLIENT_TO_GROUP, DELETE_CLIENT_FROM_GROUP)
                ));
            case CLIENT_ADDED_TO_GROUP:
                return (asList(
                        new ArrayList<>()
                ));
            case SEND_MESSAGE_MENU:
                return (asList(
                        asList(SEND_MESSAGE_TO_GROUP, SEND_MESSAGE_TO_CLIENT)
                ));
            case CHANGE_CLIENT_STEP_3:
                return (asList(
                        asList(CHANGE_CLIENT_NAME, CHANGE_CLIENT_PHONE, CHANGE_CLIENT_DESCRIPTION)
                ));
            case DELETE_CLIENT_STEP_3:
                return (asList(
                        asList(DELETE_CLIENT_YES, DELETE_CLIENT_NO)
                ));
            case POLL_WORK_MENU:
                return (asList(
                        asList(NEW_POLL, ACTIVE_POLLS)
                ));
            case NEW_POLL_MENU:
                return (asList(
                        asList(NEXT_MONTH_POLL)
                ));
            default:
                return new ArrayList<>();
        }
    }

}
