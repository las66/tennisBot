package las.bot.tennis.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "groups")
public class Group {

    @Id
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_group",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> users;

    public Group(String name) {
        this.name = name;
    }

    public String toShortString() {
        return name + " (" + users.size() + " клиентов)";
    }

}
