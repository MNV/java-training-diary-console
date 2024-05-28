package ru.ylab.utils;

/**
 * Console output processing functions.
 */
public class Console {
    private static final String COLOR_YELLOW = "\u001B[33m";
    private static final String COLOR_GREEN = "\u001B[32m";
    private static final String COLOR_RESET = "\u001B[0m";

    public static String warning(String text) {
        return String.format(COLOR_YELLOW + text + COLOR_RESET);
    }

    public static String success(String text) {
        return String.format(COLOR_GREEN + text + COLOR_RESET);
    }

    /**
     * Creating a table based on the given data.
     *
     * @param headers Headers list.
     * @param data Rows data.
     */
    public static String createTable(String[] headers, String[][] data) {
        int[] maxLengths = new int[data[0].length];
        String[][] tableData = new String[data.length][];

        for (int i = 0; i < data.length; i++) {
            tableData[i] = new String[data[i].length];
            for (int j = 0; j < data[i].length; j++) {
                tableData[i][j] = data[i][j];

                if (data[i][j].length() > maxLengths[j]) {
                    maxLengths[j] = data[i][j].length();
                }
                if (headers[j].length() > maxLengths[j]) {
                    maxLengths[j] = headers[j].length();
                }
            }
        }

        StringBuilder table = new StringBuilder();
        StringBuilder separator = new StringBuilder("+");
        for (int length : maxLengths) {
            separator.append("-".repeat(length + 2)).append("+");
        }
        separator.append("\n");

        table.append(separator);

        for (int i = 0; i < headers.length; i++) {
            table.append(String.format("| %-" + (maxLengths[i]) + "s ", headers[i]));
        }
        table.append("|\n");

        table.append(separator);

        for (String[] rowData : tableData) {
            for (int j = 0; j < rowData.length; j++) {
                table.append(String.format("| %-" + (maxLengths[j]) + "s ", rowData[j]));
            }
            table.append("|\n");
        }

        table.append(separator);

        return table.toString();
    }
}
