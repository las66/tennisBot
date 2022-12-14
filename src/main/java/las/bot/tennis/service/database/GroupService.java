package las.bot.tennis.service.database;

import com.google.common.collect.Lists;
import las.bot.tennis.model.Group;
import las.bot.tennis.repository.GroupRepository;
import las.bot.tennis.service.bot.SendMessageService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupService {
    public static final String ADMIN_GROUP = "Админы";
    public static final String ALL_CLIENTS_GROUP = "Все";

    private final GroupRepository groupRepository;
    private final SendMessageService sendMessageService;

    public GroupService(GroupRepository groupRepository, @Lazy SendMessageService sendMessageService) {
        this.groupRepository = groupRepository;
        this.sendMessageService = sendMessageService;
    }

    public Group getGroup(String name) {
        return groupRepository.findById(name).orElse(null);
    }

    public List<Group> getAll() {
        return Lists.newArrayList(groupRepository.findAll());
    }

    public List<Group> getAll(String filter) {
        return getAll().stream()
                .filter(group -> group.getName().toLowerCase().contains(filter.toLowerCase()))
                .collect(Collectors.toList());
    }

    public void createGroup(Message message) {
        String groupName = message.getText();
        if (groupName.equals(ALL_CLIENTS_GROUP)) {
            sendMessageService.sendMessage(message.getChatId(), "Имя \"" + groupName + "\" зарезервировано");
            return;
        }
        Group group = new Group(groupName);
        groupRepository.save(group);
        sendMessageService.sendMessage(message.getChatId(), "Группа " + groupName + " создана!");
    }

    public void deleteGroup(Long userId, String name) {
        if (ADMIN_GROUP.equals(name)) {
            sendMessageService.sendMessage(userId, "Нельзя удалять группу " + ADMIN_GROUP);
        } else {
            Group group = getGroup(name);
            deleteGroup(group);
            sendMessageService.sendMessage(userId, "Группа " + group.toShortString() + " удалена");
        }
    }

    public void renameGroup(Long userId, String name, String newName) {
        if (ADMIN_GROUP.equals(name)) {
            sendMessageService.sendMessage(userId, "Нельзя переименовывать группу " + ADMIN_GROUP);
        } else {
            Group group = getGroup(name);
            group.setName(newName);
            groupRepository.save(group);
            deleteGroup(name);
            sendMessageService.sendMessage(userId, "Группа " + name + " переименована в " + newName);
        }
    }

    private void deleteGroup(String name) {
        deleteGroup(getGroup(name));
    }

    private void deleteGroup(Group group) {
        groupRepository.delete(group);
    }

}
