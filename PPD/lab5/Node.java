import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Node {
    Pair<Integer, Integer> data;
    Node nextNode;
    Lock lock = new ReentrantLock();

    public Node(Pair<Integer, Integer> data, Node nextNode) {
        this.data = data;
        this.nextNode = nextNode;
    }
}
