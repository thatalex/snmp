package by.spalex.diplom.snmp.controller.admin;

import by.spalex.diplom.snmp.model.Role;
import by.spalex.diplom.snmp.model.User;
import by.spalex.diplom.snmp.server.Logger;
import by.spalex.diplom.snmp.service.GroupListService;
import by.spalex.diplom.snmp.service.GroupService;
import by.spalex.diplom.snmp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Locale;

@Controller
public class UsersController {

    private final UserService userService;

    private final GroupListService groupListService;

    private final GroupService groupService;

    private final MessageSource messageSource;

    @Autowired
    public UsersController(UserService userService, GroupListService groupListService, GroupService groupService, MessageSource messageSource) {
        this.userService = userService;
        this.groupListService = groupListService;
        this.groupService = groupService;
        this.messageSource = messageSource;
    }

    @RequestMapping(value = {"/admin", "/admin/home"}, method = RequestMethod.GET)
    public ModelAndView home() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin/home");
        return modelAndView;
    }

    @RequestMapping(value = "/admin/users", method = RequestMethod.GET)
    public ModelAndView users() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("page", "users");
        modelAndView.addObject("users", userService.findAll());
        modelAndView.setViewName("admin/home");
        return modelAndView;
    }


    @RequestMapping(value = "/admin/users/edit", method = RequestMethod.GET)
    public ModelAndView userGet(@RequestParam(value = "id", required = false) Long id) {
        ModelAndView modelAndView = new ModelAndView();
        User user = new User();
        if (id != null && id > 0) {
            user = userService.findOne(id);
        }
        modelAndView.addObject("page", "useredit");
        modelAndView.addObject("lists", groupListService.findAll());
        modelAndView.addObject("groups", groupService.findAll());
        modelAndView.addObject("roles", Role.values());
        modelAndView.addObject("user", user);
        modelAndView.setViewName("admin/home");
        return modelAndView;
    }

    @RequestMapping(value = "/admin/users/edit", method = RequestMethod.POST)
    public ModelAndView userPost(@ModelAttribute("passRepeat") String passRepeat,
                                 @ModelAttribute("user") User user,
                                 BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("page", "useredit");
        modelAndView.addObject("lists", groupListService.findAll());
        modelAndView.addObject("groups", groupService.findAll());
        modelAndView.addObject("roles", Role.values());
        if (user.getEmail().isEmpty()) {
            bindingResult
                    .rejectValue("email", "field.is.empty");
        }
        if (user.getName().isEmpty()) {
            bindingResult
                    .rejectValue("name", "field.is.empty");
        }
        if (!user.getPassword().equals(passRepeat)) {
            bindingResult
                    .rejectValue("password", "validation.passwords.not.equal");
        }
        if (bindingResult.hasErrors()) {
            modelAndView.addObject("user", user);
        } else {

            user = userService.save(user);
            Logger.INSTANCE.info("User save: " + user.getEmail());
            modelAndView.addObject("user", user);
            Locale locale = LocaleContextHolder.getLocale();
            modelAndView.addObject("message",
                    messageSource.getMessage("save.success", new String[]{}, locale));
        }
        modelAndView.setViewName("admin/home");
        return modelAndView;
    }

    @RequestMapping(value = "/admin/users/delete", method = RequestMethod.POST)
    public ModelAndView userDelete(@RequestParam(value = "id") Long id) {
        ModelAndView modelAndView = new ModelAndView();
        userService.delete(id);
        Logger.INSTANCE.info("User delete: " + id);
        modelAndView.addObject("page", "users");
        modelAndView.addObject("users", userService.findAll());
        modelAndView.setViewName("admin/home");
        return modelAndView;
    }

}
