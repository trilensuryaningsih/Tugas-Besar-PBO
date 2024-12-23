import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

//class utama program
public class VirtualDemo {
    //scanner sebagai objek untuk input
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean inputValid = false; // Variabel untuk validasi input pengguna

        // Menampilkan ASCII art
        tampilkanAsciiArt();

        // Perulangan untuk meminta login hingga valid
        while (!inputValid) {
            try {
                // Menampilkan tanggal dan waktu saat ini (manipulasi Date) 
                System.out.println("Tanggal: " + getCurrentDate());
                System.out.println("Waktu  : " + getCurrentTime());

                // Input data untuk log in
                System.out.println("-------------------------------------");
                System.out.println("     SELAMAT DATANG DI PROYEKKU     ");
                System.out.println("   SILAHKAN LOG IN TERLEBIH DAHULU");
                System.out.println("-------------------------------------");
                System.out.print("Username: ");
                String username = scanner.nextLine();  // Manipulasi string untuk input username

                System.out.print("Password: ");
                String password = scanner.nextLine();

                // Input dan verifikasi captcha
                String captcha = generateCaptcha();
                System.out.println("Captcha: " + captcha);

                System.out.print("Masukkan Captcha (case insensitive): ");
                String userCaptcha = scanner.nextLine();

                // Verifikasi log in dan captcha
                //percabangan
                if (isValidLogin(username, password) && isValidCaptcha(captcha, userCaptcha)) {
                    System.out.println("Log in berhasil!");

                    int menuChoice;
                    // Perulangan untuk menu utama hingga pengguna memilih keluar
                    do {
                        System.out.println("Menu Utama:");
                        System.out.println("1. Lihat Semua proyek");
                        System.out.println("2. Tambah Data proyek");
                        System.out.println("3. Hapus Data proyek");
                        System.out.println("4. Edit Luas proyek");  
                        System.out.println("5. Input Data pemesanan");
                        System.out.println("6. Keluar");
                        System.out.print("Pilih menu (1-6): ");
                        menuChoice = scanner.nextInt();
                        scanner.nextLine(); 

                    // Percabangan untuk setiap pilihan menu
                        switch (menuChoice) {
                            case 1:
                                lihatSemuaProyek(); // Collection untuk menampilkan semua proyek
                                //fungsi read
                                break;
                            case 2:
                                tambahProyek(); //fungsi create
                                break;
                            case 3:
                                hapusProyek(); //fungsi delete
                                break;
                            case 4:
                                updateProyek();  //fungsi update
                                break;
                            case 5:
                                klien klien = inputKlien(); // Membuat objek klien dari class "klien"
                                inputPemesanan(klien); // Menggunakan objek dalam operasi pemesanan
                                break;
                            case 6:
                                System.out.println("Keluar dari Menu Utama.");
                                terimakasihAsciiArt(); //ASCII untuk terima kasih
                                inputValid = true;
                                break;
                            default:
                                System.out.println("Pilihan tidak valid. Silakan pilih lagi.");
                        }
                    } while (!inputValid);
                    
                } else {
                    System.out.println("Log in gagal. Pastikan username, password, dan captcha benar.");
                }
                //exception handling
            } catch (InputMismatchException e) {  // Exception handling untuk input tidak valid
                System.out.println("Error: Pastikan input sesuai dengan format yang benar.");
                scanner.nextLine();
            } catch (NumberFormatException e) {  
                System.out.println("Error: luas harus angka.");
                scanner.nextLine();
            } catch (luasException e) {  // Custom exception untuk validasi input
                System.out.println("Error: " + e.getMessage());
                scanner.nextLine();
            } catch (Exception e) {  // Exception handling umum
                System.out.println("Terjadi kesalahan: " + e.getMessage());
                scanner.nextLine();
            }
        }

