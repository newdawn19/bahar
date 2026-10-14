package com.bahar.utils;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CopyRight https://www.bahar.cn
 *
 * 消息文案原存放于 international/message_*.properties，已迁移为 international/message_*.yaml。
 * ResourceBundle 仅支持 .properties，故此处自行读取 yaml 并扁平化为 Map。
 */
public class PropertiesUtil {

    /** 按 Locale 缓存已加载的文案 Map；未找到的 key 对应 value 为 null（由调用方兜底原文）。 */
    private static final Map<Locale, Map<String, String>> CACHE = new ConcurrentHashMap<>();

    private static Map<String, String> loadMessages(Locale locale) {
        Map<String, String> map = new HashMap<>();
        if (locale == null) {
            return map;
        }
        String[] candidates = {
                "international/message_" + locale.toString() + ".yaml",
                "international/message_en_US.yaml"
        };
        for (String path : candidates) {
            try (InputStream is = PropertiesUtil.class.getClassLoader().getResourceAsStream(path)) {
                if (is == null) {
                    continue;
                }
                BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                String line;
                while ((line = br.readLine()) != null) {
                    String trimmed = line.trim();
                    if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                        continue;
                    }
                    int colon = trimmed.indexOf(':');
                    if (colon < 0) {
                        continue;
                    }
                    String key = unquote(trimmed.substring(0, colon).trim());
                    String value = unquote(trimmed.substring(colon + 1).trim());
                    map.put(key, value);
                }
                break;
            } catch (Exception e) {
                // 尝试下一个候选文件
            }
        }
        return map;
    }

    /** 解析当前请求语言；为空时回退中文，避免 NPE。 */
    private static Locale currentLocale() {
        Locale loc = LocaleContextHolder.getLocale();
        return loc != null ? loc : Locale.SIMPLIFIED_CHINESE;
    }

    /** 按 Locale 懒加载对应文案 Map；找不到 key 返回 null（调用方兜底原文）。 */
    public static String getMessage(String key, Locale locale) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        Locale loc = locale != null ? locale : currentLocale();
        Map<String, String> map = CACHE.computeIfAbsent(loc, k -> loadMessages(k));
        return map.get(key);
    }

    private static String unquote(String s) {
        if (s.length() >= 2 && ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'")))) {
            return s.substring(1, s.length() - 1).replace("\\\"", "\"").replace("\\\\", "\\");
        }
        return s;
    }

    /**
     * 获取请求返回Code对应的Message（按 response.error.xxx 码取，随请求语言切换）
     * @param code
     * @param params
     * @return
     */
    public static String getResponseErrorMessageByCode(int code, String...params) {
        String pStr = getMessage("response.error." + code, currentLocale());
        if (StringUtils.isEmpty(pStr)) {
            return "";
        }
        if (params == null || params.length == 0) {
            return pStr;
        }
        MessageFormat format = new MessageFormat(pStr, currentLocale());
        return format.format(params);
    }

    /**
     * 根据Key值获取Value
     * @param key
     * @param params
     * @return
     */
    public static String getValueByKey(String key, String...params) {
        String pStr = getMessage(key, currentLocale());
        if (StringUtils.isEmpty(pStr)) {
            return "";
        }
        if (params == null || params.length == 0) {
            return pStr;
        }
        MessageFormat format = new MessageFormat(pStr, currentLocale());
        return format.format(params);
    }
}
