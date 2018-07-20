package by.spalex.diplom.snmp.controller.admin;

import by.spalex.diplom.snmp.model.Filter;
import by.spalex.diplom.snmp.model.Item;
import by.spalex.diplom.snmp.server.Logger;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Controller
public class ItemsController {

    private final ItemService itemService;

    private final MessageSource messageSource;

    @Autowired
    public ItemsController(ItemService itemService, MessageSource messageSource) {
        this.itemService = itemService;
        this.messageSource = messageSource;
    }

    private void setTypesObject(ModelAndView modelAndView) {
        List<String> types = new ArrayList<>();
        types.add("");
        types.addAll(itemService.findTypes());
        modelAndView.addObject("types", types);
    }

    @RequestMapping(value = "/admin/items/edit", method = RequestMethod.GET)
    public ModelAndView edit(@RequestParam(value = "id", required = false) Long id) {
        ModelAndView modelAndView = new ModelAndView();
        Item item = new Item();
        if (id != null && id > 0) {
            item = itemService.findOne(id);
        }
        modelAndView.addObject("page", "itemedit");
        addFreeItems(modelAndView, item);
        modelAndView.addObject("item", item);
        modelAndView.setViewName("admin/home");
        return modelAndView;
    }

    @RequestMapping(value = "/admin/items/unparent", params = "id", method = RequestMethod.GET)
    public ModelAndView unparent(@RequestParam(name = "id") Long id) {
        ModelAndView modelAndView = new ModelAndView();
        Item item = new Item();
        if (id != null && id > 0) {

            item = itemService.removeParent(id);
        }
        modelAndView.addObject("page", "itemedit");
        addFreeItems(modelAndView, item);
        modelAndView.addObject("item", item);
        modelAndView.setViewName("admin/home");
        return modelAndView;
    }

    @RequestMapping(value = "/admin/items/unсhild", params = "id", method = RequestMethod.GET)
    public ModelAndView unchild(@RequestParam(name = "id") Long id) {
        ModelAndView modelAndView = new ModelAndView();
        Item item = new Item();
        if (id != null && id > 0) {
            item = itemService.removeChildren(id);
        }
        modelAndView.addObject("page", "itemedit");
        addFreeItems(modelAndView, item);
        modelAndView.addObject("item", item);
        modelAndView.setViewName("admin/home");
        return modelAndView;
    }


    private void addFreeItems(ModelAndView modelAndView, Item item) {
        List<Item> items = item.getParentItem() == null ?
                itemService.findSolitaries() : Collections.EMPTY_LIST;
        items.remove(item);
        modelAndView.addObject("items", items);
    }


    @RequestMapping(value = "/admin/items/edit", method = RequestMethod.POST)
    public ModelAndView save(@ModelAttribute("item") Item item,
                             BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("page", "itemedit");
        addFreeItems(modelAndView, item);
        if (item.getName().isEmpty()) {
            bindingResult
                    .rejectValue("name", "field.is.empty");
        }
        if (item.getAddress().isEmpty()) {
            bindingResult
                    .rejectValue("address", "field.is.empty");
        }
        if (bindingResult.hasErrors()) {
            modelAndView.addObject("item", item);
        } else {

            item = itemService.save(item);
            Logger.INSTANCE.info("Element save: " + item.getName());
            modelAndView.addObject("item", item);
            Locale locale = LocaleContextHolder.getLocale();
            modelAndView.addObject("message",
                    messageSource.getMessage("save.success", new String[]{}, locale));
        }
        modelAndView.setViewName("admin/home");
        return modelAndView;
    }

    @RequestMapping(value = "/admin/items/delete", method = RequestMethod.POST)
    public ModelAndView delete(@RequestParam(value = "id") Long id) {
        ModelAndView modelAndView = new ModelAndView();
        Item item = itemService.delete(id);
        if (item != null) {
            Logger.INSTANCE.info("Element delete: " + item.getName());
        }
        modelAndView.addObject("page", "items");
        modelAndView.addObject("filter", new Filter());
        setTypesObject(modelAndView);
        modelAndView.addObject("items", itemService.findOrphans());
        modelAndView.setViewName("admin/home");
        return modelAndView;
    }


    @RequestMapping(value = "/admin/items", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView filter(@ModelAttribute("filter") Filter filter) {
        ModelAndView modelAndView = new ModelAndView();
        List<Item> filtered = itemService.findAll(filter);
        modelAndView.addObject("page", "items");
        setTypesObject(modelAndView);
        modelAndView.addObject("filter", filter);
        modelAndView.addObject("items", filtered);
        modelAndView.setViewName("admin/home");
        return modelAndView;
    }

    @RequestMapping(value = "/admin/items/view", params = "id", method = RequestMethod.GET)
    public ModelAndView report(@RequestParam("id") Long id) {
        ModelAndView modelAndView = new ModelAndView("admin/home");
        Item item = itemService.getItem(id);
        if (item != null) {
            modelAndView.addObject("item", item);
        }
        if (item != null && (item.getChildItems() == null || item.getChildItems().isEmpty())) {
            modelAndView.addObject("page", "probes");
        } else {
            modelAndView.addObject("page", "item_state");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/admin/probes/view", params = "id", method = RequestMethod.GET)
    public ModelAndView probes(@RequestParam("id") Long id) {
        ModelAndView modelAndView = new ModelAndView("admin/home");
        Item item = itemService.getItem(id);
        if (item != null) {
            modelAndView.addObject("item", item);
        }
        modelAndView.addObject("page", "probes");
        return modelAndView;
    }
}
