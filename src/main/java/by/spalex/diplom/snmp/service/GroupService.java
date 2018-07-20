package by.spalex.diplom.snmp.service;

import by.spalex.diplom.snmp.model.Group;

import java.util.List;

public interface GroupService {
    List<Group> findAll();

    Group findOne(Long id);

    Group getGroup(String name);

    Group save(Group group);

    Group delete(Long id);
}
