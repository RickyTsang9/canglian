import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Executes MySQL upgrade scripts with delimiter support for local delivery checks.
 */
public class SchemaUpgradeRunner {
    /**
     * Runs every SQL file passed after the JDBC configuration arguments.
     *
     * @param args jdbc url, user, password, followed by SQL file paths
     * @throws Exception when the database connection or SQL execution fails
     */
    public static void main(String[] args) throws Exception {
        if (args.length < 4) {
            throw new IllegalArgumentException("usage: SchemaUpgradeRunner <jdbcUrl> <user> <password> <sqlFile>...");
        }
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection connection = DriverManager.getConnection(args[0], args[1], args[2])) {
            for (int argumentIndex = 3; argumentIndex < args.length; argumentIndex++) {
                executeSqlFile(connection, new File(args[argumentIndex]));
            }
        }
    }

    /**
     * Executes one SQL script and honors MySQL client delimiter directives.
     *
     * @param connection active database connection
     * @param sqlFile script file to execute
     * @throws Exception when reading or executing the script fails
     */
    private static void executeSqlFile(Connection connection, File sqlFile) throws Exception {
        String delimiter = ";";
        StringBuilder statementText = new StringBuilder();
        int executedCount = 0;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(sqlFile), StandardCharsets.UTF_8))) {
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                String trimmedLine = currentLine.trim();
                if (trimmedLine.length() == 0 || trimmedLine.startsWith("--")) {
                    continue;
                }
                if (trimmedLine.toLowerCase().startsWith("delimiter ")) {
                    delimiter = trimmedLine.substring("delimiter ".length()).trim();
                    continue;
                }
                statementText.append(currentLine).append(System.lineSeparator());
                if (trimmedLine.endsWith(delimiter)) {
                    String sql = statementText.toString();
                    sql = sql.substring(0, sql.lastIndexOf(delimiter)).trim();
                    if (sql.length() > 0) {
                        executeStatement(connection, sql);
                        executedCount++;
                    }
                    statementText.setLength(0);
                }
            }
        }
        String remainingSql = statementText.toString().trim();
        if (remainingSql.length() > 0) {
            executeStatement(connection, remainingSql);
            executedCount++;
        }
        System.out.println("executed " + executedCount + " statements from " + sqlFile.getName());
    }

    /**
     * Executes a single SQL statement.
     *
     * @param connection active database connection
     * @param sql statement text
     * @throws Exception when JDBC execution fails
     */
    private static void executeStatement(Connection connection, String sql) throws Exception {
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }
}
