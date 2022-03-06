package com.ulto.conn;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public interface IUltoBridge {

    public ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

    public void join(UltoPeers.UltoPeer source, UltoPeers.UltoPeer destination) throws Exception;
}
