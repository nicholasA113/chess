package ui;

import java.util.Scanner;

public class ClientRepl {

    private final ChessClient chessClient;

    public ClientRepl(int port){
        chessClient = new ChessClient(port);
    }

    public void run() throws Exception {
        System.out.println("Welcome to the chess server! Sign in to start.");
        System.out.println("\nHere are options to get started\n");
        System.out.print(chessClient.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")){
            printPrompt();
            String line = scanner.nextLine();
            result = chessClient.request(line);
            if (!result.equalsIgnoreCase("quit")) {
                System.out.println(result);
            }
        }
    }

    private void printPrompt() {
        System.out.print("\n>>> ");
    }

}
