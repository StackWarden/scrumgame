package org.scrumgame.abstracts;

import org.scrumgame.ScreenManager;

import java.util.List;
import java.util.Scanner;

public abstract class Screen {
    protected ScreenManager screenManager;
    protected Scanner scanner;

    public String alertMessage = null;

    public Screen(ScreenManager screenManager) {
        this.screenManager = screenManager;
        this.scanner = screenManager.getScanner();
    }

    public abstract void show();

    protected void printBreadcrumb(String... parts) {
        System.out.println();
        System.out.print("📍 ");
        for (int i = 0; i < parts.length; i++) {
            System.out.print(parts[i]);
            if (i != parts.length - 1) System.out.print(" > ");
        }
        System.out.println();
        printAlertMessage();
        System.out.println("────────────────────────────────────────────");
    }

    public void setAlertMessage(String errorMessage) {
        this.alertMessage = errorMessage;
    }

    public void printAlertMessage() {
        if (alertMessage != null) {
            System.out.println("Alert: " + alertMessage);
            alertMessage = null;
        }
    }

    protected static class MenuOption {
        private final String label;
        private final Runnable action;
        private final boolean visible;

        public MenuOption(String label, Runnable action) {
            this(label, action, true); // standaard zichtbaar
        }

        public MenuOption(String label, Runnable action, boolean visible) {
            this.label = label;
            this.action = action;
            this.visible = visible;
        }

        public String getLabel() {
            return label;
        }

        public void execute() {
            action.run();
        }

        public boolean isVisible() {
            return visible;
        }
    }


    protected void displayMenu(List<MenuOption> options, Runnable onBack) {
        while (true) {
            for (int i = 0; i < options.size(); i++) {
                MenuOption option = options.get(i);
                if (option.isVisible()) {
                    System.out.println((i + 1) + ". " + option.getLabel());
                }
            }
            if (onBack != null) {
                System.out.println("0. Back");
            }

            System.out.print("Select an option: ");
            String input = scanner.nextLine();

            try {
                if (!input.isEmpty()) {
                    int choice = Integer.parseInt(input);
                    if (choice == 0 && onBack != null) {
                        onBack.run();
                        return;
                    }
                    if (choice < 1 || choice > options.size()) {
                        setAlertMessage("Invalid option.");
                        continue;
                    }
                    MenuOption option = options.get(choice - 1);
                    if (option.isVisible()) {
                        option.execute();
                        return;
                    }
                }
            } catch (NumberFormatException e) {
                setAlertMessage("Please enter a valid number.");
            }
        }
    }
}
