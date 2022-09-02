package las.bot.tennis.service.database;

import las.bot.tennis.model.UserContext;
import las.bot.tennis.repository.UserContextRepository;
import las.bot.tennis.service.bot.UserStateEnum;
import org.springframework.stereotype.Service;

@Service
public class UserContextService {

    final UserService userService;
    private final UserContextRepository userContextRepository;

    public UserContextService(UserService userService, UserContextRepository userContextRepository) {
        this.userService = userService;
        this.userContextRepository = userContextRepository;
    }

    public UserContext getContext(Long userId) {
        return userContextRepository.findById(userId).orElse(null);
    }

    public void setState(Long userId, UserStateEnum state) {
        UserContext userContext = getContext(userId);
        userContext.setState(state.getStateId());
        userContextRepository.save(userContext);
    }

    public void setUserGroup(Long userId, String group) {
        UserContext userContext = getContext(userId);
        userContext.setUserGroup(group);
        userContextRepository.save(userContext);
    }

    public void setMenuMessageId(Long userId, Integer menuMessageId) {
        UserContext userContext = getContext(userId);
        userContext.setMenuMessageId(menuMessageId);
        userContextRepository.save(userContext);
    }

}
