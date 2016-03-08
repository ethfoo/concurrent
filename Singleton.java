package com.ethfoo.patternTest;

public class Singleton {
	private static Object lock = new Object();
	private static volatile Singleton instance = null;
	
	private Singleton(){}
	
	/*
	 * version0. 非线程安全，比如当线程A判断instance==null,此时还没有执行new Singleton，然后线程B也判断instance==null,
	 * 这样会导致两个线程都创建了Singleton实例。
	 */
	public static Singleton getInstance0(){
		if( instance == null){
			instance = new Singleton();
		}
		return instance;
	}
	
	/*
	 * version1. 我们可以对getInstance方法加synchronized，这里让锁更加细粒，加在里面
	 */
	public static Singleton getInstance1(){
		synchronized(lock){
			if( instance == null){
				instance = new Singleton();
			}
		}
		
		return instance;
	}
	
	/*
	 * version2. version1有个问题，每次要判断instance==null，都需要进入锁，同步。
	 * 但是单例只是实例化一次，其他时间都是读取实例，这样会影响性能，所以再加入一个判断instance==null,
	 * 这样如果单例已经实例化了就不需要同步了。
	 * 
	 * singleton = new Singleton()这句，这并非是一个原子操作，事实上在 JVM 中这句话大概做了下面 3 件事情。
		1.在堆中分配内存
		2.调用 Singleton 的构造函数来初始化成员变量，形成实例
		3.将singleton对象指向分配的内存空间（执行完这步 singleton才是非 null 了）
		但是在 JVM 的即时编译器中存在指令重排序的优化。也就是说上面的第二步和第三步的顺序是不能保证的，
		最终的执行顺序可能是 1-2-3 也可能是 1-3-2。
		在synchronized同步块里面，仅保证只有一个线程进入，但是有可能发生线程的切换，由于singleton=new Singleton()并非原子的操作，
		所以在执行步骤1-3时，可能会切换到另外一个线程B，但此时new Singleton()在堆内存空间还没初始化，
		然后线程B在下面代码的第一次进行singleton==null的判断时，由于Singleton在内存堆中已存在（但尚未初始化），
		会直接返回singleton引用，但此时引用指向的却是未初始化对象。
		对此，我们只需要把singleton声明成 volatile 就可以了。
		使用 volatile 有两个功用：
		1）这个变量不会在多个线程中存在复本，直接从内存读取。
		2）这个关键字会禁止指令重排序优化。也就是说，在 volatile 变量的赋值操作后面会有一个内存屏障（生成的汇编代码上），
		读操作不会被重排序到内存屏障之前。
	 * 
	 */
	public static Singleton getInstance2(){
		if( instance == null){
			synchronized(lock){
				if( instance == null){
					instance = new Singleton();
				}
			}
		}
		return instance;
	}
	
}

/*
 * 使用JVM本身机制保证了线程安全问题；由于 SingletonHolder 是私有的，除了 getInstance() 之外没有办法访问它，
 * 因此它只有在getInstance()被调用时才会真正创建；同时读取实例的时候不会进行同步，没有性能缺陷；
 */
class Singleton1{
	private static class SingletonHolder{
		private static final Singleton1 INSTANCE = new Singleton1();
	}
	
	private Singleton1(){}
	
	public static final Singleton1 getInstance(){
		return SingletonHolder.INSTANCE;
	}
}

/*
 * 使用枚举
 */
enum Singleton2{
	INSTANCE;
}


