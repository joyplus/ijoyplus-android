package com.joyplus.Video;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.joyplus.widget.Log;
/**
 * 线程池
 * @author 
 */
public class HttpThreadPoolUtils {
    
	//单列子
	private HttpThreadPoolUtils(){
		
	}
	private static int CORE_POOL_SIZE = 3;
	private static int MAX_POOL_SIZE = 100;
	private static int KEEP_ALIVE_TIME = 10000;
	private static BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(10);
	private static ThreadFactory threadFactory = new ThreadFactory() {
		private final AtomicInteger integer = new AtomicInteger();
		public Thread newThread(Runnable r) {
			return new Thread(r, "myThreadPool thread:" + integer.getAndIncrement());
		}
		
	};
	private static ThreadPoolExecutor httpthreadPool;
	static {
		httpthreadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME,
				TimeUnit.SECONDS, workQueue, threadFactory);
		
	}

	public static void execute(Runnable runnable) {
		httpthreadPool.execute(runnable);
		
	}
	private synchronized static int getQueueSize(Queue queue)  
    {  
        return queue.size();  
    }   
	public static void shutdown(){
		httpthreadPool.shutdown();
	}
}
