import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GameDataHandler {
    private static final String FILE_NAME = "my_games.csv";

    public static void saveGames(List<Game> games) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Game game : games) {
                writer.write(game.toCSV());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Gagal menyimpan data: " + e.getMessage());
        }
    }

    public static List<Game> loadGames() {
        List<Game> games = new ArrayList<>();
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            try { file.createNewFile(); } 
            catch (IOException e) { e.printStackTrace(); }
            return games;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Game game = Game.fromCSV(line);
                if (game != null) games.add(game);
            }
        } catch (IOException e) {
            System.err.println("Gagal membaca data: " + e.getMessage());
        }
        return games;
    }
}