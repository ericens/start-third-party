package redis.lock;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class LockTest {

    @Test
    public void lockInTest(){
        RedisLock lock=new RedisLock("12");
        lock.lock();
        log.info("acquire lock");
        lock.unlock();
        log.info("unlock");
    }

    @Test
    public void twoLockTest() throws InterruptedException {
        RedisLock lock=new RedisLock("11");
        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                lock.lock();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info("acquire lock");
                lock.unlock();
            }).start();
        }

        Thread.sleep(10*1000);


    }
}
