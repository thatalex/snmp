package by.spalex.diplom.snmp.controller.admin;

import by.spalex.diplom.snmp.model.LogItem;
import by.spalex.diplom.snmp.model.OidInfo;
import by.spalex.diplom.snmp.model.User;
import by.spalex.diplom.snmp.server.Logger;
import by.spalex.diplom.snmp.server.Util;
import by.spalex.diplom.snmp.server.snmp.Walker;
import by.spalex.diplom.snmp.service.ItemService;
import by.spalex.diplom.snmp.service.LogService;
import by.spalex.diplom.snmp.service.OidService;
import by.spalex.diplom.snmp.service.UserService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.time.temporal.ChronoUnit.SECONDS;

@Controller
public class AdminController {

    private static final String BACKUP_FILENAME = "backup.sql";

    private static final String OIDS_FILENAME = "OIDs.txt";

    private final UserService userService;

    private final ItemService itemService;

    private final LogService logService;

    private final OidService oidService;

    private final MessageSource messageSource;

    private final ScheduledExecutorService scheduledExecutorService;

    private final JavaMailSender emailSender;
    private final EntityManager entityManager;
    private String startAddress = "127.0.0.1";
    private String endAddress = "127.0.0.2";

    @Autowired
    public AdminController(UserService userService, ItemService itemService, LogService logService,
                           OidService oidService, MessageSource messageSource,
                           ScheduledExecutorService scheduledExecutorService, JavaMailSender javaMailSender, EntityManager entityManager) {
        this.userService = userService;
        this.itemService = itemService;
        this.logService = logService;
        this.oidService = oidService;
        this.messageSource = messageSource;
        this.scheduledExecutorService = scheduledExecutorService;
        this.emailSender = javaMailSender;
        this.entityManager = entityManager;
    }

    @RequestMapping(value = "/admin/maintance", method = RequestMethod.GET)
    public ModelAndView maintance() {
        ModelAndView modelAndView = new ModelAndView();
        addObjects(modelAndView);
        modelAndView.setViewName("admin/home");
        return modelAndView;
    }

    private void addObjects(ModelAndView modelAndView) {
        modelAndView.addObject("page", "maintance");
        modelAndView.addObject("datetime", Util.now());
        modelAndView.addObject("start_address", startAddress);
        modelAndView.addObject("end_address", endAddress);
    }

    @RequestMapping(value = "/admin/logs", method = RequestMethod.GET)
    public ModelAndView logs() {
        ModelAndView modelAndView = new ModelAndView();
        addObjects(modelAndView);
        List<LogItem> logItems = logService.findAll();
        Util.sort(logItems);
        modelAndView.addObject("logitems", logItems);
        modelAndView.setViewName("admin/home");
        return modelAndView;
    }

    @RequestMapping(value = "/admin/backup", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void getFile(HttpServletResponse response) {
        Session session = entityManager.unwrap(Session.class);
        session.doWork(connection -> {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(String.format("SCRIPT TO '%s'", BACKUP_FILENAME));
                File file = new File(BACKUP_FILENAME);
                if (file.exists()) {
                    response.setContentType("application/x-msdownload");
                    response.setHeader("Content-disposition", "attachment; filename=" + BACKUP_FILENAME);
                    try (InputStream is = new FileInputStream(file)) {
                        IOUtils.copy(is, response.getOutputStream());
                        response.flushBuffer();
                        Logger.INSTANCE.info("Database was backuped");
                    } catch (IOException ex) {
                        Logger.INSTANCE.error(ex.getMessage());
                    }
                }
            } catch (SQLException e) {
                Logger.INSTANCE.error(e.getMessage());
            }
        });
    }


