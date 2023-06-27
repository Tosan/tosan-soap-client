package com.tosan.client.soap;

import com.tosan.client.soap.handler.LogEncryptor;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author MosiDev
 * @since 5/24/2014
 */
public class LogEncryptorUTest {
    private static final Set<String> ENCRYPTED_TAGS = new HashSet<>();

    static {
        ENCRYPTED_TAGS.add("pan");
        ENCRYPTED_TAGS.add("card");
        ENCRYPTED_TAGS.add("password");
        ENCRYPTED_TAGS.add("otp");
        ENCRYPTED_TAGS.add("cvv");
    }

    @Test
    public void testNullTag() {
        String input = "<xml><class>block</class></xml>";
        String output = "<xml><class>block</class></xml>";
        assertEquals(output, LogEncryptor.encrypt(input, null));
    }

    @Test
    public void testEmptyTag() {
        String input = "<xml><class>block</class></xml>";
        String output = "<xml><class>block</class></xml>";
        assertEquals(output, LogEncryptor.encrypt(input, new HashSet<>()));
    }

    @Test
    public void testEmptyString() {
        assertEquals("", LogEncryptor.encrypt("", ENCRYPTED_TAGS));
    }

    @Test
    public void testEmptyXml() {
        assertEquals("<xml></xml>", LogEncryptor.encrypt("<xml></xml>", ENCRYPTED_TAGS));
    }

    @Test
    public void testSingleTagWithTrailingPan() {
        String input = "<xml><classpan>block</classpan></xml>";
        String output = "<xml><classpan>ENCRYPTED</classpan></xml>";
        assertEquals(output, LogEncryptor.encrypt(input, ENCRYPTED_TAGS));
    }

    @Test
    public void testNoRelatedTag() {
        assertEquals("<xml><class>block</class></xml>",
                LogEncryptor.encrypt("<xml><class>block</class></xml>", ENCRYPTED_TAGS));
    }

    @Test
    public void testAlternativeNoRelatedTag() {
        String input = "<s:OTP1><XML><A>test</A></XML></s:OTP1>";
        String output = "<s:OTP1><XML><A>test</A></XML></s:OTP1>";
        assertEquals(output, LogEncryptor.encrypt(input, ENCRYPTED_TAGS));
    }

    @Test
    public void testNestedTag() {
        String input = "<XML><S:test1><OTP0><OTP1><cvv>block</cvv></OTP1><OTP2><test>block2</test><myPAN>block3</myPAN></OTP2></OTP0></S:test1></XML>";
        String output = "<XML><S:test1><OTP0><OTP1><cvv>ENCRYPTED</cvv></OTP1><OTP2><test>block2</test><myPAN>ENCRYPTED</myPAN></OTP2></OTP0></S:test1></XML>";
        assertEquals(output, LogEncryptor.encrypt(input, ENCRYPTED_TAGS));
    }

    @Test
    public void testNestedTagWithAttribute() {
        String input = "<XML><test cvv='case2'><classes><classpan>block</classpan><OTP name='test' /></classes></test></XML>";
        String output = "<XML><test cvv='case2'><classes><classpan>ENCRYPTED</classpan><OTP name='test' /></classes></test></XML>";
        assertEquals(output, LogEncryptor.encrypt(input, ENCRYPTED_TAGS));
    }

    @Test
    public void testEmptyRelatedTag() {
        String input = "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test></OTP2></OTP0></S:test1></XML>";
        String output = "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test></OTP2></OTP0></S:test1></XML>";
        assertEquals(output, LogEncryptor.encrypt(input, ENCRYPTED_TAGS));
    }

    @Test
    public void testTrailingPanAndCvv() {
        String input = "<XML><S:test1><OTP0><OTP1><myCVV>hi</myCVV></OTP1><OTP2><test>block2</test><mypan>block3</mypan></OTP2></OTP0></S:test1></XML>";
        String output = "<XML><S:test1><OTP0><OTP1><myCVV>ENCRYPTED</myCVV></OTP1><OTP2><test>block2</test><mypan>ENCRYPTED</mypan></OTP2></OTP0></S:test1></XML>";
        assertEquals(output, LogEncryptor.encrypt(input, ENCRYPTED_TAGS));
    }

    @Test
    public void testPrecedingPanAndCvv() {
        String input = "<XML><S:test1><OTP0><OTP1><CVVone>hi</CVVone></OTP1><OTP2><test>block2</test><pan2>block3</pan2></OTP2></OTP0></S:test1></XML>";
        String output = "<XML><S:test1><OTP0><OTP1><CVVone>ENCRYPTED</CVVone></OTP1><OTP2><test>block2</test><pan2>ENCRYPTED</pan2></OTP2></OTP0></S:test1></XML>";
        assertEquals(output, LogEncryptor.encrypt(input, ENCRYPTED_TAGS));
    }

