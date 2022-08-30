package las.bot.tennis.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "telegram_user")
public class User {

    @Id
    private Long chatId;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_group",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id"))
    private Set<Group> groups;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private UserContext context;

    private String name;
    private String phone;
    private String description;

    public User(Long chatId, String name) {
        this.chatId = chatId;
        this.name = name;
        this.context = new UserContext(chatId);
        this.context.setUser(this);
    }

    public String toShortString() {
        StringBuilder desc = new StringBuilder(name).append("\n")
                .append("\uD83D\uDCDE ").append(phone).append("\n")
                .append(description).append("\n\n")
                .append("Состоит в группах:");
        if (groups.size() == 0) {
            desc.append(" (пусто)");
        } else {
            for (Group group : groups) {
                desc.append("\n\uD83C\uDFBE ").append(group.getName());
            }
        }

        return desc.toString();
    }

}
