/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame.ui;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import mygame.towers.TowerType;
import mygame.towers.Tower;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
/**
 * Clase para gestionar los elementos de la interfaz de usuario
 */
public class GameUI {
      private Node guiNode;
    private AssetManager assetManager;
    private float screenWidth;
    private float screenHeight;
    
    // Elementos de la UI
    private BitmapText moneyText;
    private BitmapText scoreText;
    private BitmapText waveText;
    private BitmapText portalLifeText; // NUEVO: Vida del portal
    
    // Elementos para selección de torres
    private Node towerSelectionPanel;
    private BitmapText[] towerButtons;
    private BitmapText towerInfoText;
    private BitmapText upgradeInfoText;
    
    // Torre seleccionada
    private TowerType selectedTowerType = TowerType.BASIC;
      public GameUI(Node guiNode, AssetManager assetManager, com.jme3.system.AppSettings settings) {
        this.guiNode = guiNode;
        this.assetManager = assetManager;
        this.screenWidth = settings.getWidth();
        this.screenHeight = settings.getHeight();
        initUI();
        createTowerSelectionPanel();
    }
    
    private void initUI() {
        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        // Texto para mostrar el dinero
        moneyText = new BitmapText(guiFont, false);
        moneyText.setSize(guiFont.getCharSet().getRenderedSize() * 1.5f);
        moneyText.setColor(new ColorRGBA(1f, 0.9f, 0.1f, 1f));
        moneyText.setText("Dinero: $100");
        moneyText.setLocalTranslation(20, screenHeight - 30, 0); // SIEMPRE A LA IZQUIERDA ARRIBA
        guiNode.attachChild(moneyText);

        // NUEVO: Texto para mostrar la vida del portal
        portalLifeText = new BitmapText(guiFont, false);
        portalLifeText.setSize(guiFont.getCharSet().getRenderedSize() * 1.5f);
        portalLifeText.setColor(new ColorRGBA(0.8f, 0.5f, 1f, 1f));
        portalLifeText.setText("Vida del portal: 5");
        portalLifeText.setLocalTranslation(20, screenHeight - 120, 0); // Debajo de la oleada
        guiNode.attachChild(portalLifeText);

        // Texto para mostrar la puntuación
        scoreText = new BitmapText(guiFont, false);
        scoreText.setSize(guiFont.getCharSet().getRenderedSize() * 1.5f);
        scoreText.setColor(new ColorRGBA(0.3f, 1f, 0.3f, 1f));
        scoreText.setText("Puntuación: 0");
        scoreText.setLocalTranslation(20, screenHeight - 60, 0); // SIEMPRE A LA IZQUIERDA
        guiNode.attachChild(scoreText);

        // Texto para mostrar la oleada actual
        waveText = new BitmapText(guiFont, false);
        waveText.setSize(guiFont.getCharSet().getRenderedSize() * 1.5f);
        waveText.setColor(new ColorRGBA(1f, 0.5f, 0.5f, 1f));
        waveText.setText("Oleada: 1");
        waveText.setLocalTranslation(20, screenHeight - 90, 0); // SIEMPRE A LA IZQUIERDA
        guiNode.attachChild(waveText);

        // Crear textos para información de torres y mejoras (esquina inferior izquierda)
        towerInfoText = new BitmapText(guiFont);
        towerInfoText.setSize(guiFont.getCharSet().getRenderedSize());
        towerInfoText.setLocalTranslation(20, 120, 0); // SIEMPRE A LA IZQUIERDA ABAJO
        guiNode.attachChild(towerInfoText);

        upgradeInfoText = new BitmapText(guiFont);
        upgradeInfoText.setSize(guiFont.getCharSet().getRenderedSize());
        upgradeInfoText.setLocalTranslation(20, 160, 0); // Subido para que siempre sea visible
        guiNode.attachChild(upgradeInfoText);

        // Ocultar inicialmente
        towerInfoText.setText("");
        upgradeInfoText.setText("");
    }
    
