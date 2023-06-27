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

    public static String encrypt(String input, Set<String> secureParameters) {
        if (secureParameters == null || secureParameters.isEmpty()) {
            return input;
        }
        Pattern pattern = Pattern.compile("<([^<>]+)>([^<>]+)</\\1>");
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            String tag = matcher.group(1);
            String lowerCasedTagName = tag.toLowerCase();
            if (checkContainAnyEncryptedData(lowerCasedTagName, secureParameters)) {
                String originalTag = "<" + tag + ">" + matcher.group(2) + "</" + tag + ">";
                String toBeEncryptTag = "<" + tag + ">" + "ENCRYPTED" + "</" + tag + ">";
                input = input.replace(originalTag, toBeEncryptTag);
            }
        }
        return input;
    }

    private static boolean checkContainAnyEncryptedData(String tagName, Set<String> secureParameters) {
        return secureParameters.stream().anyMatch(tagName::contains);
    }
}