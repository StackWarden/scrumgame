package org.scrumgame;

import org.scrumgame.abstracts.Screen;
import org.scrumgame.enums.ScreenType;
import org.scrumgame.screens.StartScreen;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ScreenManager {

    private final Map<ScreenType, Screen> screenRegistry = new HashMap<>();
    private Screen currentScreen;

    // Als je een nieuwe controller maak of iets anders dat overal gebruikt moet worden instantiate het dan hier.
    private final Scanner scanner = new Scanner(System.in);

    public ScreenManager() {
        // Registreer alle Schermen die je aanmaakt (en wilt gebruiken) hieronder. Zie basetemplate voor meer info
        // Je moet ook bij ScreenType de titel van je scherm toevoegen dus als je UserStory scherm maak voeg die dan toe als USERSTORY
        // en dan hieronder register(ScreenType.USERSTORY, new UserStoryScreen(alle meuk));
        register(ScreenType.START_SCREEN, new StartScreen(this));
    }

    private void register(ScreenType type, Screen screen) {
        screenRegistry.put(type, screen);
    }

    public void switchTo(ScreenType type) {
        currentScreen = screenRegistry.get(type);
        if (currentScreen != null) {
            currentScreen.show();
        } else {
            System.out.println("Screen not found: " + type);
        }
    }

    public static void clearScreen() {
        try {
            String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            printBlankLines();
        }

        printBlankLines();
    }


    private static void printBlankLines() {
        for (int i = 0; i < 40; i++) {
            System.out.println();
        }
    }

    public Scanner getScanner() {
        return scanner;
    }
}
