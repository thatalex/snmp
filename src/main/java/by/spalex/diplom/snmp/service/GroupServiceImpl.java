package by.spalex.diplom.snmp.service;

import by.spalex.diplom.snmp.model.Group;
import by.spalex.diplom.snmp.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("groupService")
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;

    private final GroupListService groupListService;

    @Autowired
    public GroupServiceImpl(GroupRepository groupRepository, GroupListService groupListService) {
        this.groupRepository = groupRepository;
        this.groupListService = groupListService;
    }

    @Override
    public List<Group> findAll() {
        return groupRepository.findAll();
    }

    @Override
    public Group findOne(Long id) {
        return groupRepository.findOne(id);
    }

    @Override
    public Group getGroup(String name) {
        return groupRepository.findOneByName(name);
    }

    @Override
    public Group save(Group group) {
        return groupRepository.save(group);
    }

    @Override
    public Group delete(Long id) {
        Group group = groupRepository.findOne(id);
        groupListService.saveGroupLists(group);
        groupRepository.delete(group);
        return group;
    }
}
