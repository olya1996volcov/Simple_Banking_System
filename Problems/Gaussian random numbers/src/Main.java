import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // write your code here
        long k = scanner.nextInt();
        double n = scanner.nextDouble();
        double m = scanner.nextDouble();
        long seed = k;

        while (true) {
            boolean flag = true;
            Random random = new Random(seed);
            for (int i = 0; i < n && flag; i++) {
                double temp = random.nextGaussian();
                if (temp > m) {
                    flag = false;
                    seed += 1;
                    continue;
                }
            }
            if (flag) {
                break;
            }
        }
        System.out.println(seed);
    }
}