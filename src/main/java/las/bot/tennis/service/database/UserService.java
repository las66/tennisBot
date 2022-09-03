package las.bot.tennis.service.database;

import las.bot.tennis.model.Group;
import las.bot.tennis.model.User;
import las.bot.tennis.repository.UserRepository;
import las.bot.tennis.service.bot.SendMessageService;
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
    private final GroupService groupService;

    public UserService(UserRepository userRepository, @Lazy SendMessageService sendMessageService, GroupService groupService) {
        this.userRepository = userRepository;
        this.sendMessageService = sendMessageService;
        this.groupService = groupService;
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
            User user = new User(message.getChatId(), name, message.getFrom().getUserName());
            userRepository.save(user);
            sendMessageService.sendMessage(message.getChatId(), "Вы зарегистрированы!");
        }
    }

    public List<User> findUsers(String text) {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(user -> {
            if ((user.getName() != null && user.getName().toLowerCase().contains(text.toLowerCase()))
                    || (user.getPhone() != null && user.getPhone().toLowerCase().contains(text.toLowerCase()))
                    || (user.getDescription() != null && user.getDescription().toLowerCase().contains(text.toLowerCase()))) {
                users.add(user);
            }
        });
        return users;
    }

    public void addToGroup(Long currentUserId, String targetUserId, Group group) {
        if (group.getUsers().stream().anyMatch(user -> user.getChatId().toString().equals(targetUserId))) {
            sendMessageService.sendMessage(currentUserId, "Клиент уже находится в группе");
        } else {
            User user = getUser(targetUserId);
            user.getGroups().add(group);
            userRepository.save(user);
            sendMessageService.sendMessage(currentUserId, user.getName() + " добавлен(а) в группу " + group.getName());
        }
    }

    public void deleteGroup(Long currentUserId, String targetUserId, String userGroup) {
        Group group = groupService.getGroup(userGroup);
        User targetUser = getUser(targetUserId);
        targetUser.getGroups().remove(group);
        userRepository.save(targetUser);
        sendMessageService.sendMessage(currentUserId, targetUser.getName() + " удален(а) из группы " + group.getName());
    }

}
