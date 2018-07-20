package by.spalex.diplom.snmp.repository;

import by.spalex.diplom.snmp.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    Item findOneByAddressAndOid(String address, String oid);

    List<Item> findAllByProbePeriodGreaterThan(Long probePeriod);

    List<Item> findAllByParentItemIsNullAndChildItemsIsNull();


    List<Item> findAllByParentItemIsNull();

    @Query("SELECT i.type FROM Item i GROUP BY i.type ORDER BY i.type")
    List<String> findTypes();
}
