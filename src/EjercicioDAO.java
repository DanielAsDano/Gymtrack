import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase Data Access Object para manejar las operaciones CRUD de ejercicios
 */
public class EjercicioDAO {
    private DatabaseManager dbManager;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
    
    public EjercicioDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    /**
     * Inserta un nuevo ejercicio en la base de datos
     */
    public boolean insertarEjercicio(Ejercicio ejercicio) {
        String sql = "INSERT INTO ejercicios (nombre, grupo_muscular, peso, repeticiones, fecha) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection connection = dbManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setString(1, ejercicio.getNombre());
            pstmt.setString(2, ejercicio.getGrupoMuscular());
            pstmt.setDouble(3, ejercicio.getPeso());
            pstmt.setInt(4, ejercicio.getRepeticiones());
            
            if (ejercicio.getFecha() != null) {
                pstmt.setString(5, ejercicio.getFecha().format(dateFormatter));
            } else {
                pstmt.setString(5, null);
            }
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error al insertar ejercicio: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Método auxiliar para crear un objeto Ejercicio desde un ResultSet
     */
    private Ejercicio crearEjercicioDesdeResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nombre = rs.getString("nombre");
        String grupoMuscular = rs.getString("grupo_muscular");
        double peso = rs.getDouble("peso");
        int repeticiones = rs.getInt("repeticiones");
        
        LocalDate fecha = null;
        String fechaStr = rs.getString("fecha");
        if (fechaStr != null && !fechaStr.isEmpty()) {
            fecha = LocalDate.parse(fechaStr, dateFormatter);
        }
        
        return new Ejercicio(id, nombre, grupoMuscular, peso, repeticiones, fecha);
    }
    
    /**
     * Obtiene todos los ejercicios de la base de datos
     */
    public List<Ejercicio> obtenerTodosLosEjercicios() {
        List<Ejercicio> ejercicios = new ArrayList<>();
        String sql = "SELECT * FROM ejercicios ORDER BY fecha DESC, id DESC";
        
        try(Connection connection = dbManager.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                ejercicios.add(crearEjercicioDesdeResultSet(rs));
            }
            
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error al obtener ejercicios: " + e.getMessage());
            e.printStackTrace();
        }
        
        return ejercicios;
    }
    
    /**
     * Obtiene ejercicios filtrados por nombre
     */
    public List<Ejercicio> obtenerEjerciciosPorNombre(String nombreEjercicio) {
        List<Ejercicio> ejercicios = new ArrayList<>();
        String sql = "SELECT * FROM ejercicios WHERE nombre = ? ORDER BY fecha DESC, id DESC";
        
        try(Connection connection = dbManager.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setString(1, nombreEjercicio);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ejercicios.add(crearEjercicioDesdeResultSet(rs));
                }
            }
            
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error al obtener ejercicios por nombre: " + e.getMessage());
            e.printStackTrace();
        }
        
        return ejercicios;
    }
    
    /**
     * Actualiza un ejercicio existente
     */
    public boolean actualizarEjercicio(Ejercicio ejercicio) {
        String sql = "UPDATE ejercicios SET nombre = ?, grupo_muscular = ?, peso = ?, repeticiones = ?, fecha = ? WHERE id = ?";
        
        try(Connection connection = dbManager.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setString(1, ejercicio.getNombre());
            pstmt.setString(2, ejercicio.getGrupoMuscular());
            pstmt.setDouble(3, ejercicio.getPeso());
            pstmt.setInt(4, ejercicio.getRepeticiones());
            
            if (ejercicio.getFecha() != null) {
                pstmt.setString(5, ejercicio.getFecha().format(dateFormatter));
            } else {
                pstmt.setString(5, null);
            }
            
            pstmt.setInt(6, ejercicio.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error al actualizar ejercicio: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Elimina un ejercicio por su ID
     */
    public boolean eliminarEjercicio(int id) {
        String sql = "DELETE FROM ejercicios WHERE id = ?";
        
        try(Connection connection = dbManager.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error al eliminar ejercicio: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Obtiene la lista de nombres únicos de ejercicios
     */
    public List<String> obtenerNombresEjercicios() {
        List<String> nombres = new ArrayList<>();
        String sql = "SELECT DISTINCT nombre FROM ejercicios ORDER BY nombre";
        
        try(Connection connection = dbManager.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                nombres.add(rs.getString("nombre"));
            }
            
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error al obtener nombres de ejercicios: " + e.getMessage());
            e.printStackTrace();
        }
        
        return nombres;
    }
}

