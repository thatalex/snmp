package by.spalex.diplom.snmp.service;

import by.spalex.diplom.snmp.model.User;
import by.spalex.diplom.snmp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service("userService")
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public User save(User user) {
        if (user.getPassword().isEmpty()) {
            if (user.getId() != null && user.getId() > 0) {
                user.setPassword(userRepository.findOne(user.getId()).getPassword());
            }
        } else {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    @Override
    public User findOneByEmail(String email) {
        return userRepository.findOneByEmail(email);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void save(Set<User> users) {
        userRepository.save(users);
    }

    @Override
    public User findOne(Long id) {
        return userRepository.findOne(id);
    }

    @Override
    public void delete(Long id) {
        userRepository.delete(id);
    }
}
