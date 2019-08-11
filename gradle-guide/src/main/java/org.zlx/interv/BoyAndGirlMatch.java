package org.zlx.interv;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;

import java.util.Date;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 *
 * 怎么设计，一个容器有男有女，进行匹配。如果10分钟内，男女都没有匹配，从容器中删除
 */

@Slf4j
public class BoyAndGirlMatch {

	DelayQueue<People> boyQueue =new DelayQueue();
	DelayQueue<People> girlQueue =new DelayQueue();
	final static Long MAX_MATCH_TIME =10*1000L;

	@Data
	static class People implements Delayed {
		Boolean isBoy;
		String name;
		Long insertTime= System.currentTimeMillis();;

		@Override
		public long getDelay(TimeUnit unit) {
			return insertTime- System.currentTimeMillis()+ MAX_MATCH_TIME;
		}

		@Override
		public int compareTo(Delayed o) {
			People x=(People)o;
			return (this.insertTime - x.getInsertTime())>= 0 ?1:-1;
		}


		public People(String name, Boolean isBoy) {
			this.name=name;
			this.isBoy=isBoy;
		}

		@Override
		public String toString() {
			return "People{" +
					"isBoy=" + isBoy +
					", name='" + name + '\'' +
					", insertTime=" + new Date(insertTime) +
					'}';
		}
	}



	public void put(People newPeople) {
		DelayQueue toAddQueue;
		DelayQueue<People> toMatchQueue;


		if(newPeople.isBoy==true){
			toAddQueue= boyQueue;
			toMatchQueue= girlQueue;

		}else {
			toAddQueue= girlQueue;
			toMatchQueue= boyQueue;
		}

		if(toMatchQueue!=null && toMatchQueue.size()!=0){
			// 队列有
			People old=toMatchQueue.iterator().next();

			//直接删除
			toMatchQueue.remove(old);
			log.info("maching success:  new:{},old:{}", newPeople,old);
		}
		else {
			toAddQueue.put(newPeople);
			log.info("add people :{}", newPeople);
		}
	}




	class MyTask implements Runnable {
		private DelayQueue<People> delayQueue;

		public MyTask(DelayQueue<People> delayQueue){
			this.delayQueue=delayQueue;
		}
		@Override
		public void run() {
			while (true){
				try {
					People people=delayQueue.take();
					log.info("match fail:{}, now:{}",people,new Date(System.currentTimeMillis()));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void match(){
		//启动线程，只有超时没有配对成功，则配对失败。
		new Thread(new MyTask(boyQueue)).start();
		new Thread(new MyTask(girlQueue)).start();


		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true){
					log.info("\t\t\tboyQueue size：{}", boyQueue.size());
					log.info("\t\t\tgirlQueue size：{}", girlQueue.size());
					try {
						Thread.sleep(5*1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	public static void main(String[] args) throws InterruptedException {
		BoyAndGirlMatch boyAndGirlMatch =new BoyAndGirlMatch();
		boyAndGirlMatch.match();
		for(int i=0;i<1000;i++){
			int genderFlag=RandomUtils.nextInt(0,1000);
			Boolean gender=(genderFlag%2==0?true:false);
			People people=new People("name"+i,gender);
			boyAndGirlMatch.put(people);
			Thread.sleep(500);
		}

	}
}