        scanner.close(); //tutup scanner
    }

    // Perbaikan metode hapusProyek
    private static void hapusProyek() {
        System.out.println("HAPUS DATA PROYEK");
        System.out.println("-------------------------------------");

        System.out.print("Input nama proyek yang akan dihapus: ");
        String namaProyek = scanner.nextLine();

        // Panggil metode untuk menghapus proyek dari database
        hapusProyekDariDatabase(namaProyek);

        System.out.println("Data proyek berhasil dihapus.");
        System.out.println("=====================================");
    }

    private static void hapusProyekDariDatabase(String namaProyek) {
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

    // Perbaikan metode updateProyek
    private static void updateProyek() {
        System.out.println("EDIT DATA PROYEK");
        System.out.println("-------------------------------------");

        System.out.print("Input Nama Proyek yang akan diupdate: ");
        String namaProyekInput = scanner.nextLine();

        // Retrieve existing proyek data
        proyek existingProyek = proyek.getProyekByNama(namaProyekInput);

        if (existingProyek != null) {
            System.out.println("Data Proyek Saat Ini:");
            System.out.println(existingProyek);

            System.out.print("Nama proyek baru    : ");
            String namaProyek = scanner.nextLine();

            double luas;
            System.out.print("Ubah luas proyek    : ");
            luas = Double.parseDouble(scanner.nextLine());

            // Hitung estimasi waktu dan tanggal selesai berdasarkan luas
            int estimasiWaktu = hitungEstimasiWaktu(luas); // Hitung estimasi waktu berdasarkan luas
            String tanggalSelesai = hitungTanggalSelesai(existingProyek.getTanggalMulai(), estimasiWaktu); // Hitung tanggal selesai

            // Tampilkan estimasi waktu dan tanggal selesai
            System.out.println("Estimasi Waktu Pengerjaan: " + estimasiWaktu + " hari");
            System.out.println("Tanggal Selesai Proyek: " + tanggalSelesai);

            // Create a new Proyek object with updated data
            proyek updatedProyek = new proyek(namaProyek, existingProyek.getTanggalMulai(), luas);

            // Update proyek in the database
            proyek.updateProyek(updatedProyek);

            // Update tabel pemesanan jika proyek telah diperbarui
            updatePemesanan(namaProyek, luas, estimasiWaktu, tanggalSelesai);
            
                        System.out.println("Data proyek berhasil diupdate.");
                    } else {
                        System.out.println("Error: proyek tidak ditemukan.");
                    }
                }
            
                private static void updatePemesanan(String namaProyek, double luas, int estimasiWaktu, String tanggalSelesai) {
                    try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_konstrusi", "root", "")) {
                        // Query untuk update data pemesanan terkait dengan proyek yang diubah
                        String query = "UPDATE pemesanan SET luas = ?, estimasi_waktu = ?, tanggal_selesai = ? WHERE nama_proyek = ?";
                        try (PreparedStatement statement = connection.prepareStatement(query)) {
                            statement.setDouble(1, luas);
                            statement.setInt(2, estimasiWaktu);
                            statement.setString(3, tanggalSelesai);
                            statement.setString(4, namaProyek);
                            statement.executeUpdate();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            
                private static int hitungEstimasiWaktu(double luas) {
        // Misalnya, setiap 1 meter persegi memerlukan 3 hari pengerjaan
        int waktuPerMeter = 3; // 3 hari per 1 meter persegi
        return (int) (luas * waktuPerMeter); // Menghitung estimasi waktu berdasarkan luas
    }

    private static String hitungTanggalSelesai(String tanggalMulai, int estimasiWaktu) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateFormat.parse(tanggalMulai));

            // Menambahkan estimasi waktu (dalam hari) ke tanggal mulai
            calendar.add(Calendar.DAY_OF_MONTH, estimasiWaktu);

            // Mengembalikan tanggal selesai dalam format string
            return dateFormat.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return "Error menghitung tanggal selesai";
        }
    }
    
    private static klien inputKlien() {
        System.out.println("INPUT DATA KLIEN");
        System.out.println("-------------------------------------");
        System.out.print("Input Nama Klien   : ");
        String namaKlien = scanner.nextLine();
    
        System.out.print("Input No. HP klien : ");
        String noHpKlien = scanner.nextLine();
    
        System.out.print("Input Alamat Klien : ");
        String alamatKlien = scanner.nextLine();
    
        klien klien = new klien(namaKlien, noHpKlien, alamatKlien);
    
        try {
            klien.simpanKeDatabase();
            System.out.println("Data klien berhasil disimpan.");
        } catch (SQLException e) {
            System.out.println("Gagal menyimpan data klien: " + e.getMessage());
        }
    
        return klien;
    }

    private static void inputPemesanan(klien klien) throws IllegalArgumentException, luasException {
        System.out.println("INPUT DATA PROYEK KLIEN");
        System.out.println("-------------------------------------");
    
        System.out.print("Input nama proyek       : ");
        String namaProyek = scanner.nextLine();
    
        System.out.print("Input tanggal mulai (yyyy-mm-dd)      : ");
        String tanggalMulai = scanner.nextLine();
    
        double luas;
        try {
            System.out.print("Input luas proyek      : ");
            luas = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Error: luas proyek harus berupa angka.");
            return;
        }
    
        // Validasi jika luas tidak positif
        if (luas <= 0) {
            throw new luasException("Error: Luas proyek harus lebih besar dari 0.");
        }

        // Membuat objek proyek baru dan simpan ke database
        proyek proyekBaru = new proyek(namaProyek, tanggalMulai, luas);
        proyek.tambahProyek(proyekBaru);  // Menyimpan proyek ke dalam tabel proyek


        // Membuat objek Pemesanan dengan data yang telah diinput
        pemesanan pemesanan = new pemesanan(klien, new proyek(namaProyek, tanggalMulai, luas), luas);
    
        // Menampilkan informasi pemesanan
        pemesanan.tampilkanInformasi();
    
        // Simpan Pemesanan
        pemesanan.simpanPemesanan();

        System.out.println("Proyek klien berhasil disimpan.");
        System.out.println("=====================================");
    }
    

    private static String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(Calendar.getInstance().getTime());
    }

    private static String getCurrentTime() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a zzz");
        return timeFormat.format(Calendar.getInstance().getTime());
    }

    private static String generateCaptcha() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        int length = 6;
        StringBuilder captcha = new StringBuilder();

        for (int i = 0; i < length; i++) {
            captcha.append(characters.charAt((int) (Math.random() * characters.length())));
        }

        return captcha.toString();
    }

    private static boolean isValidLogin(String username, String password) {
        return "konstruktor".equals(username) && "admin123".equals(password);
    }

    private static boolean isValidCaptcha(String generatedCaptcha, String userCaptcha) {
        return generatedCaptcha.equals(userCaptcha);
    }

    // Metode untuk menampilkan semua proyek (menggunakan Collection Framework)
    private static void lihatSemuaProyek() {
        List<proyek> daftarProyek = proyek.bacaSemuaProyek();  // List sebagai Collection untuk menampung proyek

        System.out.println("DAFTAR SEMUA PROYEK");
        System.out.println("-------------------------------------");
        System.out.println("| Nama Proyek           | Tanggal Mulai  | Luas   |");
        System.out.println("-------------------------------------");

        for (proyek p : daftarProyek) {  // Perulangan untuk menampilkan proyek
            System.out.printf("| %-20s | %-20s | %-7.2f |\n", p.getNamaProyek(), p.getTanggalMulai(), p.getLuas());
        }

        System.out.println("-------------------------------------");
    }

    private static void tampilkanAsciiArt() {
        System.out.println("                                            ██      ▀██               ");
        System.out.println(" ▄▄▄▄▄▄   ▄▄▄ ▄▄   ▄▄▄    ▄▄▄▄ ▄▄▄   ▄▄▄▄   ██  ▄▄   ██  ▄▄  ▄▄▄ ▄▄▄  ");
        System.out.println(" ██▀  ██  ██▀ ▀▀ ▄█  ▀█▄   ▀█▄  █  ▄█▄▄▄██  ██ ▄▀    ██ ▄▀    ██  ██  ");
        System.out.println(" ██    █  ██     ██   ██    ▀█▄█   ██       ██▀█▄    ██▀█▄    ██  ██  ");
        System.out.println(" ██▄▄▄▀  ▄██▄    ▀█▄▄█▀      ▀█     ▀█▄▄▄▀ ▄██▄ ██▄ ▄██▄ ██▄  ▀█▄▄▀█▄ ");
        System.out.println(" ██                        ▄▄ █                                        ");
        System.out.println("▀▀▀▀                        ▀▀                                         ");
    }
    
    private static void terimakasihAsciiArt() {
        System.out.println(" ▄                    ██                        ▀██                      ██  ▀██      ");
        System.out.println("▄██▄    ▄▄▄▄  ▄▄▄ ▄▄  ▄▄▄  ▄▄ ▄▄ ▄▄    ▄▄▄▄       ██  ▄▄   ▄▄▄▄    ▄▄▄▄  ▄▄▄   ██ ▄▄   ");
        System.out.println(" ██   ▄█▄▄▄██  ██▀ ▀▀  ██   ██ ██ ██  ▀▀ ▄██      ██ ▄▀   ▀▀ ▄██  ██▄ ▀   ██   ██▀ ██  ");
        System.out.println(" ██   ██       ██      ██   ██ ██ ██  ▄█▀ ██      ██▀█▄   ▄█▀ ██  ▄ ▀█▄▄  ██   ██  ██  ");
        System.out.println(" ▀█▄▀  ▀█▄▄▄▀ ▄██▄    ▄██▄ ▄██ ██ ██▄ ▀█▄▄▀█▀    ▄██▄ ██▄ ▀█▄▄▀█▀ █▀▄▄█▀ ▄██▄ ▄██▄ ██▄ ");
        System.out.println("                                                                                        ");
        System.out.println("                                                                                        ");
    }

    // Metode untuk menambah proyek baru ke database
    public static void tambahProyek() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_konstrusi", "root", "")) {
            String query = "INSERT INTO proyek (nama_proyek, tanggal_mulai, luas) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                System.out.print("Input nama proyek   : ");
                String namaProyek = scanner.nextLine();

                // Manipulasi String untuk nama proyek
            namaProyek = namaProyek.toUpperCase();

                System.out.print("Input tanggal mulai   : ");
                String tanggalMulai = scanner.nextLine();

                double luas;
                try {
                    System.out.print("Input luas proyek  : ");
                    luas = Double.parseDouble(scanner.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Error: luas proyek harus berupa angka.");
                    return;
                }

                statement.setString(1, namaProyek);
                statement.setString(2, tanggalMulai);
                statement.setDouble(3, luas);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
