package com.zz.notebook.ciper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

public class DataPool{//用于存储明文和密文,用于完成数据分块 混合 填充等操作

    public void put(byte in){//放入一个

    }
    public <T extends Serializable> void put(T a){

    }
    public InputStream getReadStream(){
        return null;
    }
    public OutputStream getWriteStream(){
        return null;
    }
    class DataPoolInputSteam extends InputStream{
        @Override
        public int read() throws IOException {
            return 0;
        }
    }
    class DataPoolOutputSteam extends OutputStream {
        @Override
        public void write(int i) throws IOException {

        }
    }
}
