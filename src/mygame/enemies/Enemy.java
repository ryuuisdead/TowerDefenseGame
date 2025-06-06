package mygame.enemies;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import java.util.List;

public class Enemy extends Node {
    
    // Constantes
    public static final float SIZE = 0.4f; // Tamaño del enemigo (si es un cubo)
    
    // Variables de estado
    private int health;
    private int maxHealth;
    private boolean alive = true;
    private int currentWaypoint = 0;
    private List<Vector3f> waypoints;
    private float speed;
    private EnemyType type;
    private int reward; // Recompensa al matar
    
    // Modelo 3D
    private Spatial enemyModel;
    private Geometry enemyGeom; // Para cuando usamos un cubo
    private boolean useModel = true; // Por defecto intentamos usar un modelo 3D
    
    // Barra de salud
    private Node healthBarNode;
    private Geometry healthBarBg;
    private Geometry healthBarFg;
    
    // Añade esta variable miembro para almacenar el AssetManager
    private AssetManager assetManager;
    
    /**
     * Constructor para crear un enemigo con un tipo específico
     */
    public Enemy(AssetManager assetManager, List<Vector3f> path, EnemyType enemyType) {
        this.assetManager = assetManager;  // Guardar referencia
        this.waypoints = path;
        this.type = enemyType;
        this.health = enemyType.getHealth();
        this.maxHealth = enemyType.getHealth();
        this.speed = enemyType.getSpeed();
        this.reward = enemyType.getReward();
        
        // Intentar cargar el modelo 3D
        try {
            String modelPath = enemyType.getModelPath();
            System.out.println("Intentando cargar modelo desde: " + modelPath);
            
            enemyModel = assetManager.loadModel(modelPath);
            
            if (enemyModel != null) {
                System.out.println("¡Modelo cargado con éxito!");
                // Ajustar la escala según el tipo
                if (type == EnemyType.HELLHOUND) {
                    // Ajustar la escala para el perro
                    enemyModel.setLocalScale(0.4f);
                    // Ajustar la posición vertical para asegurarse de que está sobre el suelo
                    enemyModel.setLocalTranslation(0, 0.2f, 0);
                    
                    // Aplicar textura al perro infernal
                    try {
                        // Crear nuevo material para el perro
                        Material dogMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                        
                        // Cargar la textura específica del perro
                        Texture dogTexture = assetManager.loadTexture("Textures/texture_dog/texture_dog2.jpg");
                        
                        // Configurar parámetros de la textura para mejor calidad
                        dogTexture.setAnisotropicFilter(8);
                        dogTexture.setMagFilter(Texture.MagFilter.Bilinear);
                        
                        // Aplicar la textura al material
                        dogMaterial.setTexture("ColorMap", dogTexture);
                        
                        // Aplicar un tinte rojo oscuro para hacerlo parecer más infernal
                        dogMaterial.setColor("Color", new ColorRGBA(0.9f, 0.2f, 0.2f, 1.0f));
                        
                        // Aplicar el material al modelo del perro
                        applyMaterialToSpatial(enemyModel, dogMaterial);
                        
                        System.out.println("Textura de perro infernal aplicada correctamente");
                    } catch (Exception e) {
                        System.out.println("Error al aplicar textura al perro infernal: " + e.getMessage());
                        e.printStackTrace();
                    }
                    
                    System.out.println("Aplicada escala y posición al perro infernal");
                } else if (type == EnemyType.BASIC) {
                    // Ajustar la escala del zombie
                    enemyModel.setLocalScale(0.006f);
                    enemyModel.setLocalTranslation(0, 0.0f, 0);
                    
                    // Aplicar textura de lava al zombie
                    try {
                        // Crear un nuevo material para el zombie
                        Material zombieMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                        
                        // Cargar la textura
                        Texture zombieTexture = assetManager.loadTexture("Textures/zombie_textures/lava.jpg");
                        
                        // Configurar parámetros de la textura para mejor calidad
                        zombieTexture.setAnisotropicFilter(8);
                        zombieTexture.setMagFilter(Texture.MagFilter.Bilinear);
                        
                        // Aplicar la textura al material
                        zombieMaterial.setTexture("ColorMap", zombieTexture);
                        
                        // Aplicar el material al modelo del zombie
                        applyMaterialToSpatial(enemyModel, zombieMaterial);
                        
                        System.out.println("Textura de lava aplicada al zombie");
                    } catch (Exception e) {
                        System.out.println("Error al aplicar textura al zombie: " + e.getMessage());
                    }
                    
                    System.out.println("Aplicada escala y posición al zombie");
                } else if (type == EnemyType.TANK) {
                    // Ajustar la escala del tanque (monstruo del futuro)
                    enemyModel.setLocalScale(0.0006f);
                    enemyModel.setLocalTranslation(0, 0f, 0);
                
                    // Aplicar textura al tanque
                    try {
                        // Crear un nuevo material para el tanque
                        Material tankMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                        
                        // Cargar la textura específica del tanque
                        Texture tankTexture = assetManager.loadTexture("Textures/textura_tanque/textura_tanque.jpg");
                        
                        // Configurar parámetros de la textura para mejor calidad
                        tankTexture.setAnisotropicFilter(8);
                        tankTexture.setMagFilter(Texture.MagFilter.Bilinear);
                        
                        // Aplicar la textura al material
                        tankMaterial.setTexture("ColorMap", tankTexture);
                        
                        // Aplicar un color ligeramente oscuro para añadir profundidad
                        tankMaterial.setColor("Color", new ColorRGBA(0.7f, 0.7f, 0.7f, 1.0f));
                        
                        // Aplicar el material al modelo del tanque
                        applyMaterialToSpatial(enemyModel, tankMaterial);
                        
                        System.out.println("Textura del tanque aplicada correctamente");
                    } catch (Exception e) {
                        System.out.println("Error al aplicar textura al tanque: " + e.getMessage());
                        e.printStackTrace();
                    }
                    
                    System.out.println("Aplicada escala y posición al tanque");
                }
                
                // Verificar si el modelo tiene geometrías
                if (enemyModel instanceof Node) {
                    Node modelNode = (Node) enemyModel;
                    System.out.println("El modelo tiene " + modelNode.getQuantity() + " hijos");
                    // Si no tiene hijos, podría ser un nodo vacío
                    if (modelNode.getQuantity() == 0) {
                        System.out.println("ADVERTENCIA: El modelo parece estar vacío");
                        createCubeModel(assetManager, type.getColor());
                        useModel = false;
                    }
                }
                
                this.attachChild(enemyModel);
                useModel = true;
            }
        } catch (Exception e) {
            System.out.println("Error al cargar modelo: " + e.getMessage());
            e.printStackTrace();
            // Fallback a un cubo básico si no se puede cargar el modelo
            createCubeModel(assetManager, enemyType.getColor());
            useModel = false;
        }
        
        // Posicionar en el inicio del camino
        if (waypoints != null && !waypoints.isEmpty()) {
            Vector3f startPos = waypoints.get(0);
            this.setLocalTranslation(startPos);
        }
        
        // Crear barra de salud
        createHealthBar(assetManager);
    }
    
