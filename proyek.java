
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Representasi objek Proyek dengan atribut namaProyek, tanggalMulai, dan luas.
public class proyek {
    protected String namaProyek;
    protected String tanggalMulai;
    protected double luas;

    // Konstruktor untuk inisialisasi objek Proyek.
    public proyek(String namaProyek, String tanggalMulai, double luas) {
        this.namaProyek = namaProyek;
        this.tanggalMulai = tanggalMulai;
        this.luas = luas;
    }

    // Metode getter untuk mendapatkan namaProyek.
    public String getNamaProyek() {
        return namaProyek;
    }

    // Metode getter untuk mendapatkan tanggalMulai.
    public String getTanggalMulai() {
        return tanggalMulai;
    }

    // Metode getter untuk mendapatkan luas.
    public double getLuas() {
        return luas;
    }

    // Metode untuk mendapatkan proyek berdasarkan namaProyek.
    public static proyek getProyekByNama(String namaProyek) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_konstrusi", "root", "")) {
            String query = "SELECT * FROM proyek WHERE nama_proyek = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, namaProyek);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        String tanggalMulai = resultSet.getString("tanggal_mulai");
                        double luas = resultSet.getDouble("luas");

                        return new proyek(namaProyek, tanggalMulai, luas);
                    }
                }
            }
        } catch (SQLException e) { //exception handling
            e.printStackTrace(); 
        }

        return null; // Return null jika proyek tidak ditemukan.
    }

    // Metode untuk membaca semua proyek.
    public static List<proyek> bacaSemuaProyek() {
        List<proyek> daftarProyek = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_konstrusi", "root", "")) {
            String query = "SELECT * FROM proyek ORDER BY nama_proyek";  // Memastikan data diurutkan berdasarkan nama proyek
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {
    
                while (resultSet.next()) {
                    String namaProyek = resultSet.getString("nama_proyek");
                    String tanggalMulai = resultSet.getString("tanggal_mulai");
                    double luas = resultSet.getDouble("luas");
    
                    // Membuat objek proyek dan menambahkannya ke daftar
                    proyek p = new proyek(namaProyek, tanggalMulai, luas);
                    daftarProyek.add(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return daftarProyek;
    }

    // Metode untuk menambahkan proyek ke database.
    public static void tambahProyek(proyek proyek) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_konstrusi", "root", "")) {
            String query = "INSERT INTO proyek (nama_proyek, tanggal_mulai, luas) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, proyek.getNamaProyek());
                statement.setString(2, proyek.getTanggalMulai());
                statement.setDouble(3, proyek.getLuas());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Metode untuk memperbarui proyek.
    public static void updateProyek(proyek proyek) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_konstrusi", "root", "")) {
            String query = "UPDATE proyek SET tanggal_mulai = ?, luas = ? WHERE nama_proyek = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, proyek.getTanggalMulai());
                statement.setDouble(2, proyek.getLuas());
                statement.setString(3, proyek.getNamaProyek());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Metode untuk menghapus proyek berdasarkan namaProyek.
    public static void hapusProyek(String namaProyek) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_konstrusi", "root", "")) {
            String query = "DELETE FROM proyek WHERE nama_proyek = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, namaProyek);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}






