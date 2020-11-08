/* Please, do not rename it */
class Problem {

    public static void main(String[] args) {
        String operator = args[0];
        switch (operator) {
            case "MAX":
                long max = Integer.parseInt(args[1]);
                for (int i = 1; i < args.length; i++) {
                    if (max < Integer.parseInt(args[i])) {
                        max = Integer.parseInt(args[i]);
                    }
                }
                System.out.println(max);
                break;
            case "MIN":
                long min = Integer.parseInt(args[1]);
                for (int i = 1; i < args.length; i++) {
                    if (min > Integer.parseInt(args[i])) {
                        min = Integer.parseInt(args[i]);
                    }
                }

                System.out.println(min);
                break;
            case "SUM":
                int sum = 0;
                for (int i = 1; i < args.length; i++) {
                    sum += Integer.parseInt(args[i]);
                }
                System.out.println(sum);
                break;
            default:
                System.out.println("error");
                break;
        }
        // write your code here
    }
}