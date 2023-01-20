import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class Calculator {
    public void sequentialCalculate(int maxNumberFile) throws IOException {
        String path = "C:\\ppd\\lab4\\Polinom";
        int numberFile = 1;
        LinkedList linkedList = new LinkedList();
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
        Utils.writePolynomialToFile(linkedList, "C:\\ppd\\lab4\\SecvResult");
    }

    public void threadCalculate(int maxNumberFile, int p) throws Exception {
        AtomicBoolean stillReading = new AtomicBoolean(true);
        String path = "C:\\ppd\\lab4\\Polinom";
        LinkedList linkedList = new LinkedList();
        final int[] numberFile = {1};
        Queue queue = new Queue(1100);
        String nameVar = "thread";
        final int[] numberThread = {1};
        Map<String, Thread> threads = new HashMap<>();

        while (numberThread[0] <= p){
            String finaleNameVar = nameVar + numberThread[0];

            if (numberThread[0] == 1) {
                threads.put(finaleNameVar, new Thread(() -> {
                    try {
                        while (numberFile[0] <= maxNumberFile) {

                            String finalPath = path + numberFile[0];
                            BufferedReader reader = new BufferedReader(new FileReader(finalPath));
                            String line = reader.readLine();

                            while (line != null) {
                                //Thread.sleep(10000);
                                int coefficient = Integer.parseInt(line.split(" ")[0]);
                                int power = Integer.parseInt(line.split(" ")[1]);
                                queue.enqueue(new Pair<>(coefficient, power));
                                line = reader.readLine();
                                synchronized (this) {
                                    notify();
                                }
                            }

                            reader.close();
                            numberFile[0] += 1;
                        }

                        for (int i = 0; i < p; i++){
                            queue.enqueue(new Pair<>(0, 0));
                            synchronized (this) {
                                notifyAll();
                            }
                        }

                        Utils.writePolynomialToFile(linkedList, "C:\\ppd\\lab4\\ThreadResult");

                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }));
            }

            else {
                threads.put(finaleNameVar, new Thread(() -> {
                    try {
                        while (true){
                            if (queue.size() != 0) {
                                Pair<Integer, Integer> monom = queue.dequeue();
                                if (monom.getLeft() == 0 && (monom.getRight() == 0)){
                                    break;
                                }else {
                                    //System.out.println(finaleNameVar + " get " + monom);
                                    linkedList.sortInsert(monom);
                                }
                            }
                            else {
                                synchronized (this) {
                                    //System.out.println(finaleNameVar + " wait");
                                    wait();
                                }
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

    }

}
