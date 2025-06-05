package mygame.map;

import java.util.List;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Quaternion;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
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
        
        // Crear camino visible primero
        createPath(assetManager);
        
        // Crear indicadores de torres usando el mismo estilo que el camino
        createTowerSpotIndicators(assetManager);
        
        // Crear cielo con textura
        createSky(assetManager);
        
        // Añadir un muro en la orilla del mapa
        addSingleWall(assetManager);
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
    
    /**
     * Añade muros de roca en el mapa como marcadores visuales
     */
    private void addSingleWall(AssetManager assetManager) {
        // Posicionar el primer muro (el original)
        addRockWall(assetManager, 5, 2, 5, 0);
        
        // Posicionar el segundo muro junto al primero, rotado 90 grados
        addRockWall(assetManager, -5, 3, 0, 270);
    }
    
    /**
     * Añade un muro de roca en una posición específica con rotación
     * @param assetManager El administrador de recursos
     * @param x Posición X del muro
     * @param y Posición Y (altura) del muro
     * @param z Posición Z del muro
     * @param rotationDegrees Rotación en grados alrededor del eje Y
     */
    private void addRockWall(AssetManager assetManager, float x, float y, float z, float rotationDegrees) {
        try {
            // Cargar el modelo de muro de roca
            com.jme3.scene.Spatial rockWall = assetManager.loadModel("Models/RockWall/RockWall.j3o");
            System.out.println("Modelo de muro cargado correctamente en posición (" + x + ", " + y + ", " + z + ")");
            
            // Aplicar textura al muro
            applyRockTexture(rockWall, assetManager);
            
            // Escalar el muro si es necesario
            float wallScale = 1.5f; // Ajustar según el tamaño del modelo
            rockWall.setLocalScale(wallScale);
            
            // Posicionar el muro en la ubicación especificada
            rockWall.setLocalTranslation(x, y, z);
            
            // Aplicar rotación si es necesario
            if (rotationDegrees != 0) {
                // Convertir grados a radianes
                float rotationRadians = rotationDegrees * com.jme3.math.FastMath.DEG_TO_RAD;
                
                // Crear un cuaternión para la rotación alrededor del eje Y
                com.jme3.math.Quaternion rotation = new com.jme3.math.Quaternion();
                rotation.fromAngleAxis(rotationRadians, new Vector3f(0, 1, 0));
                
                // Aplicar la rotación al muro
                rockWall.setLocalRotation(rotation);
                System.out.println("Muro rotado " + rotationDegrees + " grados");
            }
            
            // Añadir el muro al grafo de escena
            this.attachChild(rockWall);
            
            System.out.println("Muro de roca añadido correctamente");
        } catch (Exception e) {
            System.err.println("Error al cargar el muro de roca: " + e.getMessage());
            e.printStackTrace();
            
            // Crear un muro simple como respaldo
            createSimpleWall(assetManager, x, y, z, rotationDegrees);
        }
    }
    
    /**
     * Aplica la textura de roca al muro
     * @param spatial El muro al que aplicar la textura
     * @param assetManager El administrador de assets
     */
    private void applyRockTexture(com.jme3.scene.Spatial spatial, AssetManager assetManager) {
        try {
            // Crear material con la textura específica
            Material rockMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            
            // Intentar varias ubicaciones posibles para la textura de roca
            Texture rockTexture = null;
            String[] possibleTexturePaths = {
                "Textures/rock_textures/rocavolcan.jpg",
                "Textures/rocavolcan.jpg",
                "rock_textures/rocavolcan.jpg",
                "Textures/rock_textures/RockWall.jpg",
                "Textures/RockWall.jpg",
                "Textures/lava.jpg"
            };
            
            // Intentar cargar la textura de una de las ubicaciones posibles
            Exception lastException = null;
            for (String path : possibleTexturePaths) {
                try {
                    System.out.println("Intentando cargar textura desde: " + path);
                    rockTexture = assetManager.loadTexture(path);
                    if (rockTexture != null) {
                        System.out.println("¡Textura cargada exitosamente desde: " + path + "!");
                        break;
                    }
                } catch (Exception e) {
                    lastException = e;
                    System.out.println("No se pudo cargar desde: " + path);
                }
            }
            
            // Si no se pudo cargar ninguna textura, usar color sólido oscuro directamente
            if (rockTexture == null) {
                // Color oscuro para simular roca volcánica sin luz
                rockMaterial.setColor("Color", new ColorRGBA(0.2f, 0.1f, 0.08f, 1.0f));
                System.out.println("Aplicando color oscuro sólido al muro");
            } else {
                // Si se cargó la textura, aplicarla pero con tinte muy oscuro
                rockTexture.setWrap(WrapMode.Repeat);
                rockMaterial.setTexture("ColorMap", rockTexture);
                
                // Aplicar tinte muy oscuro a la textura (valores bajos hacen la textura más oscura)
                rockMaterial.setColor("Color", new ColorRGBA(0.3f, 0.2f, 0.2f, 1.0f));
                
                System.out.println("Textura de roca con tinte oscuro aplicada correctamente al muro");
            }
            
            // Aplicar el material a la geometría del muro
            if (spatial instanceof Geometry) {
                ((Geometry) spatial).setMaterial(rockMaterial);
            } else if (spatial instanceof Node) {
                // Si es un nodo, aplicar a todos sus hijos recursivamente
                applyMaterialToNode((Node) spatial, rockMaterial);
            }
        } catch (Exception e) {
            System.err.println("Error al aplicar textura de roca: " + e.getMessage());
            e.printStackTrace();
            
            // Si hay un error, aplicar color oscuro como respaldo en lugar de naranja
            applyDarkFallbackMaterial(spatial, assetManager);
        }
    }
    
    /**
     * Aplica un material recursivamente a todos los elementos de un nodo
     * @param node El nodo al que aplicar el material
     * @param material El material a aplicar
     */
    private void applyMaterialToNode(Node node, Material material) {
        // Recorrer todos los hijos del nodo
        for (com.jme3.scene.Spatial child : node.getChildren()) {
            if (child instanceof Geometry) {
                // Aplicar material directamente a geometrías
                ((Geometry) child).setMaterial(material);
            } else if (child instanceof Node) {
                // Recursión para sub-nodos
                applyMaterialToNode((Node) child, material);
            }
        }
    }
    
    /**
     * Aplica un material de color oscuro como respaldo si la textura falla
     * @param spatial El muro al que aplicar el color
     * @param assetManager El administrador de assets
     */
    private void applyDarkFallbackMaterial(com.jme3.scene.Spatial spatial, AssetManager assetManager) {
        Material darkMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        darkMaterial.setColor("Color", new ColorRGBA(0.2f, 0.1f, 0.08f, 1.0f)); // Color marrón muy oscuro
        
        System.out.println("Aplicando color oscuro de respaldo al muro");
        
        // Aplicar el material oscuro
        if (spatial instanceof Geometry) {
            ((Geometry) spatial).setMaterial(darkMaterial);
        } else if (spatial instanceof Node) {
            applyMaterialToNode((Node) spatial, darkMaterial);
        }
    }
    
    /**
     * Crea un muro simple como respaldo si no se puede cargar el modelo
     */
    private void createSimpleWall(AssetManager assetManager, float x, float y, float z, float rotationDegrees) {
        // Material para el muro con color naranja
        Material wallMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        wallMat.setColor("Color", new ColorRGBA(1.0f, 0.5f, 0.0f, 1.0f)); // Color naranja brillante
        
        // Crear una geometría de caja simple
        Box wallBox = new Box(3.0f, 1.0f, 0.5f);
        Geometry wall = new Geometry("SimpleWall", wallBox);
        wall.setMaterial(wallMat);
        
        // Posicionar el muro en la ubicación especificada
        wall.setLocalTranslation(x, y, z);
        
        // Aplicar rotación si es necesario
        if (rotationDegrees != 0) {
            float rotationRadians = rotationDegrees * com.jme3.math.FastMath.DEG_TO_RAD;
            com.jme3.math.Quaternion rotation = new com.jme3.math.Quaternion();
            rotation.fromAngleAxis(rotationRadians, new Vector3f(0, 1, 0));
            wall.setLocalRotation(rotation);
        }
        
        this.attachChild(wall);
        System.out.println("Muro simple naranja añadido como respaldo en posición (" + x + ", " + y + ", " + z + ")");
    }   
    
    private void createTowerSpotIndicators(AssetManager assetManager) {
        Node spotsNode = new Node("TowerSpots");
        
        // Crear una instancia de Path para obtener los spots válidos
        Path pathCalculator = new Path();
        List<int[]> validSpots = pathCalculator.getValidTowerSpots();
        
        // Crear material para los spots
        Material spotMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        
        // Cargar y configurar la textura
        Texture pathTexture = assetManager.loadTexture("Textures/dirt.jpg");
        pathTexture.setWrap(WrapMode.Repeat);
        spotMat.setTexture("ColorMap", pathTexture);
        
        // Crear los indicadores de spots
        for (int[] spot : validSpots) {
            Box tile = new Box(TILE_SIZE/2, 0.05f, TILE_SIZE/2);
            Geometry spotGeom = new Geometry("TowerSpotTile", tile);
            spotGeom.setMaterial(spotMat);
            
            // Posicionar el spot
            spotGeom.setLocalTranslation(
                spot[0],
                PATH_Y,
                spot[1]
            );
            
            spotsNode.attachChild(spotGeom);
        }
        
        this.attachChild(spotsNode);
    }
}
