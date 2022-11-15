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

    public UserService(UserRepository userRepository, @Lazy SendMessageService sendMessageService) {
        this.userRepository = userRepository;
        this.sendMessageService = sendMessageService;
    }

    public User getUser(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public void createUserIfAbsent(Message message) {
        if (getUser(message.getChatId()) == null) {
            String lastName = message.getFrom().getLastName();
            String name = (lastName == null ? "" : lastName + " ") + message.getFrom().getFirstName();
            String userName = message.getFrom().getUserName();
            User user = new User(message.getChatId(), name, userName == null ? message.getChatId().toString() : "@" + userName);
            userRepository.save(user);
            sendMessageService.sendMessage(message.getChatId(), "Вы зарегистрированы!");
        }
    }

    public List<User> findUsers(String text) {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(user -> {
            if (((user.getName() != null && user.getName().toLowerCase().contains(text.toLowerCase()))
                    || (user.getPhone() != null && user.getPhone().toLowerCase().contains(text.toLowerCase()))
                    || (user.getDescription() != null && user.getDescription().toLowerCase().contains(text.toLowerCase()))
                    || (user.getChatId().toString().contains(text.toLowerCase())))
                    && !user.isDeleted()) {
                users.add(user);
            }
        });
        return users;
    }

    public void addToGroup(Long currentUserId, Long targetUserId, Group group) {
        if (group.getUsers().stream().anyMatch(user -> user.getChatId().equals(targetUserId))) {
            sendMessageService.sendMessage(currentUserId, "Клиент уже находится в группе " + group.getName());
        } else {
            User user = getUser(targetUserId);
            user.getGroups().add(group);
            userRepository.save(user);
            if (group.getName().equals(GroupService.ADMIN_GROUP)) {
                sendMessageService.sendMessage(targetUserId, "Теперь вы админ");
                sendMessageService.sendStateMessage(targetUserId);
            }
        }
    }

    public void deleteFromGroup(Long targetUserId, Group group) {
        User targetUser = getUser(targetUserId);
        targetUser.getGroups().remove(group);
        userRepository.save(targetUser);
        if (group.getName().equals(GroupService.ADMIN_GROUP)) {
            sendMessageService.sendMessage(targetUserId, "Вы больше не админ");
            sendMessageService.sendStateMessage(targetUserId);
        }
    }

    public void setName(Long targetUserId, String name) {
        User user = getUser(targetUserId);
        user.setName(name);
        userRepository.save(user);
    }

    public void setPhone(Long targetUserId, String phone) {
        User user = getUser(targetUserId);
        user.setPhone(phone);
        userRepository.save(user);
    }

    public void setDescription(Long targetUserId, String description) {
        User user = getUser(targetUserId);
        user.setDescription(description);
        userRepository.save(user);
    }

    public void deleteUser(Long currentUserId, Long targetUserId) {
        User targetUser = getUser(targetUserId);
        String info = targetUser.toOneLineString();
        List<User> usersWithDeletedUserInTarget = new ArrayList<>();
        userRepository.findAll().forEach(user -> {
            if (targetUserId.equals(user.getContext().getTargetUserId())) {
                user.getContext().setTargetUserId(null);
                usersWithDeletedUserInTarget.add(user);
            }
        });
        userRepository.saveAll(usersWithDeletedUserInTarget);
        for (Group group : targetUser.getGroups()) {
            sendMessageService.sendMessage(currentUserId, "Клиент удален из группы " + group.getName());
        }
        targetUser.setGroups(null);
        targetUser.setDeleted(true);
        userRepository.save(targetUser);
        sendMessageService.sendMessage(currentUserId, "Клиент " + info + " удален");
    }

    public List<User> getNewClients() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(user -> {
            if (user.isNewClient()) {
                users.add(user);
            }
        });
        return users;
    }

    public void confirmClient(Long targetUserId) {
        User user = getUser(targetUserId);
        user.setNewClient(false);
        userRepository.save(user);
    }
}
