package org.scrumgame.screens;

import org.scrumgame.ScreenManager;
import org.scrumgame.abstracts.Screen;

import java.util.List;

import static org.scrumgame.ScreenManager.clearScreen;

public class StartScreen extends Screen {

    public StartScreen(ScreenManager screenManager) {
        super(screenManager);
    }

    @Override
    public void show() {
        clearScreen();
        printBreadcrumb("Start");
        displayMenu(List.of(
                new MenuOption("Placeholder 1", () -> {
                    System.out.println("Placeholder 1");
                }),
                new MenuOption("Placeholder 2", this::placeholder),
                new MenuOption("Placeholder 3", () -> {
                    System.out.println("Placeholder 3");
                }),
                new MenuOption("Exit", () -> {
                    System.out.println("Goodbye!");
                    System.exit(0);
                })
        ), null);
    }

    public void placeholder() {
        System.out.println("Placeholder 2");
    }
}