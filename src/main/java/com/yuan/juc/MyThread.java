package com.yuan.juc;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @Description: 多线程, 异步非阻塞, 有返回，不轮询切换线程导致cpu空转
 * @Author: yfc
 * @Date: 2023/10/4 07:24
 */
public class MyThread {
	public String name;

	ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
//	ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();

	CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
		System.out.println("111");
		System.out.println(Thread.currentThread().getName());
		return "success";
	}, threadPoolExecutor);

	public static void main(String[] args) {
		try {
			Thread.sleep(1, TimeUnit.SECONDS.ordinal());
		}
		catch (Exception e) {
			System.out.println("error");
		}
		MyThread myThread = new MyThread();
		System.out.println(myThread.completableFuture.join());
	}
}
