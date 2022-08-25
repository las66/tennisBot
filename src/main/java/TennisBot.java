import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TennisBot extends TelegramLongPollingBot {

    @Override
    public String getBotUsername() {
        return MyProp.getProperty("BOT_USERNAME");
    }

    @Override
    public String getBotToken() {
        return MyProp.getProperty("BOT_TOKEN");
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message != null) {
            Long chatId = message.getChat().getId();
            SendMessage sendMessage = new SendMessage(chatId.toString(), message.getText());
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

}
