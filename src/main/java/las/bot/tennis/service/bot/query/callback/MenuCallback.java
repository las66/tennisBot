package las.bot.tennis.service.bot.query.callback;

import las.bot.tennis.model.User;
import las.bot.tennis.service.bot.BotCommandsEnum;
import las.bot.tennis.service.bot.CommandStateService;
import las.bot.tennis.service.bot.SendMessageService;
import las.bot.tennis.service.database.UserContextService;
import las.bot.tennis.service.database.UserService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Service
public class MenuCallback {

    private final UserContextService userContextService;
    private final CommandStateService commandStateService;
    private final SendMessageService sendMessageService;
    private final UserService userService;

    public MenuCallback(UserContextService userContextService,
                        CommandStateService commandStateService,
                        SendMessageService sendMessageService,
                        UserService userService) {
        this.userContextService = userContextService;
        this.commandStateService = commandStateService;
        this.sendMessageService = sendMessageService;
        this.userService = userService;
    }

    public void process(CallbackQuery callbackQuery) {
        String commandName = callbackQuery.getData().substring(CallbackPrefixEnum.LENGTH);
        BotCommandsEnum command = BotCommandsEnum.valueOf(commandName);
        Long currentUserId = callbackQuery.getMessage().getChatId();
        User currentUser = userService.getUser(currentUserId);

        switch (command) {
            case DELETE_CLIENT_YES:
                userService.deleteUser(currentUserId, currentUser.getContext().getTargetUserId());
                break;
            case DELETE_CLIENT_NO:
                sendMessageService.sendMessage(currentUserId, "Клиент не удален");
                break;
        }

        userContextService.setState(currentUserId, commandStateService.getNextState(command));
    }

}
