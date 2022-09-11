package las.bot.tennis.service.bot.query.callback;

import las.bot.tennis.model.User;
import las.bot.tennis.service.bot.SendMessageService;
import las.bot.tennis.service.bot.UserStateEnum;
import las.bot.tennis.service.database.PollService;
import las.bot.tennis.service.database.UserContextService;
import las.bot.tennis.service.database.UserService;
import las.bot.tennis.service.helper.KeyboardGenerator;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import static las.bot.tennis.service.bot.UserStateEnum.POLL_WORK_MENU;

@Service
public class PollCallback {

    private final SendMessageService sendMessageService;
    private final UserContextService userContextService;
    private final PollService pollService;
    private final UserService userService;
    private final KeyboardGenerator keyboardGenerator;

    public PollCallback(SendMessageService sendMessageService,
                        UserContextService userContextService,
                        PollService pollService,
                        UserService userService,
                        KeyboardGenerator keyboardGenerator) {
        this.sendMessageService = sendMessageService;
        this.userContextService = userContextService;
        this.pollService = pollService;
        this.userService = userService;
        this.keyboardGenerator = keyboardGenerator;
    }

    public void process(CallbackQuery callbackQuery) {
        String pollId = callbackQuery.getData().substring(CallbackPrefixEnum.LENGTH);
        Long currentUserId = callbackQuery.getMessage().getChatId();
        User currentUser = userService.getUser(currentUserId);
        UserStateEnum state = UserStateEnum.getById(currentUser.getContext().getState());
        switch (state) {
            case GET_ACTIVE_POLL_RESULT:
                sendMessageService.sendMessage(currentUserId, pollService.getReport(pollId), keyboardGenerator.closePollKeyboard(pollId));
                userContextService.setState(currentUserId, POLL_WORK_MENU);
                break;
        }
    }

}
