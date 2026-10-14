package com.bahar.utils;

import org.springframework.context.i18n.LocaleContextHolder;
import java.text.MessageFormat;
import java.util.Locale;

/**
 * 国际化上下文：出口文案统一翻译 + miss 兜底原文。
 * 中文原文当 key，未翻译文案一律原样返回（绝不空白、绝不报错）。
 */
public class I18nContext {

    /** 是否中文（含 locale 为空的情况，默认中文） */
    public static boolean isZh() {
        Locale locale = LocaleContextHolder.getLocale();
        return locale == null || !"en".equalsIgnoreCase(locale.getLanguage());
    }

    /** 翻译中文原文；查不到或为中文时原样返回 */
    public static String t(String text) {
        if (text == null || isZh()) {
            return text;
        }
        Locale locale = LocaleContextHolder.getLocale();
        String v = PropertiesUtil.getMessage(text, locale);
        return v == null ? text : v;
    }

    /** 翻译并格式化（MessageFormat，占位符 {0}{1}...）；无参数或异常时返回原文/翻译 */
    public static String t(String key, Object... params) {
        String pattern = t(key);
        if (pattern == null || params == null || params.length == 0) {
            return pattern;
        }
        try {
            Locale locale = LocaleContextHolder.getLocale();
            if (locale == null) {
                locale = Locale.SIMPLIFIED_CHINESE;
            }
            return new MessageFormat(pattern, locale).format(params);
        } catch (Exception e) {
            return pattern;
        }
    }

    /** 货币符号：中文 ￥，其他 ¥ */
    public static String currency() {
        return isZh() ? "￥" : "¥";
    }
}
