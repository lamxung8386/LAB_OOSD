import vn.edu.benhan.Specs;
import java.util.*;

/** Test that all CRUD sections use unique static table names and unique fields. No SQL Server needed. */
public class MetadataSmokeTest {
    public static void main(String[] args) {
        if (Specs.ALL.size() != 12) throw new AssertionError("Expected 12 screens");
        var keys = new HashSet<String>();
        for (var s : Specs.ALL) {
            if (!keys.add(s.key())) throw new AssertionError("Duplicate menu: " + s.key());
            var fields = new HashSet<String>();
            for (var f : s.fields()) {
                if (!fields.add(f.column())) throw new AssertionError("Duplicate column: " + f.column());
            }
            if (s.pk().isEmpty() || s.table().isEmpty()) throw new AssertionError("Missing table key");
        }
        System.out.println("PASS: 12 entity screens; all static keys and field names unique.");
    }
}
