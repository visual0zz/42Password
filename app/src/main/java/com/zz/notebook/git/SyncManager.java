package com.zz.notebook.git;

import com.zz.notebook.util.BasicService;

import org.eclipse.jgit.api.Git;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class SyncManager implements Serializable {
    private static final String repoPath=BasicService.getGitpath();//todo delete
    private static final String addressFilePath=BasicService.getRemoteAddressFilePath();//todo delete

    public static boolean isRepository(File file){//是否有仓库
        Git git=null;
        try {
            git=Git.open(file);
        } catch (IOException e) {
            return false;
        }finally {
            if(git!=null)git.close();
        }
        return true;
    }
    public static boolean hasRemote(){
        return new File(BasicService.getRemoteAddressFilePath()).exists();
    }
    public static void setRemote(){//设置远程的地址

    }



}
