import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

// Subclass Pemesanan yang turunan dari kelas Proyek dan mengimplementasikan interface estimasiWaktu.
public class pemesanan extends proyek implements estimasiWaktu {
    private klien klien;
    private double waktu;
    private String tanggal;
    private double luas;
    private String tanggalSelesai;

    // Konstruktor untuk inisialisasi objek Pemesanan
    public pemesanan(klien klien, proyek proyek, double luas) throws luasException {
        // Memanggil konstruktor superclass (Proyek).
        super(proyek.getNamaProyek(), proyek.getTanggalMulai(), proyek.getLuas());
        this.klien = klien;

        // Mengecek apakah luas proyek lebih besar dari 0
        if (luas <= 0) {
            throw new luasException("Luas proyek harus lebih besar dari 0.");
        }

        this.luas = luas;
        this.tanggal = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        hitungWaktu();  // Menghitung estimasi waktu berdasarkan luas.
        hitungTanggalSelesai();  // Menghitung estimasi waktu berdasarkan luas
    }

    // Mengimplementasikan metode hitungWaktu dari interface EstimasiWaktu
    @Override
    public double hitungWaktu() {
        waktu = luas * 3; // perhitungan matematis, setiap 1 m² membutuhkan 3 hari
        return waktu;
    }

    // Metode untuk menghitung tanggal estimasi selesai
    private void hitungTanggalSelesai() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = dateFormat.parse(getTanggalMulai());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);
            calendar.add(Calendar.DAY_OF_YEAR, (int) waktu); // Tambahkan estimasi waktu (hari) ke tanggal mulai
            tanggalSelesai = dateFormat.format(calendar.getTime()); // Format kembali ke bentuk tanggal.
        } catch (Exception e) {
            tanggalSelesai = "Tidak dapat menghitung tanggal selesai.";  //error handling
        }
    }

    // Getter untuk waktu
    public double getWaktu() {
        return waktu;
    }
    
    // Getter untuk mendapatkan tanggal estimasi selesai.
    public String getTanggalSelesai() {
        return tanggalSelesai;
    }

    // Metode untuk menampilkan informasi pemesanan ke konsol
    public void tampilkanInformasi() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd/MM/yyyy");

        System.out.println("=====================================");
        System.out.println("          PEMESANAN PROYEK    ");
        System.out.println("=====================================");
        System.out.println("Tanggal : " + dateFormat.format(new Date()).toUpperCase()); //manipulasi string
        System.out.println("Waktu   : " + new SimpleDateFormat("HH:mm:ss a zzz").format(new Date()).toUpperCase());
        System.out.println("=====================================");
        System.out.println("            DATA KLIEN          ");
        System.out.println("Nama Klien   : " + klien.getNamaKlien().toUpperCase());  //manipulasi string
        System.out.println("No. HP Klien : " + klien.getNoHp().toUpperCase());  //manipulasi string
        System.out.println("Alamat Klien : " + klien.getAlamat().toUpperCase());  //manipulasi string
        System.out.println("=====================================");
        System.out.println("            DATA PROYEK         ");
        System.out.println("Nama Proyek      : " + getNamaProyek().toUpperCase());  //manipulasi string
        System.out.println("Tanggal Mulai    : " + getTanggalMulai());
        System.out.println("Luas Proyek      : " + luas);
        System.out.println("Estimasi Waktu Selesai: " + waktu + " hari");
        System.out.println("Tanggal Selesai : " + tanggalSelesai);
        System.out.println("=====================================");
    }

    // Metode menyimpan pemesanan ke database
    public void simpanPemesanan() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_konstrusi", "root", "")) {
            String query = "INSERT INTO pemesanan (nama_klien, no_hp_klien, alamat_klien, " +
                    "nama_proyek, tanggal_mulai, luas, estimasi_waktu, tanggal, tanggal_selesai) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                // Menyusun parameter untuk query SQL
                statement.setString(1, klien.getNamaKlien());
                statement.setString(2, klien.getNoHp());
                statement.setString(3, klien.getAlamat());
                statement.setString(4, getNamaProyek());
                statement.setString(5, getTanggalMulai());
                statement.setDouble(6, luas);
                statement.setDouble(7, waktu);
                statement.setString(8, tanggal);
                statement.setString(9, tanggalSelesai);


                // Menjalankan query
                statement.executeUpdate();
                System.out.println("Pemesanan berhasil disimpan.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
