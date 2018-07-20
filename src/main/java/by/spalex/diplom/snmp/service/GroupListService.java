package by.spalex.diplom.snmp.service;

import by.spalex.diplom.snmp.model.Group;
import by.spalex.diplom.snmp.model.GroupList;

import java.util.List;

public interface GroupListService {
    List<GroupList> findAll();

    GroupList getGroupList(Long id);

    GroupList getGroupList(String name);

    GroupList delete(Long id);

    GroupList save(GroupList groupList);

    void saveGroupLists(Group group);
}
