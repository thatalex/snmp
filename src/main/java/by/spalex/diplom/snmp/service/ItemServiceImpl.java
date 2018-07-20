package by.spalex.diplom.snmp.service;

import by.spalex.diplom.snmp.model.*;
import by.spalex.diplom.snmp.repository.GroupRepository;
import by.spalex.diplom.snmp.repository.ItemRepository;
import by.spalex.diplom.snmp.server.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("itemService")
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final GroupRepository groupRepository;

    private final GroupListService groupListService;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, GroupRepository groupRepository, GroupListService groupListService) {
        this.itemRepository = itemRepository;
        this.groupRepository = groupRepository;
        this.groupListService = groupListService;
    }

    @Override
    public Item save(Item item) {
        for (Item child : item.getChildItems()) {
            child.setParentItem(item);
        }
        if (!item.getChildItems().isEmpty()) {
            item.makeRoot();
        }
        item.setModifyDate(Util.now());
        return itemRepository.save(item);
    }

    @Override
    public List<Item> findAll(long probePeriod) {
        return itemRepository.findAllByProbePeriodGreaterThan(probePeriod);
    }

    @Override
    public Item findOne(String address, String tableOid) {
        return itemRepository.findOneByAddressAndOid(address, tableOid);
    }

    @Override
    public List<String> findTypes() {
        return itemRepository.findTypes();
    }

    @Override
    public long getItemCount() {
        return itemRepository.count();
    }


    @Override
    public List<Item> findOrphans() {
        return itemRepository.findAllByParentItemIsNull();
    }

    @Override
    public Item findOne(Long id) {
        return itemRepository.findOne(id);
    }

    @Override
    public void save(Set<Item> items) {
        itemRepository.save(items);
    }

    @Override
    public List<Item> findSolitaries() {
        return itemRepository.findAllByParentItemIsNullAndChildItemsIsNull();
    }

    @Override
    public Item delete(Long id) {
        Item item = itemRepository.findOne(id);
        if (item != null) {
            for (Group group : item.getGroups()) {
                groupListService.saveGroupLists(group);
                group.getItems().remove(item);
            }
            groupRepository.save(item.getGroups());
            Item parentItem = item.getParentItem();
            if (parentItem != null) {
                parentItem.getChildItems().remove(item);
                itemRepository.save(parentItem);
            }
            itemRepository.delete(id);

        }
        return item;
    }

    @Override
    public Item getItem(Long id) {
        return itemRepository.getOne(id);
    }

    @Override
    public List<Item> findAll(Filter filter) {
        List<Item> list;
        if (filter.isIncludeChilds()) {
            list = itemRepository.findAll();
        } else {
            list = itemRepository.findAllByParentItemIsNull();
        }
        return filter(filter, list);
    }

    @Override
    public List<Item> findAllByUser(User user, Filter filter) {
        Set<Item> items = new HashSet<>();
        if (user != null) {
            for (GroupList groupList : user.getGroupLists()) {
                addGroupItems(filter.isIncludeChilds(), items, groupList.getGroups());
            }
            addGroupItems(filter.isIncludeChilds(), items, user.getGroups());
            return filter(filter, items);
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public Item removeParent(Long id) {
        Item item = itemRepository.findOne(id);
        if (item != null && item.getParentItem() != null) {
            Item parentItem = item.getParentItem();
            parentItem.getChildItems().remove(item);
            item.setParentItem(null);
            itemRepository.save(parentItem);
            itemRepository.save(item);
        }
        return item;
    }

    @Override
    public Item removeChildren(Long id) {
        Item item = itemRepository.findOne(id);
        if (item != null) {
            for (Item child : item.getChildItems()) {
                child.setParentItem(null);
            }
            itemRepository.save(item.getChildItems());
            item.getChildItems().clear();
        }
        return item;
    }

    private List<Item> filter(Filter filter, Collection<Item> list) {
        List<Item> filtered = new ArrayList<>();
        for (Item item : list) {
            if (!filter.getName().isEmpty() && !item.getName().contains(filter.getName())) {
                continue;
            }
            if (!filter.getAddress().isEmpty() && !item.getAddress().contains(filter.getAddress())) {
                continue;
            }
            if (filter.getType() != null && !filter.getType().isEmpty() && !item.getType().equals(filter.getType())) {
                continue;
            }
            if (filter.getCreateDateBegin() != null && item.getCreateDate().isBefore(filter.getCreateDateBegin())) {
                continue;
            }
            if (filter.getCreateDateEnd() != null && item.getCreateDate().isAfter(filter.getCreateDateEnd())) {
                continue;
            }
            if (filter.getModifyDateBegin() != null && item.getModifyDate().isBefore(filter.getModifyDateBegin())) {
                continue;
            }
            if (filter.getModifyDateEnd() != null && item.getModifyDate().isAfter(filter.getModifyDateEnd())) {
                continue;
            }
            filtered.add(item);
        }
        return filtered;
    }


    private void addGroupItems(boolean includeChilds, Set<Item> items, Set<Group> groups) {
        for (Group group : groups) {
            items.addAll(group.getItems());
            if (includeChilds) {
                for (Item grItem : group.getItems()) {
                    items.addAll(grItem.getChildItems());
                }
            }
        }
    }
}
