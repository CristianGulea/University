import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Calculator {
    public void sequentialCalculate(int maxNumberFile, int maxPower) throws IOException {
        String path = "C:\\ppd\\lab5\\Polinom";
        int numberFile = 1;
        LinkedList linkedList = new LinkedList(maxPower);
        while (numberFile <= maxNumberFile){
            String finalPath = path + numberFile;
            BufferedReader reader = new BufferedReader(new FileReader(finalPath));
            String line = reader.readLine();
            while (line != null){
                int coefficient = Integer.parseInt(line.split(" ")[0]);
                int power = Integer.parseInt(line.split(" ")[1]);
                linkedList.sortInsert(new Pair<>(coefficient, power));
                line = reader.readLine();
            }
            reader.close();
            numberFile += 1;
        }
        Utils.writePolynomialToFile(linkedList, "C:\\ppd\\lab5\\SecvResult");
    }

    public void threadCalculate(int maxNumberFile, int p1, int p2, int maxPower) throws Exception {
        String path = "C:\\ppd\\lab5\\Polinom";
        LinkedList linkedList = new LinkedList(maxPower);
        final int[] numberFile = {1};
        Queue queue = new Queue(1100);
        String nameVar = "thread";
        final int[] numberThread = {1};
        Map<String, Thread> threads = new HashMap<>();
        AtomicInteger atomicInteger = new AtomicInteger(0);

        int defaultLength = maxNumberFile / p1;
        int start = 0;
        int end = defaultLength;
        int rest = maxNumberFile % p1;

        while (numberThread[0] <= p1 + p2){
            String finaleNameVar = nameVar + numberThread[0];
            if (numberThread[0] <= p1) {
                if (rest > 0){
                    end++;
                    rest--;
                }
                int finalEnd = end;
                int finalStart = start;

                threads.put(finaleNameVar, new Thread(() -> {
                    try {
                        int startFile = finalStart;
                        while (startFile < finalEnd) {
                            String finalPath = path + (startFile + 1);
                            BufferedReader reader = new BufferedReader(new FileReader(finalPath));

                            synchronized (this) {
                                while (queue.isFull()) {
                                    //System.out.println("PRODUCER +" + finaleNameVar + " wait because queue is full." + " with: " + queue.size());
                                    wait();
                                }
                            }

                            String line = reader.readLine();
                            while (line != null) {
                                //Thread.sleep(10000);

                                int coefficient = Integer.parseInt(line.split(" ")[0]);
                                int power = Integer.parseInt(line.split(" ")[1]);

                                synchronized (this) {
                                    while (queue.isFull()) {
                                        //System.out.println("PRODUCER " + finaleNameVar + " wait because queue is full." + " with: " + queue.size());
                                        wait();
                                    }
                                    queue.enqueue(new Pair<>(coefficient, power));
                                }

                                synchronized (this) {
                                    //System.out.println("-----ALL START-----");
                                    notifyAll();
                                }
                                line = reader.readLine();
                            }
                            reader.close();
                            startFile += 1;
                        }
                        synchronized (this) {
                        atomicInteger.set(atomicInteger.get() + 1);
                        if (atomicInteger.get() == p1) {
                            for (int i = 0; i < p2; i++) {
                                queue.enqueue(new Pair<>(0, 0));
                                    //System.out.println("-----ALL START-----");
                                    notifyAll();
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }));

                start = end;
                end = start + defaultLength;
            }

            else {
                threads.put(finaleNameVar, new Thread(() -> {
                    try {
                        while (true){
                            synchronized (this) {
                                while (queue.size() == 0) {
                                    //System.out.println("CONSUMER " + finaleNameVar + " wait because queue is empty.");
                                    wait();
                                }
                            }

                            Pair<Integer, Integer> monom = queue.dequeue();
                            //System.out.println("DEQUEUE " + monom + "  size: " + queue.size());
                            synchronized (this) {
                                //System.out.println("-----ALL START-----");
                                notifyAll();
                            }
                            if (monom.getLeft() == 0 && (monom.getRight() == 0)){
                                break;
                            }else {
                                linkedList.sortInsert(monom);
                            }

                        }
                    } catch (Exception e) {
                        //System.out.println(finaleNameVar + " queue empty!");
                    }

                }));
            }
            numberThread[0] += 1;
        }

        threads.forEach((key, thread) -> {thread.start();});

        threads.forEach((key, thread) -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Utils.writePolynomialToFile(linkedList, "C:\\ppd\\lab5\\ThreadResult");

    }

}