package las.bot.tennis.repository;

import las.bot.tennis.model.Poll;
import org.springframework.data.repository.CrudRepository;

public interface PollRepository extends CrudRepository<Poll, Integer> {
}
