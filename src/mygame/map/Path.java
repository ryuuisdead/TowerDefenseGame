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
        // Punto inicial
        waypoints.add(new Vector3f(-5, 0.1f, 0));
        
        // Ruta en zigzag
        waypoints.add(new Vector3f(-3, 0.1f, 0));
        waypoints.add(new Vector3f(-3, 0.1f, 2));
        waypoints.add(new Vector3f(-1, 0.1f, 2));
        waypoints.add(new Vector3f(-1, 0.1f, -2));
        waypoints.add(new Vector3f(1, 0.1f, -2));
        waypoints.add(new Vector3f(1, 0.1f, 2));
        waypoints.add(new Vector3f(3, 0.1f, 2));
        waypoints.add(new Vector3f(3, 0.1f, 0));
        
        // Punto final
        waypoints.add(new Vector3f(5, 0.1f, 0));
    }
    
    private void createPathCoordinates() {
        // Esta matriz representa las coordenadas del camino en el sistema de cuadrícula
        // donde cada casilla del mapa corresponde a un par [x, y]
        // Ajustamos las coordenadas al tamaño del mapa (10x10 con centro en 0,0)
        // por lo que añadimos GameMap.MAP_SIZE/2 a cada coordenada
        
        pathCoordinates = new int[][] {
            {0, 5}, // Punto inicial (-5, 0)
            {1, 5}, 
            {2, 5}, 
            {2, 6}, 
            {2, 7}, 
            {3, 7}, 
            {4, 7}, 
            {4, 6}, 
            {4, 5}, 
            {4, 4}, 
            {4, 3}, 
            {5, 3}, 
            {6, 3}, 
            {6, 4}, 
            {6, 5}, 
            {6, 6}, 
            {6, 7}, 
            {7, 7}, 
            {8, 7}, 
            {8, 6}, 
            {8, 5}, 
            {9, 5}, // Punto final (5, 0)
            {10, 5}
        };
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
}