    /**
     * Método auxiliar para crear un modelo de cubo si no se puede cargar el modelo 3D
     */
    private void createCubeModel(AssetManager assetManager, ColorRGBA color) {
        Box box = new Box(SIZE, SIZE, SIZE);
        enemyGeom = new Geometry("EnemyBox", box);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        enemyGeom.setMaterial(mat);
        this.attachChild(enemyGeom);
    }
    
    /**
     * Crea la barra de salud para el enemigo
     */
    private void createHealthBar(AssetManager assetManager) {
        healthBarNode = new Node("HealthBar");
        
        // Tamaño para las barras de salud
        float barWidth = 0.5f;
        float barHeight = 0.05f;
        float barDepth = 0.01f;
        
        // Fondo de la barra (rojo)
        Box bgBox = new Box(barWidth, barHeight, barDepth);
        healthBarBg = new Geometry("HealthBarBg", bgBox);
        Material bgMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        bgMat.setColor("Color", ColorRGBA.Red);
        healthBarBg.setMaterial(bgMat);
        
        // Barra de salud actual (verde)
        Box fgBox = new Box(barWidth, barHeight, barDepth + 0.001f);
        healthBarFg = new Geometry("HealthBarFg", fgBox);
        Material fgMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        fgMat.setColor("Color", ColorRGBA.Green);
        healthBarFg.setMaterial(fgMat);
        
        // Ajustar la posición para que la barra siempre esté por encima del enemigo
        float healthBarHeight;
        if (type == EnemyType.HELLHOUND) {
            healthBarHeight = 2.0f; // Ajustar para hellhound
        } else if (type == EnemyType.TANK) {
            healthBarHeight = 3.0f; // Más alto para el tanque
        } else {
            healthBarHeight = 2.5f; // Altura para zombie
        }
        
        // Añadir barras al nodo
        healthBarNode.attachChild(healthBarBg);
        healthBarNode.attachChild(healthBarFg);
        
        // Posicionar por encima del enemigo
        healthBarNode.setLocalTranslation(0, healthBarHeight, 0);
        
        // Hacer que la barra siempre mire hacia la cámara
        healthBarNode.setLocalRotation(new Quaternion().fromAngles(FastMath.HALF_PI, 0, 0));
        
        this.attachChild(healthBarNode);
    }
    
