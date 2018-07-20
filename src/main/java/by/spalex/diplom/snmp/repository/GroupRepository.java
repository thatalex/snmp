package by.spalex.diplom.snmp.repository;

import by.spalex.diplom.snmp.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    Group findOneByName(String name);
}
