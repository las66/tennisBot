package las.bot.tennis.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "telegram_user")
public class User {

    @Id
    private Long chatId;

    @ManyToMany
    @JoinTable(name = "user_group",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id"))
    private List<Group> groups;

    private int state;
    private String name;
    private String phone;
    private String description;

    public User(Long chatId, String name) {
        this.chatId = chatId;
        this.name = name;
        this.state = 0;
    }

    public String toShortString() {
        return name + "\n" +
                phone + "\n" +
                description;
    }

}
