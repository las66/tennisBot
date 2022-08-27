package las.bot.tennis.service;

import las.bot.tennis.model.Group;
import las.bot.tennis.repository.GroupRepository;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final SendMessageService sendMessageService;

    public GroupService(GroupRepository groupRepository, SendMessageService sendMessageService) {
        this.groupRepository = groupRepository;
        this.sendMessageService = sendMessageService;
    }

    public Group getGroup(String name) {
        return groupRepository.findById(name).orElse(null);
    }

    public void createGroup(Message message) {
        Group group = new Group(message.getText());
        groupRepository.save(group);
        sendMessageService.sendMessage(message.getChatId(), "Группа " + message.getText() + " создана!");
    }

}
