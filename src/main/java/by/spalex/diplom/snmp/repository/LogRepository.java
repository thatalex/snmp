package by.spalex.diplom.snmp.repository;


import by.spalex.diplom.snmp.model.LogItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends JpaRepository<LogItem, Long> {
}
