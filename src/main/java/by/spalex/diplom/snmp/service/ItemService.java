package by.spalex.diplom.snmp.service;

import by.spalex.diplom.snmp.model.Filter;
import by.spalex.diplom.snmp.model.Item;
import by.spalex.diplom.snmp.model.User;

import java.util.List;
import java.util.Set;

public interface ItemService {
    Item save(Item valueItem);

    /**
     * retrieves items with probe period greater than given in argument
     *
     * @param probePeriod probe period for comparing
     * @return list of {@link Item} with probe period greater than given in argument
     */
    List<Item> findAll(long probePeriod);

    /**
     * retrieves item by given arguments
     *
     * @param address  network address of item
     * @param tableOid oid of item
     * @return {@link Item}
     */
    Item findOne(String address, String tableOid);

    List<String> findTypes();

    long getItemCount();

    /**
     * retrieves items without parents
     *
     * @return list of {@link Item}
     */
    List<Item> findOrphans();

    Item findOne(Long id);

    void save(Set<Item> items);

    /**
     * retrieves items without parents and children
     *
     * @return list of {@link Item}
     */
    List<Item> findSolitaries();

    Item delete(Long id);

    Item getItem(Long id);

    /**
     * retrieves items corresponding to the filter parameters
     *
     * @param filter {@link Filter}
     * @return list of {@link Item}
     */
    List<Item> findAll(Filter filter);

    /**
     * retrieves items corresponding to the filter parameters and linked to given user
     *
     * @param user   {@link User}
     * @param filter {@link Filter}
     * @return list of {@link Item}
     */
    List<Item> findAllByUser(User user, Filter filter);

    /**
     * remove parent from item with given id
     *
     * @param id identity of child item
     * @return child item without parent
     */
    Item removeParent(Long id);

    /**
     * remove children from item with given id
     *
     * @param id identity of parent item
     * @return parent item without children
     */
    Item removeChildren(Long id);
}
