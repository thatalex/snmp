package by.spalex.diplom.snmp.server.snmp;

import by.spalex.diplom.snmp.model.Item;
import by.spalex.diplom.snmp.model.OidInfo;
import by.spalex.diplom.snmp.server.Logger;
import by.spalex.diplom.snmp.service.ItemService;
import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TreeEvent;
import org.snmp4j.util.TreeUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Walker {

    private final ItemService itemService;

    public Walker(ItemService itemService) {
        this.itemService = itemService;
    }


    public void probe(Item item) throws Exception {
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString("public"));
        target.setAddress(GenericAddress.parse("udp:" + item.getAddress() + "/161")); // supply your own IP and port
        target.setRetries(2);
        target.setTimeout(500);
        target.setVersion(SnmpConstants.version2c);
        List<VariableBinding> variables = getVariables(item.getOid(), target);
        if (!variables.isEmpty()) {
            item.addValue(variables.get(0).toValueString());
        } else {
            item.addValue("Not available");
        }
        itemService.save(item);
    }


    private List<VariableBinding> getVariables(String oid, Target target) throws IOException {
        TransportMapping<? extends Address> transport = new DefaultUdpTransportMapping();
        Snmp snmp = new Snmp(transport);
        transport.listen();
        PDU request = new PDU();
        request.add(new VariableBinding(new OID(oid)));
        request.setType(PDU.GET);
        ResponseEvent event = snmp.send(request, target);
        snmp.close();
        List<VariableBinding> variableBindings = new ArrayList<>();
        if (event != null) {
            PDU response = event.getResponse();
            if (response != null && response.getErrorStatus() == SnmpConstants.SNMP_ERROR_SUCCESS && response.size() > 0) {
                for (VariableBinding variable : response.getVariableBindings()) {
                    if (variable.getOid().isValid() && !variable.isException()) {
                        variableBindings.add(variable);
                    }
                }
            }
        }
        return variableBindings;
    }

    public void scan(List<String> addresses, List<OidInfo> oidInfos) {
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        List<Callable<Integer>> tasks = new ArrayList<>();
        for (String address : addresses) {
            tasks.add(() -> scan(address, oidInfos));
        }
        int sum = 0;
        try {
            sum = executorService.invokeAll(tasks).stream().mapToInt(future -> {
                try {
                    return future.get();
                } catch (Exception e) {
                    Logger.INSTANCE.error(e.toString());
                    return 0;
                }
            }).sum();
        } catch (InterruptedException e) {
            Logger.INSTANCE.info(e.getMessage());
        } finally {
            executorService.shutdown();
        }
        if (sum > 0) {
            Logger.INSTANCE.info("Found " + sum + " new elements");
        }
    }

    private int scan(String address, List<OidInfo> oidInfos) throws Exception {
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString("public"));
        target.setAddress(GenericAddress.parse("udp:" + address + "/161")); // supply your own IP and port
        target.setRetries(1);
        target.setTimeout(200);
        target.setVersion(SnmpConstants.version2c);
        int result = 0;
        for (OidInfo oidInfo : oidInfos) {
            result += scan(address, target, oidInfo.getOid());
        }

        return result;
    }

    private int scan(String address, Target target, String tableOid) throws IOException {
        int result = 0;
        TransportMapping<? extends Address> transport = new DefaultUdpTransportMapping();
        Snmp snmp = new Snmp(transport);
        transport.listen();
        if (itemService.findOne(address, tableOid) != null) {
            return 0;
        }
        List<VariableBinding> variables = getVariables(tableOid, target);
        if (!variables.isEmpty()) {
            VariableBinding variable = variables.get(0);
            Item valueItem = new Item(address, tableOid);
            valueItem.setType(variable.getVariable().getSyntaxString());
            valueItem.addValue(variable.toValueString());
            List<VariableBinding> nameVars = getVariables(Mib.sysName.getOid(), target);
            if (!nameVars.isEmpty()) {
                valueItem.setName(nameVars.get(0).toValueString() + ": " + valueItem.getName());
            }
            itemService.save(valueItem);
            result++;
        } else {


            TreeUtils treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());
            List<TreeEvent> events = treeUtils.getSubtree(target, new OID(tableOid));
            snmp.close();
            if (events == null || events.size() == 0) {
                System.out.println("Error: Unable to read table...");
                return 0;
            }

            // parent.addValue(getValue(tableOid, target));
            for (TreeEvent event : events) {
                if (event == null) {
                    continue;
                }
                if (event.isError()) {
                    System.out.println("Error: target [" + address + "] " + event.getErrorMessage());
                    continue;
                }

                VariableBinding[] varBindings = event.getVariableBindings();
                Item rootItem = new Item(address, tableOid);
                rootItem.makeRoot();
                List<VariableBinding> nameVars = getVariables(Mib.sysName.getOid(), target);
                if (!nameVars.isEmpty()) {
                    rootItem.setName(nameVars.get(0).toValueString());
                }
                boolean isNameSettled = false;
                if (varBindings != null && varBindings.length != 0) {
                    for (VariableBinding varBinding : varBindings) {
                        if (varBinding == null) {
                            continue;
                        }
                        if (!isNameSettled) {
                            rootItem.setName(rootItem.getName() + ": " + varBinding.getOid().trim().trim());
                            isNameSettled = true;
                        }

                        Variable variable = varBinding.getVariable();
                        String strOid = varBinding.getOid().toString();
                        Item child = new Item(address, strOid);
                        rootItem.addChild(child);
                        child.setType(variable.getSyntaxString());
                        child.addValue(variable.toString());
                    }
                    if (!rootItem.getChildItems().isEmpty()) {
                        itemService.save(rootItem);
                        result++;
                    }
                } else {
                    System.out.println(event);
                }
            }
        }
        return result;
    }
}

