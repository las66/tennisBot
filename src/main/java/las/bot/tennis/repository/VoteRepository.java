package las.bot.tennis.repository;

import las.bot.tennis.model.Vote;
import org.springframework.data.repository.CrudRepository;

public interface VoteRepository extends CrudRepository<Vote, Integer> {
}
