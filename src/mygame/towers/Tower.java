package mygame.towers;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
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
import com.jme3.scene.shape.Sphere;
import java.util.ArrayList;
import java.util.List;
import mygame.Main;
import mygame.enemies.Enemy;

public class Tower extends Node {
    
    private static final float RANGE = 8.0f; // Aumentado para mayor cobertura
    private static final float FIRE_RATE = 1.0f; // 1.25 disparos por segundo (ajustado)
    private static final int DAMAGE = 40; // Aumentado de 25 a 40
    
    private float fireTimer = 0;
    private AssetManager assetManager;
    private Node projectilesNode;
    private List<ProjectileInfo> activeProjectiles = new ArrayList<>();
    private float projectileLifetime = 0.8f; // Reducido para que los proyectiles viajen más rápido
    private Main app;
    
    // Modelo 3D
    private Spatial towerModel;
    private Node topNode; // Para poder rotar la parte superior de la torre
    private boolean useModel = true; // Controla si usar el modelo 3D o la torre básica
    
    // Clase interna para manejar información de proyectiles
    private class ProjectileInfo {
        Geometry geometry;
        Vector3f targetPosition;
        float lifetime;
        Enemy target; // Referencia al enemigo objetivo
        
        public ProjectileInfo(Geometry geom, Vector3f target, float life, Enemy enemy) {
            this.geometry = geom;
            this.targetPosition = target;
            this.lifetime = life;
            this.target = enemy;
        }
    }
    
    public Tower(AssetManager assetManager, Vector3f position) {
        this.assetManager = assetManager;
        
        if (useModel) {
            try {
                System.out.println("Intentando cargar modelo de torre...");
                
                // Intentar diferentes rutas posibles
                String[] possiblePaths = {
                    "Models/medivialtower/medivialtower.j3o",
                    "Models/medivialtower.j3o",
                    "Models/Towers/medivialtower.j3o",
                    "medivialtower.j3o"
                };
                
                boolean loaded = false;
                for (String path : possiblePaths) {
                    try {
                        System.out.println("Probando ruta: " + path);
                        towerModel = assetManager.loadModel(path);
                        loaded = true;
                        System.out.println("¡Modelo cargado desde: " + path);
                        break;
                    } catch (Exception e) {
                        System.out.println("No se encontró en: " + path);
                    }
                }
                
                if (!loaded) {
                    throw new Exception("No se pudo encontrar el modelo en ninguna ruta");
                }
                
                // Imprimir información sobre el modelo
                System.out.println("Modelo cargado. Tipo: " + towerModel.getClass().getName());
                
                // Descomentar esta línea para ver la estructura del modelo
                printModelHierarchy(towerModel, "");
                
                // Escalar modelo (prueba con diferentes valores)
                float scale = 0.3f; // Un valor mucho mayor para ver si es visible
                System.out.println("Aplicando escala: " + scale);
                towerModel.scale(scale);
                
                // Ajustar posición vertical (prueba con diferentes valores)
                float heightOffset = 0f; // Probar sin offset primero
                System.out.println("Aplicando offset vertical: " + heightOffset);
                towerModel.setLocalTranslation(0, heightOffset, 0);
                
                // Añadir el modelo al nodo principal
                this.attachChild(towerModel);
                
                // Aplicar texturas
                applyTextures();
                
                // Configurar nodos para rotación y proyectiles
                topNode = new Node("TowerTop");
                topNode.setLocalTranslation(0, 2.0f, 0); // Aumentado de 1.2f a 2.0f para ajustar a la altura del modelo
                this.attachChild(topNode);
                
                System.out.println("Modelo 3D de torre medieval cargado correctamente");
                
            } catch (Exception e) {
                System.out.println("Error al cargar el modelo 3D: " + e.getMessage());
                e.printStackTrace();
                
                // Si hay un error, usar el modelo básico
                useModel = false;
                createBasicTowerModel();
            }
        } else {
            createBasicTowerModel();
        }
        
        // Nodo para proyectiles
        projectilesNode = new Node("ProjectilesNode");
        this.attachChild(projectilesNode);
        
        // Posicionar la torre
        this.setLocalTranslation(position);
        
        System.out.println("Torre creada en " + position);
    }
    