    private void createTowerSelectionPanel() {
        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        // Panel principal
        towerSelectionPanel = new Node("TowerSelectionPanel");
        // Título del panel - SIEMPRE A LA DERECHA ARRIBA
        BitmapText title = new BitmapText(guiFont, false);
        title.setSize(guiFont.getCharSet().getRenderedSize() * 1.2f);
        title.setColor(ColorRGBA.Yellow);
        title.setText("Seleccionar Torre:");
        float titleWidth = title.getLineWidth();
        title.setLocalTranslation(screenWidth - titleWidth - 20, screenHeight - 90, 0);
        towerSelectionPanel.attachChild(title);
        // Botones para cada tipo de torre
        TowerType[] types = TowerType.values();
        towerButtons = new BitmapText[types.length];
        for (int i = 0; i < types.length; i++) {
            TowerType type = types[i];
            BitmapText button = new BitmapText(guiFont, false);
            button.setSize(guiFont.getCharSet().getRenderedSize());
            // Color del botón según si está seleccionado
            if (type == selectedTowerType) {
                button.setColor(ColorRGBA.Green);
            } else {
                button.setColor(ColorRGBA.White);
            }
            button.setText("[" + (i+1) + "] " + type.getName() + " - $" + type.getCost());
            float buttonWidth = button.getLineWidth();
            button.setLocalTranslation(screenWidth - buttonWidth - 20, screenHeight - 120 - (i * 25), 0); // DERECHA
            towerButtons[i] = button;
            towerSelectionPanel.attachChild(button);
        }
        // Información de la torre seleccionada - SIEMPRE A LA DERECHA ABAJO
        towerInfoText = new BitmapText(guiFont, false);
        towerInfoText.setSize(guiFont.getCharSet().getRenderedSize() * 0.8f);
        towerInfoText.setColor(new ColorRGBA(0.8f, 0.8f, 1f, 1f));
        updateTowerInfo();
        float infoWidth = towerInfoText.getLineWidth();
        towerInfoText.setLocalTranslation(screenWidth - infoWidth - 20, 102, 0);
        towerSelectionPanel.attachChild(towerInfoText);
        guiNode.attachChild(towerSelectionPanel);
    }
    
    private void updateTowerInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Torre: ").append(selectedTowerType.getName()).append("\n");
        info.append("Costo: $").append(selectedTowerType.getCost()).append("\n");
        info.append("Damage: ").append(selectedTowerType.getDamage()).append("\n");
        info.append("Range: ").append(selectedTowerType.getRange()).append("\n");
        info.append("Cadencia: ").append(String.format("%.1f", selectedTowerType.getFireRate())).append("/s\n");
        info.append(selectedTowerType.getDescription());
        
