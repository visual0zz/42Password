package com.zz.notebook.ui.gallery;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.zz.notebook.R;
import com.zz.notebook.util.BasicService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Objects;
import java.util.Scanner;
import java.util.logging.Logger;

public class TestFragment extends Fragment {
    Logger logger= Logger.getLogger(TestFragment.class.getName());

    private TestViewModel galleryViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(TestViewModel.class);
        View root = inflater.inflate(R.layout.fragment_test, container, false);
        Button button=root.findViewById(R.id.test_button1);
        test();
        button.setOnClickListener(view->{test();});
        return root;
    }

    void test(){
        Context context=getContext();
        BasicService.toast("测试开始");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            String targetPath=getContext().getDataDir().getPath()+"/testfile";
            logger.info("正在打开文件:"+targetPath);
            try {
                PrintStream out=new PrintStream(new FileOutputStream(targetPath,true));
                out.println("smgfasdfsadf");

                logger.info("文件写入完成");
                out.flush();
                Scanner in=new Scanner(new FileInputStream(targetPath));
                logger.info("读入了:"+in.nextLine());
                int cout=0;
                while(in.hasNext()){
                    in.nextLine();cout++;
                }
                logger.info("读入了"+cout+"行");

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else {logger.warning("android版本过低");}
    }
    void test2(){
    }

}