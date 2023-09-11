package com.tosan.client.soap.handler;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class to encrypt specified keys as tag name with the ENCRYPTED string.
 *
 * @author MosiDev
 * @since 5/24/2014
 */
public class LogEncryptor {

    private static final String KEY = "key";
    private static final String VALUE = "value";

    public static String encrypt(String input, Set<String> secureParameters) {
        if (secureParameters == null || secureParameters.isEmpty()) {
            return input;
        }
        Pattern pattern = Pattern.compile("<([^<>]+)>([^<>]+)</\\1>");
        Matcher matcher = pattern.matcher(input);
        boolean needEncryptedKeyValue = false;
        while (matcher.find()) {
            String tag = matcher.group(1);
            String value = matcher.group(2);
            String lowerCasedTagName = tag.toLowerCase();
            if (lowerCasedTagName.equals(KEY) && secureParameters.contains(value.toLowerCase())) {
                needEncryptedKeyValue = true;
            } else if (needEncryptedKeyValue) {
                needEncryptedKeyValue = false;
                if (lowerCasedTagName.equals(VALUE)) {
                    input = replaceEncryptedData(input, tag, value);
                }
            }
            if (checkContainAnyEncryptedData(lowerCasedTagName, secureParameters)) {
                input = replaceEncryptedData(input, tag, value);
            }
        }
        return input;
    }

    private static String replaceEncryptedData(String input, String tag, String data) {
        String originalTag = "<" + tag + ">" + data + "</" + tag + ">";
        String toBeReplacedTag = "<" + tag + ">" + "ENCRYPTED" + "</" + tag + ">";
        return input.replace(originalTag, toBeReplacedTag);
    }

    private static boolean checkContainAnyEncryptedData(String tagName, Set<String> secureParameters) {
        return secureParameters.stream().anyMatch(tagName::contains);
    }
}