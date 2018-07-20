package by.spalex.diplom.snmp.service;

import by.spalex.diplom.snmp.model.OidInfo;

import java.util.Collection;
import java.util.List;

public interface OidService {
    void save(Collection<OidInfo> values);

    List<OidInfo> findAll();

    /**
     * delete all old OidInfo objects and save new given at argument
     *
     * @param oidInfos new list of {@link OidInfo} to saving
     */
    void setOidInfos(List<OidInfo> oidInfos);

    /**
     * find all enabled OidInfos
     */
    List<OidInfo> findAllActive();
}
