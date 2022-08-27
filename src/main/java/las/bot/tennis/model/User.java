package las.bot.tennis.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "telegram_user")
public class User {

    @Id
    private Long chatId;

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
