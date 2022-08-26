package las.bot.tennis.service;

import las.bot.tennis.model.User;
import las.bot.tennis.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUser(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public void createUserIfAbsent(Message message) {
        if (getUser(message.getChatId()) == null) {
            User user = new User(message.getChatId(), 0);
            userRepository.save(user);
        }
    }

    public void changeStateTo(Long userId, int stateId) {
        User user = getUser(userId);
        user.setState(stateId);
        userRepository.save(user);
    }

}
