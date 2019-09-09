package com.zz.notebook.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.zz.notebook.util.BasicService;

import java.security.acl.LastOwnerException;
import java.util.logging.Logger;

public class Database extends SQLiteOpenHelper {
    Logger logger= Logger.getLogger(Database.class.getName());
    public Database() {
        super(BasicService.rootContext, "passwords", null, 1);
        logger.info("数据库对象构造函数结尾");
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE  TABLE zz (name VAR(64)) ;");
        sqLiteDatabase.execSQL("INSERT INTO zz (name) VALUES ('空气');");
        Cursor select_name_from_zz_ = sqLiteDatabase.rawQuery("SELECT name FROM zz ", null);
        logger.info("数据表列数为"+select_name_from_zz_.getColumnCount());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
