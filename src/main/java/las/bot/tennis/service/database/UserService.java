package las.bot.tennis.service.database;

import las.bot.tennis.model.Group;
import las.bot.tennis.model.User;
import las.bot.tennis.repository.UserRepository;
import las.bot.tennis.service.bot.SendMessageService;
import las.bot.tennis.service.bot.UserStateEnum;
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

    public void changeStateTo(Long userId, UserStateEnum state, String context) {
        User user = getUser(userId);
        user.setContext(context);
        user.setState(state.getStateId());
        userRepository.save(user);
    }

    public void changeStateTo(Long userId, UserStateEnum state) {
        changeStateTo(userId, state, null);
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

    public void addToGroup(String userId, Group group) {
        User user = getUser(userId);
        user.getGroups().add(group);
        userRepository.save(user);
    }

}
