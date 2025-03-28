package ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ClientRepl {

    private final ChessClient chessClient;

    public ClientRepl(int port){
        chessClient = new ChessClient(port);
    }

    public void run() throws Exception {
        System.out.println("\nWelcome to the chess server! Register or sign in to start.\n");
        System.out.print(chessClient.help());

        String[] prePostLoginRequests = {"register", "login", "logout",
        "create", "list", "join", "observe", "help", "quit"};
        List<String> requestList = Arrays.asList(prePostLoginRequests);
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")){
            printPrompt();
            String line = scanner.nextLine();
            if (!requestList.contains(line)){
                makeGameplayReplRequest();
            }
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
