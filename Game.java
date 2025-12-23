import java.io.Serializable;

public class Game implements Serializable {
    private String id;
    private String title;
    private String genre;
    private String platform; 
    private String status;  
    private double rating;   

    public Game(String id, String title, String genre, String platform, String status, double rating) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.platform = platform;
        this.status = status;
        this.rating = rating;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getGenre() { return genre; }
    public String getPlatform() { return platform; }
    public String getStatus() { return status; }
    public double getRating() { return rating; }

    public String toCSV() {
        return id + "," + title + "," + genre + "," + platform + "," + status + "," + rating;
    }

    public static Game fromCSV(String line) {
        String[] data = line.split(",");
        if (data.length < 6) return null;
        return new Game(data[0], data[1], data[2], data[3], data[4], Double.parseDouble(data[5]));
    }
}