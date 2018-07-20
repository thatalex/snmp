package by.spalex.diplom.snmp.service;

import by.spalex.diplom.snmp.model.OidInfo;
import by.spalex.diplom.snmp.repository.OidRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;

@Service("oidService")
public class OidServiceImpl implements OidService {

    private final OidRepository oidRepository;

    @Autowired
    public OidServiceImpl(OidRepository oidRepository) {
        this.oidRepository = oidRepository;
    }

    @Override
    public void save(Collection<OidInfo> values) {
        oidRepository.save(values);
    }

    @Override
    public List<OidInfo> findAll() {
        return oidRepository.findAll();
    }

    @Override
    @Transactional
    public void setOidInfos(List<OidInfo> oidInfos) {
        oidRepository.deleteAll();
        oidRepository.save(oidInfos);
    }


    @Override
    public List<OidInfo> findAllActive() {
        return oidRepository.findAllByActiveIsTrue();
    }
}
