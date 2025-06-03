package mygame.map;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

/**
 * Crea un terreno plano como campo de juego.
 */
public class GameMap extends Node {
    
    public static final int MAP_SIZE = 20; // 10x10 terreno
    public static final float TILE_SIZE = 1.0f;
    public static final float PATH_Y = 0.1f; // Altura del camino

    public GameMap(AssetManager assetManager) {
        // Crear terreno base (plano grande rojo)
        Box ground = new Box(MAP_SIZE/2, 0.1f, MAP_SIZE/2);
        Geometry groundGeom = new Geometry("Ground", ground);
        Material groundMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        groundMat.setColor("Color", new ColorRGBA(0.5f, 0.1f, 0.1f, 1f)); // Rojo oscuro para el infierno
        groundGeom.setMaterial(groundMat);
        this.attachChild(groundGeom);
        
        // Crear camino (cubos naranjas)
        createPath(assetManager);
    }
    
    private void createPath(AssetManager assetManager) {
        // El camino será en forma de zigzag
        int[][] pathCoords = {
            {0, 1}, {1, 1}, {2, 1}, {3, 1}, {3, 2}, {3, 3}, {3, 4}, 
            {2, 4}, {1, 4}, {1, 5}, {1, 6}, {1, 7}, {2, 7}, {3, 7}, 
            {4, 7}, {5, 7}, {6, 7}, {7, 7}, {8, 7}, {9, 7}
        };
        
        Node pathNode = new Node("Path");
        
        for (int[] coord : pathCoords) {
            Box tile = new Box(TILE_SIZE/2, 0.1f, TILE_SIZE/2);
            Geometry tileGeom = new Geometry("PathTile", tile);
            Material tileMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            tileMat.setColor("Color", ColorRGBA.Orange);
            tileGeom.setMaterial(tileMat);
            
            // Posicionamos un poco más arriba que el suelo base
            tileGeom.setLocalTranslation(
                (coord[0] - MAP_SIZE/2 + 0.5f) * TILE_SIZE, 
                PATH_Y, 
                (coord[1] - MAP_SIZE/2 + 0.5f) * TILE_SIZE
            );
            
            pathNode.attachChild(tileGeom);
        }
        
        this.attachChild(pathNode);
    }
    
    // Método para obtener la ruta de coordenadas
    public int[][] getPathCoordinates() {
        return new int[][] {
            {0, 1}, {1, 1}, {2, 1}, {3, 1}, {3, 2}, {3, 3}, {3, 4}, 
            {2, 4}, {1, 4}, {1, 5}, {1, 6}, {1, 7}, {2, 7}, {3, 7}, 
            {4, 7}, {5, 7}, {6, 7}, {7, 7}, {8, 7}, {9, 7}
        };
    }
}