        towerInfoText.setText(info.toString());
    }
    
    public void selectTowerType(TowerType type) {
        this.selectedTowerType = type;
        
        // Actualizar la UI
        for (int i = 0; i < towerButtons.length; i++) {
            if (TowerType.values()[i] == selectedTowerType) {
                towerButtons[i].setColor(ColorRGBA.Green);
            } else {
                towerButtons[i].setColor(ColorRGBA.White);
            }
        }
        
        // Actualizar descripción
        updateTowerInfo();
    }
    
    public TowerType getSelectedTowerType() {
        return selectedTowerType;
    }
    
    /**
     * Actualiza el texto de la interfaz con los valores actuales
     */
    public void update(int money, int score, int currentWave, boolean waveInProgress, float timeToNextWave, int portalLife) {
        moneyText.setText("Dinero: $" + money);
        scoreText.setText("Puntuación: " + score);
        portalLifeText.setText("Vida del portal: " + portalLife);
        if (waveInProgress) {
            waveText.setText("Oleada: " + currentWave + " (en progreso)");
            waveText.setColor(new ColorRGBA(1f, 0.3f, 0.3f, 1f));
        } else {
            int timeLeft = Math.round(timeToNextWave);
            waveText.setText("Oleada: " + currentWave + " (próxima en " + timeLeft + " s)");
            waveText.setColor(new ColorRGBA(1f, 0.5f, 0.5f, 1f));
        }
        // Actualizar disponibilidad de torres según el dinero
        for (int i = 0; i < towerButtons.length; i++) {
            TowerType type = TowerType.values()[i];
            if (money >= type.getCost()) {
                if (type == selectedTowerType) {
                    towerButtons[i].setColor(ColorRGBA.Green);
                } else {
                    towerButtons[i].setColor(ColorRGBA.White);
                }
            } else {
                towerButtons[i].setColor(ColorRGBA.Red); // No hay suficiente dinero
            }
        }
    }
    
    /**
     * Muestra un mensaje de Game Over en la pantalla
     * @param score Puntuación final del jugador
     * @param wave Oleada alcanzada
     */
    public void showGameOverMessage(int score, int wave, int highScore) {
        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        // Panel para Game Over
        Node gameOverPanel = new Node("GameOverPanel");
        float centerX = screenWidth / 2f;
        float startY = screenHeight / 2f + 120;
        float spacing = 45f;
        // Título
        BitmapText titleLabel = new BitmapText(guiFont, false);
        titleLabel.setSize(guiFont.getCharSet().getRenderedSize() * 3f); // texto grande
        titleLabel.setColor(ColorRGBA.Red);
        titleLabel.setText("¡GAME OVER!");
        titleLabel.setLocalTranslation(centerX - titleLabel.getLineWidth() / 2, startY, 0);
        gameOverPanel.attachChild(titleLabel);
        // Información
        BitmapText infoLabel = new BitmapText(guiFont, false);
        infoLabel.setSize(guiFont.getCharSet().getRenderedSize() * 1.5f);
        infoLabel.setText("Han escapado demasiados demonios.");
        infoLabel.setLocalTranslation(centerX - infoLabel.getLineWidth() / 2, startY - spacing, 0);
        gameOverPanel.attachChild(infoLabel);
        // Puntuación
        BitmapText scoreLabel = new BitmapText(guiFont, false);
        scoreLabel.setSize(guiFont.getCharSet().getRenderedSize() * 1.5f);
        scoreLabel.setText("Puntuación final: " + score);
        scoreLabel.setLocalTranslation(centerX - scoreLabel.getLineWidth() / 2, startY - spacing * 2, 0);
        gameOverPanel.attachChild(scoreLabel);
        // Oleada
        BitmapText waveLabel = new BitmapText(guiFont, false);
        waveLabel.setSize(guiFont.getCharSet().getRenderedSize() * 1.5f);
        waveLabel.setText("Oleada alcanzada: " + wave);
        waveLabel.setLocalTranslation(centerX - waveLabel.getLineWidth() / 2, startY - spacing * 3, 0);
        gameOverPanel.attachChild(waveLabel);
        // Highscore
        BitmapText highScoreLabel = new BitmapText(guiFont, false);
        highScoreLabel.setSize(guiFont.getCharSet().getRenderedSize() * 1.2f);
        highScoreLabel.setColor(ColorRGBA.Yellow);
        highScoreLabel.setText("TU RECORD ES DE: " + highScore);
        highScoreLabel.setLocalTranslation(centerX - highScoreLabel.getLineWidth() / 2, startY - spacing * 4, 0);
        gameOverPanel.attachChild(highScoreLabel);
        // Mensaje de reintentar
        BitmapText retryLabel = new BitmapText(guiFont, false);
        retryLabel.setSize(guiFont.getCharSet().getRenderedSize() * 1.2f);
        retryLabel.setColor(ColorRGBA.Yellow);
        retryLabel.setText("REINTENTAR - TECLA ESPACIO");
        retryLabel.setLocalTranslation(centerX - retryLabel.getLineWidth() / 2, startY - spacing * 5, 0);
        gameOverPanel.attachChild(retryLabel);
        // Mensaje de volver al menú
        BitmapText menuLabel = new BitmapText(guiFont, false);
        menuLabel.setSize(guiFont.getCharSet().getRenderedSize() * 1.2f);
        menuLabel.setColor(ColorRGBA.Orange);
        menuLabel.setText("Volver al menu principal - tecla 0 (cero)");
        menuLabel.setLocalTranslation(centerX - menuLabel.getLineWidth() / 2, startY - spacing * 6, 0);
        gameOverPanel.attachChild(menuLabel);
        // Añadir al nodo GUI
        guiNode.attachChild(gameOverPanel);
    }
    
    // Método para mostrar información de la torre seleccionada
    public void showTowerInfo(Tower tower) {
        if (tower == null) {
            towerInfoText.setText("");
            upgradeInfoText.setText("");
            return;
        }
        TowerType type = tower.getTowerType();
        int level = tower.getLevel();
        // Mostrar información básica de la torre
        towerInfoText.setText(String.format("%s (Nivel %d)\nDaño: %d\nVelocidad: %.2f\nAlcance: %.1f",
                                           type.getName(), level,
                                           tower.getDamage(),
                                           tower.getFireRate(),
                                           tower.getRange()));
        // Información de mejora y venta
        StringBuilder controlInfo = new StringBuilder();
        if (tower.canUpgrade()) {
            int upgradeCost = tower.getUpgradeCost();
            int nextLevel = level + 1;
            controlInfo.append(String.format("MEJORA (Presiona U)\nCosto: %d\nNivel %d: Daño +%d%%, Velocidad +%d%%\n",
                                             upgradeCost, nextLevel,
                                             (int)((type.getUpgradedDamage(nextLevel) - tower.getDamage()) * 100 / tower.getDamage()),
                                             (int)((type.getUpgradedFireRate(nextLevel) / tower.getFireRate() - 1) * 100)));
        } else {
            controlInfo.append("NIVEL MÁXIMO\n");
        }
        // Mostrar siempre la opción de eliminar/vender
        int refundValue = (int)(tower.getTotalInvestment() * 0.5f);
        controlInfo.append("\nELIMINAR TORRE (Presiona E)\n");
        controlInfo.append("Reembolso: $" + refundValue);
        upgradeInfoText.setText(controlInfo.toString());
    }
    
    // Método para actualizar el dinero
    public void updateMoney(int money) {
        moneyText.setText("Oro: " + money);
    }

    // NUEVO: Método para actualizar solo la vida del portal
    public void updatePortalLife(int portalLife) {
        if (portalLifeText != null) {
            portalLifeText.setText("Vida del portal: " + portalLife);
        }
    }
}