    /**
     * Aplica manualmente las texturas al modelo si es necesario
     */
    private void applyTextures() {
        try {
            // Buscar nodos hijos en el modelo
            if (towerModel instanceof Node) {
                Node modelNode = (Node) towerModel;
                
                // Obtener el nodo que contiene las geometrías
                Node cylinderMesh = (Node) modelNode.getChild("Cylinder-mesh");
                
                if (cylinderMesh != null) {
                    System.out.println("Nodo Cylinder-mesh encontrado. Aplicando texturas...");
                    
                    // Crear materiales con las texturas
                    Material roofMaterial = createSimpleMaterial("Textures/Towers/roof.jpg");
                    Material woodMaterial = createSimpleMaterial("Textures/Towers/innerwood.jpg");
                    Material bottomMaterial = createSimpleMaterial("Textures/Towers/bottom.png");
                    Material topDoorMaterial = createSimpleMaterial("Textures/Towers/topthedoor.jpg");
                    Material towerTopMaterial = createSimpleMaterial("Textures/Towers/towertop.jpg");
                    
                    // Aplicar texturas a las partes correspondientes del modelo
                    // Usando exactamente los nombres que vimos en la estructura
                    Geometry part0 = (Geometry) cylinderMesh.getChild("Cylinder-mat-0-submesh");
                    Geometry part1 = (Geometry) cylinderMesh.getChild("Cylinder-mat-1-submesh");
                    Geometry part2 = (Geometry) cylinderMesh.getChild("Cylinder-mat-2-submesh");
                    Geometry part3 = (Geometry) cylinderMesh.getChild("Cylinder-mat-3-submesh");
                    Geometry part4 = (Geometry) cylinderMesh.getChild("Cylinder-mat-4-submesh");
                    Geometry part6 = (Geometry) cylinderMesh.getChild("Cylinder-mat-6-submesh");
                    
                    // Aplicar materiales a cada parte
                    if (part0 != null) part0.setMaterial(topDoorMaterial); // Techo
                    if (part1 != null) part1.setMaterial(topDoorMaterial); // Interior de madera
                    if (part2 != null) part2.setMaterial(bottomMaterial); // Base
                    if (part3 != null) part3.setMaterial(topDoorMaterial); // Parte superior y puerta
                    if (part4 != null) part4.setMaterial(topDoorMaterial); // Parte superior de la torre
                    // Para part6 podemos usar cualquiera de las texturas o una combinación
                    if (part6 != null) part6.setMaterial(bottomMaterial); // Otra parte de madera
                    
                    System.out.println("Texturas aplicadas correctamente");
                } else {
                    System.out.println("No se encontró el nodo Cylinder-mesh");
                }
            }
        } catch (Exception e) {
            System.out.println("Error al aplicar texturas: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Crea un material simple con textura sin iluminación (más visible)
     */
    private Material createSimpleMaterial(String texturePath) {
        try {
            Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            com.jme3.texture.Texture texture = assetManager.loadTexture(texturePath);
            
            // Ajustar la repetición de la textura para evitar que se vea estirada
            texture.setWrap(com.jme3.texture.Texture.WrapMode.Repeat);
            
            material.setTexture("ColorMap", texture);
            return material;
        } catch (Exception e) {
            System.out.println("Error cargando textura " + texturePath + ": " + e.getMessage());
            Material fallback = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            fallback.setColor("Color", ColorRGBA.Gray);
            return fallback;
        }
    }
    
    /**
     * Crea un material con una textura
     */
    private Material createMaterial(String texturePath) {
        Material material = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        material.setTexture("DiffuseMap", assetManager.loadTexture(texturePath));
        return material;
    }
    
    /**
     * Aplica un material a un hijo específico del modelo
     */
    private void applyMaterialToChild(Node node, String childName, Material material) {
        Spatial child = node.getChild(childName);
        if (child != null && child instanceof Geometry) {
            ((Geometry) child).setMaterial(material);
        } else {
            // Buscar recursivamente
            for (Spatial spatial : node.getChildren()) {
                if (spatial instanceof Node) {
                    applyMaterialToChild((Node) spatial, childName, material);
                }
            }
        }
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
    
    private void createBasicTowerModel() {
        // Base de la torre (cubo azul)
        Box base = new Box(0.4f, 0.4f, 0.4f);
        Geometry baseGeom = new Geometry("TowerBase", base);
        Material baseMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        baseMat.setColor("Color", ColorRGBA.Blue);
        baseGeom.setMaterial(baseMat);
        
        // Parte superior de la torre (cubo más pequeño)
        Box top = new Box(0.2f, 0.2f, 0.2f);
        Geometry topGeom = new Geometry("TowerTop", top);
        Material topMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        topMat.setColor("Color", ColorRGBA.Cyan);
        topGeom.setMaterial(topMat);
        topGeom.setLocalTranslation(0, 0.6f, 0);
        
        // Añadir geometrías
        this.attachChild(baseGeom);
        this.attachChild(topGeom);
        
        // Configurar el nodo superior para rotación
        topNode = new Node("TowerTop");
        topNode.attachChild(topGeom);
        topNode.setLocalTranslation(0, 0.6f, 0);
        this.attachChild(topNode);
    }
    
    public void setApplication(Main app) {
        this.app = app;
    }
    
    public void update(float tpf, List<Enemy> enemies, Node rootNode) {
        // Actualizar temporizador de disparo
        fireTimer += tpf;
        
        // Actualizar proyectiles existentes
        updateProjectiles(tpf, rootNode);
        
        if (fireTimer >= 1.0f / FIRE_RATE) {
            // Buscar el enemigo más cercano dentro del rango
            Enemy target = findNearestEnemyInRange(enemies);
            
            if (target != null) {
                // Si usamos el modelo 3D, rotar la parte superior hacia el enemigo
                if (useModel && topNode != null) {
                    // Obtener dirección al enemigo (solo en el plano XZ)
                    Vector3f direction = target.getPosition().subtract(this.getWorldTranslation());
                    direction.y = 0; // Mantener rotación horizontal
                    
                    // Si la dirección es válida, orientar el nodo superior hacia el enemigo
                    if (direction.length() > 0.1f) {
                        // Mirar hacia la dirección del enemigo
                        topNode.lookAt(this.getWorldTranslation().add(direction), Vector3f.UNIT_Y);
                    }
                }
                
                // Disparar al enemigo
                shootAt(target);
                
                // Crear proyectil visual
                createProjectile(target, rootNode);
                
                // Reiniciar temporizador
                fireTimer = 0;
            }
        }
    }
    
    private void updateProjectiles(float tpf, Node rootNode) {
        // Lista para almacenar proyectiles que deben ser eliminados
        List<ProjectileInfo> projectilesToRemove = new ArrayList<>();
        
        // Iterar a través de los proyectiles activos y actualizarlos
        for (ProjectileInfo projectileInfo : activeProjectiles) {
            projectileInfo.lifetime -= tpf;
            
            if (projectileInfo.lifetime <= 0 || !projectileInfo.target.isAlive()) {
                // Eliminar el proyectil al finalizar su vida útil o si el enemigo murió
                projectilesToRemove.add(projectileInfo);
            } else {
                // Actualizar posición objetivo para seguir al enemigo en movimiento
                projectileInfo.targetPosition = projectileInfo.target.getPosition();
                
                // Mover el proyectil hacia el objetivo
                Geometry projectile = projectileInfo.geometry;
                Vector3f currentPos = projectile.getWorldTranslation();
                Vector3f direction = projectileInfo.targetPosition.subtract(currentPos).normalizeLocal();
                
                // Velocidad del proyectil - AUMENTADA SIGNIFICATIVAMENTE
                float speed = 15.0f;
                Vector3f movement = direction.mult(speed * tpf);
                projectile.move(movement);
                
                // Comprobar si el proyectil ha alcanzado al enemigo
                float distanceToTarget = currentPos.distance(projectileInfo.targetPosition);
                if (distanceToTarget < 0.3f) {
                    // El proyectil impactó al enemigo
                    projectilesToRemove.add(projectileInfo);
                }
            }
        }
        
        // Eliminar proyectiles que han expirado o impactado
        for (ProjectileInfo projectileInfo : projectilesToRemove) {
            rootNode.detachChild(projectileInfo.geometry);
        }
        activeProjectiles.removeAll(projectilesToRemove);
    }
    
    private Enemy findNearestEnemyInRange(List<Enemy> enemies) {
        if (enemies.isEmpty()) {
            return null;
        }
        
        Enemy nearest = null;
        float minDistance = Float.MAX_VALUE;
        
        for (Enemy e : enemies) {
            if (e.isAlive() && !e.hasFinishedPath()) {
                float distance = e.getPosition().distance(this.getWorldTranslation());
                
                if (distance <= RANGE && distance < minDistance) {
                    nearest = e;
                    minDistance = distance;
                }
            }
        }
        
        return nearest;
    }
    
    private void shootAt(Enemy target) {
        target.takeDamage(DAMAGE);
        System.out.println("Disparando a enemigo! Daño: " + DAMAGE + " - Salud restante: " + target.getHealth());
    }
    
    private void createProjectile(Enemy target, Node rootNode) {
        // Crear una esfera como proyectil
        Sphere bullet = new Sphere(8, 8, 0.25f);
        Geometry bulletGeom = new Geometry("Bullet", bullet);
        Material bulletMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        bulletMat.setColor("Color", new ColorRGBA(1f, 0.8f, 0.0f, 1f)); // Amarillo-naranja brillante
        bulletGeom.setMaterial(bulletMat);
        
        // Posicionar en la parte superior de la torre
        Vector3f startPos;
        if (useModel && topNode != null) {
            startPos = topNode.getWorldTranslation().clone();
        } else {
            startPos = this.getWorldTranslation().clone();
            startPos.y += 0.8f;
        }
        bulletGeom.setLocalTranslation(startPos);
        
        // Añadir proyectil directamente al rootNode
        rootNode.attachChild(bulletGeom);
        
        // Crear y añadir información del proyectil
        ProjectileInfo projectileInfo = new ProjectileInfo(
            bulletGeom, 
            target.getPosition().clone(),
            projectileLifetime,
            target
        );
        activeProjectiles.add(projectileInfo);
    }
    
    /**
     * Crea un indicador visual para mostrar dónde se colocará la torre
     * @param assetManager Administrador de recursos
     * @return Geometría del indicador
     */
    public static Geometry createIndicator(AssetManager assetManager) {
        // Base de la torre semitransparente
        Box base = new Box(0.4f, 0.4f, 0.4f);
        Geometry indicator = new Geometry("TowerIndicator", base);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", new ColorRGBA(0.2f, 0.7f, 1f, 0.5f)); // Azul semitransparente
        indicator.setMaterial(mat);
        
        return indicator;
    }
    
    /**
     * Clase auxiliar para el indicador de torre
     */
    public static class TowerIndicator extends Geometry {
        private Material validMaterial;
        private Material invalidMaterial;
        
        public TowerIndicator(AssetManager assetManager) {
            super("TowerIndicator", new Box(0.4f, 0.4f, 0.4f));
            
            validMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            validMaterial.setColor("Color", new ColorRGBA(0.2f, 1f, 0.2f, 0.5f)); // Verde semitransparente
            
            invalidMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            invalidMaterial.setColor("Color", new ColorRGBA(1f, 0.2f, 0.2f, 0.5f)); // Rojo semitransparente
            
            // Inicialmente válido
            this.setMaterial(validMaterial);
        }
        
        public void setValid(boolean valid) {
            this.setMaterial(valid ? validMaterial : invalidMaterial);
        }
    }
}
