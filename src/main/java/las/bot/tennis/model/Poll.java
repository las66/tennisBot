package las.bot.tennis.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "poll")
public class Poll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL)
    private List<PollAnswer> answers;

    private String pollText;
    private String forGroup;
    private boolean active;

    public Poll(String text, String groupName) {
        this.pollText = text;
        this.forGroup = groupName;
        this.active = true;
    }

    public List<PollAnswer> getAnswers() {
        if (answers == null) {
            answers = new ArrayList<>();
        }
        return answers;
    }

}
