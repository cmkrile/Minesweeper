import java.util.*;
import java.util.Random;

public class Minefield {
    /**
     Global Section
     */
    public static final String ANSI_YELLOW_BRIGHT = "\u001B[33;1m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE_BRIGHT = "\u001b[34;1m";
    public static final String ANSI_BLUE = "\u001b[34m";
    public static final String ANSI_RED_BRIGHT = "\u001b[31;1m";
    public static final String ANSI_RED = "\u001b[31m";
    public static final String ANSI_GREEN = "\u001b[32m";
    public static final String ANSI_PURPLE = "\u001b[35m";
    public static final String ANSI_CYAN = "\u001b[36m";
    public static final String ANSI_WHITE_BACKGROUND = "\u001b[47m";
    public static final String ANSI_PURPLE_BACKGROUND = "\u001b[45m";
    public static final String ANSI_GREY_BACKGROUND = "\u001b[0m";


    /*
     * Class Variable Section
     *
     */
    private final Cell[][] field;
    private int rows;
    private int columns;
    private int flags;
    private int flagsRemaining;
    private boolean debugMode = false;

    /*Things to Note:
     * Please review ALL files given before attempting to write these functions.
     * Understand the Cell.java class to know what object our array contains and what methods you can utilize
     * Understand the StackGen.java class to know what type of stack you will be working with and methods you can utilize
     * Understand the QGen.java class to know what type of queue you will be working with and methods you can utilize
     */
    /*
      ✅Minefield()

     * ✅Build a 2-d Cell array representing your minefield.
     * Constructor
     * @param rows       Number of rows.
     * @param columns    Number of columns.
     * @param flags      Number of flags, should be equal to mines
     */
    public Minefield(int rows, int columns, int flags) {
        this.rows = rows;
        this.columns = columns;
        this.flags = flags;
        this.field = new Cell[rows][columns];
        this.debugMode = debugMode;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                field[i][j] = new Cell(false, "0");
            }
        }
        flagsRemaining = flags;
    }
    /*
       ✅evaluateField()
     * @function:
     * ✅Evaluate entire array.
     * ✅When a mine is found check the surrounding adjacent tiles.
     * If another mine is found during this check, increment adjacent cells status by 1.
     */
    public void evaluateField() {
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].length; j++) { // iterating through each cell
                if (field[i][j].getStatus().equals("M")) { // if a mine is found
                    int startRow = Math.max(0, i - 1);
                    int startCol = Math.max(0, j - 1);
                    int endRow = Math.min(field.length - 1, i + 1);
                    int endCol = Math.min(field[0].length - 1, j + 1);
                    for (int k = startRow; k <= endRow; k++) {// Iterate through adjacent cells
                        for (int l = startCol; l <= endCol; l++) {
                            if (k != i || l != j) { // Check if the cell is not the mine itself
                                if (!field[k][l].getStatus().equals("M")) { // Check if the cell is not a mine
                                    int currentStatus = Integer.parseInt(field[k][l].getStatus());
                                    currentStatus++;
                                    field[k][l].setStatus(String.valueOf(currentStatus));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    /*
   ✅createMines()
        ✅Randomly generate coordinates for possible mine locations.
        ✅If the coordinate has not already been generated and is not equal to the starting cell, set the cell to be a mine.
        ✅utilize rand.nextInt()
     * @param x       Start x, avoid placing on this square.
     * @param y        Start y, avoid placing on this square.
     * @param mines      Number of mines to place.
     */

    public void createMines(int x, int y, int mines) {
        Random random = new Random();
        int minesPlaced = 0;
        while (minesPlaced < mines) {
            int randomX = random.nextInt(field.length);
            int randomY = random.nextInt(field[0].length);
            if (!(randomX == x && randomY == y) && !field[randomX][randomY].getStatus().equals("M")) {
                field[randomX][randomY].setStatus("M");
                minesPlaced++;
            }
        }
    }
    /*

     * @param x       The x value the user entered ✅guess()
        ✅Check if the guessed cell is inbounds (if not done in the Main class).
        ✅Check see if the user wishes to place a flag and, if so, whether or not there are enough flags remaining to place the flag.
        ✅Check to see if the user has hit a cell with a ’0’ status. If so, call the revealZeroes() method
        ✅Finally, if the user hits a mine, end the game.
        ✅At the end reveal the cell to the user.
        ✅Make sure to also set the revealed status of the cell that the user guesses at the end of the method.
.
     * @param y       The y value the user entered.
     * @param flag    A boolean value that allows the user to place a flag on the corresponding square.
     * @return: boolean Return false if guess did not hit mine or if flag was placed, true if mine found. */
    public boolean guess(int x, int y, boolean flag) {
        if (x < 0 || x >= field.length || y < 0 || y >= field[0].length) {
            System.out.println("Invalid input.");
            return false;
        }
        Cell guessedCell = field[x][y];
        if (flag) {
            if (!guessedCell.getRevealed()) {
                if (flagsRemaining > 0) {
                    guessedCell.setStatus("F");
                    guessedCell.setRevealed(true);
                    flagsRemaining--;
                    return false;
                } else {
                    System.out.println("No flags remaining.");
                    return false;
                }
            } else {
                System.out.println("Cannot place flag on a revealed cell.");
                return false;
            }
        }
        if (!guessedCell.getRevealed()) {
            if (guessedCell.getStatus().equals("M")) {
                guessedCell.setRevealed(true);
                return true;
            }
            if (guessedCell.getStatus().equals("0")) {
                revealZeroes(x, y);
            } else {
                guessedCell.setRevealed(true);
            }
        }
        return false;
    }

    /*
     ✅gameOver()
     Ways a game of Minesweeper ends:
            ✅1. player guesses a cell with a mine: game over -> player loses
            ✅2. player has revealed the last cell without revealing any mines -> player wins
     @return boolean Return false if game is not over and squares have yet to be revealed, otheriwse return true.
     */
    public boolean gameOver() {
        boolean mineHit = false;
        int flaggedCount = flags;
        int totalCells = rows * columns;
        int revealedNonMineCount = 0;
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].length; j++) {
                if (field[i][j].getStatus().equals("M") && field[i][j].getRevealed()) {
                    mineHit = true;
                }
                if (field[i][j].getStatus().equals("F")) {
                    flaggedCount = flaggedCount - 1;
                }
                if (!field[i][j].getStatus().equals("M") && field[i][j].getRevealed()) {
                    revealedNonMineCount++;
                }
            }
        }
        if (mineHit) {
            return true;
        }
        if (flaggedCount == 0) {
            return true;
        }
        if (revealedNonMineCount == totalCells - flags) {
            return true;
        }
        return false;
    }

    /*
     * Reveal the cells that contain zeroes that surround the inputted cell.
     * Continue revealing 0-cells in every direction until no more 0-cells are found in any direction.
     * Utilize a STACK to accomplish this.
     *
     * This method should follow the psuedocode given in the lab writeup.
     * Why might a stack be useful here rather than a queue?
     *
     * @param x      The x value the user entered.
     * @param y      The y value the user entered.
     */
    //REQUIRE SPECIAL ATTENTIONS
    public void revealZeroes(int x, int y) {
        Stack1Gen<int[]> stack = new Stack1Gen<>();
        stack.push(new int[]{x, y});

        while (!stack.isEmpty()) {
            int[] currentCell = stack.pop();
            int row = currentCell[0];
            int col = currentCell[1];
            field[row][col].setRevealed(true);

            for (int i = row - 1; i <= row + 1; i++) {
                for (int j = col - 1; j <= col + 1; j++) {
                    if (i >= 0 && i < field.length && j >= 0 && j < field[0].length &&
                            !field[i][j].getRevealed() && field[i][j].getStatus().equals("0")) {
                        stack.push(new int[]{i, j});
                    }
                }
            }
        }
    }

    /*
     * revealStartingArea
     *
     * On the starting move only reveal the neighboring cells of the inital cell and continue revealing the surrounding concealed cells until a mine is found.
     * Utilize a QUEUE to accomplish this.
     *
     * This method should follow the psuedocode given in the lab writeup.
     * Why might a queue be useful for this function?
     *
     * @param x     The x value the user entered.
     * @param y     The y value the user entered.
     */
    //REQUIRE SPECIAL ATTENTIONS- all answers are in lab 10
    public void revealStartingArea(int x, int y) {
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{x, y});

        while (!queue.isEmpty()) {
            int[] currentCell = queue.poll();
            int row = currentCell[0];
            int col = currentCell[1];
            field[row][col].setRevealed(true);
            if (field[row][col].getStatus().equals("0")) {
                for (int i = row - 1; i <= row + 1; i++) {
                    for (int j = col - 1; j <= col + 1; j++) {
                        if (i >= 0 && i < field.length && j >= 0 && j < field[0].length &&
                                !field[i][j].getRevealed()) {
                            queue.add(new int[]{i, j});
                        }
                    }
                }
            }
        }
    }
    /*
     * For both printing methods utilize the ANSI colour codes provided!
     * debug
     *
     * @function This method should print the entire minefield, regardless if the user has guessed a square.
     * *This method should print out when debug mode has been selected.
     */
    public void debug() {
        System.out.println("Debug Mode:");
        System.out.print("  ");
        for (int c = 0; c < columns; c++) {
            System.out.print(c + " ");
        }
        System.out.println();
        for (int i = 0; i < field.length; i++) {
            System.out.print(i + " ");

            for (int j = 0; j < field[i].length; j++) {
                Cell currentCell = field[i][j];
                String status = currentCell.getStatus();
                String color = "";
                switch (status) {
                    case "0":
                        color = ANSI_YELLOW_BRIGHT;
                        break;
                    case "1":
                        color = ANSI_BLUE_BRIGHT;
                        break;
                    case "2":
                        color = ANSI_GREEN;
                        break;
                    case "3":
                        color = ANSI_RED;
                        break;
                    case "M":
                        color = ANSI_RED_BRIGHT;
                        break;
                    case "F":
                        color = ANSI_CYAN;
                        break;
                }
                System.out.print(color + status + " " + ANSI_GREY_BACKGROUND);
            }
            System.out.println();
        }
    }

    /**
     * toString
     *
     * @return String The string that is returned only has the squares that has been revealed to the user or that the user has guessed.
     */

    public String toString() {
        StringBuilder sb = new StringBuilder();
        int flaggedCount = 0;
        sb.append("  ");
        for (int c = 0; c < columns; c++) {
            sb.append(c).append(" ");
        }
        sb.append("\n");
        for (int i = 0; i < field.length; i++) {
            sb.append(i).append(" ");
            for (int j = 0; j < field[i].length; j++) {
                Cell currentCell = field[i][j];
                if (debugMode) {
                    // In debug mode, reveal all cells and apply color codes based on status
                    String status = currentCell.getStatus();
                    String color = ""; // Default color
                    switch (status) {
                        case "0":
                            color = ANSI_YELLOW_BRIGHT;
                            break;
                        case "1":
                            color = ANSI_BLUE_BRIGHT;
                            break;
                        case "2":
                            color = ANSI_GREEN;
                            break;
                        case "3":
                            color = ANSI_RED;
                            break;
                        case "M":
                            color = ANSI_RED_BRIGHT;
                            break;
                        case "F":
                            color = ANSI_CYAN;
                            flaggedCount++;
                            break;
                    }
                    sb.append(color).append(status).append(" ");
                } else {
                    // In non-debug mode, show only "-" for unrevealed cells
                    if (currentCell.getRevealed()) {
                        String status = currentCell.getStatus();
                        String color = ""; // Default color
                        switch (status) {
                            case "0":
                                color = ANSI_YELLOW_BRIGHT;
                                break;
                            case "1":
                                color = ANSI_BLUE_BRIGHT;
                                break;
                            case "2":
                                color = ANSI_GREEN;
                                break;
                            case "3":
                                color = ANSI_RED;
                                break;
                            case "M":
                                color = ANSI_RED_BRIGHT;
                                break;
                            case "F":
                                color = ANSI_CYAN;
                                flaggedCount++;
                                break;
                        }
                        sb.append(color).append(status).append(" ");
                    } else {
                        sb.append(ANSI_GREY_BACKGROUND).append(ANSI_CYAN).append("- ").append(ANSI_GREY_BACKGROUND);
                    }
                }
            }
            sb.append("\n");
        }
        if (debugMode) {
            sb.append("Flags remaining: ").append(flagsRemaining - flaggedCount).append(" ");
        } else {
            sb.append("Flags remaining: ").append(flagsRemaining).append(" ");
        }
        return sb.toString();
    }
    public void revealWholeBoard() {
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].length; j++) {
                field[i][j].setRevealed(true);
            }
        }
    }

}