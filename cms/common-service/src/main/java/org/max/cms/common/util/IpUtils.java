package org.max.cms.common.util;

import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * IP地址工具类
 * 用于获取客户端真实IP地址
 */
public class IpUtils {
    
    private static final String UNKNOWN = "unknown";
    private static final String LOCALHOST_IPV4 = "127.0.0.1";
    private static final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";
    private static final int IP_LENGTH = 15;
    
    /**
     * 获取客户端IP地址
     * 支持通过代理服务器获取真实IP
     */
    public static String getClientIpAddress() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return getLocalHostAddress();
            }
            
            HttpServletRequest request = attributes.getRequest();
            return getClientIpAddress(request);
        } catch (Exception e) {
            return getLocalHostAddress();
        }
    }
    
    /**
     * 获取客户端IP地址
     */
    public static String getClientIpAddress(HttpServletRequest request) {
        if (request == null) {
            return getLocalHostAddress();
        }
        
        String ip = null;
        
        // 1. X-Forwarded-For：标准的代理IP头
        ip = request.getHeader("X-Forwarded-For");
        if (isValidIp(ip)) {
            // 取第一个IP地址
            return getFirstValidIp(ip);
        }
        
        // 2. Proxy-Client-IP：Apache服务器使用
        ip = request.getHeader("Proxy-Client-IP");
        if (isValidIp(ip)) {
            return ip;
        }
        
        // 3. WL-Proxy-Client-IP：WebLogic服务器使用
        ip = request.getHeader("WL-Proxy-Client-IP");
        if (isValidIp(ip)) {
            return ip;
        }
        
        // 4. HTTP_CLIENT_IP：有些代理服务器使用
        ip = request.getHeader("HTTP_CLIENT_IP");
        if (isValidIp(ip)) {
            return ip;
        }
        
        // 5. HTTP_X_FORWARDED_FOR：有些代理服务器使用
        ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        if (isValidIp(ip)) {
            return getFirstValidIp(ip);
        }
        
        // 6. X-Real-IP：Nginx代理使用
        ip = request.getHeader("X-Real-IP");
        if (isValidIp(ip)) {
            return ip;
        }
        
        // 7. X-Client-IP：其他代理使用
        ip = request.getHeader("X-Client-IP");
        if (isValidIp(ip)) {
            return ip;
        }
        
        // 8. 最后取request.getRemoteAddr()
        ip = request.getRemoteAddr();
        if (isValidIp(ip)) {
            return ip;
        }
        
        return getLocalHostAddress();
    }
    
    /**
     * 检查IP是否有效
     */
    private static boolean isValidIp(String ip) {
        return StringUtils.hasText(ip) && 
               !UNKNOWN.equalsIgnoreCase(ip) && 
               !isLocalHost(ip);
    }
    
    /**
     * 检查是否是本地地址
     */
    private static boolean isLocalHost(String ip) {
        return LOCALHOST_IPV4.equals(ip) || LOCALHOST_IPV6.equals(ip);
    }
    
    /**
     * 从多个IP中获取第一个有效IP
     * 通常X-Forwarded-For会包含多个IP，格式为：client1, proxy1, proxy2
     */
    private static String getFirstValidIp(String ips) {
        if (!StringUtils.hasText(ips)) {
            return null;
        }
        
        String[] ipArray = ips.split(",");
        for (String ip : ipArray) {
            ip = ip.trim();
            if (isValidIp(ip)) {
                return ip.length() > IP_LENGTH ? ip.substring(0, IP_LENGTH) : ip;
            }
        }
        
        return null;
    }
    
    /**
     * 获取本机IP地址
     */
    public static String getLocalHostAddress() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            return address.getHostAddress();
        } catch (UnknownHostException e) {
            return LOCALHOST_IPV4;
        }
    }
    
    /**
     * 检查IP地址是否为内网地址
     */
    public static boolean isInternalIp(String ip) {
        if (!StringUtils.hasText(ip)) {
            return false;
        }
        
        try {
            String[] parts = ip.split("\\.");
            if (parts.length != 4) {
                return false;
            }
            
            int first = Integer.parseInt(parts[0]);
            int second = Integer.parseInt(parts[1]);
            
            // 10.0.0.0 - 10.255.255.255
            if (first == 10) {
                return true;
            }
            
            // 172.16.0.0 - 172.31.255.255
            if (first == 172 && second >= 16 && second <= 31) {
                return true;
            }
            
            // 192.168.0.0 - 192.168.255.255
            if (first == 192 && second == 168) {
                return true;
            }
            
            // 127.0.0.0 - 127.255.255.255 (本地回环)
            if (first == 127) {
                return true;
            }
            
        } catch (NumberFormatException e) {
            return false;
        }
        
        return false;
    }
    
    /**
     * 将IP地址转换为长整型
     */
    public static long ipToLong(String ip) {
        if (!StringUtils.hasText(ip)) {
            return 0;
        }
        
        try {
            String[] parts = ip.split("\\.");
            if (parts.length != 4) {
                return 0;
            }
            
            long result = 0;
            for (int i = 0; i < 4; i++) {
                int part = Integer.parseInt(parts[i]);
                if (part < 0 || part > 255) {
                    return 0;
                }
                result = (result << 8) + part;
            }
            
            return result;
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    /**
     * 将长整型转换为IP地址
     */
    public static String longToIp(long ip) {
        return ((ip >> 24) & 0xFF) + "." +
               ((ip >> 16) & 0xFF) + "." +
               ((ip >> 8) & 0xFF) + "." +
               (ip & 0xFF);
    }
}