package scrumgame.screens;

import scrumgame.ScreenManager;
import scrumgame.abstracts.Screen;

import java.util.Scanner;

// Even een beetje uitleg over de screen logica.
// Deze template kanje kopieren en herbruiken het heeft de logica al van het ontvangen van een nummer je moet alleen zelf de handlers maken
public class BaseScreenTemplate extends Screen {

    protected final Scanner scanner;
    protected final ScreenManager screenManager;
    // Definieer hier alle controllers die je nodig gaat hebben (Ja joeri je mag deze lijn weghalen als je de template kopieert

    // De constructor moet alle controllers hebben die je nodig hebt.
    public BaseScreenTemplate(Scanner scanner, ScreenManager screenManager) {
        super(screenManager);
        this.scanner = scanner;
        this.screenManager = screenManager;
        // Vergeet de controller hier niet te attachen zoals hierboven.
    }

    // Deze functie laat het op het scherm zien dit wordt door de ScreenManager aangeroepen.
    @Override
    public void show() {
        boolean running = true;

        while (running) {
            printMenu();

            String input = scanner.nextLine();

            switch (input) {
                // Hier kan je zoveel nummers plaatsen als dat je wil, iedere nummer is een functie of text die je aanroept. moet de functie naar een andere scherm gaan
                // maak dan ook een andere scherm aan. Dit soort keuze menus moeten in respectieve scherm zitten voor overzicht. Houdt deze cases echt alleen voor functies
                // gerelateerd hieraan.

                case "1" -> handleOption1();
                case "2" -> handleOption2();
                // Reserveer 0 voor back, vorige screens hadden dit niet omdat je dan uitlogt of exit.
                case "0" -> {
                    System.out.println("Returning to previous menu...");
                    running = false;
                }
                // Dit default als de gebruiker grappig wilt zijn en letters gebruikt
                default -> System.out.println("Invalid input. Please try again.");
            }
        }
    }

    // Hier geef je aan wat iedere optie doet, dit ziet de gebruiker dus wees beleeft :D xD
    protected void printMenu() {
        System.out.println("===== Screen Title =====");
        System.out.println("1. Option 1");
        System.out.println("2. Option 2");
        System.out.println("0. Back");
    }

    // Hier kan je de functies maken ze hoeven niet protected te zijn puur gedaan omdat het kon
    protected void handleOption1() {
        System.out.println("Handling option 1...");
    }

    protected void handleOption2() {
        System.out.println("Handling option 2...");
    }
} // EN JA EEN END OF LINE
