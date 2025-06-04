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
    
    // Propiedades de torre que ahora son dinámicas según el tipo
    private float range;
    private float fireRate;
    private int damage;
    private TowerType towerType;
    
    private float fireTimer = 0;
    private AssetManager assetManager;
    private Node projectilesNode;
    private List<ProjectileInfo> activeProjectiles = new ArrayList<>();
    private float projectileLifetime = 0.8f;
    private Main app;
    
    // Modelo 3D
    private Spatial towerModel;
    private Node topNode;
    private boolean useModel = true; // Cambiar a true para usar el modelo 3D
    
    // Clase interna para manejar información de proyectiles
    private class ProjectileInfo {
        Geometry geometry;
        Vector3f targetPosition;
        float lifetime;
        Enemy target;
        
        public ProjectileInfo(Geometry geom, Vector3f target, float life, Enemy enemy) {
            this.geometry = geom;
            this.targetPosition = target;
            this.lifetime = life;
            this.target = enemy;
        }
    }
    
    public Tower(AssetManager assetManager, Vector3f position, TowerType type) {
        this.assetManager = assetManager;
        this.towerType = type;
        
        // Configurar propiedades según el tipo
        this.range = type.getRange();
        this.fireRate = type.getFireRate();
        this.damage = type.getDamage();
        
        // Crear modelo de torre según tipo
        if (useModel) {
            createTower3DModel(type);
        } else {
            createBasicTowerModel(type);
        }
        
        // Nodo para proyectiles
        projectilesNode = new Node("ProjectilesNode");
        this.attachChild(projectilesNode);
        
        // Posicionar la torre
        this.setLocalTranslation(position);
        
        System.out.println("Torre " + type.getName() + " creada en " + position);
    }
    
    private void createBasicTowerModel(TowerType type) {
        // Crear base según el tipo de torre
        Box base = new Box(0.4f, 0.4f, 0.4f);
        Geometry baseGeom = new Geometry("TowerBase", base);
        Material baseMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        baseMat.setColor("Color", type.getBaseColor());
        baseGeom.setMaterial(baseMat);
        this.attachChild(baseGeom);
        
        // Crear parte superior según el tipo de torre
        Box top = new Box(0.2f, 0.2f, 0.2f);
        Geometry topGeom = new Geometry("TowerTop", top);
        Material topMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        topMat.setColor("Color", type.getTopColor());
        topGeom.setMaterial(topMat);
        
        // Configurar el nodo superior para rotación
        topNode = new Node("TowerTop");
        topNode.attachChild(topGeom);
        
        // Características específicas según el tipo
        switch (type) {
            case SNIPER:
                // Torre más alta con un "cañón" más largo
                baseGeom.setLocalScale(0.9f, 1.2f, 0.9f);
                Box barrel = new Box(0.05f, 0.05f, 0.4f);
                Geometry barrelGeom = new Geometry("Barrel", barrel);
                Material barrelMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                barrelMat.setColor("Color", ColorRGBA.DarkGray);
                barrelGeom.setMaterial(barrelMat);
                barrelGeom.setLocalTranslation(0, 0, 0.4f);
                topNode.attachChild(barrelGeom);
                topNode.setLocalTranslation(0, 0.9f, 0);
                break;
                
            case RAPID:
                // Torre con múltiples "cañones" pequeños
                Box barrel1 = new Box(0.08f, 0.08f, 0.2f);
                Box barrel2 = new Box(0.08f, 0.08f, 0.2f);
                Geometry barrel1Geom = new Geometry("Barrel1", barrel1);
                Geometry barrel2Geom = new Geometry("Barrel2", barrel2);
                Material barrelsMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                barrelsMat.setColor("Color", ColorRGBA.Yellow);
                barrel1Geom.setMaterial(barrelsMat);
                barrel2Geom.setMaterial(barrelsMat);
                barrel1Geom.setLocalTranslation(0.1f, 0, 0.2f);
                barrel2Geom.setLocalTranslation(-0.1f, 0, 0.2f);
                topNode.attachChild(barrel1Geom);
                topNode.attachChild(barrel2Geom);
                topNode.setLocalTranslation(0, 0.7f, 0);
                break;
                
            default: // BASIC
                // Torre estándar
                topNode.setLocalTranslation(0, 0.6f, 0);
                break;
        }
        
        this.attachChild(topNode);
    }
    
    // Modificar el método que carga los modelos 3D para usar diferentes modelos según el tipo
    private void createTower3DModel(TowerType type) {
        try {
            // Variable para la ruta del modelo
            String modelPath;
            float modelScale;
            
            // Seleccionar el modelo según el tipo de torre
            switch (type) {
                case SNIPER:
                    modelPath = "Models/uploads_files_2746671_Sci-fi+Tower/uploads_files_2746671_Sci-fi+Tower.j3o";
                    modelScale = 0.11f; // Ajustar la escala según este modelo
                    break;
                case RAPID:
                    modelPath = "Models/wizard tower/wizard tower.j3o"; // Por ahora usa el mismo modelo
                    modelScale = 0.06f;
                    break;
                default: // BASIC
                    modelPath = "Models/medivialtower/medivialtower.j3o";
                    modelScale = 0.3f;
                    break;
            }
            
            // Cargar el modelo
            towerModel = assetManager.loadModel(modelPath);
            
            // Ajustar escala del modelo según el tipo
            towerModel.setLocalScale(modelScale);
            
            // Ajustar la posición según el tipo de torre para corregir desfases
            if (type == TowerType.SNIPER) {
                // Corregir el offset del modelo Sci-Fi
                // Estos valores debes ajustarlos según necesites
                towerModel.setLocalTranslation(0.1f,-1f, 1.3f); // Valores iniciales para probar
            }
            
            if (type == TowerType.RAPID) {
               
                towerModel.setLocalTranslation(0,-0.4f,0); // Valores iniciales para probar
            }
            
            
            
            // La base del modelo estará en la posición de la torre
            this.attachChild(towerModel);
            
            // Crear el nodo superior para la rotación (parte que apunta a los enemigos)
            topNode = new Node("TowerTop");
            
            // Verificar el tipo de modelo y manejarlo adecuadamente
            if (towerModel instanceof Node) {
                // Si es un Node, podemos buscar partes específicas
                Node modelNode = (Node) towerModel;
                
                // Buscar un componente que pueda ser la torreta
                boolean foundTurret = false;
                for (Spatial child : modelNode.getChildren()) {
                    String name = child.getName().toLowerCase();
                    if (name.contains("top") || name.contains("turret") || 
                        name.contains("cannon") || name.contains("gun")) {
                        topNode.attachChild(child);
                        foundTurret = true;
                        System.out.println("Usando " + child.getName() + " como torreta giratoria");
                        break;
                    }
                }
                
                // Si no encontramos una parte específica, usar todo el modelo
                if (!foundTurret) {
                    // En lugar de asignar directamente, creamos un nodo nuevo
                    // y lo conectamos al modelo principal
                    topNode = new Node("TowerTopWrapper");
                    this.attachChild(topNode);
                    topNode.setLocalTranslation(0, 1.0f, 0); // Posicionar adecuadamente
                }
            } else {
                // Si el modelo es una geometría directa, crear un nodo superior simple
                System.out.println("Modelo de torre es una geometría, no un nodo. Usando nodo superior para rotación.");
                topNode = new Node("TowerTopWrapper");
                this.attachChild(topNode);
                topNode.setLocalTranslation(0, 1.0f, 0); // Posicionarlo encima del modelo
            }
            
            System.out.println("Modelo 3D cargado para torre " + type.getName() + ": " + modelPath);
        } catch (Exception e) {
            System.err.println("Error al cargar modelo 3D para " + type.getName() + ": " + e.getMessage());
            e.printStackTrace();
            
            // Si falla la carga del modelo 3D, usar el modelo básico de cubos
            useModel = false;
            createBasicTowerModel(type);
        }
    }
    
    public void setApplication(Main app) {
        this.app = app;
    }
    
    public void update(float tpf, List<Enemy> enemies, Node rootNode) {
        // Actualizar temporizador de disparo
        fireTimer += tpf;
        
        // Actualizar proyectiles existentes
        updateProjectiles(tpf, rootNode);
        
        // Solo disparar cuando el temporizador alcance el tiempo adecuado según la cadencia
        if (fireTimer >= 1.0f / fireRate) {
            // Buscar el enemigo más cercano dentro del rango
            Enemy target = findNearestEnemyInRange(enemies);
            
            if (target != null) {
                // Rotar hacia el enemigo
                Vector3f direction = target.getPosition().subtract(this.getWorldTranslation());
                direction.y = 0; // Mantener rotación horizontal
                
                // Si la dirección es válida, orientar el nodo superior hacia el enemigo
                if (direction.length() > 0.1f && topNode != null) {
                    topNode.lookAt(this.getWorldTranslation().add(direction), Vector3f.UNIT_Y);
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
                
                // Velocidad del proyectil
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
                
                if (distance <= range && distance < minDistance) {
                    nearest = e;
                    minDistance = distance;
                }
            }
        }
        
        return nearest;
    }
    
    private void shootAt(Enemy target) {
        target.takeDamage(damage);
        System.out.println("¡" + towerType.getName() + " dispara! Daño: " + damage + " - Salud restante: " + target.getHealth());
    }
    
    private void createProjectile(Enemy target, Node rootNode) {
        // Crear una forma para el proyectil según el tipo de torre
        Geometry bulletGeom;
        
        switch (towerType) {
            case SNIPER:
                // Proyectil más grande y rápido (bala de francotirador)
                Sphere bullet = new Sphere(8, 8, 0.3f);
                bulletGeom = new Geometry("Bullet", bullet);
                Material bulletMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                bulletMat.setColor("Color", new ColorRGBA(1f, 0.2f, 0.1f, 1f)); // Rojo
                bulletGeom.setMaterial(bulletMat);
                break;
                
            case RAPID:
                // Proyectil pequeño (fuego rápido)
                Sphere fastBullet = new Sphere(6, 6, 0.15f);
                bulletGeom = new Geometry("Bullet", fastBullet);
                Material fastBulletMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                fastBulletMat.setColor("Color", new ColorRGBA(0.8f, 1f, 0.2f, 1f)); // Amarillo-verde
                bulletGeom.setMaterial(fastBulletMat);
                break;
                
            default: // BASIC
                // Proyectil estándar
                Sphere standardBullet = new Sphere(8, 8, 0.25f);
                bulletGeom = new Geometry("Bullet", standardBullet);
                Material standardBulletMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                standardBulletMat.setColor("Color", new ColorRGBA(1f, 0.8f, 0.0f, 1f)); // Amarillo-naranja
                bulletGeom.setMaterial(standardBulletMat);
                break;
        }
        
        // Posicionar en la parte superior de la torre
        Vector3f startPos = topNode.getWorldTranslation().clone();
        bulletGeom.setLocalTranslation(startPos);
        
        // Añadir proyectil al rootNode
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
     */
    public static Geometry createIndicator(AssetManager assetManager, TowerType type) {
        Box base = new Box(0.4f, 0.4f, 0.4f);
        Geometry indicator = new Geometry("TowerIndicator", base);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        
        ColorRGBA color = type.getBaseColor().clone();
        color.a = 0.5f; // Semitransparente
        mat.setColor("Color", color);
        indicator.setMaterial(mat);
        
        return indicator;
    }
    
    /**
     * Clase para el indicador de torre
     */
    public static class TowerIndicator extends Geometry {
        private Material validMaterial;
        private Material invalidMaterial;
        
        public TowerIndicator(AssetManager assetManager, TowerType type) {
            super("TowerIndicator", new Box(0.4f, 0.4f, 0.4f));
            
            validMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            ColorRGBA validColor = type.getBaseColor().clone();
            validColor.a = 0.5f;
            validMaterial.setColor("Color", validColor);
            
            invalidMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            invalidMaterial.setColor("Color", new ColorRGBA(1f, 0.2f, 0.2f, 0.5f)); // Rojo
            
            this.setMaterial(validMaterial);
        }
        
        public void setValid(boolean valid) {
            this.setMaterial(valid ? validMaterial : invalidMaterial);
        }
    }
    
    // Getters para información sobre la torre
    public TowerType getTowerType() { return towerType; }
    public int getDamage() { return damage; }
    public float getRange() { return range; }
    public float getFireRate() { return fireRate; }
    public int getCost() { return towerType.getCost(); }
}
