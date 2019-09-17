package com.zz.notebook.util;



import java.io.*;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.exit;

public final class Bash {
    /**
     * @apiNote 函数行为和linux指令touch基本一致，唯一区别是可以自动建立沿途的所有文件夹。
     * @param file 要新建的文件路径，
     * @throws IOException 文件系统操作异常
     */
    public static void touch(File file) throws IOException {//确保一个文件存在，如果不存在就新建
        if(!file.exists())
        {
            File parent=file.getParentFile();
            if(parent==null||parent.isDirectory()){
                if(!file.createNewFile())throw new InvalidParameterException("文件已经存在"+file.getPath());
            }else{
                mkdir(parent);
                if(!file.createNewFile())throw new InvalidParameterException("文件已经存在"+file.getPath());
            }
        }
    }

    /**
     * 新建文件夹,与linux指令不同的是，可以沿途建立所有上级文件夹。
     * @param file 要建立的文件夹路径
     * @throws FileAlreadyExistsException 要建立的文件夹路径已经被一个文件占据
     * @throws AccessDeniedException 没有足够的权限来进行操作
     */
    public static void mkdir(File file) throws FileAlreadyExistsException, AccessDeniedException {
        if(file.exists()){
            if(file.isDirectory())
                return;//都已经存在了还干毛。
            else
                throw new InvalidParameterException("文件 "+file.getAbsolutePath()+" 已经存在");
        }

        File parent=file.getParentFile();//得到上级文件夹

        if(parent!=null){//如果父文件夹需要建立
            mkdir(parent);//建立父文件夹
        }
        if(!file.mkdir()) throw new InvalidParameterException("无法操作 "+file.getAbsolutePath());


    }

    /**
     * @apiNote 用于文件地址变换，将一个文件相对于某个文件夹的路径变换到相对于另一个文件夹。\
     * 比如递归复制文件夹的时候需要把每个源文件夹的文件路径变换为目标文件夹内的对应路径。
     * @param file 要变换路径的文件
     * @param originBase 原始的路径基准
     * @param targetBase 新的路径基准
     * @return 新的路径
     */
    public static File changeBase(File file, File originBase, File targetBase) {//将一个文件路径的rootFrom部分替换为rootTo
        String filepath=null,root1=null,root2=null;
        try {
            filepath = "*+-.,<>?'\";:]}[{|\\=+-_\0/0)9(8*&^%$#@!`~////\\\\\\\\" + file.getCanonicalPath();//用一个不可能在文件路径中出现的字符串确保替换只发生在路径开始。
            root1 = "*+-.,<>?'\";:]}[{|\\=+-_\0/0)9(8*&^%$#@!`~////\\\\\\\\" + originBase.getCanonicalPath();
            root2 = targetBase.getCanonicalPath();
        }catch (IOException e){
            //这个异常其实根本不会发生，getCanonicalPath()内部代码只会在发现\0字符后抛出这个异常，并没有复杂的格式检查。
            e.printStackTrace();
            exit(1);
        }
        return new File(filepath.replace(root1, root2));
    }

    /**
     * @param regex 用于搜索的正则表达式
     * @param file 用于匹配是数据来源
     * @return 返回所有file中能找到匹配regex的子字符串的行。
     */
    public static ArrayList<String> grep(String regex, InputStream file){
        Pattern pattern=Pattern.compile(regex);
        Scanner in=new Scanner(file);
        ArrayList<String> result=new ArrayList<>();
        while (in.hasNextLine()){//循环遍历文件的每一行，
            String str=in.nextLine();
            Matcher matcher=pattern.matcher(str);
            if(matcher.find()){//如果找到了
                result.add(str);
            }
        }
        return result;
    }

    /**
     * 判断一个字符串是否符合regex的描述
     * @param regex 正则表达式
     * @param str 要匹配的字符串
     * @return 是否匹配
     */
    public boolean match(String regex,String str){
        Pattern pattern=Pattern.compile(regex);
        Matcher m=pattern.matcher(str);
        return m.matches();
    }

    /**
     * 递归删除文件和文件夹 相当于rm -R -d
     * @param file 要删除的文件或文件夹
     */
    public static void rmRd(File file) throws AccessDeniedException {
        if(file.exists()){
            if(file.isFile()){
                if(!file.delete())throw new InvalidParameterException("删除操作被拒绝"+file.getPath());
            }
            else if(file.isDirectory()){//如果是一个文件夹，就递归删除所有下级文件夹和文件。
                try {
                    new FileTreeTravelManager(file).travel(new FileTreeTraveler() {//遍历下级文件和文件夹。
                        @Override
                        public boolean shouldTravelIntoFolder(int layer_level, File folder) {
                            return true;
                        }

                        @Override
                        public void travelFolder(int layer_level, File folder, List<String> result) throws AccessDeniedException {
                            boolean res;
                            res=folder.delete();
                            if(!res)throw new InvalidParameterException("删除操作被拒绝"+folder.getPath());
                        }

                        @Override
                        public void travelFile(int layer_level, File file, List<String> result) throws AccessDeniedException {
                            boolean res;
                            res=file.delete();
                            if(!res)throw new InvalidParameterException("删除操作被拒绝"+file.getPath());
                        }
                    });
                }
                catch (FileNotFoundException e) {
                    //如果文件没找到就正好，什么也不干直接退出。
                }
                catch (RuntimeException e){
                    throw e;
                } catch (Exception e){
                    e.printStackTrace();
                    exit(1);
                    //什么也不做，因为根本不会执行到这里
                }
            }
        }
    }

