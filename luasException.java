// Pengecualian khusus untuk menangani kesalahan jumlah pembelian yang tidak valid.
public class luasException extends Exception {
    public luasException(String message) {
        super(message);
    }
}
