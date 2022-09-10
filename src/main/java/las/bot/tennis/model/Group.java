package las.bot.tennis.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "groups")
public class Group {

    @Id
    private String name;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "groups")
    private List<User> users;

    public Group(String name) {
        this.name = name;
    }

    public String toShortString() {
        return name + " (" + users.size() + " человек)";
    }

    public String toLongString() {
        StringBuilder str = new StringBuilder("Группа ").append(name).append(":\n");
        if (users.isEmpty()) {
            str.append("(пусто)");
        } else {
            for (int i = 1; i <= users.size(); i++) {
                str.append(i).append(". ").append(users.get(i - 1).toOneLineString()).append("\n");
            }
        }
        return str.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Group)) {
            return false;
        }
        return this.name.equals(((Group) obj).name);
    }

    @Override
    public int hashCode() {
        if (name == null) {
            return 0;
        }
        return name.hashCode();
    }
}
