package cn.az.project.shop.core.utils;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

/**
 * The type Ip util.
 *
 * @author Liz
 */
@Slf4j
public class IpUtil {

    private static final String UNKNOWN = "unknown";
    private static final String LOCAL = "127.0.0.1";
    private static final String LOCAL_IPV6 = "0:0:0:0:0:0:0:1";
    private static final int LENGTH = 15;

    /**
     * Instantiates a new Ip util.
     */
    protected IpUtil() {

    }

    /**
     * 获取 IP地址
     * 使用 Nginx等反向代理软件， 则不能通过 request.getRemoteAddr()获取 IP地址
     * 如果使用了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP地址，
     * X-Forwarded-For中第一个非 unknown的有效IP字符串，则为真实IP地址
     *
     * @param request the request
     * @return the ip addr
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return LOCAL_IPV6.equals(ip) ? LOCAL : ip;
    }

}