import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ReadabilityScore rs = new ReadabilityScore(args[0]);
        System.out.println("Enter the score you want to calculate (ARI, FK, SMOG, CL, all):");
        rs.scoreCalculate(scanner.next());

    }
}
