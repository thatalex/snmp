package by.spalex.diplom.snmp.server;

import by.spalex.diplom.snmp.model.LogItem;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import static java.time.temporal.ChronoUnit.MINUTES;

public enum Util {
    ;

    public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    private static final Comparator<LogItem> dateComparator =
            (o1, o2) -> o1 == null && o2 == null ? 1 : o1 == null ? -1 : o2 == null ? 1 : o2.getDate().compareTo(o1.getDate());
    private static final Pattern IP_PATTERN = Pattern.compile(
            "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    public static LocalDateTime now() {
        return LocalDateTime.now().truncatedTo(MINUTES);
    }

    public static boolean isAddressValid(String address) {
        return IP_PATTERN.matcher(address).matches();
    }

    private static int packIp(byte[] bytes) {
        int val = 0;
        for (byte aByte : bytes) {
            val <<= 8;
            val |= aByte & 0xff;
        }
        return val;
    }

    private static byte[] unpackIp(int bytes) {
        return new byte[]{
                (byte) ((bytes >>> 24) & 0xff),
                (byte) ((bytes >>> 16) & 0xff),
                (byte) ((bytes >>> 8) & 0xff),
                (byte) ((bytes) & 0xff)
        };
    }

    public static void sort(List<LogItem> logItems) {
        logItems.sort(dateComparator);
    }

    public static List<String> getSNMPAddresses(String startAddress, String endAddress) {
        List<String> addresses = new ArrayList<>();
        int startIp = 0;
        try {
            startIp = packIp(InetAddress.getByName(startAddress).getAddress());
        } catch (UnknownHostException e) {
            Logger.INSTANCE.info(e.toString());
        }
        int endIp = 0;
        try {
            endIp = packIp(InetAddress.getByName(endAddress).getAddress());
        } catch (UnknownHostException e) {
            Logger.INSTANCE.info(e.toString());
        }
        if (endIp == 0) {
            endIp = startIp;
        }
        if (startIp != 0 && startIp <= endIp) {
            for (int i = startIp; i <= endIp; i++) {
                try {
                    addresses.add(InetAddress.getByAddress(unpackIp(i)).getHostAddress());
                } catch (UnknownHostException e) {
                    Logger.INSTANCE.info(e.toString());
                }
            }
        }
        return addresses;
    }
}
