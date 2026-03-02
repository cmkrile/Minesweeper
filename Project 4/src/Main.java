//Import Section
import java.util.Random;
import java.util.Scanner;

/*
 * Provided in this class is the neccessary code to get started with your game's implementation
 * You will find a while loop that should take your minefield's gameOver() method as its conditional
 * Then you will prompt the user with input and manipulate the data as before in project 2
 *
 * Things to Note:
 * 1. Think back to project 1 when we asked our user to give a shape. In this project we will be asking the user to provide a mode. Then create a minefield accordingly
 * 2. You must implement a way to check if we are playing in debug mode or not.
 * 3. When working inside your while loop think about what happens each turn. We get input, user our methods, check their return values. repeat.
 * 4. Once while loop is complete figure out how to determine if the user won or lost. Print appropriate statement.
 */

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Do you want to play in debug mode? (true/false): ");
        boolean debugMode = scanner.nextBoolean();
        System.out.println("Choose difficulty level:");
        System.out.println("1. Easy");
        System.out.println("2. Medium");
        System.out.println("3. Hard");
        System.out.print("Enter your choice (1/2/3): ");
        int choice = scanner.nextInt();
        int rows;
        int columns;
        int mines;
        int flags;
        switch (choice) {
            case 1: // Easy
                rows = 5;
                columns = 5;
                mines = 5;
                flags = 5;
                break;
            case 2: // Medium
                rows = 9;
                columns = 9;
                mines = 12;
                flags = 12;
                break;
            case 3: // Hard
                rows = 20;
                columns = 20;
                mines = 40;
                flags = 40;
                break;
            default:
                System.out.println("Invalid choice.");
                scanner.close();
                return;
        }
        Minefield minefield = new Minefield(rows, columns, flags);
        minefield.createMines(-1, -1, mines);
        minefield.evaluateField();
        if (debugMode) {
            minefield.debug();
        } else {
            System.out.println(minefield);
            System.out.print("Enter starting row: ");
            int startRow = scanner.nextInt();
            System.out.print("Enter starting column: ");
            int startColumn = scanner.nextInt();
            minefield.revealStartingArea(startRow, startColumn);
        }
        while (!minefield.gameOver()) {
            if (!debugMode) {
                System.out.println(minefield);
            }
            System.out.print("Enter row for guess: ");
            int guessRow = scanner.nextInt();
            System.out.print("Enter column for guess: ");
            int guessColumn = scanner.nextInt();
            System.out.print("Do you want to place a flag? (true/false): ");
            boolean flag = scanner.nextBoolean();
            boolean hitMine = minefield.guess(guessRow, guessColumn, flag);
            if (minefield.gameOver()) {
                if (hitMine) {
                    System.out.println("Mine hit, Game over!");
                    minefield.revealWholeBoard();
                    System.out.println(minefield);
                } else {
                    System.out.println("You won!");
                }
            }
        }
        scanner.close();
    }
}