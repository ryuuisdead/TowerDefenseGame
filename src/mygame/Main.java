package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResults;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import java.util.ArrayList;
import java.util.List;
import mygame.enemies.Enemy;
import mygame.enemies.EnemyType;
import mygame.map.GameMap;
import mygame.map.Path;
import mygame.towers.Tower;
import mygame.towers.TowerType;
import mygame.ui.GameUI;
import com.jme3.material.Material;
import com.jme3.scene.Node;
import com.jme3.math.ColorRGBA;
import com.jme3.texture.Texture;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioData.DataType;
import mygame.menu.MenuState;
import com.jme3.material.RenderState;
import com.jme3.scene.shape.Quad;

public class Main extends SimpleApplication {

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
    private int money = 100;
    private int score = 0;
    
    // Constantes para la torre
    private static final int TOWER_COST = 50; // Coste de construir una torre
    private static final String PLACE_TOWER = "PlaceTower"; // Nombre de la acción
    
    // Indicador visual de colocación de torre
    private Geometry towerPlacementIndicator;
    private boolean isValidPlacement = false;
    private Geometry towerIndicator; // Indicador de tipo de torre
    
    // Portal y sistema de game over
    private Spatial portal;
    private int escapedDemons = 0;
    private final int MAX_ESCAPED_DEMONS = 5;
    
    private Tower selectedTower = null; // Torre seleccionada para mejorar
    
    // Estado del menú
    private MenuState menuState;
    private boolean gameStarted = false;
    
    // Audio del juego
    private AudioNode gameMusic;

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
        setDisplayStatView(false);
        setDisplayFps(false);
        
