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
import java.util.List;

public class Enemy extends Node {
    
    private static final float SPEED = 1.5f;
    private static final float SIZE = 0.3f;
    
    private List<Vector3f> waypoints;
    private int currentWaypoint = 0;
    private int maxHealth = 200;
    private int health = 200;
    private boolean alive = true;
    private Geometry enemyGeom; // Mantener para compatibilidad con daño
    
    // Modelo 3D
    private Spatial zombieModel;
    private boolean useModel = true;
    
    // Geometrías para la barra de salud
    private Geometry healthBarBg;
    private Geometry healthBarFg;
    private Node healthBarNode;
    
    // Mantener referencia al AssetManager para crear materiales
    private AssetManager assetManager;
    
    public Enemy(AssetManager assetManager, List<Vector3f> waypoints) {
        this.assetManager = assetManager; // Guardar referencia
        this.waypoints = waypoints;
        
        if (useModel) {
            try {
                // Cargar el modelo de zombie
                zombieModel = assetManager.loadModel("Models/Zombie Agonizing/ZombieAgonizing.j3o");
                
                // Ajustar escala del modelo (experimenta con estos valores)
                float scale = 0.015f; // Ajusta según el tamaño deseado
                zombieModel.scale(scale);
                
                // Ajustar posición vertical si es necesario
                zombieModel.setLocalTranslation(0, -0.3f, 0); // Ajustar para que "camine" en el suelo
                
                // Rotar el modelo para que mire hacia adelante por defecto
                Quaternion rotation = new Quaternion();
                rotation.fromAngleAxis(FastMath.PI, Vector3f.UNIT_Y); // Rotar 180° si es necesario
                zombieModel.setLocalRotation(rotation);
                
                // Añadir el modelo al nodo del enemigo
                this.attachChild(zombieModel);
                
                // Aplicar texturas al modelo de zombie
                applyZombieTextures();
                
                // Crear una geometría invisible para mantener compatibilidad con el sistema de daño
                Box invisibleBox = new Box(SIZE, SIZE, SIZE);
                enemyGeom = new Geometry("InvisibleEnemy", invisibleBox);
                Material invisibleMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                invisibleMat.setColor("Color", new ColorRGBA(1f, 0f, 0f, 0f)); // Transparente
                enemyGeom.setMaterial(invisibleMat);
                enemyGeom.setCullHint(Spatial.CullHint.Always); // Hacer invisible
                this.attachChild(enemyGeom);
                
                System.out.println("Modelo de zombie cargado correctamente");
                
            } catch (Exception e) {
                System.out.println("Error cargando modelo de zombie: " + e.getMessage());
                e.printStackTrace();
                useModel = false;
                createBasicEnemy(assetManager);
            }
        } else {
            createBasicEnemy(assetManager);
        }
        
        // Crear barra de salud
        createHealthBar(assetManager);
        
        // Posicionar al enemigo en el primer waypoint
        if (!waypoints.isEmpty()) {
            this.setLocalTranslation(waypoints.get(0));
        }
    }
    
