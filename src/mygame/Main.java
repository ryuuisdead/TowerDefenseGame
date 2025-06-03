package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResults;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import java.util.ArrayList;
import java.util.List;
import mygame.enemies.Enemy;
import mygame.map.GameMap;
import mygame.map.Path;
import mygame.towers.Tower;
import mygame.ui.GameUI;
import com.jme3.material.Material; // Añadir esta importación
import com.jme3.math.ColorRGBA; // Añadir esta importación




public class Main extends SimpleApplication implements ActionListener {

    private List<Enemy> enemies = new ArrayList<>();
    private List<Tower> towers = new ArrayList<>();
    private GameMap gameMap;
    private Path path;
    private GameUI gameUI;
    
    // Variables para el sistema de oleadas
    private float waveTimer = 0;
    private float spawnTimer = 0;
    private int currentWave = 1;
    private int enemiesInWave = 5; // Inicialmente 5 enemigos por oleada
    private int enemiesSpawned = 0;
    private boolean waveInProgress = false;
    
    // Economía del jugador
    private int money = 200;
    private int score = 0;
    
    // Constantes para la torre
    private static final int TOWER_COST = 50; // Coste de construir una torre
    private static final String PLACE_TOWER = "PlaceTower"; // Nombre de la acción
    
    // Indicador visual de colocación de torre
    private Geometry towerPlacementIndicator;
    private boolean isValidPlacement = false;

    public static void main(String[] args) {
        Main app = new Main();
        
        AppSettings settings = new AppSettings(true);
        settings.setTitle("Tower Defense Infernal");
        settings.setResolution(800, 600);
        
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        // Crear mapa/terreno
        gameMap = new GameMap(assetManager);
        rootNode.attachChild(gameMap);

        // Crear camino para enemigos
        path = new Path();
        
        // Configurar cámara isométrica fija
        cam.setLocation(new Vector3f(8, 12, 12));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        flyCam.setEnabled(false);
        
        // Colocar una torre inicialmente
        Tower tower = new Tower(assetManager, new Vector3f(2, 0.5f, 3));
        towers.add(tower);
        rootNode.attachChild(tower);
        
        // Inicializar la interfaz de usuario
        gameUI = new GameUI(guiNode, assetManager);
        
        // Configurar inputs
        setupInputs();
        
        // Crear indicador de colocación de torres
        createTowerPlacementIndicator();
        
        
    }
    
    private void setupInputs() {
        // Registrar acción para colocar torres
        inputManager.addMapping(PLACE_TOWER, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(this, PLACE_TOWER);
    }
    
    private void createTowerPlacementIndicator() {
        // Crear un indicador semitransparente para mostrar dónde se colocará la torre
        towerPlacementIndicator = Tower.createIndicator(assetManager);
        towerPlacementIndicator.setCullHint(Spatial.CullHint.Always); // Oculto por defecto
        rootNode.attachChild(towerPlacementIndicator);
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals(PLACE_TOWER) && !isPressed) {
            // Solo procesar cuando se suelta el botón
            placeTowerAtMousePosition();
        }
    }
    
    private void placeTowerAtMousePosition() {
        // Obtener la posición del ratón
        Vector2f mousePos = inputManager.getCursorPosition();
        
        // Crear un rayo desde la cámara hacia la posición del ratón
        Vector3f worldCoordinates = cam.getWorldCoordinates(mousePos, 0f).clone();
        Vector3f dir = cam.getWorldCoordinates(mousePos, 1f).subtractLocal(worldCoordinates).normalizeLocal();
        Ray ray = new Ray(worldCoordinates, dir);
        
        // Comprobar colisiones con el mapa
        CollisionResults results = new CollisionResults();
        gameMap.collideWith(ray, results);
        
        if (results.size() > 0) {
            // Obtener el punto de colisión
            Vector3f contactPoint = results.getClosestCollision().getContactPoint();
            
            // Comprobar si es una posición válida para colocar una torre
            if (isValidTowerPosition(contactPoint) && money >= TOWER_COST) {
                // Redondear a la posición de la cuadrícula
                Vector3f gridPos = new Vector3f(
                    Math.round(contactPoint.x),
                    0.5f, // Altura fija para las torres
                    Math.round(contactPoint.z)
                );
                
                // Crear y colocar la nueva torre
                Tower newTower = new Tower(assetManager, gridPos);
                towers.add(newTower);
                rootNode.attachChild(newTower);
                
                // Reducir el dinero del jugador
                money -= TOWER_COST;
                
                System.out.println("Torre colocada en " + gridPos + ". Dinero restante: " + money);
            } else {
                if (money < TOWER_COST) {
                    System.out.println("No tienes suficiente dinero para construir una torre. Necesitas: " + TOWER_COST);
                } else {
                    System.out.println("No se puede colocar una torre en esta posición.");
                }
            }
        }
    }
    
