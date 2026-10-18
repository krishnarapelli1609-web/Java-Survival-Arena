package org.example;

public class DatabaseSaveTest {

    public static void main(String[] args) {

        boolean saved = Database.savePlayer(
                "Krishna",
                85,
                2,
                50,
                40
        );

        if (saved) {
            System.out.println("Test completed!");
        }
    }
}
