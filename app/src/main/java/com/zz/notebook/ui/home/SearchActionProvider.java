package com.zz.notebook.ui.home;

public interface SearchActionProvider {//用于让Activity发生搜索事件后回调HomeFragment来处理搜索
    void doSearch(String str);
}