        // Inicializar el menú en lugar del juego directamente
        initMenu();
    }
    
    private void initMenu() {
        // Crear y añadir el estado del menú
        menuState = new MenuState(() -> startGame());
        stateManager.attach(menuState);
        
        // Configurar cámara para el menú
        cam.setLocation(new Vector3f(0, 0, 10));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        
        // Mostrar el cursor y desactivar el FlyCam para interactuar con el menú
        inputManager.setCursorVisible(true);
        flyCam.setEnabled(false);
    }
    
    /**
     * Método que se llama cuando el jugador presiona el botón "Iniciar Juego"
     */
    private void startGame() {
        // Eliminar el menú
        stateManager.detach(menuState);
        
        // Inicializar el juego
        initGame();
        
        // Marcar el juego como iniciado
        gameStarted = true;
        
        // Mantener el cursor visible para el juego
        inputManager.setCursorVisible(true);
        
        System.out.println("¡Juego iniciado!");
    }
      /**
     * Inicializa todos los componentes del juego
     */    private void initGame() {
        // Iniciar la música del juego
        setupGameMusic();
          // Crear mapa/terreno con camino visible
        gameMap = new GameMap(assetManager);
        rootNode.attachChild(gameMap);
        
        // Crear instancia del camino para la lógica del juego
        path = new Path();
        
        // Configurar cámara isométrica fija para visualizar mejor el mapa completo
        cam.setLocation(new Vector3f(-12, 10, 8));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        flyCam.setEnabled(false);
        flyCam.setMoveSpeed(15f);
        
        // Crear portal en el punto final del recorrido
        createPortal();
        
        // Inicializar la interfaz de usuario
        gameUI = new GameUI(guiNode, assetManager, settings);
        
        // Configurar inputs
        setupInputs();
        
        // Crear indicador de colocación de torres
        createTowerPlacementIndicator();
    }
    
    private void setupInputs() {
        // Registrar acción para colocar torres
        inputManager.addMapping(PLACE_TOWER, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(actionListener, PLACE_TOWER);
        
        // Mapeo de teclas para seleccionar torres
        inputManager.addMapping("SelectTower1", new KeyTrigger(com.jme3.input.KeyInput.KEY_1));
        inputManager.addMapping("SelectTower2", new KeyTrigger(com.jme3.input.KeyInput.KEY_2));
        inputManager.addMapping("SelectTower3", new KeyTrigger(com.jme3.input.KeyInput.KEY_3));
        
        // Añadir mapeo de teclas para mejoras
        inputManager.addMapping("UpgradeTower", new KeyTrigger(com.jme3.input.KeyInput.KEY_U));
        inputManager.addListener(actionListener, "UpgradeTower");
        
        // Añadir mapeo para seleccionar torres existentes
        inputManager.addMapping("SelectTower", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addListener(actionListener, "SelectTower");
        
        // Cambiar DeleteTower para usar la tecla E
        inputManager.addMapping("DeleteTower", new KeyTrigger(com.jme3.input.KeyInput.KEY_E));
        inputManager.addListener(actionListener, "DeleteTower");
        
        // Listener para acciones
        inputManager.addListener(actionListener, "PlaceTower", "SelectTower1", "SelectTower2", "SelectTower3");
    }
    
    private void createTowerPlacementIndicator() {
        // Crear un indicador semitransparente para mostrar dónde se colocará la torre
        towerPlacementIndicator = Tower.createIndicator(assetManager, TowerType.BASIC);
        towerPlacementIndicator.setCullHint(Spatial.CullHint.Always); // Oculto por defecto
        rootNode.attachChild(towerPlacementIndicator);
    }
    
    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (isPressed) {
                switch (name) {
                    case "PlaceTower":
                        placeTowerAtCursor();
                        break;
                    case "SelectTower1":
                        gameUI.selectTowerType(TowerType.BASIC);
                        updateTowerIndicator();
                        break;
                    case "SelectTower2":
                        gameUI.selectTowerType(TowerType.SNIPER);
                        updateTowerIndicator();
                        break;
                    case "SelectTower3":
                        gameUI.selectTowerType(TowerType.RAPID);
                        updateTowerIndicator();
                        break;
                    case "SelectTower":
                        selectTowerAtCursor();
                        break;
                    case "UpgradeTower":
                        upgradeTower();
                        break;
                    case "DeleteTower":
                        deleteTower();
                        break;
                }
            }
        }
    };

    // Método para actualizar el indicador de torre
    private void updateTowerIndicator() {
        // Eliminar indicador actual
        if (towerIndicator != null) {
            guiNode.detachChild(towerIndicator);
        }
        
        // Crear nuevo indicador
        TowerType selectedType = gameUI.getSelectedTowerType();
        towerIndicator = Tower.createIndicator(assetManager, selectedType);
        
        // Posicionar en una esquina de la pantalla (coordenadas 2D)
        towerIndicator.setLocalTranslation(550, 80, 0);
        
        // Ajustar escala para que se vea bien en la GUI
        towerIndicator.setLocalScale(50);
        
        guiNode.attachChild(towerIndicator);
    }

    private void placeTowerAtCursor() {
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
            
            // Comprobar si es una posición válida para colocar una torre
            if (isValidTowerPosition(contactPoint)) {
                // Obtener el tipo de torre seleccionado
                TowerType selectedType = gameUI.getSelectedTowerType();
                
                // Verificar si hay suficiente dinero
                if (money < selectedType.getCost()) {
                    System.out.println("¡No hay suficiente dinero para construir la torre!");
                    return;
                }
                
                // Crear torre del tipo seleccionado
                Tower newTower = new Tower(assetManager, gridPos, selectedType);
                towers.add(newTower);
                rootNode.attachChild(newTower);
                
                // Reducir el dinero del jugador
                money -= selectedType.getCost();
                gameUI.updateMoney(money);
                
                System.out.println("Torre colocada en " + gridPos + " del tipo " + selectedType + ". Dinero restante: " + money);
            } else {
                if (money < TOWER_COST) {
                    System.out.println("No tienes suficiente dinero para construir una torre. Necesitas: " + TOWER_COST);
                } else {
                    System.out.println("No se puede colocar una torre en esta posición.");
                }
            }
        }
    }    // Método para validar la posición de una torre
    private boolean isValidTowerPosition(Vector3f position) {
        // Redondear a la posición de la cuadrícula
        int gridX = Math.round(position.x);
        int gridZ = Math.round(position.z);
        
        // Verificar si está en los límites del mapa
        if (gridX < -9 || gridX > 9 || gridZ < -9 || gridZ > 9) {
            return false;
        }
        
        // Verificar si es un spot válido para torres
        if (!path.isValidTowerSpot(gridX, gridZ)) {
            return false;
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
        // Solo actualizar la lógica del juego si el juego ha comenzado
        if (!gameStarted) {
            return;
        }
        
        // Gestión de oleadas
        manageWaves(tpf);
        
        // Actualizar enemigos
        List<Enemy> deadEnemies = new ArrayList<>();
        for (Enemy e : enemies) {
            if (e.isAlive()) {
                e.update(tpf);
                
                // Verificar si el enemigo ha llegado al final del camino
                if (e.hasFinishedPath()) {
                    handleEnemyEscape(e);
                    deadEnemies.add(e);
                }
            } else {
                deadEnemies.add(e);
                
                // Dar recompensas por enemigo derrotado
                money += e.getReward();
                score += e.getType() == EnemyType.HELLHOUND ? 25 : 10;
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
            TowerType selectedType = gameUI.getSelectedTowerType();
            isValidPlacement = isValidTowerPosition(contactPoint) && money >= selectedType.getCost();
            
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
                
                // Calcular el intervalo de spawn basado en la oleada
                float spawnInterval;
                if (currentWave >= 10) {
                    // A partir de la oleada 10: intervalo aleatorio entre 0.5 y 1 segundo
                    spawnInterval = 0.5f + FastMath.nextRandomFloat() * 0.5f;
                } else if (currentWave >= 6) {
                    // Entre oleada 6-9: intervalo decrece linealmente de 1.5 a 1.0
                    float progress = (currentWave - 6) / 4.0f; // 0.0 a 1.0
                    spawnInterval = 1.5f - (0.5f * progress);
                } else {
                    // Oleadas 1-5: intervalo fijo de 1.5 segundos
                    spawnInterval = 1.5f;
                }
                
                if (spawnTimer >= spawnInterval) {
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
    
    /**
     * Crea y añade un nuevo enemigo a la escena
     */
    private void spawnEnemy() {
        // Seleccionar tipo de enemigo según la oleada actual y probabilidad
        EnemyType enemyType;
        float random = FastMath.nextRandomFloat();
        float mejora = 0;          if (currentWave >= 6) {
            // A partir de la oleada 6, tanques con probabilidad creciente
            float tankChance = Math.min(0.60f, 0.10f + (currentWave - 6) * 0.10f); // Aumenta 10% por ronda, máximo 60%
            float hellhoundChance = 0.20f; // Probabilidad fija de perros infernales
            
            if (random < tankChance) { // Probabilidad creciente de tanques
                enemyType = EnemyType.TANK;
            } else if (random < (tankChance + hellhoundChance)) { // 20% probabilidad de hellhound
                enemyType = EnemyType.HELLHOUND;
            } else { // Resto son zombies básicos
                enemyType = EnemyType.BASIC;
            }
        } else if (currentWave >= 2) {
            // Oleadas 2-5: probabilidad creciente de perros infernales
            float hellhoundChance = Math.min(0.60f, 0.10f + (currentWave - 2) * 0.125f); // 10%, 22.5%, 35%, 47.5%, 60%
            
            if (random < hellhoundChance) {
                enemyType = EnemyType.HELLHOUND;
            } else {
                enemyType = EnemyType.BASIC;
            }
        } else {
            // Oleada inicial: solo zombies básicos
            enemyType = EnemyType.BASIC;
        }// Crear el enemigo con el tipo seleccionado
        Enemy enemy = new Enemy(assetManager, path.getWaypoints(), enemyType);
        
        if (currentWave >= 2) {
            if (currentWave <= 6) {
                // Mejora lineal de la ronda 2 a 6 (*1, *2, *3, *4, *5)
                mejora = 0.2f * (currentWave - 1); // 0.2, 0.4, 0.6, 0.8, 1.0
            } else {
                mejora = 1.6f * (float)Math.pow(1.25, currentWave - 6);
            } 
            enemy.upgradeStats(mejora);
        }

        // Colocar el enemigo en el inicio del camino (primer waypoint)
        enemy.setLocalTranslation(path.getWaypoints().get(0));
        
        // Añadir a la escena
        enemies.add(enemy);
        rootNode.attachChild(enemy);
        
        // Actualizar contador
        enemiesSpawned++;
        
        // Informar sobre el tipo de enemigo generado
        System.out.println("Generado enemigo tipo: " + enemyType.getName());
    }
    
    // Crear el portal en el punto final del camino
    private void createPortal() {
        try {
            System.out.println("Intentando cargar modelo de portal...");
            
            // Cargar el modelo del portal
            portal = assetManager.loadModel("Models/portal frame game ready/portal frame game ready.j3o");
            
            // Obtener la última posición del camino (destino de los enemigos)
            List<Vector3f> waypoints = path.getWaypoints();
            Vector3f portalPosition = waypoints.get(waypoints.size() - 1);
            
            System.out.println("Modelo de portal cargado. Posicionando en: " + portalPosition);
            
            // Ajustar la escala y posición del portal
            portal.setLocalScale(1.5f);
            
            // Posicionar el portal al final del camino, ligeramente elevado
            portal.setLocalTranslation(portalPosition.x, 0.74f, portalPosition.z);
            
            // Rotar el portal con un Quaternion para mayor precisión
            com.jme3.math.Quaternion rotation = new com.jme3.math.Quaternion();
            rotation.fromAngles(0, -FastMath.PI * 2.5f, 0);  // 1.5π = 270 grados
            portal.setLocalRotation(rotation);
            
            // Aplicar texturas al modelo del portal
            applyPortalTextures(portal);
            
            // Añadir el portal a la escena
            rootNode.attachChild(portal);
            System.out.println("Portal añadido a la escena correctamente");
            
        } catch (Exception e) {
            System.out.println("Error al cargar el modelo del portal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Aplica colores sólidos a las diferentes partes del portal
     */
    private void applyPortalTextures(Spatial portalModel) {
        if (!(portalModel instanceof Node)) {
            return;
        }
        
        Node portalNode = (Node) portalModel;
        
        try {
            // Cargar texturas
            Texture frameMetalAlbedo = assetManager.loadTexture("Textures/portal_textures/frame_metal_albedo.png");
            Texture portalAlbedo = assetManager.loadTexture("Textures/portal_textures/portal_albedo.png");
            Texture mossyBricksAlbedo = assetManager.loadTexture("Textures/portal_textures/mossy_bricks_albedo.png");
            Texture torchMetal = assetManager.loadTexture("Textures/portal_textures/torch_metal.png");
            Texture torchLight = assetManager.loadTexture("Textures/portal_textures/torch_light2.png");
            
            // Crear materiales texturizados
            Material frameMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            frameMaterial.setTexture("ColorMap", frameMetalAlbedo);
            frameMaterial.setColor("Color", new ColorRGBA(0.4f, 0.4f, 0.5f, 1.0f));
            
            // Mejorar el material del portal central con color más intenso
            Material portalMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            portalMaterial.setTexture("ColorMap", portalAlbedo);
            portalMaterial.setColor("Color", new ColorRGBA(0.8f, 0.2f, 0.9f, 0.7f));
            portalMaterial.getAdditionalRenderState().setBlendMode(com.jme3.material.RenderState.BlendMode.Alpha);
            
            Material brickMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            brickMaterial.setTexture("ColorMap", mossyBricksAlbedo);
            
            Material torchBaseMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            torchBaseMaterial.setTexture("ColorMap", torchMetal);
            
            Material torchLightMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            torchLightMaterial.setTexture("ColorMap", torchLight);
            torchLightMaterial.setColor("Color", new ColorRGBA(1.0f, 0.6f, 0.0f, 1.0f));
            
            // Matriz de materiales para aplicar por orden
            Material[] materials = {
                brickMaterial,    // geom-0: Base/piedras
                frameMaterial,    // geom-1: Marco metálico
                portalMaterial,   // geom-2: Portal central
                torchBaseMaterial,// geom-3: Antorcha base
                torchLightMaterial,// geom-4: Antorcha luz
                frameMaterial,    // geom-5: Marco superior
                brickMaterial,    // geom-6: Detalles de piedra
                torchBaseMaterial,// geom-7: Otra antorcha base
                torchLightMaterial,// geom-8: Otra antorcha luz
                brickMaterial     // geom-9: Más detalles de piedra
            };
            
            // Aplicar texturas específicas a cada geometría según su índice
            for (int i = 0; i < portalNode.getChildren().size(); i++) {
                if (i < materials.length) {
                    applyMaterialToSpatial(portalNode.getChild(i), materials[i]);
                } else {
                    // Para cualquier geometría adicional
                    applyMaterialToSpatial(portalNode.getChild(i), frameMaterial);
                }
            }
            
            // Asegurar que el portal central tenga el material correcto
            for (int i = 0; i < portalNode.getChildren().size(); i++) {
                String name = portalNode.getChild(i).getName().toLowerCase();
                if (name.contains("portal") || name.contains("energy") || name.contains("center") || i == 2) {
                    // Aplicar un material de portal mejorado
                    Material enhancedPortalMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                    enhancedPortalMaterial.setTexture("ColorMap", portalAlbedo);
                    enhancedPortalMaterial.setColor("Color", new ColorRGBA(0.8f, 0.2f, 0.9f, 0.7f));
                    enhancedPortalMaterial.getAdditionalRenderState().setBlendMode(com.jme3.material.RenderState.BlendMode.Alpha);
                    
                    applyMaterialToSpatial(portalNode.getChild(i), enhancedPortalMaterial);
                    System.out.println("Aplicado material de portal mejorado al nodo: " + portalNode.getChild(i).getName());
                }
            }
            
            System.out.println("Texturas aplicadas al portal con éxito");
            
        } catch (Exception e) {
            System.out.println("Error al aplicar texturas al portal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método para aplicar material a todas las geometrías en un spatial
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

    // Método para manejar cuando un enemigo escapa
    private void handleEnemyEscape(Enemy enemy) {
        // Incrementar contador de escapados
        escapedDemons++;
        
        System.out.println("¡Un " + enemy.getType().getName() + " ha escapado! Total: " + escapedDemons + "/" + MAX_ESCAPED_DEMONS);
        
        // Efecto visual en el portal (opcional)
        if (portal != null) {
            // Efecto de escala
            portal.setLocalScale(2.5f);
            
            // Programar retorno al tamaño normal
            java.util.Timer timer = new java.util.Timer();
            timer.schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    if (portal != null) {
                        portal.setLocalScale(2.0f);
                    }
                }
            }, 300);
        }
        
        // Verificar condición de game over
        if (escapedDemons >= MAX_ESCAPED_DEMONS) {
            gameOver();
        }
    }    // Método para manejar el game over
    private void gameOver() {
        System.out.println("=== GAME OVER ===");
        System.out.println("Han escapado " + escapedDemons + " demonios");
        System.out.println("Puntuación final: " + score);
        System.out.println("Oleada alcanzada: " + currentWave);
        
        // Detener la música del juego gradualmente
        if (gameMusic != null) {
            // Crear un hilo para bajar el volumen gradualmente
            new Thread(() -> {
                float volume = gameMusic.getVolume();
                while (volume > 0) {
                    volume -= 0.05f;
                    gameMusic.setVolume(volume);
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                gameMusic.stop();
                gameMusic = null;
            }).start();
        }
        
        // Mostrar mensaje de game over en la UI
        gameUI.showGameOverMessage(score, currentWave);
        
        // Detener la generación de oleadas
        waveInProgress = false;
    }

    private void selectTowerAtCursor() {
        // Resetear la torre seleccionada
        if (selectedTower != null) {
            selectedTower.removeHighlight();
        }
        selectedTower = null;
        
        // Obtener la posición del ratón
        Vector2f mousePos = inputManager.getCursorPosition();
        
        // Crear un rayo desde la cámara hacia la posición del ratón
        Vector3f worldCoordinates = cam.getWorldCoordinates(mousePos, 0f).clone();
        Vector3f dir = cam.getWorldCoordinates(mousePos, 1f).subtractLocal(worldCoordinates).normalizeLocal();
        Ray ray = new Ray(worldCoordinates, dir);
        
        // Verificar colisión con todas las torres
        float closestDistance = Float.MAX_VALUE;
        Tower closestTower = null;
        
        for (Tower tower : towers) {
            // Crear una colisión con un volumen alrededor de la torre
            com.jme3.bounding.BoundingSphere boundingSphere = 
                new com.jme3.bounding.BoundingSphere(1f, tower.getWorldTranslation());
            CollisionResults results = new CollisionResults();
            
            // Verificar colisión entre el rayo y la esfera
            boundingSphere.collideWith(ray, results);
            
            // Si hay colisión y es la más cercana hasta ahora
            if (results.size() > 0 && results.getClosestCollision().getDistance() < closestDistance) {
                closestDistance = results.getClosestCollision().getDistance();
                closestTower = tower;
            }
        }
        
        // Si encontramos una torre cercana, seleccionarla
        if (closestTower != null) {
            selectedTower = closestTower;
            
            // Mostrar información de la torre seleccionada
            showTowerInfo(selectedTower);
            
            // Añadir efecto visual para indicar selección
            highlightSelectedTower(selectedTower);
            
            System.out.println("Torre seleccionada: " + selectedTower.getTowerType().getName() + 
                              " (Nivel " + selectedTower.getLevel() + ")");
        } else {
            showTowerInfo(selectedTower);
            System.out.println("No se ha seleccionado ninguna torre");
        }
    }
    
    private void upgradeTower() {
        if (selectedTower == null) {
            System.out.println("Selecciona una torre para mejorar.");
            return;
        }
        
        // Verificar si la torre puede mejorarse
        if (!selectedTower.canUpgrade()) {
            System.out.println("Esta torre ya está al nivel máximo.");
            return;
        }
        
        // Obtener costo de la mejora
        int upgradeCost = selectedTower.getUpgradeCost();
        
        // Verificar si hay suficiente dinero
        if (money < upgradeCost) {
            System.out.println("No hay suficiente dinero para mejorar. Necesitas: " + upgradeCost);
            return;
        }
        
        // Realizar la mejora
        if (selectedTower.upgrade()) {
            // Reducir el dinero del jugador
            money -= upgradeCost;
            
            // Actualizar UI
            gameUI.updateMoney(money);
            
            // Actualizar la información mostrada
            showTowerInfo(selectedTower);
            
            System.out.println("¡Torre mejorada! Nivel actual: " + selectedTower.getLevel());
        }
    }
    
    private void showTowerInfo(Tower tower) {
        // Este método actualiza la UI para mostrar información de la torre seleccionada
        gameUI.showTowerInfo(tower);
    }
    
    // Método para destacar visualmente la torre seleccionada
    private void highlightSelectedTower(Tower tower) {
        // Eliminar cualquier destacado anterior
        for (Tower t : towers) {
            t.removeHighlight();
        }
        
        // Añadir destacado a la torre seleccionada
        tower.addHighlight(assetManager);
    }
    
    /**
     * Elimina la torre actualmente seleccionada y devuelve parte del costo
     */
    private void deleteTower() {
        if (selectedTower == null) {
            System.out.println("Ninguna torre seleccionada para eliminar.");
            return;
        }
        
        // Calcular el valor de reembolso (40% del costo total)
        int refundValue = (int)(selectedTower.getTotalInvestment() * 0.4f);
        
        // Eliminar la torre
        rootNode.detachChild(selectedTower);
        towers.remove(selectedTower);
        
        System.out.println("Torre eliminada. Reembolso: $" + refundValue);
        
        // Aumentar el dinero del jugador
        money += refundValue;
        
        // Actualizar la interfaz
        gameUI.updateMoney(money);
        gameUI.showTowerInfo(null); // Limpiar la información de la torre
        
        // Limpiar la selección
        selectedTower = null;
    }
    
    // Getter para GameMap (necesario para verificación en GameState)
    public GameMap getGameMap() {
        return gameMap;
    }
      /**
     * Configura y reproduce la música del juego
     */
    private void setupGameMusic() {
        try {
            // Detener música anterior si existe
            if (gameMusic != null) {
                gameMusic.stop();
                gameMusic = null;
            }
            
            // Verificar que el archivo de música existe
            if (assetManager.locateAsset(new com.jme3.asset.AssetKey("Sounds/Music/music_game.wav")) == null) {
                System.err.println("¡ADVERTENCIA! No se encuentra el archivo de música: Sounds/Music/music_game.wav");
                return;
            }
            
            // Configurar música del juego
            gameMusic = new AudioNode(assetManager, "Sounds/Music/music_game.wav", DataType.Buffer);
            gameMusic.setLooping(true);
            gameMusic.setPositional(false);
            gameMusic.setVolume(0.4f);
            
            // Reproducir música del juego
            gameMusic.play();
            System.out.println("Música del juego iniciada correctamente");
            
        } catch (Exception e) {
            System.err.println("Error al configurar la música del juego: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void createTowerSpotIndicators() {
        // Cargar la textura dirt.jpg
        Texture dirtTexture = assetManager.loadTexture("Textures/dirt.jpg");
        
        // Crear material con la textura
        Material spotMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        spotMaterial.setTexture("ColorMap", dirtTexture);
        spotMaterial.setColor("Color", new ColorRGBA(1f, 1f, 1f, 0.5f)); // Semi-transparente
        spotMaterial.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        
        // Obtener los spots válidos
        List<int[]> validSpots = path.getValidTowerSpots();
        
        // Crear un indicador para cada spot válido
        for (int[] spot : validSpots) {
            // Crear una geometría plana (quad) para el spot
            Quad quad = new Quad(0.8f, 0.8f); // Tamaño ligeramente menor que una unidad
            Geometry spotGeom = new Geometry("TowerSpot", quad);
            
            // Aplicar el material
            spotGeom.setMaterial(spotMaterial);
            
            // Rotar para que sea horizontal
            spotGeom.rotate(-FastMath.HALF_PI, 0, 0);
            
            // Posicionar en el spot
            spotGeom.setLocalTranslation(
                spot[0] - 0.4f, // Centrar el quad
                0.01f,         // Ligeramente sobre el suelo
                spot[1] + 0.4f  // Centrar el quad
            );
            
            // Añadir al rootNode
            rootNode.attachChild(spotGeom);
        }
    }
}
