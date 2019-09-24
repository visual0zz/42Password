package com.zz.notebook.git;

import com.zz.notebook.util.BasicService;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import static com.zz.notebook.util.BasicService.getTmpDatabaseFilePath;
import static java.lang.System.exit;

public class SyncManager implements Serializable {

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
