package las.bot.tennis.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import static las.bot.tennis.service.UserStateEnum.CLIENT_WORK_MENU;
import static las.bot.tennis.service.UserStateEnum.MAIN_MENU;

@Service
public class MessageHandler {

    private final UserService userService;
    private final SendMessageService sendMessageService;

    public MessageHandler(UserService userService, SendMessageService sendMessageService) {
        this.userService = userService;
        this.sendMessageService = sendMessageService;
    }

    public void process(Message message) {
        switch (BotCommandsEnum.getByCommand(message.getText())) {
            case GO_TO_MAIN_MENU:
                userService.changeStateTo(message.getChatId(), MAIN_MENU.getStateId());
                break;
            case WORK_WITH_CLIENTS:
                userService.changeStateTo(message.getChatId(), CLIENT_WORK_MENU.getStateId());
                break;
        }
        sendMessageService.sendStateMessage(message.getChatId());
    }

}
