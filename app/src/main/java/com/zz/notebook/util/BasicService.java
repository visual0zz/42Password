package com.zz.notebook.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.zz.notebook.R;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class BasicService {
    public static Context rootContext;
    private static Logger logger=Logger.getLogger(BasicService.class.getName());
    public static void toast(String str){
        try{
            if(isWorkable()){
                Toast.makeText(rootContext,str,Toast.LENGTH_LONG).show();
                return;
            }
        }catch (RuntimeException e){
        }
        logger.warning("产生Toast消息失败,消息原文为:\""+str+"\"。");//不管是rootContext=null还是发生异常，都视为失败，记录log
    }
    final private static int[] avatars=new int[]{
        R.drawable.avata1,
        R.drawable.avata2,
        R.drawable.avata3,
        R.drawable.avata4,
        R.drawable.avata5,
        R.drawable.avata6,
        R.drawable.avata7,
        R.drawable.avata8,
        R.drawable.avata9,
        R.drawable.avata10,
        R.drawable.avata11,
        R.drawable.avata12,
        R.drawable.avata13,
        R.drawable.avata14,
        R.drawable.avata15,
        R.drawable.avata16,
        R.drawable.avata17,
        R.drawable.avata18,
        R.drawable.avata19,
        R.drawable.avata20,
        R.drawable.avata21,
        R.drawable.avata22,
        R.drawable.avata23,
        R.drawable.avata24,
        R.drawable.avata25,
        R.drawable.avata26,
        R.drawable.avata27,
        R.drawable.avata28,
        R.drawable.avata29,
        R.drawable.avata30,
        R.drawable.avata31,
        R.drawable.avata32,
        R.drawable.avata33,
        R.drawable.avata34,
        R.drawable.avata35,
        R.drawable.avata36,
        R.drawable.avata37,
        R.drawable.avata38,
        R.drawable.avata39,
        R.drawable.avata40,
        R.drawable.avata41,
        R.drawable.avata42,
        R.drawable.avata43,
        R.drawable.avata44,
        R.drawable.avata45,
        R.drawable.avata46,
        R.drawable.avata47,
        R.drawable.avata48,
        R.drawable.avata49,
    };//用于暂存所有的头像id

    public static Drawable getAvatar(int index){
        logger.info("正在尝试获得avatar["+index+"]");
        try{
            if(isWorkable()){
                return rootContext.getResources().getDrawable(avatars[index%avatars.length]);
            }
        }catch (RuntimeException e){
            logger.warning("读取avatar失败,e="+e);
        }
        logger.warning("读取avatar失败,rootContext="+rootContext);
        return null;
    }
    private static boolean isWorkable(){//用于验证自身工作需要的数据有没有到位
        return (rootContext!=null);
    }

}
