import java.sql.*;
import java.util.ArrayList;
import java.util.List;
// Representasi objek klien dengan atribut namaKlien, noHp, dan alamat.
//  metode untuk mengakses informasi dan representasi string.
public class klien {
    private String namaKlien;
    private String noHp;
    private String alamat;

    // Konstruktor untuk inisialisasi objek klien.
    public klien(String namaKlien, String noHp, String alamat) {
        this.namaKlien = namaKlien;
        this.noHp = noHp;
        this.alamat = alamat;
    }

    // Metode getter untuk mendapatkan namaKlien.
    public String getNamaKlien() {
        return namaKlien;
    }

    // Metode getter untuk mendapatkan noHp.
    public String getNoHp() {
        return noHp;
    }

    // Metode getter untuk mendapatkan alamat.
    public String getAlamat() {
        return alamat;
    }
    
     // Method untuk membaca semua data klien dari database.
    public static List<klien> bacaSemuaKlien() {  // Membuat list untuk menampung data klien
        List<klien> daftarKlien = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection("", "root", "")) {
            String query = "SELECT * FROM klien";
            try (Statement statement = connection.createStatement()) {
                try (ResultSet resultSet = statement.executeQuery(query)) {
                    while (resultSet.next()) {
                        klien klien = new klien(
                                resultSet.getString("nama_klien"),
                                resultSet.getString("no_hp_klien"),
                                resultSet.getString("alamat_klien")
                        );
                        daftarKlien.add(klien);  // Menambahkan objek klien ke dalam daftar
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();  // Menangani exception SQL jika ada kesalahan dalam query
        }
        return daftarKlien;
    }
// Method untuk menambah data klien ke database.
    public static void tambahKlien(klien klien) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_konstrusi", "root", "")) {
            String query = "INSERT INTO klien (nama_klien, no_hp_klien, alamat_klien) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, klien.getNamaKlien());
                statement.setString(2, klien.getNoHp());
                statement.setString(3, klien.getAlamat());
                statement.executeUpdate();
            }
        } catch (SQLException e) {  // Menangani exception SQL jika terjadi kesalahan saat menambah data
            e.printStackTrace();
        }
    }

    // Method untuk memperbarui data klien di database.
    public static void updateKlien(klien klien) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_konstrusi", "root", "")) {
            String query = "UPDATE klien SET no_hp_klien = ?, alamat_klien = ? WHERE nama_klien = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, klien.getNoHp());
                statement.setString(2, klien.getAlamat());
                statement.setString(3, klien.getNamaKlien());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

     // Method untuk menghapus data klien dari database berdasarkan nama.
    public static void hapusKlien(String namaKlien) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_konstrusi", "root", "")) {
            String query = "DELETE FROM klien WHERE nama_klien = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, namaKlien);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method untuk menyimpan data klien ke database
    public void simpanKeDatabase() throws SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_konstrusi", "root", "")) {
            String query = "INSERT INTO klien (nama_klien, no_hp_klien, alamat_klien) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, getNamaKlien());
                statement.setString(2, getNoHp());
                statement.setString(3, getAlamat());
                statement.executeUpdate();
            }
        }
    }
}
