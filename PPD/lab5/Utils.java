import java.io.*;
import java.util.Random;

public class Utils {
    public static LinkedList readPolynomial(String filename) throws IOException {
        LinkedList linkedList = new LinkedList(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line = reader.readLine();
        while (line != null){
            int coefficient = Integer.parseInt(line.split(" ")[0]);
            int power = Integer.parseInt(line.split(" ")[1]);
            linkedList.sortInsert(new Pair<>(coefficient, power));
            line = reader.readLine();
        }
        reader.close();
        return linkedList;
    }

    public static void generateRandomPolynomials(int maxNumberFile, int maxPower, int numberPolynomial, int maxNumber, String filename) throws IOException {
        int numberFile = 1;
        while (numberFile <= maxNumberFile) {
            String finalPath = filename + numberFile;
            BufferedWriter bw = new BufferedWriter(new FileWriter(finalPath));
            Random random = new Random();
            //for (int i = 0; i < random.nextInt(numberPolynomial) + 1; i++) {
            for (int i = 0; i < numberPolynomial; i++) {
                int value = random.nextInt(maxNumber) + 1;
                int power = random.nextInt(maxPower) + 1;
                if (random.nextInt(10) % 10 > 4) {
                    bw.write("-" + value + " " + power);
                } else {
                    bw.write(value + " " + power);
                }
                bw.newLine();
            }
            bw.close();
            numberFile += 1;
        }
    }

    public static void writePolynomialToFile(LinkedList linkedList, String filename) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
        Node temporaryNode = linkedList.head;
        while (temporaryNode != null) {
            if (!temporaryNode.data.getLeft().toString().contains("-")){
                bw.write(" +" + temporaryNode.data.getLeft() + "x^" + temporaryNode.data.getRight());
            }
            else {
                bw.write(" " + temporaryNode.data);
            }
            temporaryNode = temporaryNode.nextNode;
        }
        bw.close();
    }

    public static boolean validator(String filename1, String filename2) throws IOException {
        BufferedReader bufferedReader1 = new BufferedReader(new FileReader(filename1));
        BufferedReader bufferedReader2 = new BufferedReader(new FileReader(filename2));
        String line1 = bufferedReader1.readLine();
        String line2 = bufferedReader2.readLine();
        bufferedReader1.close();
        bufferedReader2.close();
        for (int i = 0; i < line1.length(); i++){
            if (line1.charAt(i) != line2.charAt(i)){
                System.out.println("Error at column " + i);
                return false;
            }
        }
        return line1.equals(line2);
    }

}
