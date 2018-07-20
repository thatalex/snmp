package by.spalex.diplom.snmp.controller;

import by.spalex.diplom.snmp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ApiController {

    private final UserService userService;

    @Autowired
    public ApiController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/api/users", method = RequestMethod.GET)
    public List getAllUsers() {
        return userService.findAll();
    }
}
