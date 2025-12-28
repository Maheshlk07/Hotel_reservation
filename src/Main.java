//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import java.sql.*;
import java.util.Scanner;

public class Main {
    private static final String url = "jdbc:mysql://127.0.0.1:3306/hotel";
    private static final String user = "root";
    private static final String password = "Maisu@2004";

    public static void main(String[] args) throws Exception {

        Scanner sc = new Scanner(System.in);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("DRIVERS LOADED SUCCESSFULLY");
        } catch (ClassNotFoundException E) {
            System.out.println(E.getMessage());
        }

        try {
            Connection con = DriverManager.getConnection(url, user, password);
            System.out.println("CONNECTION ESTABLISHED SUCCESSFULLY");
            while (true) {
                System.out.println("WELCOME TO THE ENGINEER'S PARADISE\n");
                System.out.println("ENTER YOUR CHOICE");
                System.out.println("1. RESERVE A ROOM");
                System.out.println("2. CHECK RESERVATION");
                System.out.println("3. GET ROOM NUMBER");
                System.out.println("4. UPDATE RESERVATION");
                System.out.println("5. DELETE RESERVATION");
                System.out.println("6. EXIT");
                System.out.print("CHOOSE AN OPTION: ");
                int choice = sc.nextInt();

                switch (choice) {
                    case 1:
                        reserveroom(con, sc);
                        break;
                    case 2:
                        viewreservations(con);
                        break;
                    case 3:
                        getroomnumber(con, sc);
                        break;
                    case 4:
                        updatereservation(con, sc);
                        break;
                    case 5:
                        deletereservation(con, sc);
                        break;
                    case 6:
                        exit(con);
                        sc.close();
                        return;
                    default:
                        System.out.println("NOT A VALID OPTION. SELECT ANY ONE PROVIDED ABOVE");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("CONNECTION FAILED");
        }
    }
    public static void reserveroom(Connection con, Scanner sc) {
        try {
            System.out.print("ENTER GUEST NAME: ");
            String name = sc.next();
            sc.nextLine();
            System.out.print("ENTER ROOM NUMBER: ");
            int roomnumber = sc.nextInt();
            System.out.print("ENTER CONTACT NUMBER: ");
            String contactnumber = sc.next();
            System.out.print("ENTER 12 DIGIT AADHAR NUMBER: ");
            String aadharnumber = sc.next();

            String insertquery = "INSERT INTO reservations(name, roomnumber, contactnumber, aadharnumber) " +
                    "VALUES('" + name + "', " + roomnumber + ", '" + contactnumber + "', " + aadharnumber + ")";

            Statement st = con.createStatement();
            int numb = st.executeUpdate(insertquery);

            if (numb > 0) {
                System.out.println("DATA INSERTED SUCCESSFULLY");
                System.out.println(numb + " ROWS AFFECTED");
            } else {
                System.out.println("SOME ERROR OCCURRED. DATA INSERTION FAILED");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static void viewreservations(Connection con) {
        String selectquery = "SELECT * FROM reservations;";
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(selectquery);

            if (!rs.isBeforeFirst()) {
                System.out.println("THE DATABASE IS EMPTY");
            } else {
                System.out.println("\n===== RESERVATION LIST =====");
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    int roomnumber = rs.getInt("roomnumber");
                    String contactnumber = rs.getString("contactnumber");
                    String reservationdate = rs.getTimestamp("reservationdate").toString();
                    String aadharnumber = rs.getString("aadharnumber");

                    System.out.println("\n=======================================================================");
                    System.out.println("ID: " + id);
                    System.out.println("NAME: " + name);
                    System.out.println("ROOM NUMBER: " + roomnumber);
                    System.out.println("CONTACT NUMBER: " + contactnumber);
                    System.out.println("RESERVATION DATE: " + reservationdate);
                    System.out.println("AADHAR NUMBER: " + aadharnumber);
                }
            }
        } catch (SQLException E) {
            System.out.println(E.getMessage());
        }
    }

    public static void getroomnumber(Connection con, Scanner sc) {
        System.out.print("ENTER RESERVATION ID TO RETRIEVE DESIRED ROOM NUMBER: ");
        int reservationid = sc.nextInt();

        try {
            String roomquery = "SELECT roomnumber FROM reservations WHERE ID=" + reservationid + ";";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(roomquery);
            if (rs.next()) {
                int ans = rs.getInt("roomnumber");
                System.out.println("THE ROOM NUMBER IS: " + ans);
            } else {
                System.out.println("RESERVATION NOT FOUND. PLEASE CONFIRM YOUR RESERVATION ID");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static void updatereservation(Connection con, Scanner sc) {
        System.out.print("ENTER RESERVATION ID TO UPDATE: ");
        int resid = sc.nextInt();
        sc.nextLine();
        if (!reservationexist(con, resid)) {
            System.out.println("RESERVATION NOT FOUND FOR PROVIDED ID");
            return;
        }

        System.out.print("ENTER THE NAME TO BE UPDATED: ");
        String name = sc.nextLine();
        System.out.print("ENTER ROOM NUMBER ALLOTTED: ");
        int roomnumber = sc.nextInt();
        System.out.print("ENTER CONTACT NUMBER: ");
        String contactnumber = sc.next();
        System.out.print("ENTER AADHAR NUMBER: ");
        String aadharnumber = sc.next();

        String updatequery = "UPDATE reservations " +
                "SET name='" + name + "', roomnumber=" + roomnumber + ", contactnumber='" + contactnumber + "', aadharnumber=" + aadharnumber +
                " WHERE id=" + resid + ";";

        try {
            Statement st = con.createStatement();
            int n = st.executeUpdate(updatequery);
            if (n > 0) {
                System.out.println("DATA UPDATED SUCCESSFULLY. ROWS AFFECTED: " + n);
            } else {
                System.out.println("UPDATION FAILED. 0 ROWS AFFECTED");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void deletereservation(Connection con, Scanner sc) {
        System.out.print("ENTER THE RESERVATION ID TO BE DELETED: ");
        int reid = sc.nextInt();
        if (!reservationexist(con, reid)) {
            System.out.println("THE RESERVATION DOES NOT EXIST. PLEASE ENTER AN EXISTING RESERVATION ID");
            return;
        }
        String deletequery = "DELETE FROM reservations WHERE id=" + reid + ";";
        try {
            Statement st = con.createStatement();
            int n = st.executeUpdate(deletequery);
            if (n > 0) {
                System.out.println("DELETION WAS SUCCESSFUL. ROWS AFFECTED: " + n);
            } else {
                System.out.println("NOTHING WAS DELETED");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static boolean reservationexist(Connection con, int reid) {
        String existquery = "SELECT id FROM reservations WHERE id=" + reid + ";";
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(existquery);
            return rs.next();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public static void exit(Connection con) {
        System.out.print("EXITING SYSTEM");
        for (int i = 0; i < 5; i++) {
            System.out.print(".");
            try {
                Thread.sleep(850);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("\nTHANK YOU FOR CHOOSING THE ENGINEER'S PARADISE!!!");
        try {
            con.close();
            System.out.println("CONNECTION CLOSED SUCCESSFULLY");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}