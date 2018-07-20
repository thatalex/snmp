package by.spalex.diplom.snmp.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "groups")
public class Group {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "groups")
    private Set<GroupList> groupLists = new HashSet<>();


    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Item> items = new HashSet<>();

    public Group() {
    }

    public Group(Group group) {
        name = group.name;
        items = new HashSet<>(group.items);
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Item> getItems() {
        return items;
    }

    public void setItems(Set<Item> items) {
        this.items = items;
    }

    public Set<GroupList> getGroupLists() {
        return groupLists;
    }

    public void setGroupLists(Set<GroupList> groupLists) {
        this.groupLists = groupLists;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return Objects.equals(id, group.id) &&
                Objects.equals(name, group.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name);
    }
}
