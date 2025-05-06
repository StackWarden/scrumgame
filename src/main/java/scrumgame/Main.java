package scrumgame;

import scrumgame.enums.ScreenType;

public class Main {
    public static void main(String[] args) {
        ScreenManager screenManager = new ScreenManager();
        screenManager.switchTo(ScreenType.START_SCREEN);
    }
}