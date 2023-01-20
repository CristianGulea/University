import java.util.Objects;

public class Main {
    public static void main(String[] args) throws Exception {

        int maxPower = 10000;
        Long startTime = System.nanoTime();
        //Utils.generateRandomPolynomials(10, 3, maxPower, 3, "C:\\ppd\\lab5\\Polinom");
        //Utils.generateRandomPolynomials(10, maxPower, 100, 1000000, "C:\\ppd\\lab5\\Polinom");

        Calculator calculator = new Calculator();
        if(Objects.equals(args[0], "0")) {
           calculator.sequentialCalculate(5, maxPower);
       }
        else {
            int p = Integer.parseInt(args[0]);
            calculator.threadCalculate(5, 3, p-3, maxPower);
        }
        //System.out.println(Utils.validator("C:\\ppd\\lab5\\SecvResult", "C:\\ppd\\lab5\\ThreadResult"));
        Long endTime = System.nanoTime();
        System.out.println(endTime-startTime);

    }
}
