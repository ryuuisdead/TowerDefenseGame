package mygame.map;

import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.List;

public class Path {
    
    private List<Vector3f> waypoints = new ArrayList<>();
    private int[][] pathCoordinates; // Representación de la cuadrícula del camino
    
    public Path() {
        // Inicializar waypoints (puntos del camino)
        createPath();
        
        // Crear la representación en coordenadas de cuadrícula del camino
        createPathCoordinates();
    }
    
    private void createPath() {
        // Nuevo camino más claro y con más puntos para suavizar el movimiento
        
        // Punto de inicio - Borde izquierdo del mapa
        waypoints.add(new Vector3f(-9, 0.1f, 0));
        
        // Primera curva - Entrada
        waypoints.add(new Vector3f(-7, 0.1f, 0));
        waypoints.add(new Vector3f(-5, 0.1f, 0));
        waypoints.add(new Vector3f(-4, 0.1f, 0));
        
        // Giro hacia arriba
        waypoints.add(new Vector3f(-4, 0.1f, 1));
        waypoints.add(new Vector3f(-4, 0.1f, 3));
        waypoints.add(new Vector3f(-4, 0.1f, 5));
        
        // Giro hacia derecha
        waypoints.add(new Vector3f(-3, 0.1f, 5));
        waypoints.add(new Vector3f(-1, 0.1f, 5));
        waypoints.add(new Vector3f(1, 0.1f, 5));
        
        // Giro hacia abajo
        waypoints.add(new Vector3f(1, 0.1f, 4));
        waypoints.add(new Vector3f(1, 0.1f, 2));
        waypoints.add(new Vector3f(1, 0.1f, 0));
        
        // Giro hacia derecha
        waypoints.add(new Vector3f(2, 0.1f, 0));
        waypoints.add(new Vector3f(4, 0.1f, 0));
        
        // Giro hacia arriba
        waypoints.add(new Vector3f(4, 0.1f, 1));
        waypoints.add(new Vector3f(4, 0.1f, 3));
        
        // Giro hacia derecha - final
        waypoints.add(new Vector3f(5, 0.1f, 3));
        waypoints.add(new Vector3f(7, 0.1f, 3));
        waypoints.add(new Vector3f(9, 0.1f, 3));
    }
    
    private void createPathCoordinates() {
        // Esta matriz representa las coordenadas del camino en el sistema de cuadrícula
        // Convertimos los waypoints 3D a coordenadas de cuadrícula
        
        List<int[]> coords = new ArrayList<>();
        
        // Para cada par de waypoints consecutivos, generamos los puntos intermedios en la cuadrícula
        for (int i = 0; i < waypoints.size() - 1; i++) {
            Vector3f start = waypoints.get(i);
            Vector3f end = waypoints.get(i + 1);
            
            // Convertir a coordenadas de cuadrícula
            int startX = Math.round(start.x);
            int startZ = Math.round(start.z);
            int endX = Math.round(end.x);
            int endZ = Math.round(end.z);
            
            // Si los puntos están en la misma línea
            if (startX == endX) {
                // Línea vertical
                int minZ = Math.min(startZ, endZ);
                int maxZ = Math.max(startZ, endZ);
                for (int z = minZ; z <= maxZ; z++) {
                    coords.add(new int[]{startX, z});
                }
            } else if (startZ == endZ) {
                // Línea horizontal
                int minX = Math.min(startX, endX);
                int maxX = Math.max(startX, endX);
                for (int x = minX; x <= maxX; x++) {
                    coords.add(new int[]{x, startZ});
                }
            }
        }
        
        // Eliminar duplicados
        List<int[]> uniqueCoords = new ArrayList<>();
        for (int[] coord : coords) {
            boolean exists = false;
            for (int[] unique : uniqueCoords) {
                if (unique[0] == coord[0] && unique[1] == coord[1]) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                uniqueCoords.add(coord);
            }
        }
        
        // Convertir a array
        pathCoordinates = uniqueCoords.toArray(new int[0][]);
    }
    
    public List<Vector3f> getWaypoints() {
        return waypoints;
    }
    
    /**
     * Devuelve las coordenadas del camino en el sistema de cuadrícula del mapa
     * @return Matriz de pares [x, y] que representan las casillas ocupadas por el camino
     */
    public int[][] getPathCoordinates() {
        return pathCoordinates;
    }
    
    /**
     * Comprueba si una coordenada del mundo está en el camino
     * @param worldX Coordenada X del mundo
     * @param worldZ Coordenada Z del mundo
     * @return true si la coordenada está en el camino, false en caso contrario
     */
    public boolean isOnPath(int worldX, int worldZ) {
        for (int[] coord : pathCoordinates) {
            if (coord[0] == worldX && coord[1] == worldZ) {
                return true;
            }
        }
        return false;
    }
}