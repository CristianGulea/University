import java.util.Objects;

public class Main {
    public static void main(String[] args) throws Exception {

        Long startTime = System.nanoTime();
        //Utils.generateRandomPolynomials(5, 10000, 100, 1000000, "C:\\ppd\\lab4\\Polinom");

        Calculator calculator = new Calculator();
        if(Objects.equals(args[0], "0")) {
            calculator.sequentialCalculate(5);
        }
        else {
            int p = Integer.parseInt(args[0]);
            calculator.threadCalculate(5, p);
        }
        //System.out.println(Utils.validator("C:\\ppd\\lab4\\SecvResult", "C:\\ppd\\lab4\\ThreadResult"));
        Long endTime = System.nanoTime();
        System.out.println(endTime-startTime);

    }
}
