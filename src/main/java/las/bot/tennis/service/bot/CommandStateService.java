package las.bot.tennis.service.bot;

import org.springframework.stereotype.Service;

import static las.bot.tennis.service.bot.UserStateEnum.*;

@Service
public class CommandStateService {

    public UserStateEnum getNextState(BotCommandsEnum command) {
        switch (command) {
            case START:
            case GO_TO_MAIN_MENU:
            case DELETE_CLIENT_YES:
                return MAIN_MENU;
            case WORK_WITH_CLIENTS:
            case GET_CLIENT:
                return GET_CLIENT_STEP_1;
            case WORK_WITH_GROUPS:
                return GROUPS_WORK_MENU;
            case NEW_GROUP:
                return NEW_GROUP_STEP_1;
            case RENAME_GROUP:
                return RENAME_GROUP_STEP_1;
            case DELETE_GROUP:
                return DELETE_GROUP_STEP_1;
            case LIST_GROUP:
                return LIST_GROUP_STEP_1;
            case DELETE_CLIENT_FROM_GROUP:
                return DELETE_CLIENT_FROM_GROUP_STEP_2;
            case DELETE_CLIENT_FROM_GROUP_2:
                return DELETE_CLIENT_FROM_GROUP_STEP_1;
            case ADD_CLIENT_TO_GROUP:
                return ADD_CLIENT_TO_GROUP_STEP_2;
            case ADD_CLIENT_TO_GROUP_2:
                return ADD_CLIENT_TO_GROUP_STEP_1;
            case SEND_MESSAGE:
                return SEND_MESSAGE_MENU;
            case SEND_MESSAGE_TO_CLIENT:
                return SEND_MESSAGE_TO_CLIENT_STEP_1;
            case SEND_MESSAGE_TO_GROUP:
                return SEND_MESSAGE_TO_GROUP_MENU;
            case CHANGE_CLIENT_NAME:
                return CHANGE_CLIENT_NAME;
            case CHANGE_CLIENT_PHONE:
                return CHANGE_CLIENT_PHONE;
            case CHANGE_CLIENT_DESCRIPTION:
                return CHANGE_CLIENT_DESC;
            case DELETE_CLIENT:
                return DELETE_CLIENT_MENU;
            case ACTIVE_POLLS:
                return GET_ACTIVE_POLL_RESULT;
            case POLL_MENU:
                return POLL_WORK_MENU;
            case NEW_POLL:
                return NEW_POLL_MENU;
            case NEXT_MONTH_POLL:
                return SEND_POLL_STEP_1;
            case NEW_CLIENTS:
                return NEW_CLIENTS_LIST;
            case CONFIRM_CLIENT:
            case DELETE_CLIENT_NO:
                return CLIENT_WORK_MENU;
        }
        return null;
    }

    public UserStateEnum getNextState(UserStateEnum state) {
        switch (state) {
            case SEND_MESSAGE_TO_GROUP_MENU:
                return MESSAGE_FOR_GROUP;
            case RENAME_GROUP_STEP_1:
                return RENAME_GROUP_STEP_2;
            case LIST_GROUP_STEP_1:
            case DELETE_GROUP_STEP_1:
                return GROUPS_WORK_MENU;
            case SEND_POLL_STEP_1:
                return POLL_WORK_MENU;
            case SEND_MESSAGE_TO_CLIENT_STEP_2:
                return SEND_MESSAGE_TO_CLIENT_STEP_3;
            case ADD_CLIENT_TO_GROUP_STEP_3:
                return CLIENT_ADDED_TO_GROUP;
            case DELETE_CLIENT_FROM_GROUP_STEP_2:
                return MAIN_MENU;
            case GET_CLIENT_STEP_1:
            case GET_CLIENT_STEP_2:
            case ADD_CLIENT_TO_GROUP_STEP_1:
            case DELETE_CLIENT_FROM_GROUP_STEP_1:
            case NEW_CLIENTS_LIST:
                return CLIENT_WORK_MENU;
        }
        return null;
    }

}
