package las.bot.tennis.repository;

import las.bot.tennis.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
}
