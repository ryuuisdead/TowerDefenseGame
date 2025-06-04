package mygame.map;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.util.SkyFactory;

/**
 * Crea un terreno plano como campo de juego.
 */
public class GameMap extends Node {
    
    public static final int MAP_SIZE = 30; // 20x20 terreno
    public static final float TILE_SIZE = 1.0f;
    public static final float PATH_Y = 0.1f; // Altura del camino

    public GameMap(AssetManager assetManager) {
        // Crear terreno base con textura
        Box ground = new Box(MAP_SIZE/2, 0.1f, MAP_SIZE/2);
        
        // Configurar coordenadas UV para la textura
        // Valores menores hacen la textura más grande (menos repeticiones)
        float textureRepeat = 1f; // Prueba con 1 para ver si cubre todo el mapa
        ground.scaleTextureCoordinates(new Vector2f(textureRepeat, textureRepeat));
        
        Geometry groundGeom = new Geometry("Ground", ground);
        
        // Usar material con textura
        Material groundMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        
        // Cargar la textura
        Texture groundTexture = assetManager.loadTexture("Textures/ground.jpg");
        groundTexture.setWrap(Texture.WrapMode.Repeat); // Asegurar que la textura se repite
        groundMat.setTexture("ColorMap", groundTexture);
        
        groundGeom.setMaterial(groundMat);
        this.attachChild(groundGeom);
        
        // Crear camino visible
        createPath(assetManager);
        
        // Crear cielo con textura
        createSky(assetManager);
    }
    
    // Método para crear el cielo
    private void createSky(AssetManager assetManager) {
        // Cargar la textura del cielo
        Texture skyTexture = assetManager.loadTexture("Textures/redsky.jpg");
        
        // Mejorar la calidad de la textura
        skyTexture.setAnisotropicFilter(16); // Mejora la nitidez en ángulos oblicuos
        skyTexture.setMagFilter(Texture.MagFilter.Bilinear); // Suaviza la textura cuando se amplía
        
        // Crear el skybox usando la textura en todas las caras
        // Usamos EquirectMap en lugar de SphereMap para mejor mapeo de una sola imagen rectangular
        com.jme3.scene.Spatial sky = SkyFactory.createSky(
                assetManager,
                skyTexture,
                Vector3f.UNIT_XYZ,
                SkyFactory.EnvMapType.EquirectMap); // EquirectMap funciona mejor para imágenes panorámicas rectangulares
        
        // Ajustar el tamaño del cielo para asegurar que cubra toda la escena
        sky.setLocalScale(500f);
        
        this.attachChild(sky);
    }
    
    private void createPath(AssetManager assetManager) {
        Node pathNode = new Node("Path");
        
        // Crear una instancia de Path para obtener las coordenadas
        Path pathCalculator = new Path();
        int[][] pathCoords = pathCalculator.getPathCoordinates();
        
        // Crear material para el camino
        Material pathMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        
        // Cargar textura para el camino
        Texture pathTexture = assetManager.loadTexture("Textures/redsky.jpg");
        pathTexture.setWrap(WrapMode.Repeat);
        pathMat.setTexture("ColorMap", pathTexture);
        
        // Alternativa: usar un color sólido
        // pathMat.setColor("Color", new ColorRGBA(0.6f, 0.4f, 0.2f, 1f)); // Color marrón para el camino
        
        for (int[] coord : pathCoords) {
            // Crear una "baldosa" para cada posición del camino
            Box tile = new Box(TILE_SIZE/2, 0.05f, TILE_SIZE/2);
            Geometry tileGeom = new Geometry("PathTile", tile);
            tileGeom.setMaterial(pathMat);
            
            // Posicionar la baldosa
            tileGeom.setLocalTranslation(
                coord[0], 
                PATH_Y, 
                coord[1]
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
