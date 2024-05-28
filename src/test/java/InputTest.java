import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.ylab.utils.Input;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class InputTest {

    private PrintStream originalOut;

    @BeforeEach
    public void setUp() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
        System.setIn(System.in);
    }

    @ParameterizedTest
    @CsvSource({
        "null, true",
        "'', true",
        "'   ', true",
        "'   string  ', false",
        "string, false"
    })
    void testIsEmptyString(String input, boolean expected) {
        if ("null".equals(input)) {
            input = null;
        }
        assertEquals(expected, Input.isEmptyString(input));
    }

    @Test
    void testInputNonEmptyString() {
        String input = "\n   \nvalid\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);

        assertEquals("valid", Input.inputNonEmptyString(scanner, ""));
    }

    @Test
    void testInputBoolean() {
        String input = "invalid\ntrue\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);

        assertTrue(Input.inputBoolean(scanner, ""));
    }

    @Test
    void testInputPositiveInteger() {
        String input = "invalid\n-1\n101\n0\n50\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);

        assertEquals(50, Input.inputPositiveInteger(scanner, "", 1, 100));
    }

    @Test
    void testInputDate() throws ParseException {
        String input = "invalid\n31.02.2020\n15.05.2024\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);

        Date expected = new SimpleDateFormat("dd.MM.yyyy").parse("31.02.2020");
        Date actual = Input.inputDate(scanner, "");

        assertEquals(expected, actual);
    }
}
