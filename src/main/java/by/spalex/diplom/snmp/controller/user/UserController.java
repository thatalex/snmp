package by.spalex.diplom.snmp.controller.user;

import by.spalex.diplom.snmp.model.*;
import by.spalex.diplom.snmp.server.Logger;
import by.spalex.diplom.snmp.service.ItemService;
import by.spalex.diplom.snmp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
public class UserController {

    private final UserService userService;

    private final ItemService itemService;

    @Autowired
    public UserController(UserService userService, ItemService itemService) {
        this.userService = userService;
        this.itemService = itemService;
    }

    @RequestMapping(value = {"/user", "/user/home"}, method = RequestMethod.GET)
    public ModelAndView home() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("user/home");
        return modelAndView;
    }

    @RequestMapping(value = "/user/lists", method = RequestMethod.GET)
    public ModelAndView lists(Principal principal) {
        ModelAndView modelAndView = new ModelAndView();
        User user = userService.findOneByEmail(principal.getName());
        if (user != null) {
            modelAndView.addObject("lists", user.getGroupLists());
        }
        modelAndView.setViewName("user/lists");
        return modelAndView;
    }


    @RequestMapping(value = "/user/lists", params = "id", method = RequestMethod.GET)
    public ModelAndView lists(@RequestParam("id") Long id, Principal principal) {
        ModelAndView modelAndView = new ModelAndView();
        User user = userService.findOneByEmail(principal.getName());
        if (id != null && user != null) {
            for (GroupList groupList : user.getGroupLists()) {
                if (id.equals(groupList.getId())) {
                    modelAndView.addObject("list", groupList);
                    break;
                }
            }
        }
        modelAndView.setViewName("user/listgroups");
        return modelAndView;
    }

    @RequestMapping(value = {"/user/listgroups", "/user/groups"}, params = "id", method = RequestMethod.GET)
    public ModelAndView listgroups(@RequestParam("id") Long id, Principal principal) {
        ModelAndView modelAndView = new ModelAndView();
        User user = userService.findOneByEmail(principal.getName());
        exit:
        if (id != null && user != null) {
            for (GroupList groupList : user.getGroupLists()) {
                for (Group group : groupList.getGroups()) {
                    if (id.equals(group.getId())) {
                        modelAndView.addObject("group", group);
                        break exit;
                    }
                }
            }
            for (Group group : user.getGroups()) {
                if (id.equals(group.getId())) {
                    modelAndView.addObject("group", group);
                    break exit;
                }
            }
        }
        modelAndView.setViewName("user/items");
        return modelAndView;
    }

    private Item getItem(Set<Item> items, Long id) {
        for (Item item : items) {
            if (id.equals(item.getId())) {
                return item;
            } else {
                for (Item child : item.getChildItems()) {
                    if (id.equals(child.getId())) {
                        return child;
                    }
                }
            }
        }
        return null;
    }

    @RequestMapping(value = "/user/items", params = "id", method = RequestMethod.GET)
    public ModelAndView itemview(@RequestParam("id") Long id, Principal principal) {
        ModelAndView modelAndView = new ModelAndView();
        User user = userService.findOneByEmail(principal.getName());
        Item item = getItem(id, user);
        modelAndView.addObject("item", item);
        modelAndView.setViewName("user/itemview");
        return modelAndView;
    }


    @RequestMapping(value = "/user/items/edit", method = RequestMethod.POST)
    public ModelAndView save(@ModelAttribute("item") Item newItem, Principal principal) {
        ModelAndView modelAndView = new ModelAndView();
        User user = userService.findOneByEmail(principal.getName());
        Item item = getItem(newItem.getId(), user);
        if (item != null) {
            item.setName(newItem.getName());
            item.setProbePeriod(newItem.getProbePeriod());
            itemService.save(item);
            Logger.INSTANCE.info("Change name or probe period of element - " + item.getAddress() + ": " + item.getOid());
        }
        modelAndView.addObject("item", item != null ? item : newItem);
        modelAndView.setViewName("user/itemview");
        return modelAndView;
    }

    private Item getItem(@RequestParam("id") Long id, User user) {
        Item item = null;
        exit:
        if (id != null && user != null) {
            for (GroupList groupList : user.getGroupLists()) {
                for (Group group : groupList.getGroups()) {
                    item = getItem(group.getItems(), id);
                    if (item != null) {
                        break exit;
                    }
                }
            }
            for (Group group : user.getGroups()) {
                item = getItem(group.getItems(), id);
                if (item != null) {
                    break exit;
                }
            }
        }
        return item;
    }


    @RequestMapping(value = "/user/groups", method = RequestMethod.GET)
    public ModelAndView groups(Principal principal) {
        ModelAndView modelAndView = new ModelAndView();
        User user = userService.findOneByEmail(principal.getName());
        if (user != null) {
            modelAndView.addObject("user", user);
        }
        modelAndView.setViewName("user/groups");
        return modelAndView;
    }

    @RequestMapping(value = "/user/report", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView filter(@ModelAttribute("filter") Filter filter, Principal principal) {
        ModelAndView modelAndView = new ModelAndView();
        Logger.INSTANCE.info("Url request: /user/report");
        User user = userService.findOneByEmail(principal.getName());
        List<Item> filtered = itemService.findAllByUser(user, filter);
        modelAndView.addObject("filter", new Filter());
        setTypesObject(modelAndView);
        modelAndView.addObject("items", filtered);
        modelAndView.setViewName("user/report");
        return modelAndView;
    }


    private void setTypesObject(ModelAndView modelAndView) {
        List<String> types = new ArrayList<>();
        types.add("");
        types.addAll(itemService.findTypes());
        modelAndView.addObject("types", types);
    }


    @RequestMapping(value = "/user/report/view", params = "id", method = RequestMethod.GET)
    public ModelAndView report(@RequestParam("id") Long id, Principal principal) {
        ModelAndView modelAndView = new ModelAndView();
        Logger.INSTANCE.info("Url request: /user/reportview");
        User user = userService.findOneByEmail(principal.getName());
        Item item = getItem(id, user);
        modelAndView.addObject("item", item);
        if (item.getChildItems().isEmpty()) {
            modelAndView.setViewName("user/probes");
        } else {
            modelAndView.setViewName("user/reportview");
        }
        return modelAndView;
    }


    @RequestMapping(value = "/user/probes/view", params = "id", method = RequestMethod.GET)
    public ModelAndView probes(@RequestParam("id") Long id, Principal principal) {
        ModelAndView modelAndView = new ModelAndView();
        Logger.INSTANCE.info("Url request: /user/probes");
        User user = userService.findOneByEmail(principal.getName());
        Item item = getItem(id, user);
        if (item != null) {
            modelAndView.addObject("item", item);
        }
        modelAndView.setViewName("user/probes");
        return modelAndView;
    }
}
