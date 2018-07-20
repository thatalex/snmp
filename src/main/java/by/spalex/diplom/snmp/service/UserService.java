package by.spalex.diplom.snmp.service;

import by.spalex.diplom.snmp.model.User;

import java.util.List;
import java.util.Set;

public interface UserService {
    User save(User user);

    User findOneByEmail(String email);

    List<User> findAll();

    void save(Set<User> users);

    User findOne(Long id);

    void delete(Long id);
}