    /**
     * Actualiza la posición del enemigo, moviéndolo a través del camino
     */
    public void update(float tpf) {
        if (!alive || waypoints == null || currentWaypoint >= waypoints.size()) {
            return;
        }
        
        Vector3f targetPos = waypoints.get(currentWaypoint);
        Vector3f currentPos = getLocalTranslation();
        Vector3f direction = targetPos.subtract(currentPos).normalizeLocal();
        Vector3f movement = direction.mult(speed * tpf);
        
        // Rotar el modelo para que mire hacia donde se mueve
        if (direction.length() > 0.1f) {
            float angle = FastMath.atan2(direction.x, direction.z);
            Quaternion rotation = new Quaternion();
            rotation.fromAngleAxis(angle, Vector3f.UNIT_Y);
            
            if (useModel && enemyModel != null) {
                // Para el modelo 3D
                enemyModel.setLocalRotation(rotation);
            } else if (enemyGeom != null) {
                // Para el cubo
                enemyGeom.setLocalRotation(rotation);
            }
        }
        
        // Mover el enemigo
        move(movement);
        
        // Comprobar si llegó al waypoint
        float distance = currentPos.distance(targetPos);
        if (distance < 0.1f) {
            currentWaypoint++;
        }
        
        // Actualizar la orientación de la barra de salud para que mire hacia la cámara
        updateHealthBarOrientation();
    }
    
    /**
     * Mantiene la barra de salud siempre visible (haciendo que mire a la cámara)
     */
    private void updateHealthBarOrientation() {
        if (healthBarNode != null) {
            // Se asume una orientación fija para la barra de salud que funciona en la vista isométrica
            Quaternion rotation = new Quaternion().fromAngles(FastMath.HALF_PI, 0, 0);
            healthBarNode.setLocalRotation(rotation);
        }
    }
    
    /**
     * Procesa el daño recibido y actualiza la barra de salud
     */
    public void takeDamage(int damage) {
        this.health -= damage;
        
        if (health <= 0) {
            alive = false;
            
            // Efecto visual de muerte
            if (useModel && enemyModel != null) {
                // Para el modelo 3D podemos aplicar un material oscuro o cambiar su opacidad
                // Usar el assetManager guardado en el objeto en lugar de obtenerlo del Light
                Material deathMaterial = new Material(assetManager, 
                                                     "Common/MatDefs/Misc/Unshaded.j3md");
                deathMaterial.setColor("Color", new ColorRGBA(0.3f, 0.3f, 0.3f, 1f)); // Gris oscuro
                applyMaterialToSpatial(enemyModel, deathMaterial);
            } else if (enemyGeom != null) {
                // Para el cubo simplemente cambiamos su color
                Material mat = enemyGeom.getMaterial();
                mat.setColor("Color", ColorRGBA.DarkGray);
            }
            
            // Ocultar barra de salud
            healthBarNode.removeFromParent();
        } else {
            // Actualizar barra de salud
            updateHealthBar();
        }
    }
    
    /**
     * Actualiza la apariencia de la barra de salud según el nivel actual
     */
    private void updateHealthBar() {
        // Calcular porcentaje de salud
        float healthPercent = health / (float)maxHealth;
        
        // Redimensionar la barra de salud
        Vector3f scale = healthBarFg.getLocalScale();
        scale.x = healthPercent;
        healthBarFg.setLocalScale(scale);
        
        // Ajustar posición para que se reduzca de manera correcta
        float barWidth = 0.5f; // El mismo que usamos en createHealthBar
        float offset = barWidth * (1 - healthPercent);
        healthBarFg.setLocalTranslation(-offset, 0, 0);
        
        // Cambiar color según el nivel de salud
        Material fgMat = healthBarFg.getMaterial();
        if (healthPercent > 0.6f) {
            // Verde para buena salud
            fgMat.setColor("Color", new ColorRGBA(0.0f, 1.0f, 0.0f, 1.0f));
        } else if (healthPercent > 0.3f) {
            // Amarillo para salud media
            fgMat.setColor("Color", new ColorRGBA(1.0f, 1.0f, 0.0f, 1.0f));
        } else {
            // Naranja para salud baja
            fgMat.setColor("Color", new ColorRGBA(1.0f, 0.5f, 0.0f, 1.0f));
        }
    }
    
    /**
     * Aplica un material a todas las geometrías en un spatial
     */
    private void applyMaterialToSpatial(Spatial spatial, Material material) {
        if (spatial instanceof Geometry) {
            ((Geometry) spatial).setMaterial(material);
        } else if (spatial instanceof Node) {
            Node node = (Node) spatial;
            for (Spatial child : node.getChildren()) {
                applyMaterialToSpatial(child, material);
            }
        }
    }
    
    // Añade este método auxiliar en la clase Enemy:
    private void applyMaterialRecursively(Node node, Material material) {
        for (Spatial child : node.getChildren()) {
            if (child instanceof Geometry) {
                ((Geometry)child).setMaterial(material);
            } else if (child instanceof Node) {
                applyMaterialRecursively((Node)child, material);
            }
        }
    }

    public void upgradeStats(float param){
        this.maxHealth += (int)(this.maxHealth * param);
        this.speed += this.speed * param;
    }
    
    // Getters
    public boolean isAlive() { return alive; }
    public int getHealth() { return health; }
    public Vector3f getPosition() { return getLocalTranslation(); }
    public boolean hasFinishedPath() {
        // Asegúrate de que esta función existe y devuelve true cuando el enemigo ha llegado al final
        return currentWaypoint >= waypoints.size();
    }
    public EnemyType getType() { return type; }
    public int getReward() { return reward; }
}
