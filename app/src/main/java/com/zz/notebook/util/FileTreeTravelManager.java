package com.zz.notebook.util;



import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author vis
 * @apiNote  采用后序遍历，一个文件夹的所有子文件和文件夹被travel一遍之后，这个文件夹本身才会被travel
 * 这个类本身只会抛出FileNotFoundException 抛出Exception只是转发FileTreeTraveler接口抛出的异常
 */
public final class FileTreeTravelManager {
    private File rootFolder;//储存迭代树的根目录
    private FileTreeTraveler traveler;
    private ArrayList<String> list;
    /**
     *
     * @param folder 迭代树的根目录，但也可以是一个文件，如果是一个文件，就只会调用traveler.travelFile一次。
     * @throws FileNotFoundException  如果根目录根本不存在就抛出异常
     */
    public FileTreeTravelManager(String folder) throws FileNotFoundException {
        this(new File(folder));
    }

    /**
     *
     * @param folder 迭代树的根目录，但也可以是一个文件，如果是一个文件，就只会调用traveler.travelFile一次。
     * @throws FileNotFoundException 如果根目录根本不存在就抛出异常
     */
    public FileTreeTravelManager(File folder) throws FileNotFoundException {
        if(!folder.exists())throw new FileNotFoundException("找不到这个文件:"+folder.getPath());
        rootFolder=folder;//保存文件夹
    }

    /**
     *
     * @param traveler 用于处理每个被遍历到的文件夹和文件的处理器对象
     * @return  返回用于用户自定义功能的字符串列表，这个列表会在遍历过程中反复传给traveler，供其根据情况修改其内容。
     */
    public List<String> travel(FileTreeTraveler traveler) throws Exception {
        this.traveler=traveler;//保存traveler
        list=new ArrayList<>();
        travelOneLayer(rootFolder,0);
        return list;
    }
    private void travelOneLayer(File file,int level_count) throws Exception {
        if(file.exists()){//如果文件存在
            if(file.isDirectory()){//如果是文件夹
                if(traveler.shouldTravelIntoFolder(level_count,file)) {
                    File[] files = file.listFiles();
                    if (files != null && files.length > 0) {
                        for (File f : files) {
                            travelOneLayer(f, level_count + 1);//如果返回true，继续迭代其下内容
                        }
                    }
                }
                traveler.travelFolder(level_count, file, list);
            }else if(file.isFile()){//如果是文件
                traveler.travelFile(level_count,file,list);
            }
        }
    }

}
