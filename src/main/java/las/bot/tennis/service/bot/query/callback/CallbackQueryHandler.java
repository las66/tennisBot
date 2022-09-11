package las.bot.tennis.service.bot.query.callback;

import las.bot.tennis.model.Group;
import las.bot.tennis.model.TennisBot;
import las.bot.tennis.model.User;
import las.bot.tennis.service.bot.SendMessageService;
import las.bot.tennis.service.database.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static las.bot.tennis.service.bot.query.callback.CallbackPrefixEnum.ANSW;
import static las.bot.tennis.service.database.GroupService.ADMIN_GROUP;

@Slf4j
@Service
public class CallbackQueryHandler {

    private final UserService userService;
    private final TennisBot bot;
    private final AnswerCallback answerCallback;
    private final MenuCallback menuCallback;
    private final PollCallback pollCallback;
    private final GroupCallback groupCallback;
    private final UserCallback userCallback;
    private final SendMessageService sendMessageService;
    private final ClosePollCallback closePollCallback;

    public CallbackQueryHandler(UserService userService,
                                TennisBot bot,
                                AnswerCallback answerCallback,
                                MenuCallback menuCallback,
                                PollCallback pollCallback,
                                GroupCallback groupCallback,
                                UserCallback userCallback,
                                SendMessageService sendMessageService,
                                ClosePollCallback closePollCallback) {
        this.userService = userService;
        this.bot = bot;
        this.answerCallback = answerCallback;
        this.menuCallback = menuCallback;
        this.pollCallback = pollCallback;
        this.groupCallback = groupCallback;
        this.userCallback = userCallback;
        this.sendMessageService = sendMessageService;
        this.closePollCallback = closePollCallback;
    }

    public void process(CallbackQuery callbackQuery) {
        Long currentUserId = callbackQuery.getMessage().getChatId();
        User currentUser = userService.getUser(currentUserId);
        boolean currentUserIsAdmin = currentUser.getGroups().stream().map(Group::getName).anyMatch(name -> name.equals(ADMIN_GROUP));
        String data = callbackQuery.getData();
        CallbackPrefixEnum commandType = CallbackPrefixEnum.valueOf(data.substring(0, CallbackPrefixEnum.LENGTH));

        if (commandType == ANSW) {
            answerCallback.process(callbackQuery);
        }
        if (currentUserIsAdmin) {
            switch (commandType) {
                case MENU:
                    menuCallback.process(callbackQuery);
                    break;
                case POLL:
                    pollCallback.process(callbackQuery);
                    break;
                case GRUP:
                    groupCallback.process(callbackQuery);
                    break;
                case USER:
                    userCallback.process(callbackQuery);
                    break;
                case CLSP:
                    closePollCallback.process(callbackQuery);
                    break;
            }
            sendMessageService.sendStateMessage(currentUserId);
        }

        try {
            bot.execute(new AnswerCallbackQuery(callbackQuery.getId()));
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке AnswerCallbackQuery", e);
        }
    }

}
