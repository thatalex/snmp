package by.spalex.diplom.snmp.repository;

import by.spalex.diplom.snmp.model.GroupList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupListRepository extends JpaRepository<GroupList, Long> {
    GroupList findOneByName(String name);
}
