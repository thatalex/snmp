package by.spalex.diplom.snmp.repository;

import by.spalex.diplom.snmp.model.OidInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OidRepository extends JpaRepository<OidInfo, Long> {
    List<OidInfo> findAllByActiveIsTrue();
}
