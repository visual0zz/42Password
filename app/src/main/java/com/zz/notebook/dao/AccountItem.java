package com.zz.notebook.dao;


import androidx.core.graphics.PathUtils;

import java.io.File;
import java.util.Map;
import java.util.UUID;

public class AccountItem {//表示一条密码记录
    UUID uid;
    Map<String,PropertyItem> properties;

    void sta(){
        File file=new File("asdf");
    }
}

