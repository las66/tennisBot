package las.bot.tennis.service.bot;

import las.bot.tennis.model.TennisBot;
import las.bot.tennis.model.User;
import las.bot.tennis.service.database.GroupService;
import las.bot.tennis.service.database.UserContextService;
import las.bot.tennis.service.database.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static las.bot.tennis.service.bot.UserStateEnum.*;

@Slf4j
@Service
public class CallbackQueryHandler {

    private final UserService userService;
    private final UserContextService userContextService;
    private final GroupService groupService;
    private final SendMessageService sendMessageService;
    private final TennisBot bot;

    public CallbackQueryHandler(UserService userService,
                                UserContextService userContextService,
                                GroupService groupService,
                                SendMessageService sendMessageService,
                                TennisBot bot) {
        this.userService = userService;
        this.userContextService = userContextService;
        this.groupService = groupService;
        this.sendMessageService = sendMessageService;
        this.bot = bot;
    }

    public void process(CallbackQuery callbackQuery) {
        Long currentUserId = callbackQuery.getMessage().getChatId();
        User currentUser = userService.getUser(currentUserId);
        UserStateEnum state = UserStateEnum.getById(currentUser.getContext().getState());
        switch (state) {
            case GET_CLIENT_STEP_2:
                userContextService.setState(currentUserId, CLIENT_WORK_MENU);
                String clientInfo = userService.getUser(callbackQuery.getData()).toShortString();
                sendMessageService.sendMessage(currentUserId, clientInfo);
                sendMessageService.sendStateMessage(currentUserId);
                break;
            case ADD_CLIENT_TO_GROUP_STEP_1:
                userContextService.setState(currentUserId, ADD_CLIENT_TO_GROUP_STEP_2);
                userContextService.setGroup(currentUserId, callbackQuery.getData());
                String groupInfo = groupService.getGroup(callbackQuery.getData()).toShortString();
                sendMessageService.sendMessage(currentUserId, groupInfo);
                sendMessageService.sendStateMessage(currentUserId);
                break;
            case ADD_CLIENT_TO_GROUP_STEP_3:
                userContextService.setState(currentUserId, CLIENT_ADDED_TO_GROUP);
                userService.addToGroup(callbackQuery.getData(), groupService.getGroup(currentUser.getContext().getUserGroup()));
                User addedUser = userService.getUser(callbackQuery.getData());
                sendMessageService.sendMessage(currentUserId, addedUser.getName() + " добавлен в группу " + currentUser.getContext());
                sendMessageService.sendStateMessage(currentUserId);
                break;
            case SEND_MESSAGE_TO_GROUP_MENU:
                userContextService.setState(currentUserId, MESSAGE_FOR_GROUP);
                userContextService.setGroup(currentUserId, callbackQuery.getData());
                groupInfo = groupService.getGroup(callbackQuery.getData()).toShortString();
                sendMessageService.sendMessage(currentUserId, groupInfo);
                sendMessageService.sendStateMessage(currentUserId);
                break;
        }

        try {
            bot.execute(new AnswerCallbackQuery(callbackQuery.getId()));
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке AnswerCallbackQuery", e);
        }
    }

}