    /**
     * 列出一个文件夹下面的所有文件
     * @param file 目标文件夹，如果是一个文件就列出它自身
     * @param recursive 是否递归穷举文件夹的子文件夹
     * @param fileOnly 是否只列出文件，忽略文件夹
     * @return 列出的文件列表
     */
    public static ArrayList<File> ls(File file,boolean recursive,boolean fileOnly){
        ArrayList<File> result=new ArrayList<>();
        try {
            new FileTreeTravelManager(file).travel(new FileTreeTraveler() {
                @Override
                public boolean shouldTravelIntoFolder(int layer_level, File folder){
                    if(layer_level>0)
                        return recursive;
                    else
                        return true;
                }

                @Override
                public void travelFolder(int layer_level, File folder, List<String> res) {
                    if(!fileOnly&&layer_level>0)
                        result.add(folder);
                }

                @Override
                public void travelFile(int layer_level, File file, List<String> res){
                    result.add(file);
                }
            });
        }
        catch (FileNotFoundException e) {
            result.clear();
            return result;//如果目标不存在就返回空列表。
        }
        catch (RuntimeException e){
            throw e;
        }
        catch (Exception e){
            e.printStackTrace();
            exit(1);
            //什么也不做，因为根本不会执行到这里
        }
        return result;
    }
    public static ArrayList<File> ls(File file){
        return ls(file,false,false);
    }

    /**
     * 查找一个文件夹下面的名字符合特定特征的文件或文件夹，搜索范围包括它自己
     * @param rootFolder 要搜索的目录
     * @param regex 文件名匹配的正则表达式
     * @param findDirectory 为true则搜索文件夹，为false则搜索文件。
     * @return 返回搜索到的文件或文件夹列表
     */
    public static ArrayList<File> find(File rootFolder,String regex,boolean findDirectory){
        ArrayList<File> list=new ArrayList<>();
        try {
            new FileTreeTravelManager(rootFolder).travel(new FileTreeTraveler() {
                @Override
                public boolean shouldTravelIntoFolder(int layer_level, File folder){
                    return true;
                }

                @Override
                public void travelFolder(int layer_level, File folder, List<String> result) {
                    if(findDirectory){
                        Pattern p=Pattern.compile(regex);
                        Matcher m=p.matcher(folder.getName());
                        if(m.matches())list.add(folder);
                    }
                }

                @Override
                public void travelFile(int layer_level, File file, List<String> result) {
                    if(!findDirectory){
                        Pattern p=Pattern.compile(regex);
                        Matcher m=p.matcher(file.getName());
                        if(m.matches())list.add(file);
                    }
                }
            });
        } catch (FileNotFoundException e) {
            list.clear();
            return list;
        }
        catch (RuntimeException e){
            throw e;
        }
        catch (Exception e){
            e.printStackTrace();
            exit(1);
            //什么也不做，因为根本不会执行到这里
        }
        return list;
    }
    public static ArrayList<File> find(File rootFolder,String regex){//不指定搜索文件还是文件夹时默认搜索文件
        return find(rootFolder,regex,false);
    }

    /**
     * 清除一个文件夹下面的所有空文件夹,不包括它自己
     * @param rootFolder 要清理的根目录
     */
    public static void purge(File rootFolder) throws AccessDeniedException {
        try {
            new FileTreeTravelManager(rootFolder).travel(new FileTreeTraveler() {
                @Override
                public boolean shouldTravelIntoFolder(int layer_level, File folder){
                    return true;
                }

                @Override
                public void travelFolder(int layer_level, File folder, List<String> result) throws AccessDeniedException {
                    if(layer_level==0)return;//不对根目录进行操作
                    File[] files = folder.listFiles();
                    if(files==null||files.length==0){
                        boolean success=folder.delete();
                        if(!success) throw new InvalidParameterException("无法删除文件夹"+folder.getPath());
                    }
                }

                @Override
                public void travelFile(int layer_level, File file, List<String> result){
                }
            });
        } catch (FileNotFoundException e) {
            //什么也不做，如果目标根目录找不到就不需要做什么。
        }
        catch (RuntimeException e){
            throw e;
        } catch (Exception e){
            e.printStackTrace();
            exit(1);
            //什么也不做，因为根本不会执行到这里
        }
    }
}
