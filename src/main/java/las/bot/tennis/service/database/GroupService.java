package las.bot.tennis.service.database;

import com.google.common.collect.Lists;
import las.bot.tennis.model.Group;
import las.bot.tennis.repository.GroupRepository;
import las.bot.tennis.service.bot.SendMessageService;
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

    public GroupService(GroupRepository groupRepository, SendMessageService sendMessageService) {
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
        Group group = new Group(message.getText());
        groupRepository.save(group);
        sendMessageService.sendMessage(message.getChatId(), "Группа " + message.getText() + " создана!");
    }

}
