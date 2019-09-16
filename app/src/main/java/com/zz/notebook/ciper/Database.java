package com.zz.notebook.ciper;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import static com.zz.notebook.ciper.CipherService.getRandomBytes;
import static com.zz.notebook.ciper.CipherService.hash;
import static com.zz.notebook.util.ByteArrayUtils.bytesToHex;
import static com.zz.notebook.util.ByteArrayUtils.concat;
import static com.zz.notebook.util.ByteArrayUtils.hexToBytes;
import static com.zz.notebook.util.ByteArrayUtils.uuidToBytes;

public class Database {
    Logger logger=Logger.getLogger(Database.class.getName());
    private File databaseFile;
    private CipherProvider cipherProvider;
    private byte[] salt;
    private List<AccountItem> data;
    /**
     * 读取或者新建数据库用于储存数据
     * @param xmlDatabaseFile 用于储存数据的数据库文件
     */
    Database(File xmlDatabaseFile,byte[] masterkey) throws DatabaseException {
        try {
            this.databaseFile =xmlDatabaseFile;
            if(xmlDatabaseFile==null)
                initNewDatabase(masterkey,false);//文件为空表示生成一个仅仅存在于内存中的数据库
            else if(xmlDatabaseFile.isDirectory())
                throw new IOException("数据库需要一个文件，不是一个文件夹");
            else if(xmlDatabaseFile.exists())
                readDataFromFile(masterkey);
            else{
                initNewDatabase(masterkey,true);
                saveDataToFile(masterkey);
            }
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | ParserConfigurationException | TransformerException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            throw new Database.UnfixableDatabaseException("建立数据库时发生不可恢复错误");
        }
        catch (IOException|SAXException|InvalidKeyException|NullPointerException e){
            throw new DatabaseException(e);
        }
    }

    /**
     * 负责读取数据库文件数据到内存中
     * @throws ParserConfigurationException 解析xml文件失败
     * @throws IOException ..
     * @throws SAXException ..
     */
    private void readDataFromFile(byte[] masterkey) throws ParserConfigurationException, IOException, SAXException {
        Document document=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(databaseFile);
        NodeList nodes=document.getElementsByTagName("root").item(0).getChildNodes();
        Node salt_node=null,master_node=null;
        for(int i=0;i<nodes.getLength();i++){//循环所有一级节点，找到 盐 和 头
            Node node=nodes.item(i);
            if(node.getNodeName().equals("salt"))
                salt_node=node;
            else if(node.getNodeName().equals("master"))
                master_node=node;
        }
        byte[] salt=hexToBytes(salt_node.getTextContent());
        byte[] master=hexToBytes(master_node.getTextContent());


//        byte[] salt=getRandomBytes();//用于隐藏密文统计规律的盐，防止攻击者判断两个密文是否使用了相同的密钥进行加密
//        Key key=aesKeyFromSeed(concat(masterkey,salt));
//        byte[] master_data=concat(concat(salt,getRandomBytes()),hash(masterkey));
//        Cipher cipher=Cipher.getInstance(global_encrypt_algorithm);
//        cipher.init(Cipher.ENCRYPT_MODE,key);
//        byte[] master=cipher.doFinal(master_data);
    }

    /**
     * 负责新建空的数据库文件
     * @param masterkey 用于加密数据库的主密码
     * @throws ParserConfigurationException
     * @throws TransformerException
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    private void saveDataToFile(byte[] masterkey) throws ParserConfigurationException, TransformerException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Document document=DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        document.setXmlStandalone(true);
        //////////////////////////////////////////////////////////////////////////
        Element root_element=document.createElement("root");
        Element salt_element=document.createElement("salt");
        Element master_element=document.createElement("master");
        byte[] master_data=concat(concat(salt,cipherProvider.randomkey),hash(masterkey));
        Cipher cipher=cipherProvider.getCipherMaster(Cipher.ENCRYPT_MODE);
        byte[] master=cipher.doFinal(master_data);

        salt_element.setTextContent(bytesToHex(salt));
        master_element.setTextContent(bytesToHex(master));
        root_element.appendChild(salt_element);
        root_element.appendChild(master_element);
        for(AccountItem item:data){
            Element account_element=document.createElement("account");
            Element uuid_element=document.createElement("uuid");
            Element data_element=document.createElement("data");
            data_element.setTextContent(bytesToHex(item.getEncryptedData(cipherProvider)));
            uuid_element.setTextContent(bytesToHex(uuidToBytes(item.getUid())));
            account_element.appendChild(uuid_element);
            account_element.appendChild(data_element);
            root_element.appendChild(account_element);
        }
        document.appendChild(root_element);

        /////////////////////////////////////////////////////////////////////////
        Transformer transformer= TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT,"yes");
        transformer.setParameter(OutputKeys.ENCODING,"utf-8");
        transformer.transform(new DOMSource(document), new StreamResult(databaseFile));
    }

    /**
     * 初始化数据库
     * @param master_key 数据库的主密钥
     * @param withSample 是否在新数据库中添加示例账户
     */
    private void initNewDatabase(byte[] master_key,boolean withSample){
        salt= getRandomBytes();//用于隐藏密文统计规律的盐，防止攻击者判断两个密文是否使用了相同的密钥进行加密
        data=new ArrayList<>();
        cipherProvider=new CipherProvider(hash(concat(salt,master_key)), getRandomBytes());
        if(!withSample)return;
        AccountItem item=new AccountItem();
        item.setTitle("示例账户");
        item.setGroup("示例分组");
        item.setNotes("我有一壶酒，可以慰风尘，倾尽江海里，赠饮天下人。");
        item.setPassword(new PasswordProperty("abcd",cipherProvider));
        data.add(item);
    }
    public void close(){salt=null;data=null;cipherProvider=null;databaseFile=null;}
    public static class DatabaseException extends Exception{
        public DatabaseException(String message) {
            super(message);
        }

        public DatabaseException(Throwable cause) {
            super(cause);
        }
    }
    public static class WrongMasterPasswordException extends Exception{
        public WrongMasterPasswordException(String message) {
            super(message);
        }
    }
    public static class UnfixableDatabaseException extends RuntimeException{
        public UnfixableDatabaseException(String message){super(message);}
    }
}
