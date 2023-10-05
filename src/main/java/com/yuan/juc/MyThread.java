package com.yuan.juc;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @Description: 多线程, 异步非阻塞, 有返回，不轮询切换线程导致cpu空转
 * @Author: yfc
 * @Date: 2023/10/4 07:24
 */
public class MyThread {
	public String name;
	public int a;

	//使用线程池，注意线程池的策略
	ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);

	//spring的线程池
    //ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();

//	CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> {
//		a = 1;
//		System.out.println(Thread.currentThread().getName());
//		return a;
//		//thenApplyAsync后的任务是丢到线程池当中执行的，不是由执行上一个任务的线程执行的
//		//thenApply后的任务是由执行上一个任务的线程接着执行的
//	}, threadPoolExecutor).thenApplyAsync((x)->{
//		x = x+2;
//		return x;
//	}).whenComplete((r,e)->{
//		r = r+3;
//		System.out.println("result is :"+r);
//		//线程池用完后手动关闭，否则程序一直处于运行状态
//		threadPoolExecutor.shutdown();
//	});


	public void runMyThing(){
		CompletableFuture<Integer> completableFuture1 = CompletableFuture.supplyAsync(() -> {
			a = 1;
			System.out.println(Thread.currentThread().getName());
			return a;
			//thenApplyAsync后的任务是丢到线程池当中执行的，不是由执行上一个任务的线程执行的
			//thenApply后的任务是由执行上一个任务的线程接着执行的
		}, threadPoolExecutor).thenApplyAsync((x)->{
			x = x+2;
			return x;
		}).whenComplete((r,e)->{
			r = r+3;
			System.out.println("result is :"+r);
			//线程池用完后手动关闭，否则程序一直处于运行状态
			threadPoolExecutor.shutdown();
		});
	}

	/**
	 * 6.1 AND组合关系
	 * thenCombine / thenAcceptBoth / runAfterBoth都表示：「当任务一和任务二都完成再执行任务三」。
	 *
	 * 区别在于：
	 *
	 * 「runAfterBoth」 不会把执行结果当做方法入参，且没有返回值
	 *
	 * 「thenAcceptBoth」: 会将两个任务的执行结果作为方法入参，传递到指定方法中，且无返回值
	 *
	 * 「thenCombine」：会将两个任务的执行结果作为方法入参，传递到指定方法中，且有返回值
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@Test
	public void testCompletableThenCombine() throws ExecutionException, InterruptedException {
		//创建线程池
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		//开启异步任务1
		CompletableFuture<Integer> task = CompletableFuture.supplyAsync(() -> {
			System.out.println("异步任务1，当前线程是：" + Thread.currentThread().getId());
			int result = 1 + 1;
			System.out.println("异步任务1结束");
			return result;
		}, executorService);

		//开启异步任务2
		CompletableFuture<Integer> task2 = CompletableFuture.supplyAsync(() -> {
			System.out.println("异步任务2，当前线程是：" + Thread.currentThread().getId());
			int result = 1 + 1;
			System.out.println("异步任务2结束");
			return result;
		}, executorService);

		//任务组合
		CompletableFuture<Integer> task3 = task.thenCombineAsync(task2, (f1, f2) -> {
			System.out.println("执行任务3，当前线程是：" + Thread.currentThread().getId());
			System.out.println("任务1返回值：" + f1);
			System.out.println("任务2返回值：" + f2);
			return f1 + f2;
		}, executorService);

		Integer res = task3.get();
		System.out.println("最终结果：" + res);
	}


	/**
	 * 6.2 OR组合关系
	 * applyToEither / acceptEither / runAfterEither 都表示：「两个任务，只要有一个任务完成，就执行任务三」。
	 *
	 * 区别在于：
	 *
	 * 「runAfterEither」：不会把执行结果当做方法入参，且没有返回值
	 *
	 * 「acceptEither」: 会将已经执行完成的任务，作为方法入参，传递到指定方法中，且无返回值
	 *
	 * 「applyToEither」：会将已经执行完成的任务，作为方法入参，传递到指定方法中，且有返回值
	 * ————————————————
	 * 版权声明：本文为CSDN博主「熊出没」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
	 * 原文链接：https://blog.csdn.net/weixin_40141628/article/details/132164984
	 */
	@Test
	public void testCompletableEitherAsync() {
		//创建线程池
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		//开启异步任务1
		CompletableFuture<Integer> task = CompletableFuture.supplyAsync(() -> {
			System.out.println("异步任务1，当前线程是：" + Thread.currentThread().getId());

			int result = 1 + 1;
			System.out.println("异步任务1结束");
			return result;
		}, executorService);

		//开启异步任务2
		CompletableFuture<Integer> task2 = CompletableFuture.supplyAsync(() -> {
			System.out.println("异步任务2，当前线程是：" + Thread.currentThread().getId());
			int result = 1 + 2;
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("异步任务2结束");
			return result;
		}, executorService);

		//任务组合
		task.acceptEitherAsync(task2, (res) -> {
			System.out.println("执行任务3，当前线程是：" + Thread.currentThread().getId());
			System.out.println("上一个任务的结果为："+res);
		}, executorService);
	}


	/**
	 * 6.3 多任务组合
	 * 「allOf」：等待所有任务完成
	 *
	 * 「anyOf」：只要有一个任务完成
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@Test
	public void testCompletableAallOf() throws ExecutionException, InterruptedException {
		//创建线程池
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		//开启异步任务1
		CompletableFuture<Integer> task = CompletableFuture.supplyAsync(() -> {
			System.out.println("异步任务1，当前线程是：" + Thread.currentThread().getId());
			int result = 1 + 1;
			System.out.println("异步任务1结束");
			return result;
		}, executorService);

		//开启异步任务2
		CompletableFuture<Integer> task2 = CompletableFuture.supplyAsync(() -> {
			System.out.println("异步任务2，当前线程是：" + Thread.currentThread().getId());
			int result = 1 + 2;
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("异步任务2结束");
			return result;
		}, executorService);

		//开启异步任务3
		CompletableFuture<Integer> task3 = CompletableFuture.supplyAsync(() -> {
			System.out.println("异步任务3，当前线程是：" + Thread.currentThread().getId());
			int result = 1 + 3;
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("异步任务3结束");
			return result;
		}, executorService);

		//任务组合
		CompletableFuture<Void> allOf = CompletableFuture.allOf(task, task2, task3);

		//等待所有任务完成
		allOf.get();
		//获取任务的返回结果
		System.out.println("task结果为：" + task.get());
		System.out.println("task2结果为：" + task2.get());
		System.out.println("task3结果为：" + task3.get());
	}


	/**
	 * 总结
	 * join()方法抛出的是unchecked异常（即RuntimeException),不会强制开发者抛出，会将异常包装成CompletionException异常 /CancellationException异常，但是本质原因还是代码内存在的真正的异常，在运行时会抛出
	 *
	 * get()方法抛出的是经过检查的异常，ExecutionException, InterruptedException 需要用户手动处理（抛出或者 try catch）
	 *
	 * 7.2 CompletableFuture的get()方法是阻塞的
	 * CompletableFuture的get()方法是阻塞的，如果使用它来获取异步调用的返回值，需要添加超时时间。
	 *
	 *
	 *
	 * 7.3、不建议使用默认线程池
	 * CompletableFuture代码中又使用了默认的 「ForkJoin线程池」，处理的线程个数是电脑 「CPU核数-1」。在大量请求过来的时候，处理逻辑复杂的话，响应会很慢。一般建议使用自定义线程池，优化线程池配置参数。
	 *
	 * 7.4、自定义线程池时，注意饱和策略
	 * CompletableFuture的get()方法是阻塞的，我们一般建议使用future.get(5, TimeUnit.SECONDS)。并且一般建议使用自定义线程池。
	 *
	 * 但是如果线程池拒绝策略是DiscardPolicy或者DiscardOldestPolicy，当线程池饱和时，会直接丢弃任务，不会抛弃异常。因此建议，CompletableFuture线程池策略最好使用AbortPolicy，然后耗时的异步线程，做好线程池隔离。
	 * ————————————————
	 * 版权声明：本文为CSDN博主「熊出没」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
	 * 原文链接：https://blog.csdn.net/weixin_40141628/article/details/132164984
	 * @param args
	 */


	public static void main(String[] args) {
		//主线程不能立即结束，否则会关闭守护线程
		try {
			Thread.sleep(1, TimeUnit.SECONDS.ordinal());
		}
		catch (Exception e) {
			System.out.println("error");
		}
		MyThread myThread = new MyThread();
		myThread.runMyThing();

		//join()就是获取线程执行后的返回结果，和get()效果一样，会阻塞线程，区别在于不抛出编译异常
//		System.out.println(myThread.completableFuture.join());

	}


	/**
	 * 3.1 应用场景
	 * 1️⃣ 执行比较耗时的操作时，尤其是那些依赖一个或多个远程服务的操作，使用异步任务可以改善程序的性能，加快程序的响应速度；
	 * 2️⃣ 使用CompletableFuture类，它提供了异常管理的机制，让你有机会抛出、管理异步任务执行种发生的异常；
	 * 3️⃣ 如果这些异步任务之间相互独立，或者他们之间的的某一些的结果是另一些的输入，你可以讲这些异步任务构造或合并成一个。
	 *
	 * 举个常见的案例 ，在APP查询首页信息的时候，一般会涉及到不同的RPC远程调用来获取很多用户相关信息数据，
	 * 比如：商品banner轮播图信息、用户message消息信息、用户权益信息、用户优惠券信息 等，
	 * 假设每个rpc invoke()耗时是250ms，那么基于同步的方式获取到话，算下来接口的RT至少大于1s，
	 * 这响应时长对于首页来说是万万不能接受的，因此，我们这种场景就可以通过多线程异步的方式去优化。
	 */


}
