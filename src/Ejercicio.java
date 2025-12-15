import java.time.LocalDate;

/**
 * Clase modelo que representa un ejercicio registrado en el gimnasio
 */
public class Ejercicio {
    private int id;
    private String nombre;
    private String grupoMuscular;
    private double peso;
    private int repeticiones;
    private LocalDate fecha;
    
    // Constructor para crear nuevos ejercicios (sin ID)
    public Ejercicio(String nombre, String grupoMuscular, double peso, int repeticiones, LocalDate fecha) {
        this.nombre = nombre;
        this.grupoMuscular = grupoMuscular;
        this.peso = peso;
        this.repeticiones = repeticiones;
        this.fecha = fecha;
    }
    
    // Constructor completo con ID (para cargar desde BD)
    public Ejercicio(int id, String nombre, String grupoMuscular, double peso, int repeticiones, LocalDate fecha) {
        this.id = id;
        this.nombre = nombre;
        this.grupoMuscular = grupoMuscular;
        this.peso = peso;
        this.repeticiones = repeticiones;
        this.fecha = fecha;
    }
    
    // Getters y Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getGrupoMuscular() {
        return grupoMuscular;
    }
    
    public void setGrupoMuscular(String grupoMuscular) {
        this.grupoMuscular = grupoMuscular;
    }
    
    public double getPeso() {
        return peso;
    }
    
    public void setPeso(double peso) {
        this.peso = peso;
    }
    
    public int getRepeticiones() {
        return repeticiones;
    }
    
    public void setRepeticiones(int repeticiones) {
        this.repeticiones = repeticiones;
    }
    
    public LocalDate getFecha() {
        return fecha;
    }
    
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
    
    @Override
    public String toString() {
        return String.format("%s - %s kg x %d reps (%s)", 
            nombre, peso, repeticiones, 
            fecha != null ? fecha.toString() : "Sin fecha");
    }
}


