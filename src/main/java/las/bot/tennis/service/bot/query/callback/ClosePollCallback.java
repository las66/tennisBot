package las.bot.tennis.service.bot.query.callback;

import las.bot.tennis.service.bot.SendMessageService;
import las.bot.tennis.service.database.PollService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.Arrays;
import java.util.List;

@Service
public class ClosePollCallback {

    private final SendMessageService sendMessageService;
    private final PollService pollService;

    public ClosePollCallback(SendMessageService sendMessageService, PollService pollService) {
        this.sendMessageService = sendMessageService;
        this.pollService = pollService;
    }

    public void process(CallbackQuery callbackQuery) {
        String pollId = callbackQuery.getData().substring(CallbackPrefixEnum.LENGTH);
        Long currentUserId = callbackQuery.getMessage().getChatId();

        pollService.closePoll(pollId);
        List<String> messageRows = Arrays.asList(callbackQuery.getMessage().getText().split("\n"));
        String newMessage = "[Опрос закрыт]\n" + messageRows.get(0) + "\n" + messageRows.get(1);
        sendMessageService.editMessage(currentUserId, callbackQuery.getMessage().getMessageId(), newMessage);
    }

}
