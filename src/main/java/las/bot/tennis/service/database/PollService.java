package las.bot.tennis.service.database;

import com.google.common.collect.Lists;
import las.bot.tennis.model.*;
import las.bot.tennis.repository.PollAnswerRepository;
import las.bot.tennis.repository.PollRepository;
import las.bot.tennis.repository.VoteRepository;
import las.bot.tennis.service.bot.SendMessageService;
import las.bot.tennis.service.helper.KeyboardGenerator;
import las.bot.tennis.service.helper.Month;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PollService {

    private final GroupService groupService;
    private final PollRepository pollRepository;
    private final PollAnswerRepository pollAnswerRepository;
    private final VoteRepository voteRepository;
    private final SendMessageService sendMessageService;
    private final KeyboardGenerator keyboardGenerator;

    public PollService(GroupService groupService,
                       PollRepository pollRepository,
                       PollAnswerRepository pollAnswerRepository,
                       VoteRepository voteRepository,
                       SendMessageService sendMessageService,
                       KeyboardGenerator keyboardGenerator) {
        this.groupService = groupService;
        this.pollRepository = pollRepository;
        this.pollAnswerRepository = pollAnswerRepository;
        this.voteRepository = voteRepository;
        this.sendMessageService = sendMessageService;
        this.keyboardGenerator = keyboardGenerator;
    }

    public void createNewNextMonthPoll(Long currentUserId, String groupName) {
        Month month = Month.values()[(Calendar.getInstance().get(Calendar.MONTH) + 1) % 12];
        String text = "Будете ли вы продолжать занятия в " + month.getAccusative() + "?";
        Poll poll = new Poll(month.name(), text, groupName);
        poll.getAnswers().add(new PollAnswer(poll, "Да"));
        poll.getAnswers().add(new PollAnswer(poll, "Нет"));

        poll = pollRepository.save(poll);

        Group group = groupService.getGroup(groupName);
        for (User user : group.getUsers()) {
            sendMessageService.sendMessage(user.getChatId(), text, keyboardGenerator.pullKeyboard(poll));
        }
        sendMessageService.sendMessage(currentUserId, "Опрос создан");
    }

    public void saveVote(User user, String answerId) {
        PollAnswer answer = getAnswer(answerId);
        Vote vote = new Vote(user, answer);
        user.getVotes().add(vote);
        answer.getVotes().add(vote);
        voteRepository.save(vote);
    }

    public PollAnswer getAnswer(String answerId) {
        return pollAnswerRepository.findById(Integer.parseInt(answerId)).get();
    }

    public List<Poll> getAllActive() {
        return Lists.newArrayList(pollRepository.findAll()).stream()
                .filter(Poll::isActive)
                .collect(Collectors.toList());
    }

    public String getReport(String pollId) {
        Poll poll = getPoll(pollId);
        Group group = groupService.getGroup(poll.getForGroup());
        StringBuilder report = new StringBuilder("Опрос группы ").append(group.getName()).append("\n")
                .append(poll.getQuestion());

        for (User user : group.getUsers()) {
            Vote vote = user.getVotes().stream()
                    .filter(v -> v.getAnswer().getPoll().getId().toString().equals(pollId))
                    .findAny()
                    .orElse(null);
            if (vote == null) {
                report.append("\n[   ] ").append(user.toOneLineString());
            } else {
                report.append("\n[").append(vote.getAnswer().getAnswerText()).append("] ").append(user.toOneLineString());
            }
        }
        return report.toString();
    }

    public Poll getPoll(String pollId) {
        return pollRepository.findById(Integer.parseInt(pollId)).get();
    }

    public void closePoll(String pollId) {
        Poll poll = getPoll(pollId);
        poll.setActive(false);
        pollRepository.save(poll);
    }

}
