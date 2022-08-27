package las.bot.tennis.service;

import las.bot.tennis.model.TennisBot;
import las.bot.tennis.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static las.bot.tennis.service.UserStateEnum.CLIENT_WORK_MENU;

@Slf4j
@Service
public class CallbackQueryHandler {

    private final UserService userService;
    private final SendMessageService sendMessageService;
    private final TennisBot bot;

    public CallbackQueryHandler(UserService userService, SendMessageService sendMessageService, TennisBot bot) {
        this.userService = userService;
        this.sendMessageService = sendMessageService;
        this.bot = bot;
    }

    public void process(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        User user = userService.getUser(chatId);
        UserStateEnum state = UserStateEnum.getById(user.getState());
        switch (state) {
            case GET_CLIENT_STEP_2:
                userService.changeStateTo(chatId, CLIENT_WORK_MENU);
                String clientInfo = userService.getUser(callbackQuery.getData()).toShortString();
                sendMessageService.sendMessage(chatId, clientInfo);
                sendMessageService.sendStateMessage(chatId);
        }

        try {
            bot.execute(new AnswerCallbackQuery(callbackQuery.getId()));
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке AnswerCallbackQuery", e);
        }
    }

}
