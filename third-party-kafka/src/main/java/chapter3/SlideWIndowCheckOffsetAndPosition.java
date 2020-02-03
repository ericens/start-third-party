package chapter3;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 代码清单3-2
 * Created by ericens
 *
 *  通过有序数组多线程处理，kafka消息
 */

@Slf4j
public class SlideWIndowCheckOffsetAndPosition {


    static  OffsetQueueManager offsetQueueManager=new OffsetQueueManager();
    static KafkaConsumer consumer;
    public static final String brokerList = "localhost:9092";
    public static final String topic = "topic-demo";
    public static final String groupId = "group.demo";
    private static AtomicBoolean running = new AtomicBoolean(true);

    static ExecutorService executor = Executors.newFixedThreadPool(10);


    public static void main(String[] args) throws InterruptedException {
        SlideWIndowCheckOffsetAndPosition slideWIndowCheckOffsetAndPosition=new SlideWIndowCheckOffsetAndPosition();
        slideWIndowCheckOffsetAndPosition.start();

    }

    public void start() throws InterruptedException {
        Properties props = initConfig();
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList("topicx"));
        while (true) {
            if(offsetQueueManager.size() < 20){
                ConsumerRecords<String, String> records = consumer.poll(1000);
                if (records.isEmpty()) {
                    continue;
                }
                List<ConsumerRecord> list = new ArrayList();
                records.iterator().forEachRemaining(
                        i -> list.add((ConsumerRecord) i)
                );
                long lastConsumedOffset = list.get(list.size() - 1).offset();
                OffsetObject offsetObject=new OffsetObject(lastConsumedOffset,false);
                offsetQueueManager.addToQueue(offsetObject);

                executor.submit(new BatchRecordProcess(list,offsetObject));
            }else{
                TimeUnit.SECONDS.sleep(2);
            }

        }
    }


    public static Properties initConfig() {
        Properties props = new Properties();
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }






    @Test
    public void test() throws InterruptedException {
        int size=10;
        CountDownLatch countDownLatch=new CountDownLatch(size);
        AtomicLong offset=new AtomicLong(0);
        for (int i = 0; i < size; i++) {
            new Thread(new Runnable() {

                @Override
                public void run() {

                    for (int j = 0; j < 100; j++) {

                        try {
                            TimeUnit.MILLISECONDS.sleep(RandomUtils.nextLong(0,2000));
                            offset.set( offset.get()+ RandomUtils.nextLong(1,200));
                            OffsetObject offsetObject=new OffsetObject(offset.get(),false);

                            offsetQueueManager.addToQueue(offsetObject);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    countDownLatch.countDown();
                }
            }).start();

            countDownLatch.await();
        }

    }

    public static class OffsetObject implements Comparable {
        long toCommitOffset;
        boolean isConsumed;

        public long getToCommitOffset() {
            return toCommitOffset;
        }

        public void setToCommitOffset(long toCommitOffset) {
            this.toCommitOffset = toCommitOffset;
        }

        public boolean isConsumed() {
            return isConsumed;
        }

        public void setConsumed(boolean consumed) {
            isConsumed = consumed;
        }

        public OffsetObject(long toCommitOffset, boolean isConsumed) {
            this.toCommitOffset = toCommitOffset;
            this.isConsumed = isConsumed;

            OffsetObject offsetObject=this;
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        TimeUnit.MILLISECONDS.sleep(RandomUtils.nextLong(500,4000));
                        log.info("consumed the offset :{}", offsetObject.getToCommitOffset());
                        offsetQueueManager.setConsumed(offsetObject);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }



                }
            }).start();
        }

        @Override
        public int compareTo(Object o) {
            OffsetObject to=(OffsetObject)o;
            return (int)(this.getToCommitOffset()-to.getToCommitOffset());
        }
    }

    public static class OffsetQueueManager {
        static SortedSet<OffsetObject> offsetQueue = new TreeSet<>();


        public synchronized void addToQueue(OffsetObject offsetObject){
            offsetQueue.add(offsetObject);
            log.info("queue state:{}", JSON.toJSON(offsetQueue));
        }

        public int size(){
            return offsetQueue.size();
        }

        public synchronized void setConsumed(OffsetObject offsetObject ){
            Object offsetObjects[]= offsetQueue.toArray();
            for (int i = 0; i < offsetObjects.length ; i++) {
                OffsetObject object= (OffsetObject) offsetObjects[i];
                if(object.getToCommitOffset()== offsetObject.getToCommitOffset()){
                    object.setConsumed(true);
                }
            }

            OffsetObject offsetObject1=offsetQueue.first();
            while (offsetObject1.isConsumed()){
                offsetQueue.remove(offsetObject1);
                //按照partition来提交
                Map m=new HashMap();
                m.put("tp",offsetObject1.getToCommitOffset());
                //todo 测试去掉
//                consumer.commitSync(m);
               log.info("submit the offset {} ",offsetObject1.getToCommitOffset());

                //查看下一个是否也已经提交了
                if(offsetQueue.size()>0){
                    offsetObject1=offsetQueue.first();
                }else{
                    break;
                }


            }


        }
    }






    public static class BatchRecordProcess implements Runnable {
        List<ConsumerRecord> records;
        OffsetObject offsetObject;
        public BatchRecordProcess(List<ConsumerRecord> records, OffsetObject offsetObject) {
            this.records = records;
            this.offsetObject=offsetObject;
        }


        @Override
        public void run() {

            //do process record
            System.out.println("consume the offset "+offsetObject.getToCommitOffset());

            //提交
            offsetQueueManager.setConsumed(offsetObject);


        }
    }

}

