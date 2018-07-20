package by.spalex.diplom.snmp.controller;

import by.spalex.diplom.snmp.model.Role;
import by.spalex.diplom.snmp.model.User;
import by.spalex.diplom.snmp.server.Logger;
import by.spalex.diplom.snmp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.HashSet;

@Controller
public class MainController {
    private final UserService userService;

    @Autowired
    public MainController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = {"/", "/home"}, method = RequestMethod.GET)
    public ModelAndView homeGet() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("home");
        return modelAndView;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ModelAndView homePost() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("home");
        return modelAndView;
    }

    @RequestMapping(value = {"/login"}, method = RequestMethod.GET)
    public ModelAndView login() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        return modelAndView;
    }


    @RequestMapping(value = {"/registration"}, method = RequestMethod.GET)
    public ModelAndView registration() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("user", new User());
        modelAndView.setViewName("registration");
        return modelAndView;
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public ModelAndView registrationPost(@ModelAttribute("passRepeat") String passRepeat,
                                         @ModelAttribute("user") User user,
                                         BindingResult bindingResult,
                                         RedirectAttributes redirectAttributes) {
        ModelAndView modelAndView = new ModelAndView();
        if (!user.getPassword().equals(passRepeat)) {
            bindingResult
                    .rejectValue("password", "validation.passwords.not.equal");
        }
        User userExists = userService.findOneByEmail(user.getEmail());
        if (userExists != null) {
            bindingResult
                    .rejectValue("email", "validation.email.exists");
        }
        if (bindingResult.hasErrors()) {
            modelAndView.addObject("user", user);
            modelAndView.setViewName("registration");
        } else {
            user.setRoles(new HashSet<>(Arrays.asList(Role.USER)));
            userService.save(user);
            modelAndView.addObject("email", user.getEmail());
            modelAndView.addObject("password", passRepeat);
            modelAndView.setViewName("login");
            Logger.INSTANCE.info("User registration: " + user.getEmail());
        }
        return modelAndView;
    }

    @RequestMapping(value = "/about", method = RequestMethod.GET)
    public ModelAndView about() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("about");
        return modelAndView;
    }

    @RequestMapping(value = "/403", method = RequestMethod.GET)
    public ModelAndView error403() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/error/403");
        return modelAndView;
    }


    @RequestMapping(value = "/error", method = RequestMethod.GET)
    public ModelAndView error() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/error");
        return modelAndView;
    }

}
