import java.util.Random;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class BattleshipGame {

    // its value used when game get resumed
    private static int player_remaining_ships = 0;
    private static int computer_remaining_ships = 0;

    public static void saveGame(char[][] playerGrid, char[][] computerGrid, String status) {
        try {
            File file = new File("player.txt");
            FileWriter writer = new FileWriter(file);
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    writer.write(playerGrid[i][j]);
                }
                writer.write("\n");
            }
            writer.close();

            File file2 = new File("com.txt");
            FileWriter writer2 = new FileWriter(file2);
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    writer2.write(computerGrid[i][j]);
                }
                writer2.write("\n");
            }
            writer2.close();

            File file3 = new File("status.txt");
            FileWriter writer3 = new FileWriter(file3);
            writer3.write(status); // writing game status too wether the game was completed or not
            writer3.close();
        } catch (IOException e) {
            System.out.println("An error occurred while saving the game.");
        }
    }

    public static boolean loadGame(char[][] playerGrid, char[][] computerGrid) {
        try {
            File status = new File("status.txt");
            Scanner sc = new Scanner(status);
            if (sc.nextInt() == 0) {
                System.out.println("Continue where you leave? (y/n)");
                Scanner scanner = new Scanner(System.in);
                if (scanner.next().toLowerCase().equals("y")) {
                    File player = new File("player.txt");
                    File computer = new File("com.txt");
                    BufferedReader reader = new BufferedReader(new FileReader(player));
                    BufferedReader reader2 = new BufferedReader(new FileReader(computer));
                    String line;
                    int row = 0;
                    while ((line = reader.readLine()) != null) {
                        for (int col = 0; col < 4; col++) {
                            playerGrid[row][col] = line.charAt(col);
                            if (playerGrid[row][col] == 'X') {
                                player_remaining_ships++;
                            }
                        }
                        row++;
                    }

                    String line2;
                    int row2 = 0;
                    while ((line2 = reader2.readLine()) != null) {
                        for (int col = 0; col < 4; col++) {
                            computerGrid[row2][col] = line2.charAt(col);
                            if (computerGrid[row2][col] == 'X') {
                                computer_remaining_ships++;
                            }
                        }
                        row2++;
                    }

                    reader.close();
                    reader2.close();
                    return true;

                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while loading the game." + e.getMessage());
        }
        return false;
    }

    public static void main(String[] args) {
        char[][] playerGrid = new char[4][4];
        char[][] computerGrid = new char[4][4];
        boolean newGame = false;
        boolean resume = false;
        resume = loadGame(playerGrid, computerGrid);// wether to resume

        outer: do {
            int playerShips = 3 - player_remaining_ships;
            int computerShips = 3 - computer_remaining_ships;
            int rounds = 0;

            if (!resume) {
                // Initialize grids with '-'
                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 4; j++) {
                        playerGrid[i][j] = '-';
                        computerGrid[i][j] = '-';
                    }
                }
                // Randomly place ships for player and computer
                placeShips(playerGrid);
                placeShips(computerGrid);
            }

            // Game loop
            while (playerShips > 0 && computerShips > 0) {
                // clearConsole();
                rounds++;

                // Display grids
                System.out.println("Player's Grid:");
                displayGrid(playerGrid);
                System.out.println("Computer's Grid:");
                displayGrid(computerGrid);

                try {
                    // Player's move
                    System.out.println("Enter your attack (e.g A4):");
                    Scanner scanner = new Scanner(System.in);
                    String input = scanner.nextLine();
                    int row = input.charAt(0) - 'A';
                    int col = input.charAt(1) - '1';
                    if (computerGrid[row][col] == 'X' || computerGrid[row][col] == 'O') {
                        System.out.println("You already attacked the area. Try another cell.");
                        continue;
                    }
                    if (computerGrid[row][col] == '-') {
                        computerGrid[row][col] = 'O'; // Mark as miss
                        System.out.println("You missed.");
                    } else {
                        computerGrid[row][col] = 'X'; // Mark as hit
                        System.out.println("You hit the computer's ship!");
                        computerShips--;
                    }

                    // Computer's move
                    Random random = new Random();
                    row = random.nextInt(4);
                    col = random.nextInt(4);
                    if (playerGrid[row][col] == 'X' || playerGrid[row][col] == 'O') {
                        continue; // Already attacked this cell, try again
                    }
                    if (playerGrid[row][col] == '-') {
                        playerGrid[row][col] = 'O'; // Mark as miss
                        System.out.println("Computer missed.");
                    } else {
                        playerGrid[row][col] = 'X'; // Mark as hit
                        System.out.println("Computer hit your ship!");
                        playerShips--;
                    }

                    // saving game states
                    if (playerShips == 0 || computerShips == 0) {
                        saveGame(playerGrid, computerGrid, "1");
                    } else {
                        saveGame(playerGrid, computerGrid, "0");
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("\033[1;33mWrong area try again\033[0m");

                } catch (StringIndexOutOfBoundsException e) {
                    System.out.println(
                            "\033[1;33mInvalid quardinate must be combination of alphabet and number\033[0m");
                }

            }

            // Game over
            System.out.println("\033[1;33mGame over!\033[0m");
            if (playerShips == 0) {
                System.out.println("The computer won!");
            } else {
                System.out.println("\033[1;32mYou won!\033[0m");
            }
            System.out.println("Rounds: " + rounds);
            System.out.println("New Game? (Y/N)");
            Scanner scanner = new Scanner(System.in);
            newGame = scanner.next().toLowerCase().equals("y") ? true : false;

        } while (newGame);
    }

    public static void placeShips(char[][] grid) {
        Random random = new Random();
        int count = 0;
        while (count < 3) {
            int row = random.nextInt(4);
            int col = random.nextInt(4);
            if (grid[row][col] == '-') {
                grid[row][col] = 'S'; // Mark as ship
                count++;
            }
        }
    }

    public static void displayGrid(char[][] grid) {
        System.out.println("  1 2 3 4");
        for (int i = 0; i < 4; i++) {
            System.out.print((char) ('A' + i) + " ");
            for (int j = 0; j < 4; j++) {
                if (grid[i][j] == 'S') {
                    System.out.print("- ");
                } else {
                    System.out.print(grid[i][j] + " ");
                }
            }
            System.out.println();
        }
    }
}
