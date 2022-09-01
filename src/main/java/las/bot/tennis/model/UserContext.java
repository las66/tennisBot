package las.bot.tennis.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "user_context")
public class UserContext {

    @Id
    private Long userId;

    @OneToOne
    @MapsId
    private User user;

    private int state;
    private String userGroup;
    private Integer menuMessageId;

    public UserContext(Long userId) {
        this.userId = userId;
        this.state = 0;
    }
}
