import org.junit.jupiter.api.Test;
import ru.ylab.utils.Console;

import static org.junit.jupiter.api.Assertions.*;

public class ConsoleTest {

    @Test
    public void testWarning() {
        String input = "Warning message.";
        String expected = "\u001B[33mWarning message.\u001B[0m";
        assertEquals(expected, Console.warning(input));
    }

    @Test
    public void testSuccess() {
        String input = "Success message.";
        String expected = "\u001B[32mSuccess message.\u001B[0m";
        assertEquals(expected, Console.success(input));
    }

    @Test
    public void testCreateTable() {
        String[] headers = {"Column1", "Column2", "Column3"};
        String[][] data = {
            {"Value11", "Value12", "Value13"},
            {"Value21", "Value22", "Value23"},
            {"Value31", "Value32", "Value33"},
        };
        
        String expected = """
              +---------+---------+---------+
              | Column1 | Column2 | Column3 |
              +---------+---------+---------+
              | Value11 | Value12 | Value13 |
              | Value21 | Value22 | Value23 |
              | Value31 | Value32 | Value33 |
              +---------+---------+---------+
              """;
        
        assertEquals(expected, Console.createTable(headers, data));
    }

    @Test
    public void testCreateTableLongHeader() {
        String[] headers = {"Short header", "Long and descriptive header"};
        String[][] data = {
            {"A", "Data"},
            {"More data", "Much more data"}
        };

        String expected = """
                +--------------+-----------------------------+
                | Short header | Long and descriptive header |
                +--------------+-----------------------------+
                | A            | Data                        |
                | More data    | Much more data              |
                +--------------+-----------------------------+
                """;

        assertEquals(expected, Console.createTable(headers, data));
    }

    @Test
    public void testCreateTableLongData() {
        String[] headers = {"Short header", "Short header 2"};
        String[][] data = {
            {"A", "Data"},
            {"Much more long data", "Short data"}
        };

        String expected = """
              +---------------------+----------------+
              | Short header        | Short header 2 |
              +---------------------+----------------+
              | A                   | Data           |
              | Much more long data | Short data     |
              +---------------------+----------------+
              """;

        assertEquals(expected, Console.createTable(headers, data));
    }

    @Test
    public void testCreateTableEmptyData() {
        String[] headers = {"Column1", "Column2"};
        String[][] data = {};

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> Console.createTable(headers, data));
    }
}
