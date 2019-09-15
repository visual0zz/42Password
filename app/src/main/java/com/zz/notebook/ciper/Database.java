package com.zz.notebook.ciper;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import static com.zz.notebook.ciper.CipherService.aesKeyFromSeed;
import static com.zz.notebook.ciper.CipherService.getSalt;
import static com.zz.notebook.ciper.CipherService.hash;
import static com.zz.notebook.util.BasicService.global_encrypt_algorithm;
import static com.zz.notebook.util.ByteArrayUtils.concat;
import static java.lang.System.exit;

public class Database {
    Logger logger=Logger.getLogger(Database.class.getName());
    private File databaseFile;
    private KeyProvider keyProvider;
    private List<AccountItem> data;
    /**
     * 读取或者新建数据库用于储存数据
     * @param xmlDatabaseFile 用于储存数据的数据库文件
     */
    Database(File xmlDatabaseFile,byte[] masterkey) throws IOException, SAXException, InvalidKeyException {
        try {
            this.databaseFile =xmlDatabaseFile;
            if(xmlDatabaseFile.isDirectory())
                throw new IOException("数据库需要一个文件，不是一个文件夹");
            else if(xmlDatabaseFile.exists())
                readDataFromFile();
            else{
                createNewDataFile(masterkey);
                readDataFromFile();
            }
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | ParserConfigurationException | TransformerException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            exit(1);
        }
    }

    /**
     * 负责读取数据库文件数据到内存中
     * @throws ParserConfigurationException 解析xml文件失败
     * @throws IOException ..
     * @throws SAXException ..
     */
    private void readDataFromFile() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
        DocumentBuilder builder=factory.newDocumentBuilder();
        Document document=builder.parse(databaseFile);
    }
    private void createNewDataFile(byte[] masterkey) throws ParserConfigurationException, TransformerException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Document document=DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        document.setXmlStandalone(true);
        //////////////////////////////////////////////////////////////////////////
        Element data_element=document.createElement("data");
        Element salt_element=document.createElement("salt");
        Element master_element=document.createElement("master");
        byte[] salt=getSalt();//用于隐藏密文统计规律的盐，防止攻击者判断两个密文是否使用了相同的密钥进行加密
        Key key=aesKeyFromSeed(concat(masterkey,salt));
        byte[] master_data=concat(concat(salt,getSalt()),hash(masterkey));
        Cipher cipher=Cipher.getInstance(global_encrypt_algorithm);
        cipher.init(Cipher.ENCRYPT_MODE,key);
        byte[] master=cipher.doFinal(master_data);

        /////////////////////////////////////////////////////////////////////////
        Transformer transformer= TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT,"yes");
        transformer.setParameter(OutputKeys.ENCODING,"utf-8");
        transformer.transform(new DOMSource(document), new StreamResult(databaseFile));
    }
}
