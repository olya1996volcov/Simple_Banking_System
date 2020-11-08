class Problem {

    public static void main(String[] args) {
        String operation = args[0];
        switch (operation) {
            case "+":
                System.out.println(Integer.parseInt(args[1]) + Integer.parseInt(args[2]));
                break;
            case "-":
                System.out.println(Integer.parseInt(args[1]) - Integer.parseInt(args[2]));
                break;
            case "*":
                System.out.println(Integer.parseInt(args[1]) * Integer.parseInt(args[2]));
                break;
            default:
                System.out.println("Unknown operator");
                break;
        }
    }
}