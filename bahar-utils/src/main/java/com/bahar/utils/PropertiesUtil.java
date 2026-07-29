package com.bahar.utils;

import org.apache.commons.lang.StringUtils;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 *
 * 消息文案原存放于 international/message_*.properties，已迁移为 international/message_*.yaml。
 * ResourceBundle 仅支持 .properties，故此处自行读取 yaml 并扁平化为 Map。
 */
public class PropertiesUtil {

    private static final Map<String, String> messageMap = loadMessages(Locale.getDefault());

    private static Map<String, String> loadMessages(Locale locale) {
        Map<String, String> map = new HashMap<>();
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

    private static String unquote(String s) {
        if (s.length() >= 2 && ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'")))) {
            return s.substring(1, s.length() - 1).replace("\\\"", "\"").replace("\\\\", "\\");
        }
        return s;
    }

    /**
     * 获取请求返回Code对应的Message
     * @param code
     * @param params
     * @return
     */
    public static String getResponseErrorMessageByCode(int code, String...params) {
        String pStr = messageMap.get("response.error." + code);
        if (StringUtils.isEmpty(pStr)) {
            return "";
        }
        if (params == null || params.length == 0) {
            return pStr;
        }
        MessageFormat format = new MessageFormat(pStr, Locale.getDefault());
        return format.format(params);
    }

    /**
     * 根据Key值获取Value
     * @param key
     * @param params
     * @return
     */
    public static String getValueByKey(String key, String...params) {
        String pStr = messageMap.get(key);
        if (StringUtils.isEmpty(pStr)) {
            return "";
        }
        if (params == null || params.length == 0) {
            return pStr;
        }
        MessageFormat format = new MessageFormat(pStr, Locale.getDefault());
        return format.format(params);
    }
}
