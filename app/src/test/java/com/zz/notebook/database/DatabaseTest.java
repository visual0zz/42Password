package com.zz.notebook.database;

import org.junit.After;
import org.junit.Test;

import java.io.File;

public class DatabaseTest {
    private static final String database_test_file="zz_test.xml";
    @Test
    public void testDatabase() throws Database.DatabaseException, Database.WrongMasterPasswordException {
        new Database(new File(database_test_file),"123456".getBytes());
        Database database=new Database(new File(database_test_file),"123456".getBytes());
    }
    @After
    public void after(){
        new File(database_test_file).delete();
    }

}