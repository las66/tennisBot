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

    private String header;
    private String question;
    private String forGroup;
    private boolean active;

    public Poll(String header, String question, String groupName) {
        this.header = header;
        this.question = question;
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
