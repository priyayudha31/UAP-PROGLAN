import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class GameLibraryApp extends JFrame {
    private List<Game> gameList;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private DefaultTableModel tableModel;
    private JTable table;

    private JTextArea reportArea;

    public GameLibraryApp() {
        setTitle("My Game Library Manager");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        gameList = GameDataHandler.loadGames();

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createDashboard(), "Dashboard");
        mainPanel.add(createInputForm(), "Input");
        mainPanel.add(createGameList(), "List");
        mainPanel.add(createReport(), "Report");

        add(mainPanel);
    }

    private JPanel createDashboard() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(40, 44, 52));

        JLabel title = new JLabel("Welcome to Game Library");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);

        JButton btnInput = createStyledButton("Tambah Game Baru");
        JButton btnList = createStyledButton("Lihat Koleksi");
        JButton btnReport = createStyledButton("Statistik / Laporan");

        btnInput.addActionListener(e -> cardLayout.show(mainPanel, "Input"));
        btnList.addActionListener(e -> {
            refreshTable(gameList); 
            cardLayout.show(mainPanel, "List");
        });
        btnReport.addActionListener(e -> {
            refreshReportData();
            cardLayout.show(mainPanel, "Report");
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(10,10,20,10);
        panel.add(title, gbc);
        gbc.gridy = 1; panel.add(btnInput, gbc);
        gbc.gridy = 2; panel.add(btnList, gbc);
        gbc.gridy = 3; panel.add(btnReport, gbc);

        return panel;
    }

    private JPanel createInputForm() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        JTextField txtTitle = new JTextField();
        String[] genres = {"RPG", "FPS", "Action", "Strategy", "Simulation"};
        JComboBox<String> cbGenre = new JComboBox<>(genres);
        String[] platforms = {"PC", "PlayStation 5", "Xbox", "Nintendo Switch", "Mobile"};
        JComboBox<String> cbPlatform = new JComboBox<>(platforms);
        String[] statuses = {"Backlog", "Playing", "Completed", "Dropped"};
        JComboBox<String> cbStatus = new JComboBox<>(statuses);
        JTextField txtRating = new JTextField(); // Input angka 1-10

        panel.add(new JLabel("Judul Game:")); panel.add(txtTitle);
        panel.add(new JLabel("Genre:")); panel.add(cbGenre);
        panel.add(new JLabel("Platform:")); panel.add(cbPlatform);
        panel.add(new JLabel("Status:")); panel.add(cbStatus);
        panel.add(new JLabel("Rating (1-10):")); panel.add(txtRating);

        JButton btnSave = new JButton("Simpan ke Library");
        JButton btnBack = new JButton("Kembali");

        btnSave.addActionListener(e -> {
            try {
                String title = txtTitle.getText();
                double rating = Double.parseDouble(txtRating.getText());
                
                if (title.isEmpty()) throw new Exception("Judul tidak boleh kosong!");
                if (rating < 0 || rating > 10) throw new Exception("Rating harus 1-10!");

                String id = String.valueOf(System.currentTimeMillis());
                Game newGame = new Game(id, title, (String)cbGenre.getSelectedItem(), 
                        (String)cbPlatform.getSelectedItem(), (String)cbStatus.getSelectedItem(), rating);

                gameList.add(newGame);
                GameDataHandler.saveGames(gameList);

                JOptionPane.showMessageDialog(this, "Game berhasil ditambahkan!");
                txtTitle.setText(""); txtRating.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Rating harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnBack.addActionListener(e -> cardLayout.show(mainPanel, "Dashboard"));
        panel.add(btnSave); panel.add(btnBack);
        return panel;
    }

    private JPanel createGameList() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel();
        JTextField txtSearch = new JTextField(15);
        JButton btnSearch = new JButton("Cari Judul");
        JButton btnSortRating = new JButton("Urutkan Rating (Tinggi->Rendah)");
        JButton btnDelete = new JButton("Hapus");
        JButton btnBack = new JButton("Home");

        topPanel.add(new JLabel("Cari:")); topPanel.add(txtSearch);
        topPanel.add(btnSearch); topPanel.add(btnSortRating);
        topPanel.add(btnDelete); topPanel.add(btnBack);

        String[] cols = {"ID", "Judul", "Genre", "Platform", "Status", "Rating"};
        tableModel = new DefaultTableModel(cols, 0);
        table = new JTable(tableModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(topPanel, BorderLayout.NORTH);

        btnSearch.addActionListener(e -> {
            String keyword = txtSearch.getText().toLowerCase();
            List<Game> filtered = gameList.stream()
                .filter(g -> g.getTitle().toLowerCase().contains(keyword))
                .collect(Collectors.toList());
            refreshTable(filtered);
        });

        btnSortRating.addActionListener(e -> {
            gameList.sort(Comparator.comparingDouble(Game::getRating).reversed());
            refreshTable(gameList);
            GameDataHandler.saveGames(gameList); 
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                String id = (String) tableModel.getValueAt(row, 0);
                gameList.removeIf(g -> g.getId().equals(id));
                GameDataHandler.saveGames(gameList);
                refreshTable(gameList);
                JOptionPane.showMessageDialog(this, "Data dihapus!");
            } else {
                JOptionPane.showMessageDialog(this, "Pilih baris dulu!");
            }
        });

        btnBack.addActionListener(e -> cardLayout.show(mainPanel, "Dashboard"));
        return panel;
    }

    private JPanel createReport() {
        JPanel panel = new JPanel(new BorderLayout());
        reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        reportArea.setMargin(new Insets(20,20,20,20));

        JButton btnBack = new JButton("Kembali ke Dashboard");
        btnBack.addActionListener(e -> cardLayout.show(mainPanel, "Dashboard"));

        panel.add(new JScrollPane(reportArea), BorderLayout.CENTER);
        panel.add(btnBack, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshTable(List<Game> data) {
        tableModel.setRowCount(0);
        for (Game g : data) {
            tableModel.addRow(new Object[]{g.getId(), g.getTitle(), g.getGenre(), 
                                           g.getPlatform(), g.getStatus(), g.getRating()});
        }
    }

    private void refreshReportData() {
        long completed = gameList.stream().filter(g -> g.getStatus().equals("Completed")).count();
        long playing = gameList.stream().filter(g -> g.getStatus().equals("Playing")).count();
        long backlog = gameList.stream().filter(g -> g.getStatus().equals("Backlog")).count();
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== LAPORAN KOLEKSI GAME ===\n\n");
        sb.append("Total Game di Library : ").append(gameList.size()).append("\n");
        sb.append("Game Tamat (Completed): ").append(completed).append("\n");
        sb.append("Sedang Dimainkan      : ").append(playing).append("\n");
        sb.append("Tumpukan Backlog      : ").append(backlog).append("\n\n");
        sb.append("--- Detail Rating Tertinggi ---\n");
        
        gameList.stream()
                .sorted(Comparator.comparingDouble(Game::getRating).reversed())
                .limit(3)
                .forEach(g -> sb.append(g.getTitle()).append(" - Score: ").append(g.getRating()).append("\n"));

        reportArea.setText(sb.toString());
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(250, 40));
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameLibraryApp().setVisible(true));
    }
}