package by.spalex.diplom.snmp.repository;

import by.spalex.diplom.snmp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findOneByEmail(String email);
}
