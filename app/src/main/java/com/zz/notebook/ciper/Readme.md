ciper这个包应该不使用任何android特性，可以在pc java上运行

暂时使用了很多exit 为了和电脑端通用，后面需要替换为thr

**每一条账户信息的定义**
-----------------------
```
    AccountItem{
        UUID uid;
        String group;
        String title;
        String username;
        String url;
        String notes;
        PasswordProperty password{
            byte[] data;
        }
    }
```
**数据库的文件储存形态**
------------------------
```
<data>
<master>这里放置主密钥的密文base64</master>
<salt>这里放置盐的明文base64</salt>
<account>
    <uuid>这里放置uuid的base64</uuid>
    <data>这里放置账户信息的密文base64</data>
</account>
<account>
    <uuid>这里放置uuid的base64</uuid>
    <data>这里放置账户信息的密文base64</data>
</account>
<account>
    <uuid>这里放置uuid的base64</uuid>
    <data>这里放置账户信息的密文base64</data>
</account>
...
</data>
```
其中
* master= base64( aes( key(master_key+salt), salt+random_key+hash(master_key) ) )
* salt=base64( salt )
* data=base64( aes( 
    key( uuid,random_key,hash( master_key ))
    ,account_item
  ))