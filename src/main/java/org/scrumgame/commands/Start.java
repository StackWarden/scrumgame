package org.scrumgame.commands;

import org.scrumgame.classes.Player;
import org.scrumgame.database.DatabaseConnection;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;


@ShellComponent
public class Start {


    Player player = new Player();
    boolean naamBestaat(String name) {
        String sql = "SELECT 1 FROM player WHERE name = ?";

        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void voegSpelerToe(String name) {
        String sql = "INSERT INTO player (name) VALUES (?)";

        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Fout bij toevoegen speler: " + e.getMessage(), e);
        }
    }


    @ShellMethod (key = "start", value = "Launch the game")
    public String start() {
        return ("""
                ╔══════════════════════════════════════════════╗
                ║            Welkom bij de ScrumQuiz!          ║
                ╠══════════════════════════════════════════════╣
                ║                                              ║
                ║  Type '1 of begin' om te beginnen.           ║
                ║  Type '2 of load' om verder te gaan.         ║
                ║  Type '3 of exit' om de game te sluiten.     ║
                ║                                              ║
                ╚══════════════════════════════════════════════╝
                """);
    }

    @ShellMethod (key = {"begin", "1"}, value = "Start de game")
    public String begin() {
        Scanner scanner = new Scanner(System.in);
    System.out.println("""
                ╔══════════════════════════════════════════════╗
                ║                                              ║
                ║              Wat is jouw naam?               ║
                ║                                              ║
                ╚══════════════════════════════════════════════╝
                """);
    String name = scanner.nextLine();

        if (naamBestaat(name)) {
                return """
                ╔══════════════════════════════════════════════╗
                ║                                              ║
                ║       Je kan niet meer dan 1 keer met        ║
                ║        dezelfde naam de game starten.        ║
                ║                                              ║
                ╚══════════════════════════════════════════════╝
                """;
        }

        voegSpelerToe(name);
        player.setName(name);  // Set the name in the Player instance
        player.setCurrentPlayer(player);
        return """
                ╔══════════════════════════════════════════════╗
                ║                                              ║
                ║                 Placeholder                  ║
                ║                                              ║
                ╚══════════════════════════════════════════════╝
                """;
    }

@ShellMethod(key = {"load", "2"}, value = "Load de Save")
public String load() {
    String sql = "SELECT id, name FROM player ORDER BY id DESC LIMIT 1";
    
    try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            int playerId = rs.getInt("id");
            player.loadFromDatabase(playerId);
            player.setCurrentPlayer(player);
            return String.format("""
            ╔══════════════════════════════════════════════════════════╗
            ║                                                          ║
            ║      Welkom terug, %s!                                   ║
            ║                                                          ║
            ╚══════════════════════════════════════════════════════════╝
            """, player.getName());
        } else {
            return """
            ╔══════════════════════════════════════════════╗
            ║                                              ║
            ║        Er zijn nog geen spelers.             ║
            ║        Start eerst een nieuwe game.          ║
            ║                                              ║
            ╚══════════════════════════════════════════════╝
            """;
        }
    } catch (SQLException e) {
        throw new RuntimeException("Fout bij laden speler: " + e.getMessage(), e);
    }
}
}