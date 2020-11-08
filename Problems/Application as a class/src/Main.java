class Application {

    String name;

    void run(String[] args) {
        // implement me
        System.out.println(name);
        for (String arg : args) {
            System.out.println(arg);
        }
    }
}