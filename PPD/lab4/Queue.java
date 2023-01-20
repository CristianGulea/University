import java.util.Arrays;

public class Queue {
    private Pair[] array;
    private int capacity;
    private int currentSize;
    private int start;
    private int end;


    public Queue(int capacity) {
        this.array = new Pair[capacity];
        this.capacity = capacity;
        this.currentSize = 0;
        start = 0;
        end = -1;
    }

    public synchronized int size() {
        return this.currentSize;
    }

    public synchronized Pair<Integer, Integer> dequeue() throws Exception {
        if (size() == 0)
        {
            throw new Exception("Queue is empty!");
        }

        Pair<Integer, Integer> x = array[start];
        start += 1;
        this.currentSize--;
        return x;
    }

    public synchronized void enqueue(Pair<Integer, Integer> pair) throws Exception {
        if (size() == capacity)
        {
            throw new Exception("Queue is full!");
        }

        end += 1;
        array[end] = pair;
        this.currentSize++;
    }

    @Override
    public String toString() {
        return "Queue{" +
                "array=" + Arrays.toString(array) +
                ", capacity=" + capacity +
                ", currentSize=" + currentSize +
                ", front=" + start +
                ", rear=" + end +
                '}';
    }
}
