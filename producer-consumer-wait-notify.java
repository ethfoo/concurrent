
public class MainTest {
	private static final int MAXSIZE = 5;

	
	public static void main(String[] args) {
	
		Deque<Integer> queue = new LinkedList<>(); 
		
		Producer producer = new Producer(queue, MAXSIZE);
		Consumer consumer = new Consumer(queue, MAXSIZE);
		
		new Thread(producer, "producer").start();
		new Thread(consumer, "consumer").start();
	}
	

	 
}


class Producer implements Runnable{		
	 private final Deque<Integer> queue;
	 private final int maxSize;
	 Producer(Deque queue, int maxSize){
		 this.queue = queue;
		 this.maxSize = maxSize;
	 }

	@Override
	public void run() {
		
		while(true){
			
			synchronized (queue) {
				while( queue.size() >= maxSize){
					try {
						//System.out.println("queue is full");
						queue.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}	
				}
				Random random = new Random();
				int i = random.nextInt(100);
				queue.offer(i);
				//System.out.println("producer offer " + i);
				queue.notifyAll();
				
			}
		}
	}
}

class Consumer implements Runnable{
	 private final Deque<Integer> queue;
	 private final int maxSize;
	 Consumer(Deque queue, int maxSize){
		 this.queue = queue;
		 this.maxSize = maxSize;
	 }
	 
	@Override
	public void run() {
		while(true){
			
			synchronized (queue){
				while( queue.size() <= 0){
					try{
					//	System.out.println("queue is empty");
						queue.wait();
					}catch(InterruptedException e){
						e.printStackTrace();
					}
				}
				
				Random random = new Random();
				int i = random.nextInt(100);
				queue.poll();
				//System.out.println("consumer poll " + i);
				queue.notifyAll();
				
			}
			
		}
	}
	 
}