    @Test
    public void testTrailingMultiCasePan() {
        String input = "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>block3</myPaN></OTP2></OTP0></S:test1></XML>";
        String output = "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>ENCRYPTED</myPaN></OTP2></OTP0></S:test1></XML>";
        assertEquals(output, LogEncryptor.encrypt(input, ENCRYPTED_TAGS));
    }

    @Test
    public void testPrecedingPan() {
        String input = "<XML><OTP1><panel>test</panel></OTP1></XML>";
        String output = "<XML><OTP1><panel>ENCRYPTED</panel></OTP1></XML>";
        assertEquals(output, LogEncryptor.encrypt(input, ENCRYPTED_TAGS));
    }

    @Test
    public void testPanWithSpace() {
        String input = "<XML><OTP1><pan a>test</pan a></OTP1></XML>";
        String output = "<XML><OTP1><pan a>ENCRYPTED</pan a></OTP1></XML>";
        assertEquals(output, LogEncryptor.encrypt(input, ENCRYPTED_TAGS));
    }

    @Test
    public void testDuplicateTagsForPan() {
        String input = "<XML><OTP1><pan>test</pan></OTP1></XML><XML><OTP1><pan>test</pan></OTP1></XML>";
        String output = "<XML><OTP1><pan>ENCRYPTED</pan></OTP1></XML><XML><OTP1><pan>ENCRYPTED</pan></OTP1></XML>";
        assertEquals(output, LogEncryptor.encrypt(input, ENCRYPTED_TAGS));
    }

    @Test
    public void testAlternativeForPan() {
        String input = "<XML><OTP1><XML><pan>test</pan></XML></OTP1></XML>";
        String output = "<XML><OTP1><XML><pan>ENCRYPTED</pan></XML></OTP1></XML>";
        assertEquals(output, LogEncryptor.encrypt(input, ENCRYPTED_TAGS));
    }

    @Test
    public void testAlternativeForPassword() {
        String input = "<XML><OTP1><XML><password>test</password></XML></OTP1></XML>";
        String output = "<XML><OTP1><XML><password>ENCRYPTED</password></XML></OTP1></XML>";
        assertEquals(output, LogEncryptor.encrypt(input, ENCRYPTED_TAGS));
    }

    @Test
    public void testAlternativeForSecondPassword() {
        String input = "<XML><OTP3><XML><secondPassword>test</secondPassword></XML></OTP3></XML>";
        String output = "<XML><OTP3><XML><secondPassword>ENCRYPTED</secondPassword></XML></OTP3></XML>";
        assertEquals(output, LogEncryptor.encrypt(input, ENCRYPTED_TAGS));
    }

    @Test
    public void testLongInputString() {
        String input = "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>block3</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>block3</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>block3</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>block3</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>block3</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>block3</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>block3</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>block3</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>block3</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>block3</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>block3</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>block3</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>block3</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>block3</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>block3</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>block3</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>block3</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>block3</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>block3</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>block3</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>block3</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>block3</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>block3</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>block3</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>block3</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>block3</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>block3</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>block3</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>block3</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>block3</myPaN></OTP2></OTP0></S:test1></XML>";
        String output = "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>ENCRYPTED</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>ENCRYPTED</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>ENCRYPTED</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>ENCRYPTED</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>ENCRYPTED</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>ENCRYPTED</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>ENCRYPTED</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>ENCRYPTED</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>ENCRYPTED</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>ENCRYPTED</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>ENCRYPTED</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>ENCRYPTED</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>ENCRYPTED</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>ENCRYPTED</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>ENCRYPTED</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>ENCRYPTED</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>ENCRYPTED</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>ENCRYPTED</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>ENCRYPTED</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>ENCRYPTED</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>ENCRYPTED</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>ENCRYPTED</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>ENCRYPTED</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>ENCRYPTED</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>ENCRYPTED</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>ENCRYPTED</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>ENCRYPTED</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>ENCRYPTED</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>ENCRYPTED</myPaN></OTP2></OTP0></S:test1></XML>" +
                "<XML><S:test1><OTP0><OTP1><cvv></cvv></OTP1><OTP2><test>block2</test><myPaN>ENCRYPTED</myPaN></OTP2></OTP0></S:test1></XML>" +
                "";
        assertEquals(output, LogEncryptor.encrypt(input, ENCRYPTED_TAGS));
    }

    @Test
    public void testDifferentFormat() {
        String input = "<s:OTP1><XML><pan>test</pan></XML></s:OTP1>";
        String output = "<s:OTP1><XML><pan>ENCRYPTED</pan></XML></s:OTP1>";
        assertEquals(output, LogEncryptor.encrypt(input, ENCRYPTED_TAGS));
    }
}