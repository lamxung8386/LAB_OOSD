package ngk;

import java.io.*;
import java.nio.file.*;
import static ngk.Models.*;

/** Lưu và mở dữ liệu cục bộ, không cần cài đặt cơ sở dữ liệu ngoài. */
public final class DataStore {
    private final Path path;
    public DataStore(Path path) { this.path=path; }
    public Database load() throws IOException, ClassNotFoundException {
        if (!Files.exists(path)) return BusinessService.sample();
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(path))) {
            return (Database)in.readObject();
        }
    }
    public void save(Database db) throws IOException {
        Files.createDirectories(path.toAbsolutePath().getParent());
        Path temp = path.resolveSibling(path.getFileName().toString() + ".tmp");
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(temp))) {
            out.writeObject(db);
        }
        try { Files.move(temp,path,StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.ATOMIC_MOVE); }
        catch (AtomicMoveNotSupportedException e) { Files.move(temp,path,StandardCopyOption.REPLACE_EXISTING); }
    }
}