    /**
     * Aplica las texturas del zombie desde la carpeta zombie_textures
     */
    private void applyZombieTextures() {
        try {
            System.out.println("Aplicando texturas de zombie...");
            
            // Imprimir la estructura del modelo para ver cómo está organizado
            printModelHierarchy(zombieModel, "");
            
            // Crear material con texturas de zombie
            Material zombieMaterial = createZombieMaterial();
            
            // Aplicar el material a todas las geometrías del modelo
            applyMaterialToSpatial(zombieModel, zombieMaterial);
            
            System.out.println("Texturas de zombie aplicadas correctamente");
            
            // DIAGNÓSTICO: Verificar qué material se aplicó realmente
            checkAppliedMaterials(zombieModel, "");
            
        } catch (Exception e) {
            System.out.println("Error aplicando texturas de zombie: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Método temporal para diagnosticar qué materiales se aplicaron
     */
    private void checkAppliedMaterials(Spatial spatial, String indent) {
        if (spatial instanceof Geometry) {
            Geometry geom = (Geometry) spatial;
            Material mat = geom.getMaterial();
            System.out.println(indent + geom.getName() + " tiene material: " + mat.getMaterialDef().getName());
        } else if (spatial instanceof Node) {
            Node node = (Node) spatial;
            for (Spatial child : node.getChildren()) {
                checkAppliedMaterials(child, indent + "  ");
            }
        }
    }
    
    /**
     * Crea un material con lava.jpg (versión sin try-catch)
     */
    private Material createZombieMaterial() {
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        
        // Cargar la textura directamente sin try-catch
        com.jme3.texture.Texture lavaTexture = assetManager.loadTexture("Textures/zombie_textures/lava.jpg");
        lavaTexture.setWrap(com.jme3.texture.Texture.WrapMode.Repeat);
        mat.setTexture("ColorMap", lavaTexture);
        
        System.out.println("✓ Textura de lava aplicada al zombie");
        
        return mat;
    }
    
    /**
     * Imprime la jerarquía del modelo para depuración
     */
    private void printModelHierarchy(Spatial spatial, String indent) {
        System.out.println(indent + spatial.getName() + " (" + spatial.getClass().getSimpleName() + ")");
        if (spatial instanceof Node) {
            for (Spatial child : ((Node) spatial).getChildren()) {
                printModelHierarchy(child, indent + "  ");
            }
        }
    }
    
    private void createBasicEnemy(AssetManager assetManager) {
        // Crear cubo rojo como respaldo
        Box box = new Box(SIZE, SIZE, SIZE);
        enemyGeom = new Geometry("Enemy", box);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        enemyGeom.setMaterial(mat);
        this.attachChild(enemyGeom);
    }
    
    private void createHealthBar(AssetManager assetManager) {
        healthBarNode = new Node("HealthBar");
        
        // Fondo de la barra (rojo)
        Box bgBox = new Box(0.3f, 0.03f, 0.01f);
        healthBarBg = new Geometry("HealthBarBg", bgBox);
        Material bgMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        bgMat.setColor("Color", ColorRGBA.Red);
        healthBarBg.setMaterial(bgMat);
        
        // Barra de salud actual (verde)
        Box fgBox = new Box(0.3f, 0.03f, 0.02f);
        healthBarFg = new Geometry("HealthBarFg", fgBox);
        Material fgMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        fgMat.setColor("Color", ColorRGBA.Green);
        healthBarFg.setMaterial(fgMat);
        
        // Añadir barras de salud al nodo
        healthBarNode.attachChild(healthBarBg);
        healthBarNode.attachChild(healthBarFg);
        
        // Posicionar la barra de salud arriba del enemigo (ajustado para el modelo 3D)
        float healthBarHeight = useModel ? 1.0f : SIZE + 0.15f;
        healthBarNode.setLocalTranslation(0, healthBarHeight, 0);
        
        // Añadir barra de salud al enemigo
        this.attachChild(healthBarNode);
    }
    
    public void update(float tpf) {
        if (currentWaypoint < waypoints.size()) {
            // Movimiento hacia el siguiente waypoint
            Vector3f targetPos = waypoints.get(currentWaypoint);
            Vector3f currentPos = getLocalTranslation();
            Vector3f direction = targetPos.subtract(currentPos).normalize();
            Vector3f movement = direction.mult(SPEED * tpf);
            
            // Rotar el zombie para que mire hacia donde se mueve
            if (useModel && zombieModel != null && direction.length() > 0.1f) {
                // Calcular la rotación para que el zombie mire hacia donde va
                float angle = FastMath.atan2(direction.x, direction.z);
                Quaternion rotation = new Quaternion();
                rotation.fromAngleAxis(angle, Vector3f.UNIT_Y);
                zombieModel.setLocalRotation(rotation);
            }
            
            // Mover el enemigo
            this.move(movement);
            
            // Comprobar si llegó al waypoint
            float distance = currentPos.distance(targetPos);
            if (distance < 0.1f) {
                currentWaypoint++;
            }
        }
    }
    
    public void takeDamage(int amount) {
        health -= amount;
        if (health <= 0) {
            alive = false;
            
            // Aplicar efecto visual de muerte al modelo
            if (useModel && zombieModel != null) {
                // Hacer el zombie más oscuro cuando muere
                applyMaterialToSpatial(zombieModel, createDeathMaterial());
            } else {
                // Cambiar color del cubo a gris cuando muere
                Material mat = enemyGeom.getMaterial();
                mat.setColor("Color", ColorRGBA.DarkGray);
            }
            
            // Ocultar barra de salud cuando muere
            healthBarNode.removeFromParent();
        } else {
            // Efecto visual de daño
            if (useModel && zombieModel != null) {
                // Aplicar tinte rojizo al zombie cuando recibe daño
                float healthPercent = health / (float)maxHealth;
                applyMaterialToSpatial(zombieModel, createDamageMaterial(healthPercent));
            } else {
                // Cambiar color del cubo
                float healthPercent = health / (float)maxHealth;
                Material mat = enemyGeom.getMaterial();
                mat.setColor("Color", new ColorRGBA(1f, healthPercent * 0.5f, healthPercent * 0.5f, 1f));
            }
            
            // Actualizar tamaño de la barra de salud
            float healthPercent = health / (float)maxHealth;
            Vector3f scale = healthBarFg.getLocalScale();
            scale.x = healthPercent;
            healthBarFg.setLocalScale(scale);
            
            // Ajustar la posición de la barra según el tamaño
            healthBarFg.setLocalTranslation((1 - healthPercent) * 0.3f, 0, 0);
        }
    }
    
    /**
     * Aplica un material a todas las geometrías de un spatial
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
    
    /**
     * Crea un material para cuando el zombie recibe daño
     */
    private Material createDamageMaterial(float healthPercent) {
        // Usar la referencia del AssetManager guardada
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        
        // Intensidad del tinte rojo basada en el daño recibido
        float redIntensity = 1.0f - healthPercent;
        ColorRGBA damageColor = new ColorRGBA(1f, 1f - redIntensity * 0.5f, 1f - redIntensity * 0.5f, 1f);
        mat.setColor("Color", damageColor);
        
        return mat;
    }
    
    /**
     * Crea un material para cuando el zombie muere
     */
    private Material createDeathMaterial() {
        // Usar la referencia del AssetManager guardada
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", new ColorRGBA(0.3f, 0.3f, 0.3f, 1f)); // Gris oscuro
        return mat;
    }
    
    // Métodos sin cambios
    public boolean isAlive() {
        return alive;
    }
    
    public boolean hasFinishedPath() {
        return currentWaypoint >= waypoints.size();
    }
    
    public Vector3f getPosition() {
        return getWorldTranslation();
    }
    
    public int getHealth() {
        return health;
    }
    
    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
        this.health = maxHealth;
    }
}
