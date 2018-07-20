package by.spalex.diplom.snmp.server.snmp;

import by.spalex.diplom.snmp.model.OidInfo;
import by.spalex.diplom.snmp.service.OidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

public enum Mib {
    sys("1.3.6.1.2.1.1", true),
    sysDescr("1.3.6.1.2.1.1.1.0", false),
    sysObjectID("1.3.6.1.2.1.1.2.0", false),
    sysUpTime("1.3.6.1.2.1.1.3.0", false),
    sysContact("1.3.6.1.2.1.1.4.0", false),
    sysName("1.3.6.1.2.1.1.5.0", false),
    sysLocation("1.3.6.1.2.1.1.6.0", false),
    sysServices("1.3.6.1.2.1.1.7.0", false);

    private static final Map<String, OidInfo> mibMap = new HashMap<>();
    private final String oid;
    private final boolean active;

    Mib(String oid, boolean active) {
        this.oid = oid;
        this.active = active;
    }

    /**
     * resolve name of given oid
     *
     * @param oid oid for name resolving
     * @return name of oid if it presence or return oid if not
     */
    public static String getName(String oid) {
        OidInfo oidInfo = mibMap.get(oid);
        return oidInfo != null ? oidInfo.getName() : oid;
    }

    public String getOid() {
        return oid;
    }

    public boolean isActive() {
        return active;
    }

    /**
     * initialize and store at repository default Mib objects
     */
    @Component
    private static class Initiator {

        @Autowired
        private OidService oidService;

        @PostConstruct
        public void init() {
            mibMap.clear();
            for (Mib mib : Mib.values()) {
                OidInfo oidInfo = new OidInfo();
                oidInfo.setOid(mib.oid);
                oidInfo.setName(mib.name());
                oidInfo.setActive(mib.active);
                mibMap.put(oidInfo.getOid(), oidInfo);
            }
            oidService.save(mibMap.values());
        }

    }

}
