package by.spalex.diplom.snmp.controller.admin;

import by.spalex.diplom.snmp.model.GroupList;
import by.spalex.diplom.snmp.server.Logger;
import by.spalex.diplom.snmp.service.GroupListService;
import by.spalex.diplom.snmp.service.GroupService;
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
public class ListsController {

    private final GroupListService groupListService;

    private final GroupService groupService;

    private final MessageSource messageSource;

    @Autowired
    public ListsController(GroupListService groupListService, GroupService groupService, MessageSource messageSource) {
        this.groupListService = groupListService;
        this.groupService = groupService;
        this.messageSource = messageSource;
    }

    @RequestMapping(value = "/admin/lists", method = RequestMethod.GET)
    public ModelAndView lists() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("page", "lists");
        modelAndView.addObject("lists", groupListService.findAll());
        modelAndView.setViewName("admin/home");
        return modelAndView;
    }


    @RequestMapping(value = "/admin/lists/edit", method = RequestMethod.GET)
    public ModelAndView listEdit(@RequestParam(value = "id", required = false) Long id) {
        ModelAndView modelAndView = new ModelAndView();
        GroupList groupList = new GroupList();
        if (id != null && id > 0) {
            groupList = groupListService.getGroupList(id);
        }
        modelAndView.addObject("page", "listedit");
        modelAndView.addObject("groups", groupService.findAll());
        modelAndView.addObject("list", groupList);
        modelAndView.setViewName("admin/home");
        return modelAndView;
    }

    @RequestMapping(value = "/admin/lists/edit", method = RequestMethod.POST)
    public ModelAndView listSave(@ModelAttribute("list") GroupList list,
                                 BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("page", "listedit");
        modelAndView.addObject("groups", groupService.findAll());
        if (list.getName().isEmpty()) {
            bindingResult
                    .rejectValue("name", "field.is.empty");
        }
        if (bindingResult.hasErrors()) {
            modelAndView.addObject("list", list);
        } else {
            list = groupListService.save(list);
            Logger.INSTANCE.info("List save: " + list.getName());
            modelAndView.addObject("list", list);
            Locale locale = LocaleContextHolder.getLocale();
            modelAndView.addObject("message",
                    messageSource.getMessage("save.success", new String[]{}, locale));
        }
        modelAndView.setViewName("admin/home");
        return modelAndView;
    }

    @RequestMapping(value = "/admin/lists/copy", method = RequestMethod.POST)
    public ModelAndView copy(@RequestParam(value = "id") Long id) {
        ModelAndView modelAndView = new ModelAndView();
        GroupList groupList = groupListService.getGroupList(id);
        if (groupList != null) {
            GroupList copy = new GroupList(groupList);
            String name = copy.getName();
            for (int i = 1; i < 100; i++) {
                String newName = name + "_copy_" + i;
                GroupList groupList1 = groupListService.getGroupList(newName);
                if (groupList1 == null) {
                    copy.setName(newName);
                    break;
                }
            }
            groupListService.save(copy);
            Logger.INSTANCE.info("List copy: " + copy.getName());
        }
        modelAndView.addObject("page", "lists");
        modelAndView.addObject("lists", groupListService.findAll());
        modelAndView.setViewName("admin/home");
        return modelAndView;
    }

    @RequestMapping(value = "/admin/lists/delete", method = RequestMethod.POST)
    public ModelAndView listDelete(@RequestParam(value = "id") Long id) {
        ModelAndView modelAndView = new ModelAndView();
        GroupList groupList = groupListService.delete(id);
        if (groupList != null) {
            Logger.INSTANCE.info("List delete: " + groupList.getName());
        }
        modelAndView.addObject("page", "lists");
        modelAndView.addObject("lists", groupListService.findAll());
        modelAndView.setViewName("admin/home");
        return modelAndView;
    }
}
