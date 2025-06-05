package mygame.map;

import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;

public class Path {
    
    private List<Vector3f> waypoints = new ArrayList<>();
    private int[][] pathCoordinates; // Representación de la cuadrícula del camino
    private List<int[]> validTowerSpots; // Lista de coordenadas válidas para torres
    
    public Path() {
        // Inicializar waypoints (puntos del camino)
        createPath();
        
        // Crear la representación en coordenadas de cuadrícula del camino
        createPathCoordinates();
        
        // Definir posiciones válidas para torres
        createValidTowerSpots();
    }
    
    private void createValidTowerSpots() {
        validTowerSpots = new ArrayList<>();
        
        // Definir spots válidos para torres (coordenadas X,Z)
        // Spots cerca del primer giro
        validTowerSpots.add(new int[]{-3, -1});
        validTowerSpots.add(new int[]{-3, 3});

        
        // Spots cerca del segundo giro
        validTowerSpots.add(new int[]{2, 5});
        validTowerSpots.add(new int[]{1, 6});
        validTowerSpots.add(new int[]{-5, 6});
        
        // Spots cerca del tercer giro
        validTowerSpots.add(new int[]{0, -1});
        validTowerSpots.add(new int[]{5, -1});
        validTowerSpots.add(new int[]{5, 4});
        validTowerSpots.add(new int[]{5, 2});
        
        // Spots adicionales estratégicos
        validTowerSpots.add(new int[]{-2, 1});
        validTowerSpots.add(new int[]{-1, 1});

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

    /**
     * Comprueba si una coordenada está a la derecha del camino
     * @param worldX Coordenada X del mundo
     * @param worldZ Coordenada Z del mundo
     * @return true si la coordenada está a la derecha del camino más cercano
     */
    public boolean isRightOfPath(int worldX, int worldZ) {
        // Encontrar el punto del camino más cercano a la coordenada dada
        int[] nearestPathPoint = null;
        double minDistance = Double.MAX_VALUE;
        
        for (int[] coord : pathCoordinates) {
            double distance = Math.sqrt(
                Math.pow(coord[0] - worldX, 2) + 
                Math.pow(coord[1] - worldZ, 2)
            );
            if (distance < minDistance) {
                minDistance = distance;
                nearestPathPoint = coord;
            }
        }
        
        if (nearestPathPoint == null) {
            return false;
        }

        // Encuentra el siguiente punto en el camino para determinar la dirección
        int[] nextPoint = null;
        for (int i = 0; i < pathCoordinates.length - 1; i++) {
            if (pathCoordinates[i][0] == nearestPathPoint[0] && 
                pathCoordinates[i][1] == nearestPathPoint[1]) {
                nextPoint = pathCoordinates[i + 1];
                break;
            }
        }

        if (nextPoint == null) {
            return false;
        }

        // Calcular el vector de dirección del camino
        int pathDirX = nextPoint[0] - nearestPathPoint[0];
        int pathDirZ = nextPoint[1] - nearestPathPoint[1];

        // Calcular el vector desde el punto del camino hasta la posición de la torre
        int towerDirX = worldX - nearestPathPoint[0];
        int towerDirZ = worldZ - nearestPathPoint[1];

        // Producto cruz 2D (si es positivo, el punto está a la derecha)
        return (pathDirX * towerDirZ - pathDirZ * towerDirX) < 0;
    }
    
    /**
     * Verifica si una coordenada es un spot válido para colocar torres
     * @param worldX Coordenada X del mundo
     * @param worldZ Coordenada Z del mundo
     * @return true si es un spot válido para torres
     */
    public boolean isValidTowerSpot(int worldX, int worldZ) {
        for (int[] spot : validTowerSpots) {
            if (spot[0] == worldX && spot[1] == worldZ) {
                return true;
            }
        }
        return false;
    }

    /**
     * Obtiene la lista de spots válidos para torres
     * @return Lista de coordenadas [x,z] de spots válidos
     */
    public List<int[]> getValidTowerSpots() {
        return validTowerSpots;
    }
}