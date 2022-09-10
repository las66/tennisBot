package las.bot.tennis.service.bot.query.callback;

import las.bot.tennis.model.User;
import las.bot.tennis.service.bot.SendMessageService;
import las.bot.tennis.service.database.PollService;
import las.bot.tennis.service.database.UserService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Service
public class AnswerCallback {

    private final SendMessageService sendMessageService;
    private final PollService pollService;
    private final UserService userService;

    public AnswerCallback(SendMessageService sendMessageService, PollService pollService, UserService userService) {
        this.sendMessageService = sendMessageService;
        this.pollService = pollService;
        this.userService = userService;
    }

    public void process(CallbackQuery callbackQuery) {
        String answerId = callbackQuery.getData().substring(CallbackPrefixEnum.LENGTH);
        Long currentUserId = callbackQuery.getMessage().getChatId();
        User currentUser = userService.getUser(currentUserId);

        pollService.saveVote(currentUser, answerId);
        sendMessageService.editPollMessage(currentUser, callbackQuery.getMessage().getMessageId(), pollService.getAnswer(answerId).getAnswerText());
    }

}
