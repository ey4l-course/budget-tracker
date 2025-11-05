package com.budget.gateway.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
public class IpUtil {
    protected String ExtractIp (HttpServletRequest request) throws UnknownHostException {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        String IpAddress = StringUtils.hasText(xForwardedFor)
                ? xForwardedFor.split(",")[0].trim()
                : request.getRemoteAddr();
        return normalizeIp(IpAddress);
    }

    private String normalizeIp (String ip) throws UnknownHostException{
        InetAddress inetAddress = InetAddress.getByName(ip);
        if (inetAddress instanceof Inet6Address) {
            byte[] addr = inetAddress.getAddress();
            if (isIpv4(addr))
                return String.format("%d.%d.%d.%d",
                        addr[12] & 0xff,
                        addr[13] & 0xff,
                        addr[14] & 0xff,
                        addr[15] & 0xff);
        }
        return inetAddress.getHostAddress();
    }

    private boolean isIpv4 (byte[] addr) {
        if (addr.length != 16) return false;
        for (int i = 0; i < 10; i ++){
            if (addr[i] != 0) return false;
        }
        return (addr[10] == (byte) 0xff) && (addr[11] == (byte) 0xff);
    }
}
