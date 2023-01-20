import java.time.temporal.ChronoUnit;
import java.util.Currency;
import java.util.Objects;

public class LinkedList {
    Node head;
    Node currentNode;

    public synchronized void sortInsert(Pair<Integer, Integer> data){
        if (head == null){
            head = new Node(data, null);
            currentNode = head;
        }
        else {
            currentNode = head;
            while ((currentNode.nextNode != null)) {
                if (currentNode.nextNode.data.getRight() < data.getRight()) {
                    currentNode = currentNode.nextNode;
                    continue;
                }
                break;
            }
            if (Objects.equals(currentNode.data.getRight(), data.getRight())) {
                int sum = currentNode.data.getLeft() + data.getLeft();
                if (sum != 0){
                    currentNode.data.setLeft(sum);
                }
                else {
                    head = currentNode.nextNode;
                }
                return;
            }

            if (currentNode.nextNode != null) {
                if (Objects.equals(currentNode.nextNode.data.getRight(), data.getRight())) {
                    int sum = currentNode.nextNode.data.getLeft() + data.getLeft();
                    if (sum != 0){
                        currentNode.nextNode.data.setLeft(sum);
                    }
                    else {
                        if (currentNode.nextNode.nextNode == null){
                            currentNode.nextNode = null;
                        }
                        else{
                            currentNode.nextNode = currentNode.nextNode.nextNode;
                        }
                    }
                }
                else{
                    Node savedNode = currentNode.nextNode;
                    currentNode.nextNode = new Node(data, savedNode);
                }
            }
            else {
                if (Objects.equals(currentNode.data.getRight(), data.getRight())){
                    int sum = currentNode.data.getLeft() + data.getLeft();
                    if (sum != 0){
                        currentNode.data.setLeft(sum);
                    }
                    else{
                        head = null;
                        currentNode = null;
                    }
                }
                else{
                    Node savedNode = currentNode.nextNode;
                    currentNode.nextNode = new Node(data, savedNode);
                }
            }
        }
        if (head != null) {
            if (head.nextNode != null) {
                if (head.data.getRight() > head.nextNode.data.getRight()) {
                    Node savedList = head.nextNode.nextNode;
                    Node firstNode = head;
                    head = head.nextNode;
                    head.nextNode = firstNode;
                    head.nextNode.nextNode = savedList;
                }
            }
        }
    }

    public void printList()
    {
        Node tempNode = this.head;
        while (tempNode != null) {
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
