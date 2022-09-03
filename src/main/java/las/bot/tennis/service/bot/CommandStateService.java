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
            case NEW_GROUP:
                return NEW_GROUP_STEP_1;
            case RENAME_GROUP:
                return RENAME_GROUP_STEP_1;
            case DELETE_GROUP:
                return DELETE_GROUP_STEP_1;
            case LIST_GROUP:
                return LIST_GROUP_STEP_1;
            case ADD_CLIENT_TO_GROUP:
                return ADD_CLIENT_TO_GROUP_STEP_1;
            case SEND_MESSAGE:
                return SEND_MESSAGE_MENU;
            case SEND_MESSAGE_TO_CLIENT:
                return SEND_MESSAGE_TO_CLIENT_MENU;
            case SEND_MESSAGE_TO_GROUP:
                return SEND_MESSAGE_TO_GROUP_MENU;
        }
        return null;
    }

    public List<List<BotCommandsEnum>> getKeyboardSkeleton(UserStateEnum state) {
        switch (state) {
            case MAIN_MENU:
                return (asList(
                        asList(SEND_MESSAGE),
                        asList(WORK_WITH_GROUPS, WORK_WITH_CLIENTS)
                ));
            case CLIENTS_WORK_MENU:
            case CLIENTS_NOT_FOUND:
                return (asList(
                        asList(GET_CLIENT),
                        asList(ADD_CLIENT_TO_GROUP)
                ));
            case CLIENT_WORK_MENU:
                return (asList(
                        new ArrayList<>()
                ));
            case GROUPS_WORK_MENU:
            case GROUP_ALREADY_EXISTS:
                return (asList(
                        asList(LIST_GROUP, RENAME_GROUP),
                        asList(NEW_GROUP, DELETE_GROUP),
                        asList(ADD_CLIENT_TO_GROUP)
                ));
            case CLIENT_ADDED_TO_GROUP:
                return (asList(
                        new ArrayList<>()
                ));
            case SEND_MESSAGE_MENU:
                return (asList(
                        asList(SEND_MESSAGE_TO_GROUP, SEND_MESSAGE_TO_CLIENT)
                ));
            default:
                return new ArrayList<>();
        }
    }

}
