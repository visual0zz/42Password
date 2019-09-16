package com.zz.notebook.ciper;

import org.junit.After;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;

import static org.junit.Assert.*;

public class DatabaseTest {
    private static final String database_test_file="zz_test.xml";
    @Test
    public void testDatabase() throws Database.DatabaseException, Database.WrongMasterPasswordException {
        Database database=new Database(new File(database_test_file),"123456".getBytes());
        new Database(new File(database_test_file),"123456".getBytes());

    }
    @After
    public void after(){
        new File(database_test_file).delete();
    }

}