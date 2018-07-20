package by.spalex.diplom.snmp.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "list")
public class GroupList {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "groupLists")
    private Set<User> users = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Group> groups = new HashSet<>();

    public GroupList() {
    }

    public GroupList(GroupList groupList) {
        name = groupList.name;
        groups = new HashSet<>(groups);
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

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupList groupList = (GroupList) o;
        return Objects.equals(id, groupList.id) &&
                Objects.equals(name, groupList.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name);
    }
}
