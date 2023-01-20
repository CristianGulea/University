import java.util.Objects;


public class LinkedList {
    Node head;
    Node lastNode;

    LinkedList(int maxPower){
        lastNode = new Node(new Pair<>(-1, maxPower), null);
        head = new Node(new Pair<>(-1, -1), lastNode);
    }

    public void sortInsert(Pair<Integer, Integer> data){
        Node prevNode = head;
        prevNode.lock.lock();
        Node currentNode = head.nextNode;
        currentNode.lock.lock();

        while ((currentNode != lastNode) && (currentNode.data.getRight() < data.getRight())) {
            prevNode.lock.unlock();
            prevNode = currentNode;
            currentNode.nextNode.lock.lock();
            currentNode = currentNode.nextNode;
        }
        if (Objects.equals(currentNode.data.getRight(), data.getRight())) {
            int sum = currentNode.data.getLeft() + data.getLeft();
            if (sum != 0) {
                currentNode.data.setLeft(sum);
            } else {
                prevNode.nextNode = currentNode.nextNode;
            }
        } else {
            prevNode.nextNode = new Node(data, currentNode);
        }
        prevNode.lock.unlock();
        currentNode.lock.unlock();
    }


    public void printList()
    {
        Node tempNode = this.head.nextNode;
        while (tempNode.nextNode != null) {
            if (!tempNode.data.getLeft().toString().contains("-")){
                System.out.print(" +" + tempNode.data.getLeft() + "x^" + tempNode.data.getRight());
            }
            else {
                System.out.print(" " + tempNode.data);
            }
            tempNode = tempNode.nextNode;
        }
    }
}