    private boolean isValidTowerPosition(Vector3f position) {
        // Redondear a la posición de la cuadrícula
        int gridX = Math.round(position.x);
        int gridZ = Math.round(position.z);
        
        // Verificar si está en los límites del mapa
        if (gridX < -4 || gridX > 4 || gridZ < -4 || gridZ > 4) {
            return false;
        }
        
        // Verificar si está en el camino
        int[][] pathTiles = path.getPathCoordinates();
        for (int[] tile : pathTiles) {
            // Convertir coordenadas del mundo a coordenadas del mapa
            int mapX = gridX + GameMap.MAP_SIZE/2;
            int mapZ = gridZ + GameMap.MAP_SIZE/2;
            
            if (tile[0] == mapX && tile[1] == mapZ) {
                return false;
            }
        }
        
        // Verificar si ya hay una torre en esta posición
        for (Tower t : towers) {
            Vector3f towerPos = t.getLocalTranslation();
            if (Math.round(towerPos.x) == gridX && Math.round(towerPos.z) == gridZ) {
                return false;
            }
        }
        
        // Si pasó todas las comprobaciones, la posición es válida
        return true;
    }

    @Override
    public void simpleUpdate(float tpf) {
        // Gestión de oleadas
        manageWaves(tpf);
        
        // Actualizar enemigos
        List<Enemy> deadEnemies = new ArrayList<>();
        for (Enemy e : enemies) {
            if (e.isAlive() && !e.hasFinishedPath()) {
                e.update(tpf);
            } else {
                deadEnemies.add(e);
                
                // Dar dinero si el enemigo fue derrotado (no llegó al final)
                if (!e.isAlive()) {
                    money += 10;
                    score += 5;
                }
            }
        }

        // Remover enemigos muertos o que llegaron al final
        for (Enemy e : deadEnemies) {
            rootNode.detachChild(e);
        }
        enemies.removeAll(deadEnemies);

        // Actualizar torres
        for (Tower t : towers) {
            t.update(tpf, enemies, rootNode);
        }
        
        // Actualizar indicador de colocación de torre
        updateTowerPlacementIndicator();
        
        // Actualizar UI
        gameUI.update(money, score, currentWave, waveInProgress, 5.0f - waveTimer);
    }
    
    private void updateTowerPlacementIndicator() {
        // Obtener la posición del ratón
        Vector2f mousePos = inputManager.getCursorPosition();
        
        // Crear un rayo desde la cámara hacia la posición del ratón
        Vector3f worldCoordinates = cam.getWorldCoordinates(mousePos, 0f).clone();
        Vector3f dir = cam.getWorldCoordinates(mousePos, 1f).subtractLocal(worldCoordinates).normalizeLocal();
        Ray ray = new Ray(worldCoordinates, dir);
        
        // Comprobar colisiones con el mapa
        CollisionResults results = new CollisionResults();
        gameMap.collideWith(ray, results);
        
        if (results.size() > 0) {
            // Obtener el punto de colisión
            Vector3f contactPoint = results.getClosestCollision().getContactPoint();
            
            // Redondear a la posición de la cuadrícula
            Vector3f gridPos = new Vector3f(
                Math.round(contactPoint.x),
                0.5f, // Altura fija para las torres
                Math.round(contactPoint.z)
            );
            
            // Comprobar si es una posición válida
            isValidPlacement = isValidTowerPosition(contactPoint) && money >= TOWER_COST;
            
            // Mostrar indicador en la posición del ratón
            towerPlacementIndicator.setCullHint(Spatial.CullHint.Never);
            towerPlacementIndicator.setLocalTranslation(gridPos);
            
            // Cambiar el color según si es válido o no
            Material mat = towerPlacementIndicator.getMaterial();
            if (isValidPlacement) {
                mat.setColor("Color", new com.jme3.math.ColorRGBA(0.2f, 1f, 0.2f, 0.5f)); // Verde válido
            } else {
                mat.setColor("Color", new com.jme3.math.ColorRGBA(1f, 0.2f, 0.2f, 0.5f)); // Rojo inválido
            }
        } else {
            // Ocultar el indicador si no hay colisión con el mapa
            towerPlacementIndicator.setCullHint(Spatial.CullHint.Always);
            isValidPlacement = false;
        }
    }
    
    private void manageWaves(float tpf) {
        // Si no hay oleada en progreso, empezar una nueva después de un tiempo
        if (!waveInProgress) {
            waveTimer += tpf;
            if (waveTimer >= 5.0f) { // 5 segundos entre oleadas
                startNewWave();
                waveTimer = 0;
            }
        } else {
            // Si hay una oleada en progreso, generar enemigos
            if (enemiesSpawned < enemiesInWave) {
                spawnTimer += tpf;
                if (spawnTimer >= 1.5f) { // Intervalos de 1.5 segundos entre enemigos
                    spawnEnemy();
                    spawnTimer = 0;
                }
            } else if (enemies.isEmpty()) {
                // Si se han generado todos los enemigos y no queda ninguno, la oleada ha terminado
                waveInProgress = false;
                currentWave++;
                enemiesInWave = 5 + (currentWave * 2); // Aumentar dificultad
            }
        }
    }
    
    private void startNewWave() {
        waveInProgress = true;
        enemiesSpawned = 0;
        System.out.println("¡Comienza la oleada " + currentWave + "!");
    }
    
    private void spawnEnemy() {
        Enemy enemy = new Enemy(assetManager, path.getWaypoints());
        
        // Ajustar la salud del enemigo según la oleada actual
        int baseHealth = 100;
        int waveBonus = (currentWave - 1) * 20; // +20 de salud por cada oleada
        int healthForThisEnemy = baseHealth + waveBonus;
        enemy.setMaxHealth(healthForThisEnemy);
        
        enemies.add(enemy);
        rootNode.attachChild(enemy);
        enemiesSpawned++;
        
        System.out.println("Enemigo generado con " + healthForThisEnemy + " de salud");
    }
}
