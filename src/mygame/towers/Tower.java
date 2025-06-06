package mygame.towers;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioData.DataType;
import java.util.ArrayList;
import java.util.List;
import mygame.Main;
import mygame.enemies.Enemy;
import com.jme3.texture.Texture;

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
    
    // Sonidos
    private AudioNode shootSound;
    private AudioNode impactSound;
    private static AudioNode currentImpactSound; // Sonido de impacto compartido entre todas las torres
    
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
    
    private int level = 0; // Nivel 0 = sin mejoras
    private static final int MAX_LEVEL = 2; // Máximo nivel de mejora
    
    // Constructor modificado
    public Tower(AssetManager assetManager, Vector3f position, TowerType type) {
        this.assetManager = assetManager;
        this.towerType = type;
        
        // Configurar sonidos
        setupSounds();
        
        // Configurar propiedades según el tipo
        updateStats(); // Nuevo método para actualizar estadísticas basado en el nivel
        
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
      
    // Método para actualizar estadísticas basadas en el nivel
    private void updateStats() {
        if (level == 0) {
            // Nivel base
            this.range = towerType.getRange();
            this.fireRate = towerType.getFireRate();
            this.damage = towerType.getDamage();
        } else {
            // Nivel mejorado
            this.damage = towerType.getUpgradedDamage(level);
            this.fireRate = towerType.getUpgradedFireRate(level);
            this.range = towerType.getUpgradedRange(level);
        }
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
                    modelScale = 0.09f; // Ajustar la escala según este modelo
                    break;
                case RAPID:
                    modelPath = "Models/wizard tower/wizard tower.j3o"; // Por ahora usa el mismo modelo
                    modelScale = 0.04f;
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
                towerModel.setLocalTranslation(0.1f, -1f, 0.9f);
            } else if (type == TowerType.RAPID) {
                towerModel.setLocalTranslation(0, -0.4f, 0);
            } else { // BASIC
                // Ajustar la posición de la torre básica para corregir el desfase
                towerModel.setLocalTranslation(0, 0f, 0.3f);
            }
            
            // Aplicar textura específica según el tipo de torre
            if (type == TowerType.BASIC) {
                try {
                    // Crear el material para la torre básica
                    Material towerMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                    
                    // Cargar la textura específica
                    Texture towerTexture = assetManager.loadTexture("Textures/texture_basic_tower/basictower2.jpg");
                    
                    // Configurar parámetros de la textura para mejor calidad
                    towerTexture.setAnisotropicFilter(8);
                    towerTexture.setMagFilter(Texture.MagFilter.Bilinear);
                    
                    // Aplicar la textura al material
                    towerMaterial.setTexture("ColorMap", towerTexture);
                    
                    // Optimizar el tinte para resaltar detalles de la textura
                    towerMaterial.setColor("Color", new ColorRGBA(0.9f, 0.9f, 0.9f, 1.0f));
                    
                    // Aplicar el material al modelo completo
                    applyMaterialToSpatial(towerModel, towerMaterial);
                    
                    System.out.println("Textura de torre básica aplicada correctamente");
                } catch (Exception e) {
                    System.out.println("Error al aplicar textura a la torre básica: " + e.getMessage());
                    e.printStackTrace();
                }
            } else if (type == TowerType.RAPID) {
                try {
                    // Crear el material para la torre rápida
                    Material rapidTowerMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                    
                    // Cargar la textura específica para la torre rápida
                    Texture rapidTowerTexture = assetManager.loadTexture("Textures/texture_rapid_tower/rapidtower1.jpg");
                    
                    // Configurar parámetros de la textura para mejor calidad
                    rapidTowerTexture.setAnisotropicFilter(8);
                    rapidTowerTexture.setMagFilter(Texture.MagFilter.Bilinear);
                    
                    // Aplicar la textura al material
                    rapidTowerMaterial.setTexture("ColorMap", rapidTowerTexture);
                    
                    // Añadir un tinte ligeramente verde para enfatizar que es la torre rápida
                    rapidTowerMaterial.setColor("Color", new ColorRGBA(0.8f, 1.0f, 0.8f, 1.0f));
                    
                    // Aplicar el material al modelo completo
                    applyMaterialToSpatial(towerModel, rapidTowerMaterial);
                    
                    System.out.println("Textura de torre rápida aplicada correctamente");
                } catch (Exception e) {
                    System.out.println("Error al aplicar textura a la torre rápida: " + e.getMessage());
                    e.printStackTrace();
                }
            } else if (type == TowerType.SNIPER) {
                try {
                    // Crear el material para la torre francotirador
                    Material sniperTowerMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                    
                    // Cargar la textura específica para la torre francotirador
                    Texture sniperTowerTexture = assetManager.loadTexture("Textures/texture_sniper_tower/franco1.jpg");
                    
                    // Configurar parámetros de la textura para mejor calidad
                    sniperTowerTexture.setAnisotropicFilter(8);
                    sniperTowerTexture.setMagFilter(Texture.MagFilter.Bilinear);
                    
                    // Aplicar la textura al material
                    sniperTowerMaterial.setTexture("ColorMap", sniperTowerTexture);
                    
                    // Añadir un tinte ligeramente rojizo para enfatizar que es la torre francotirador
                    sniperTowerMaterial.setColor("Color", new ColorRGBA(1.0f, 0.8f, 0.8f, 1.0f));
                    
                    // Aplicar el material al modelo completo
                    applyMaterialToSpatial(towerModel, sniperTowerMaterial);
                    
                    System.out.println("Textura de torre francotirador aplicada correctamente");
                } catch (Exception e) {
                    System.out.println("Error al aplicar textura a la torre francotirador: " + e.getMessage());
                    e.printStackTrace();
                }
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
    
    /**
     * Aplica un material a todas las geometrías de un objeto espacial (Spatial)
     * @param spatial El objeto al que aplicar el material
     * @param material El material a aplicar
     */
    private void applyMaterialToSpatial(Spatial spatial, Material material) {
        if (spatial instanceof Geometry) {
            ((Geometry) spatial).setMaterial(material);
        } else if (spatial instanceof Node) {
            // Si es un nodo, aplicar recursivamente a todos sus hijos
            for (Spatial child : ((Node) spatial).getChildren()) {
                applyMaterialToSpatial(child, material);
            }
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
                    
                    // Reproducir sonido de impacto
                    if (currentImpactSound != null) {
                        // Detener cualquier reproducción anterior
                        currentImpactSound.stop();
                        // Reproducir el sonido de impacto
                        currentImpactSound.playInstance();
                    }
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
        
        // Reproducir sonido de disparo
        if (shootSound != null) {
            shootSound.playInstance();
        }
        
        System.out.println("¡" + towerType.getName() + " dispara! Daño: " + damage + " - Salud restante: " + target.getHealth());
    }
    
    private void createProjectile(Enemy target, Node rootNode) {
        // Crear una forma para el proyectil según el tipo de torre
        Geometry bulletGeom;
        
        switch (towerType) {
            case SNIPER:
                // Proyectil más grande y rápido (bala de francotirador)
                Sphere bullet = new Sphere(4, 4, 0.3f);
                bulletGeom = new Geometry("Bullet", bullet);
                Material bulletMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                bulletMat.setColor("Color", new ColorRGBA(1f, 0.2f, 0.1f, 1f)); // Rojo
                bulletGeom.setMaterial(bulletMat);
                break;
                
            case RAPID:
                // Proyectil pequeño (fuego rápido)
                Sphere fastBullet = new Sphere(3, 3, 0.15f);
                bulletGeom = new Geometry("Bullet", fastBullet);
                Material fastBulletMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                fastBulletMat.setColor("Color", new ColorRGBA(0.8f, 1f, 0.2f, 1f)); // Amarillo-verde
                bulletGeom.setMaterial(fastBulletMat);
                break;
                
            default: // BASIC
                // Proyectil estándar
                Sphere standardBullet = new Sphere(3, 3, 0.25f);
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
        Box base = new Box(0.4f, 0.05f, 0.4f);
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
    
    // Nuevos getters para el sistema de mejoras
    public int getLevel() { return level; }
    public int getUpgradeCost() { 
        return towerType.getUpgradeCost(level + 1);
    }
    public boolean canUpgrade() {
        return level < MAX_LEVEL && level < towerType.getMaxUpgradeLevel();
    }
    
    /**
     * Calcula la inversión total en esta torre (costo base + mejoras)
     * @return El valor total invertido en la torre
     */
    public int getTotalInvestment() {
        int baseCost = towerType.getCost();
        int upgradeCost = 0;
        
        // Sumar los costos de mejora realizados
        for (int i = 1; i <= level; i++) {
            upgradeCost += towerType.getUpgradeCost(i);
        }
        
        return baseCost + upgradeCost;
    }
    
    // Método para mejorar la torre
    public boolean upgrade() {
        if (level >= MAX_LEVEL || level >= towerType.getMaxUpgradeLevel()) {
            System.out.println("Esta torre ya está al nivel máximo.");
            return false;
        }
        
        level++;
        updateStats();
        
        // Actualizar visual de la torre según el nivel
        updateTowerVisual();
        
        System.out.println("Torre mejorada a nivel " + level + 
                          ". Nuevo daño: " + damage + 
                          ", Nueva cadencia: " + fireRate);
        return true;
    }
    
    // Método para actualizar el aspecto de la torre según el nivel
    private void updateTowerVisual() {
        // En el modelo básico, cambiamos el color según el nivel
        if (!useModel) {
            Material baseMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            Material topMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            
            switch (level) {
                case 1:
                    // Nivel 1: Colores más intensos
                    baseMat.setColor("Color", towerType.getBaseColor().mult(1.2f));
                    topMat.setColor("Color", towerType.getTopColor().mult(1.2f));
                    break;
                case 2:
                    // Nivel 2: Colores brillantes y escala ligeramente mayor
                    baseMat.setColor("Color", towerType.getBaseColor().mult(1.5f));
                    topMat.setColor("Color", towerType.getTopColor().mult(1.5f));
                    
                    // Aumentar un poco la escala
                    this.setLocalScale(1.15f);
                    break;
            }
            
            // Buscar las geometrías para aplicar nuevos materiales
            for (Spatial child : this.getChildren()) {
                if (child instanceof Geometry) {
                    Geometry geom = (Geometry) child;
                    if (geom.getName().equals("TowerBase")) {
                        geom.setMaterial(baseMat);
                    } else if (geom.getName().equals("TowerTop")) {
                        geom.setMaterial(topMat);
                    }
                }
            }
        } else {
            // Para modelos 3D, podemos añadir efectos visuales según el nivel
            // Ejemplo: aumentar un poco la escala o añadir un brillo
            switch (level) {
                case 1:
                    // Nivel 1: Escala un poco mayor
                    if (towerModel != null) {
                        towerModel.setLocalScale(towerModel.getLocalScale().mult(1.1f));
                    }
                    break;
                case 2:
                    // Nivel 2: Efecto de brillo o partículas
                    if (towerModel != null) {
                        // Añadir un efecto de brillo o color más intenso
                        applyGlowEffect();
                    }
                    break;
            }
        }
    }
    
    // Método para aplicar efecto de brillo a torres mejoradas al máximo
    private void applyGlowEffect() {
        try {
            // Si es torre francotirador al nivel máximo, aplicar textura especial
            if (towerType == TowerType.SNIPER && level >= MAX_LEVEL) {
                // Crear un nuevo material para la textura de la torre sniper máxima
                Material maxSniperMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                
                // Cargar la textura especial para la torre francotirador al máximo nivel
                Texture maxSniperTexture = assetManager.loadTexture("Textures/texture_sniper_tower/maximo_sniper.jpg");
                
                // Configurar parámetros de la textura para mejor calidad
                maxSniperTexture.setAnisotropicFilter(8);
                maxSniperTexture.setMagFilter(Texture.MagFilter.Bilinear);
                
                // Aplicar la textura al material
                maxSniperMaterial.setTexture("ColorMap", maxSniperTexture);
                
                // Aplicar el material al modelo completo
                if (towerModel instanceof Geometry) {
                    ((Geometry) towerModel).setMaterial(maxSniperMaterial);
                } else if (towerModel instanceof Node) {
                    applyMaterialToNode((Node) towerModel, maxSniperMaterial);
                }
                
                System.out.println("Textura de nivel máximo aplicada a la torre francotirador");
                return; // Salir del método para no aplicar el efecto de brillo
            }
            // NUEVO: Si es torre básica al nivel máximo, aplicar textura especial
            else if (towerType == TowerType.BASIC && level >= MAX_LEVEL) {
                // Crear un nuevo material para la textura de la torre básica máxima
                Material maxBasicMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                
                // Cargar la textura especial para la torre básica al máximo nivel
                Texture maxBasicTexture = assetManager.loadTexture("Textures/texture_basic_tower/maximo_basic.jpg");
                
                // Configurar parámetros de la textura para mejor calidad
                maxBasicTexture.setAnisotropicFilter(8);
                maxBasicTexture.setMagFilter(Texture.MagFilter.Bilinear);
                
                // Aplicar la textura al material
                maxBasicMaterial.setTexture("ColorMap", maxBasicTexture);
                
                // Aplicar el material al modelo completo
                if (towerModel instanceof Geometry) {
                    ((Geometry) towerModel).setMaterial(maxBasicMaterial);
                } else if (towerModel instanceof Node) {
                    applyMaterialToNode((Node) towerModel, maxBasicMaterial);
                }
                
                System.out.println("Textura de nivel máximo aplicada a la torre básica");
                return; // Salir del método para no aplicar el efecto de brillo
            }
            // NUEVO: Si es torre rápida al nivel máximo, aplicar textura especial
            else if (towerType == TowerType.RAPID && level >= MAX_LEVEL) {
                // Crear un nuevo material para la textura de la torre rápida máxima
                Material maxRapidMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                
                // Cargar la textura especial para la torre rápida al máximo nivel
                Texture maxRapidTexture = assetManager.loadTexture("Textures/texture_rapid_tower/max_rapid3.jpg");
                
                // Configurar parámetros de la textura para mejor calidad
                maxRapidTexture.setAnisotropicFilter(8);
                maxRapidTexture.setMagFilter(Texture.MagFilter.Bilinear);
                
                // Aplicar la textura al material
                maxRapidMaterial.setTexture("ColorMap", maxRapidTexture);
                
                // Aplicar el material al modelo completo
                if (towerModel instanceof Geometry) {
                    ((Geometry) towerModel).setMaterial(maxRapidMaterial);
                } else if (towerModel instanceof Node) {
                    applyMaterialToNode((Node) towerModel, maxRapidMaterial);
                }
                
                System.out.println("Textura de nivel máximo aplicada a la torre rápida");
                return; // Salir del método para no aplicar el efecto de brillo
            }
            
            // Para las otras torres, aplicar el efecto de brillo original
            Material glowMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            
            // Definir colores brillantes según el tipo de torre
            ColorRGBA glowColor;
            switch (towerType) {
                case SNIPER:
                    glowColor = new ColorRGBA(1.0f, 0.3f, 0.3f, 1.0f); // Rojo brillante
                    break;
                case RAPID:
                    glowColor = new ColorRGBA(0.3f, 1.0f, 0.3f, 1.0f); // Verde brillante
                    break;
                default:
                    glowColor = new ColorRGBA(0.3f, 0.3f, 1.0f, 1.0f); // Azul brillante
                    break;
            }
            
            // Configurar el material con el color brillante
            glowMat.setColor("Color", glowColor);
            
            // Aplicar el material según el tipo de modelo
            if (towerModel instanceof Geometry) {
                // Si el modelo es una geometría directa
                ((Geometry) towerModel).setMaterial(glowMat);
            } else if (towerModel instanceof Node) {
                // Si es un modelo compuesto (Node), aplicamos a todas sus geometrías
                applyMaterialToNode((Node) towerModel, glowMat);
            }
            
            // Aumentar ligeramente la escala para efecto visual
            towerModel.setLocalScale(towerModel.getLocalScale().mult(1.05f));
            
            System.out.println("Efecto de brillo aplicado a torre nivel " + level);
        } catch (Exception e) {
            System.out.println("No se pudo aplicar efecto visual: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Método auxiliar para aplicar un material a todas las geometrías de un nodo
    private void applyMaterialToNode(Node node, Material material) {
        for (Spatial child : node.getChildren()) {
            if (child instanceof Geometry) {
                ((Geometry) child).setMaterial(material);
            } else if (child instanceof Node) {
                applyMaterialToNode((Node) child, material);
            }
        }
    }
    
    // Geometría para el destacado (selección)
    private Geometry highlightGeometry;
    
    // Método para añadir un destacado visual cuando se selecciona
    public void addHighlight(AssetManager assetManager) {
        // Eliminar cualquier destacado existente
        removeHighlight();
        
        // Crear un anillo o círculo alrededor de la torre
        // Rotamos el cilindro para que quede horizontal (paralelo al suelo)
        com.jme3.scene.shape.Cylinder highlightCylinder = 
            new com.jme3.scene.shape.Cylinder(32, 32, range, 0.05f, true);
        
        highlightGeometry = new Geometry("TowerHighlight", highlightCylinder);
        
        // Material semitransparente para el destacado
        Material highlightMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        
        // Color según el tipo de torre con mayor transparencia (reducir valor alpha)
        ColorRGBA highlightColor;
        switch (towerType) {
            case SNIPER:
                highlightColor = new ColorRGBA(1.0f, 0.2f, 0.2f, 0.05f); // Rojo más translúcido
                break;
            case RAPID:
                highlightColor = new ColorRGBA(0.2f, 1.0f, 0.2f, 0.05f); // Verde más translúcido
                break;
            default:
                highlightColor = new ColorRGBA(0.2f, 0.5f, 1.0f, 0.05f); // Azul más translúcido
                break;
        }
        
        highlightMat.setColor("Color", highlightColor);
        
        // Configurar el renderState para una mejor transparencia
        highlightMat.getAdditionalRenderState().setBlendMode(com.jme3.material.RenderState.BlendMode.Alpha);
        highlightMat.getAdditionalRenderState().setDepthTest(true);
        highlightMat.getAdditionalRenderState().setDepthWrite(false); // Importante para transparencia
        
        highlightGeometry.setMaterial(highlightMat);
        highlightGeometry.setQueueBucket(com.jme3.renderer.queue.RenderQueue.Bucket.Transparent);
        
        // Rotar el cilindro 90 grados para que quede paralelo al suelo
        Quaternion rotation = new Quaternion();
        rotation.fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_X);
        highlightGeometry.setLocalRotation(rotation);
        
        // Posicionar el destacado a la altura correcta
        // Posicionar el destacado a la altura correcta (ahora a nivel del suelo)
        float heightOffset;
        if (towerType == TowerType.SNIPER) {
            heightOffset = 0.05f; // Reducido de 0.5f a 0.05f (casi a nivel del suelo)
        } else if (towerType == TowerType.RAPID) {
            heightOffset = 0.05f; // Reducido de 0.3f a 0.05f
        } else {
            heightOffset = 0.05f; // Reducido de 0.2f a 0.05f
        }

        // Añadir un mínimo desplazamiento vertical para evitar z-fighting con el terreno
        
        
        // Añadir el destacado a la torre
        this.attachChild(highlightGeometry);
        
        System.out.println("Añadido círculo de selección para torre " + towerType.getName());
    }
    
    // Método para eliminar el destacado
    public void removeHighlight() {
        if (highlightGeometry != null) {
            highlightGeometry.removeFromParent();
            highlightGeometry = null;
        }
    }    private void setupSounds() {
        try {
            // Obtener la ruta del sonido de disparo
            String shootSoundPath = towerType.getSoundPath();
            System.out.println("Configurando sonido de disparo para " + towerType.getName() + ": " + shootSoundPath);
            
            // Verificar que los archivos existen
            if (assetManager.locateAsset(new com.jme3.asset.AssetKey(shootSoundPath)) == null) {
                System.err.println("¡ADVERTENCIA! No se encuentra el archivo de sonido: " + shootSoundPath);
            }
            
            // Intentar cargar el sonido de disparo
            try {
                // Primero intentar como Buffer para mejor rendimiento
                shootSound = new AudioNode(assetManager, shootSoundPath, DataType.Buffer);
                System.out.println("Sonido de disparo cargado como Buffer");
            } catch (Exception e) {
                // Si falla, intentar como Stream
                System.out.println("Error cargando como Buffer, intentando como Stream...");
                shootSound = new AudioNode(assetManager, shootSoundPath, DataType.Stream);
                System.out.println("Sonido de disparo cargado como Stream");
            }
            
            // Configurar el sonido de disparo
            shootSound.setPositional(false);
            shootSound.setLooping(false);
            shootSound.setVolume(0.4f);
            shootSound.setReverbEnabled(false);
            this.attachChild(shootSound);
            
            // Configurar sonido de impacto compartido si aún no existe
            if (currentImpactSound == null) {
                String impactSoundPath = "Sounds/Ambiente/impacto.wav";
                System.out.println("Configurando sonido de impacto compartido: " + impactSoundPath);
                
                // Verificar que el archivo existe
                if (assetManager.locateAsset(new com.jme3.asset.AssetKey(impactSoundPath)) == null) {
                    System.err.println("¡ADVERTENCIA! No se encuentra el archivo de sonido: " + impactSoundPath);
                }
                
                try {
                    currentImpactSound = new AudioNode(assetManager, impactSoundPath, DataType.Buffer);
                    System.out.println("Sonido de impacto cargado como Buffer");
                } catch (Exception e) {
                    System.out.println("Error cargando como Buffer, intentando como Stream...");
                    currentImpactSound = new AudioNode(assetManager, impactSoundPath, DataType.Stream);
                    System.out.println("Sonido de impacto cargado como Stream");
                }
                currentImpactSound.setPositional(false);
                currentImpactSound.setLooping(false);
                currentImpactSound.setVolume(0.3f);
                currentImpactSound.setReverbEnabled(false);
            }
            
            System.out.println("Configuración de sonidos completada exitosamente");
            
        } catch (Exception e) {
            System.err.println("Error cargando sonidos para " + towerType.getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}

