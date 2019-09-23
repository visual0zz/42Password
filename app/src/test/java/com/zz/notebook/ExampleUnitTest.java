package com.zz.notebook;

import com.zz.notebook.database.ByteArrayUtils;
import com.zz.notebook.database.CipherProvider;
import com.zz.notebook.database.CipherService;
import com.zz.notebook.util.BasicService;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Scanner;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import static com.zz.notebook.database.CipherService.hash;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        UUID uuid=UUID.randomUUID();
        HashSet<UUID> uuids=new HashSet<>();
        uuids.add(uuid);
        Assert.assertTrue(uuids.contains(new UUID(uuid.getMostSignificantBits(),uuid.getLeastSignificantBits())));
        byte[] data=new CipherProvider("1".getBytes(),"1".getBytes())
                .getCipherMaster(Cipher.ENCRYPT_MODE).doFinal(hash("1".getBytes(),"1".getBytes(),3));
        System.out.println(data.length);
    }
}