    @RequestMapping(value = "/admin/oids", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public List<OidInfo> getOids(HttpServletResponse response) {
        response.setContentType("application/x-msdownload");
        response.setHeader("Content-disposition", "attachment; filename=" + OIDS_FILENAME);
        return new ArrayList<>(oidService.findAll());
    }

    @PostMapping("/admin/upload/oids")
    public ModelAndView uploadOids(@RequestParam("file") MultipartFile file) {
        ModelAndView modelAndView = new ModelAndView();
        ObjectMapper objectMapper = new ObjectMapper();
        addObjects(modelAndView);
        OidInfo[] oidInfos;
        try (InputStream inputStream = file.getInputStream()) {
            oidInfos = objectMapper.readValue(inputStream, OidInfo[].class);
            oidService.setOidInfos(Arrays.asList(oidInfos));
            Logger.INSTANCE.info("Upload new OIDs list");
            Locale locale = LocaleContextHolder.getLocale();
            modelAndView.addObject("message", messageSource.getMessage("oids.upload.complete", new String[]{}, locale));
        } catch (Exception e) {
            modelAndView.addObject("message", "Upload error: " + e.toString());
            Logger.INSTANCE.error(e.toString());
        }
        modelAndView.setViewName("admin/home");
        return modelAndView;
    }

    @RequestMapping(value = "/admin/search", method = RequestMethod.POST)
    public ModelAndView search(@ModelAttribute(name = "start_address") String startAddress,
                               @ModelAttribute(name = "end_address") String endAddress) {
        ModelAndView modelAndView = new ModelAndView();

        this.startAddress = startAddress;
        this.endAddress = endAddress;
        addObjects(modelAndView);
        Locale locale = LocaleContextHolder.getLocale();
        List<String> addresses = Util.getSNMPAddresses(startAddress, endAddress);
        if (addresses.isEmpty()) {
            modelAndView.addObject("message", messageSource.getMessage("range.not.valid", new String[]{}, locale));
        } else {
            Walker walker = new Walker(itemService);
            try {
                walker.scan(addresses, oidService.findAllActive());
                Logger.INSTANCE.info("Search complete");
                modelAndView.addObject("message", messageSource.getMessage("search.complete", new String[]{}, locale));
            } catch (Exception e) {
                Logger.INSTANCE.error(e.toString());
            }
        }
        modelAndView.setViewName("admin/home");
        return modelAndView;
    }


    @RequestMapping(value = "/admin/schedulle", method = RequestMethod.POST)
    public ModelAndView schedulle(@ModelAttribute(name = "start_address") String startAddress,
                                  @ModelAttribute(name = "end_address") String endAddress,
                                  @ModelAttribute(name = "datetime") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime date) {
        ModelAndView modelAndView = new ModelAndView();
        this.startAddress = startAddress;
        this.endAddress = endAddress;
        addObjects(modelAndView);
        Locale locale = LocaleContextHolder.getLocale();
        Logger.INSTANCE.info("Search schedule");
        LocalDateTime current = Util.now();
        if (date != null) {
            long seconds = SECONDS.between(current, date);
            if (seconds > 0) {
                scheduledExecutorService.schedule(() -> {
                    Logger.INSTANCE.info("Scheduled search successfully completed");
                    long count = itemService.getItemCount();
                    for (User user : userService.findAll()) {
                        SimpleMailMessage message = new SimpleMailMessage();
                        message.setTo(user.getEmail());
                        message.setSubject("Elements search");
                        message.setText("Scheduled searching successfully completed. Total count of elements is " + count);
                        try {
                            emailSender.send(message);
                        } catch (Exception e) {
                            Logger.INSTANCE.error(e.getMessage());
                        }
                    }
                }, seconds, TimeUnit.SECONDS);
                modelAndView.addObject("message", messageSource.getMessage("search.schedulled", new String[]{}, locale));
                Logger.INSTANCE.info("Schedule searching at " + Util.DATE_TIME_FORMAT.format(date));

            } else {
                modelAndView.addObject("message", messageSource.getMessage("wrong.date", new String[]{}, locale));
            }
        }
        modelAndView.setViewName("admin/home");
        return modelAndView;
    }
}
