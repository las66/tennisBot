package las.bot.tennis.service;

import las.bot.tennis.model.User;
import las.bot.tennis.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final SendMessageService sendMessageService;

    public UserService(UserRepository userRepository, @Lazy SendMessageService sendMessageService) {
        this.userRepository = userRepository;
        this.sendMessageService = sendMessageService;
    }

    public User getUser(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public User getUser(String userId) {
        return getUser(Long.parseLong(userId));
    }

    public void createUserIfAbsent(Message message) {
        if (getUser(message.getChatId()) == null) {
            String lastName = message.getFrom().getLastName();
            String name = (lastName == null ? "" : lastName + " ") + message.getFrom().getFirstName();
            User user = new User(message.getChatId(), name);
            userRepository.save(user);
            sendMessageService.sendMessage(message.getChatId(), "Вы зарегистрированы!");
        }
    }

    public void changeStateTo(Long userId, UserStateEnum state) {
        User user = getUser(userId);
        user.setState(state.getStateId());
        userRepository.save(user);
    }

    public List<User> findUsers(String text) {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(user -> {
            if (user.getName().toLowerCase().contains(text.toLowerCase())
                    || user.getPhone().toLowerCase().contains(text.toLowerCase())
                    || user.getDescription().toLowerCase().contains(text.toLowerCase())) {
                users.add(user);
            }
        });
        return users;
    }

}
