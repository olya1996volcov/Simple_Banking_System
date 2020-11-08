package banking;

import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:" + args[1];
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);
        try (Connection con = dataSource.getConnection()) {
            try (Statement statement = con.createStatement()) {
                // Statement execution
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS card(" +
                        "id INTEGER," +
                        "number TEXT," +
                        "pin TEXT," +
                        "balance INTEGER DEFAULT 0)");
            } catch (SQLException e) {
                e.printStackTrace();
            }

            Random random = new Random();
            int k = 100;
            //double[] balance = new double[k];

            int countCustomers = 0;

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("1. Create an account\n" +
                        "2. Log into account\n" +
                        "0. Exit");
                int operation = scanner.nextInt();
                switch (operation) {
                    case 1:
                        createAccount(countCustomers, url);
                        countCustomers++;
                        break;
                    case 2:
                        logIntoAccount(countCustomers, url);
                        break;
                    case 0:
                        System.out.println("Bye!");
                        System.exit(0);
                    default:
                        System.out.println("ERROR");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private static double createAccount(int countCustomers, String url) {
        StringBuilder cardNumber = new StringBuilder("400000");
        StringBuilder password = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 9; i++) {
            int n = random.nextInt(10);
            cardNumber.append(n);
        }
        char[] chars = new char[15];
        int[] nums = new int[15];
        int sum = 0;
        chars = cardNumber.toString().toCharArray();
        for (int i = 0; i < 15; i++) {
            nums[i] = (int) chars[i] - '0';
        }
        for (int i = 0; i < 15; i += 2) {
            nums[i] *= 2;
            if (nums[i] > 9) {
                nums[i] -= 9;
            }
        }
        for (int i = 0; i < 15; i++) {
            sum += nums[i];
        }
        int need;
        need = sum % 10;
        if (need != 0) {
            need = 10 - need;
        } else {
            need = 0;
        }
        cardNumber.append(need); // checksum
        for (int i = 0; i < 4; i++) {
            password.append(random.nextInt(10));
        }
        System.out.println("Your card has been created\n" +
                "Your card number:");
        System.out.println((String.valueOf(cardNumber)));
        System.out.println("Your card PIN:");
        System.out.println(String.valueOf(password));
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);
        String str1 = String.valueOf(cardNumber);
        String str2 = String.valueOf(password);

        try (Connection con = dataSource.getConnection()) {
            try (Statement statement = con.createStatement()) {
                statement.executeUpdate("INSERT INTO card VALUES " +
                        "(" + countCustomers + ", " + str1 + ",  " + str2 + ", 0)");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static void logIntoAccount(int countCustomers, String url) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your card number:");
        String card = scanner.nextLine();
        System.out.println("Enter your PIN:");
        String pinCode = scanner.nextLine();
        int balance = 0;
        boolean logged = false;

        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);
        try (Connection con = dataSource.getConnection()) {
            try (Statement statement = con.createStatement()) {
                // Statement execution
                try (ResultSet res = statement.executeQuery("SELECT * FROM card WHERE pin = " + pinCode + " AND number =" + card + "")) {
                    if (res.next()) {
                        System.out.println("You have successfully logged in!\n");
                        logged = true;

                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (!logged) {
            System.out.println("Wrong card number or PIN!");
            return;
        }
        while (logged) {
            System.out.println("1. Balance\n" +
                    "2. Add income\n" +
                    "3. Do transfer\n" +
                    "4. Close account\n" +
                    "5. Log out\n" +
                    "0. Exit");
            boolean inAccount = true;
            // balance = Integer.parseInt(res.getString("balance"));

            int operation = scanner.nextInt();
            switch (operation) {
                case 1:
                    getBalance(card, pinCode, url);
                    // System.out.println("Balance: " + balance);
                    break;
                case 2:
                    addIncome(card, pinCode, url);
                    break;
                case 3:
                    doTransfer(card, url);
                    break;
                case 4:
                    closeAccount(card, url);
                    logged = false;
                    break;
                case 5:
                    System.out.println("You have successfully logged out!");
                    logged = false;
                    break;
                case 0:
                    System.out.println("Bye!");
                    System.exit(0);
            }
        }


    }

    private static void doTransfer(String card, String url) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Transfer");
        System.out.println("Enter card number:");
        String cardToTransfer = scanner.nextLine();
        int money = 0;
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);

        int check = luhn(cardToTransfer);
        if (check != 0) {
            System.out.println("Probably you made mistake in the card number. Please try again!");
            System.out.println(check);
            return;
        } else if (cardToTransfer.equals(card)) {
            System.out.println("You can't transfer money to the same account!");
            return;
        } else {
            try (Connection con = dataSource.getConnection()) {
                try (Statement statement = con.createStatement()) {
                    try (ResultSet res = statement.executeQuery("SELECT * FROM card WHERE number =" + card)) {
                        if (res.next()) {
                            money = res.getInt("balance");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    try (ResultSet res = statement.executeQuery("SELECT * FROM card WHERE number =" + cardToTransfer)) {
                        if (res.next()) {
                            System.out.println("Enter how much money you want to transfer:");
                            int moneyToTransfer = scanner.nextInt();
                            if (moneyToTransfer > money) {
                                System.out.println("Not enough money!");
                            } else {
                                statement.executeUpdate("UPDATE card SET balance = balance-" + moneyToTransfer + " WHERE  number =" + card);
                                statement.executeUpdate("UPDATE card SET balance = balance+" + moneyToTransfer + " WHERE  number =" + cardToTransfer);
                                System.out.println("Success!");
                            }
                            // System.out.println("Balance: " + money);

                        } else {
                            System.out.println("Such a card does not exist.");
                        }
                    } catch (SQLException e) {

                        e.printStackTrace();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (
                    SQLException e) {
                e.printStackTrace();
            }

        }
    }

    private static int luhn(String card) {
        char[] chars = new char[16];
        int[] nums = new int[16];
        int sum = 0;
        chars = card.toCharArray();
        for (int i = 0; i < 16; i++) {
            nums[i] = (int) chars[i] - '0';
        }
        for (int i = 0; i < 16; i += 2) {
            nums[i] *= 2;
            if (nums[i] > 9) {
                nums[i] -= 9;
            }
        }
        for (int i = 0; i < 16; i++) {
            sum += nums[i];
        }
        return sum % 10;
    }

    private static void closeAccount(String card, String url) {
        Scanner scanner = new Scanner(System.in);

        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);
        try (Connection con = dataSource.getConnection()) {
            try (Statement statement = con.createStatement()) {
                statement.executeUpdate("DELETE FROM card WHERE number =" + card);
                System.out.println("The account has been closed!\n");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (
                SQLException e) {
            e.printStackTrace();
        }
    }

    private static void getBalance(String card, String pinCode, String url) {
        Scanner scanner = new Scanner(System.in);

        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);
        try (Connection con = dataSource.getConnection()) {
            try (Statement statement = con.createStatement()) {
                try (ResultSet res = statement.executeQuery("SELECT * FROM card WHERE pin = " + pinCode + " AND number =" + card + "")) {
                    if (res.next()) {

                        int money = res.getInt("balance");
                        System.out.println("Balance: " + money);

                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (
                SQLException e) {
            e.printStackTrace();
        }

    }

    private static void addIncome(String card, String pinCode, String url) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter income:\n");
        int money = scanner.nextInt();


        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);
        try (Connection con = dataSource.getConnection()) {
            try (Statement statement = con.createStatement()) {
                // Statement execution
                statement.executeUpdate(
                        "UPDATE card SET balance = balance+" + money + " WHERE  number =" + card);
                System.out.println("Income was added!");
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
}