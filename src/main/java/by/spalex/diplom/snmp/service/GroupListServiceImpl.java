package by.spalex.diplom.snmp.service;

import by.spalex.diplom.snmp.model.Group;
import by.spalex.diplom.snmp.model.GroupList;
import by.spalex.diplom.snmp.model.User;
import by.spalex.diplom.snmp.repository.GroupListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("groupListService")
public class GroupListServiceImpl implements GroupListService {
    private final GroupListRepository groupListRepository;

    private final UserService userService;

    @Autowired
    public GroupListServiceImpl(GroupListRepository groupListRepository, UserService userService) {
        this.groupListRepository = groupListRepository;
        this.userService = userService;
    }

    @Override
    public List<GroupList> findAll() {
        return groupListRepository.findAll();
    }

    @Override
    public GroupList getGroupList(Long id) {
        return groupListRepository.findOne(id);
    }

    @Override
    public GroupList getGroupList(String name) {
        return groupListRepository.findOneByName(name);
    }

    @Override
    public GroupList delete(Long id) {
        GroupList groupList = groupListRepository.getOne(id);
        if (groupList != null) {
            for (User user : groupList.getUsers()) {
                user.getGroupLists().remove(groupList);
            }
            userService.save(groupList.getUsers());
            groupListRepository.delete(id);
        }
        return groupList;
    }

    @Override
    public GroupList save(GroupList groupList) {
        return groupListRepository.save(groupList);
    }

    @Override
    public void saveGroupLists(Group group) {
        for (GroupList groupList : group.getGroupLists()) {
            for (User user : groupList.getUsers()) {
                user.getGroupLists().remove(groupList);
            }
            userService.save(groupList.getUsers());
            groupList.getGroups().remove(group);
        }
        groupListRepository.save(group.getGroupLists());
    }
}
