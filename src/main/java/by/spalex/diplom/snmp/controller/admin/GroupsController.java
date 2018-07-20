package by.spalex.diplom.snmp.controller.admin;

import by.spalex.diplom.snmp.model.Group;
import by.spalex.diplom.snmp.server.Logger;
import by.spalex.diplom.snmp.service.GroupService;
import by.spalex.diplom.snmp.service.ItemService;
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
public class GroupsController {

    private final ItemService itemService;

    private final GroupService groupService;

    private final MessageSource messageSource;

    @Autowired
    public GroupsController(ItemService itemService, GroupService groupService, MessageSource messageSource) {
        this.itemService = itemService;
        this.groupService = groupService;
        this.messageSource = messageSource;
    }

    @RequestMapping(value = "/admin/groups", method = RequestMethod.GET)
    public ModelAndView getAll() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("page", "groups");
        modelAndView.addObject("groups", groupService.findAll());
        modelAndView.setViewName("admin/home");
        return modelAndView;
    }

    @RequestMapping(value = "/admin/groups/edit", method = RequestMethod.GET)
    public ModelAndView edit(@RequestParam(value = "id", required = false) Long id) {
        ModelAndView modelAndView = new ModelAndView();
        Group group = new Group();
        if (id != null && id > 0) {
            group = groupService.findOne(id);
        }
        modelAndView.addObject("page", "groupedit");
        modelAndView.addObject("items", itemService.findOrphans());
        modelAndView.addObject("group", group);
        modelAndView.setViewName("admin/home");
        return modelAndView;
    }

    @RequestMapping(value = "/admin/groups/edit", method = RequestMethod.POST)
    public ModelAndView save(@ModelAttribute("group") Group group,
                             BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("page", "groupedit");
        modelAndView.addObject("items", itemService.findOrphans());
        if (group.getName().isEmpty()) {
            bindingResult
                    .rejectValue("name", "field.is.empty");
        }
        if (bindingResult.hasErrors()) {
            modelAndView.addObject("group", group);
        } else {
            group = groupService.save(group);
            Logger.INSTANCE.info("Group save: " + group.getName());
            modelAndView.addObject("group", group);
            Locale locale = LocaleContextHolder.getLocale();
            modelAndView.addObject("message", messageSource.getMessage("save.success", new String[]{}, locale));
        }
        modelAndView.setViewName("admin/home");
        return modelAndView;
    }

    @RequestMapping(value = "/admin/groups/copy", method = RequestMethod.POST)
    public ModelAndView copy(@RequestParam(value = "id") Long id) {
        ModelAndView modelAndView = new ModelAndView();
        Group group = groupService.findOne(id);
        if (group != null) {
            Group copy = new Group(group);
            String groupName = copy.getName();
            for (int i = 1; i < 100; i++) {
                String newName = groupName + "_copy_" + i;
                Group group1 = groupService.getGroup(newName);
                if (group1 == null) {
                    copy.setName(newName);
                    break;
                }
            }
            groupService.save(copy);
            Logger.INSTANCE.info("Group copy: " + copy.getName());
        }
        modelAndView.addObject("page", "groups");
        modelAndView.addObject("groups", groupService.findAll());
        modelAndView.setViewName("admin/home");
        return modelAndView;
    }

    @RequestMapping(value = "/admin/groups/delete", method = RequestMethod.POST)
    public ModelAndView delete(@RequestParam(value = "id") Long id) {
        ModelAndView modelAndView = new ModelAndView();

        Group group = groupService.delete(id);
        if (group != null) {
            Logger.INSTANCE.info("Group delete: " + group.getName());
        }
        modelAndView.addObject("page", "groups");
        modelAndView.addObject("groups", groupService.findAll());
        modelAndView.setViewName("admin/home");
        return modelAndView;
    }
}
