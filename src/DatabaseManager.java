import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Clase para gestionar la conexión a la base de datos y crear las tablas necesarias
 */
public class DatabaseManager {
    private static final String DB_PATH = "database/Gymtracker.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_PATH;
    private static DatabaseManager instance;
    
    private DatabaseManager() {
        // Constructor privado para patrón Singleton
    }
    
    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    /**
     * Obtiene una conexión a la base de datos
     */
    public Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        return DriverManager.getConnection(DB_URL);
    }
    
    /**
     * Crea la tabla de ejercicios si no existe
     */
    public void initializeDatabase() {
        String createTableSQL = 
            "CREATE TABLE IF NOT EXISTS ejercicios (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "nombre TEXT NOT NULL, " +
            "grupo_muscular TEXT NOT NULL, " +
            "peso REAL NOT NULL, " +
            "repeticiones INTEGER NOT NULL, " +
            "fecha TEXT" +
            ");";
        
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            
            statement.execute(createTableSQL);
            
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error al inicializar la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

