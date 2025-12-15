import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

/**
 * Clase principal que inicia la aplicación GymTracker
 */
public class Main {
    public static void main(String[] args) {
        // Iniciar la interfaz gráfica en el hilo de eventos de Swing
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Iniciando GymTracker...");
                    GymTrackerGUI gui = new GymTrackerGUI();
                    System.out.println("GymTracker iniciado correctamente.");
                } catch (Exception e) {
                    System.err.println("Error al iniciar la aplicación: " + e.getMessage());
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, 
                        "Error al iniciar la aplicación: " + e.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}

