package com.zz.notebook.database;
import com.zz.notebook.util.Bash;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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

import static com.zz.notebook.database.CipherService.aes_key_length;
import static com.zz.notebook.database.CipherService.getRandomBytes;
import static com.zz.notebook.database.CipherService.hash;
import static com.zz.notebook.database.CipherService.hash_length;
import static com.zz.notebook.database.CipherService.random_bytes_length;
import static com.zz.notebook.database.ByteArrayUtils.bytesToHex;
import static com.zz.notebook.database.ByteArrayUtils.bytesToUUID;
import static com.zz.notebook.database.ByteArrayUtils.concat;
import static com.zz.notebook.database.ByteArrayUtils.hexToBytes;
import static com.zz.notebook.database.ByteArrayUtils.isEqual;
import static com.zz.notebook.database.ByteArrayUtils.uuidToBytes;

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
    public Database(File xmlDatabaseFile,byte[] masterkey) throws DatabaseException, WrongMasterPasswordException {
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
                saveDataToFile(hash(salt,masterkey));
            }
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | ParserConfigurationException | TransformerException | IllegalBlockSizeException e) {
            e.printStackTrace();
            throw new Database.UnfixableDatabaseException("建立数据库时发生不可恢复错误");
        }
        catch (IOException|SAXException|InvalidKeyException|NullPointerException e){
            e.printStackTrace();
            throw new DatabaseException("数据库文件读取失败",e);
        } catch (BadPaddingException e) {
            e.printStackTrace();
            throw new WrongMasterPasswordException("密码错误");
        }
    }

    /**
     * 负责读取数据库文件数据到内存中
     * @param masterkey 用户的主密码
     * @throws ParserConfigurationException 解析xml文件失败
     * @throws IOException ..
     * @throws SAXException ..
     */
    private void readDataFromFile(byte[] masterkey)
            throws ParserConfigurationException,
            IOException, SAXException, BadPaddingException,
            IllegalBlockSizeException, WrongMasterPasswordException,
            InvalidKeyException {
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
        salt=hexToBytes(salt_node.getTextContent());
        byte[] master=hexToBytes(master_node.getTextContent());
        logger.info("解密用的盐="+bytesToHex(salt));
        logger.info("解密用的密钥="+bytesToHex(masterkey));
        logger.info("解密用的哈希="+bytesToHex(hash(salt,masterkey)));
        cipherProvider=new CipherProvider(hash(salt,masterkey),null);//解密之前不知道randomkey
        byte[]master_data=cipherProvider.getCipherMaster(Cipher.DECRYPT_MODE).doFinal(master);//解码文件头
        logger.info("解密后master_data="+bytesToHex(master_data));//todo delete
        //{//进行master_data的切分 master_data= salt+randomkey+hash(masterkey)
            byte[] salt_inner=new byte[aes_key_length];
            byte[] randomkey_inner=new byte[random_bytes_length];
            byte[] hashmasterkey_inner=new byte[hash_length];
            if(aes_key_length+random_bytes_length+hash_length!=master_data.length)throw new UnfixableDatabaseException("数据库读入的数据头格式异常");
            for (int i=0;i<master_data.length;i++){//进行数组切分
                if(i<aes_key_length)
                    salt_inner[i]=master_data[i];
                else if(i<aes_key_length+random_bytes_length)
                    randomkey_inner[i-aes_key_length]=master_data[i];
                else
                    hashmasterkey_inner[i-aes_key_length-random_bytes_length]=master_data[i];
            }
        //}
        logger.info("解密后");//todo delete
        logger.info("salt="+bytesToHex(salt_inner));
        logger.info("randomkey="+bytesToHex(randomkey_inner));
        logger.info("hash_masterkey="+bytesToHex(hashmasterkey_inner));
        if(!isEqual(salt_inner,salt)||!isEqual(hashmasterkey_inner,hash(salt,masterkey)))throw new WrongMasterPasswordException("密码错误，无法解密数据库");
        cipherProvider=new CipherProvider(hashmasterkey_inner,randomkey_inner);
        ///////////////////////////////////////开始读取数据区///////////////////////////////////////////////////////
        data=new ArrayList<>();
        for(int i=0;i<nodes.getLength();i++){
            Node node=nodes.item(i);
            if(node.getNodeName().equals("account")){//如果找到一个账户条目
                NodeList account_nodes=node.getChildNodes();
                UUID uuid=null;
                byte[]account_data=null;
                for(int j=0;j<account_nodes.getLength();j++){//找uuid
                    Node account_node=account_nodes.item(j);
                    if(account_node.getNodeName().equals("uuid"))
                        uuid=bytesToUUID(hexToBytes(account_node.getTextContent()));
                }
                for(int j=0;j<account_nodes.getLength();j++){//找data
                    Node account_node=account_nodes.item(j);
                    if(account_node.getNodeName().equals("newItem"))
                        account_data=hexToBytes(account_node.getTextContent());
                }
                AccountItem item=new AccountItem();
                try {
                    item.setAndDecryptData(uuid,cipherProvider,account_data);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    continue;//如果某一条消息解析失败，就直接忽略，跳到其他消息去
                }
                data.add(item);//添加account
            }
        }
    }

    /**
     * 负责新建空的数据库文件
     * @param masterkey_hash 用于加密数据库的主密码的加盐哈希值
     * @throws ParserConfigurationException
     * @throws TransformerException
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    private void saveDataToFile(byte[] masterkey_hash) throws ParserConfigurationException, TransformerException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
        Document document=DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        document.setXmlStandalone(true);
        //////////////////////////////////////////////////////////////////////////
        Element root_element=document.createElement("root");
        Element salt_element=document.createElement("salt");
        Element master_element=document.createElement("master");
        logger.info("加密前");//todo delete
        logger.info("salt="+bytesToHex(salt));
        logger.info("randomkey="+bytesToHex(cipherProvider.randomkey));
        logger.info("hash_masterkey="+bytesToHex(masterkey_hash));
        byte[] master_data=concat(concat(salt,cipherProvider.randomkey),masterkey_hash);

        logger.info("加密前master_data="+bytesToHex(master_data));//todo delete
        Cipher cipher=cipherProvider.getCipherMaster(Cipher.ENCRYPT_MODE);
        byte[] master=cipher.doFinal(master_data);

        salt_element.setTextContent(bytesToHex(salt));
        master_element.setTextContent(bytesToHex(master));
        root_element.appendChild(salt_element);
        root_element.appendChild(master_element);//写入头信息
        for(AccountItem item:data){//写入所有的账户信息条目
            Element account_element=document.createElement("account");
            Element uuid_element=document.createElement("uuid");
            Element data_element=document.createElement("newItem");
            data_element.setTextContent(bytesToHex(item.getEncryptedData(cipherProvider)));
            uuid_element.setTextContent(bytesToHex(uuidToBytes(item.getUid())));
            account_element.appendChild(uuid_element);
            account_element.appendChild(data_element);
            root_element.appendChild(account_element);
        }
        document.appendChild(root_element);

        /////////////////////////////////////////////////////////////////////////
        Transformer transformer= TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT,"yes");//进行分行，使便于阅读
        transformer.setParameter(OutputKeys.ENCODING,"utf-8");//使用utf-8编码
        Bash.touch(databaseFile);
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
        cipherProvider=new CipherProvider(hash(salt,master_key), getRandomBytes());
        if(!withSample)return;
        AccountItem item=new AccountItem();
        item.setTitle("示例账户");
        item.setGroup("示例分组");
        item.setNotes("我有一壶酒，可以慰风尘，倾尽江海里，赠饮天下人。");
        item.setPassword(new PasswordProperty("abcd",cipherProvider));
        data.add(item);
    }
    public static class DatabaseException extends Exception{
        public DatabaseException(String message,Throwable cause) {
            super(message,cause);
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

    public int size(){return data.size();}
    public List<UUID> getUUIDs(){
        ArrayList<UUID> result=new ArrayList<>();
        for(AccountItem item:data){
            result.add(item.getUid());
        }
        return result;
    }
    public AccountItem getAccountItem(UUID uuid){
        for(AccountItem item:data){
            if(item.getUid().equals(uuid))
                return item;
        }
        return null;
    }

    /**
     * 将数据库数据保存到文件
     * @return 保存操作成功返回true 失败返回false
     */
    public boolean saveToFile(){
        try {
            saveDataToFile(cipherProvider.masterkey_hash);
        } catch (ParserConfigurationException | TransformerException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public AccountItem getAccountItem(int index){
        return data.get(data.size()-index-1);//向外暴漏的顺序与内部储存顺序反过来，使自动按时间顺序排列
    }

    public Editor getEditor(int index){
        return new Editor(getAccountItem(index));
    }
    public Editor getEditor(AccountItem item){
        return new Editor(item);
    }

    public class Editor {
        private Editor(AccountItem in){
            finished=false;
            newItem =new AccountItem();
            if(in!=null){//不为空表示编辑
                oldItem=in.getUid();//缓存旧的UUID为了最后删除旧的条目
                newItem.assign(in);
            }else {//编辑对象为空表示新建
                oldItem=null;
            }
        }
        boolean finished;//用于限制提交之后继续操作的变量
        UUID oldItem;
        AccountItem newItem;

        public Editor submit(){//将编辑动作实际作用到数据库
            if(finished)throw new InvalidParameterException("Editor只能使用一次，一旦提交，其他操作都无效");
            newItem.setUid(UUID.randomUUID());//产生新的uuid
            newItem.setTimestamp(System.currentTimeMillis());//插入新的时间戳
            Database.this.data.add(newItem);//插入新的
            if(oldItem!=null)
                Database.this.data.remove(getAccountItem(oldItem));//删除旧的
            finished=true;
            return new Editor(newItem);//将新插入的对象对应的editor返回，以用于后续操作
        }
        public void delete(){//将Editor对应的条目删除
            if(finished)throw new InvalidParameterException("Editor只能使用一次，一旦提交，其他操作都无效");
            if(oldItem!=null)
                Database.this.data.remove(getAccountItem(oldItem));//删除旧的
            finished=true;
        }
        public void revert(){//放弃本编辑器的所有修改
            if(finished)return;
            if(oldItem!=null)//如果有旧的就撤回旧的
                newItem=Database.this.getAccountItem(oldItem);
            else
                newItem=new AccountItem();//如果是新建操作的撤销，就直接新建
        }
        public String getGroup() { return newItem.getGroup(); }
        public String getTitle() { return newItem.getTitle(); }
        public String getAccountName() { return newItem.getAccountName(); }
        public String getUrl() { return newItem.getUrl(); }
        public String getNotes() { return newItem.getNotes(); }
        public String getPassword() {
            return newItem.getPassword().get(Database.this.cipherProvider);
        }

        public Editor setGroup(String group) {
            newItem.setGroup(group);
            return this;
        }
        public Editor setTitle(String title) {
            newItem.setTitle(title);
            return this;
        }
        public Editor setUsername(String username) {
            newItem.setUsername(username);
            return this;
        }
        public Editor setUrl(String url) {
            newItem.setUrl(url);
            return this;
        }
        public Editor setNotes(String notes) {
            newItem.setNotes(notes);
            return this;
        }
        public Editor setPassword(String password) {
            newItem.setPassword(new PasswordProperty(password,Database.this.cipherProvider));
            return this;
        }
        public boolean isFinished(){
            return finished;
        }
        public boolean isNew(){
            return oldItem==null;
        }
    }
}
