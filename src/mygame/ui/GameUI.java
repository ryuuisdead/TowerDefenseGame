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

/**
 * Clase para gestionar los elementos de la interfaz de usuario
 */
public class GameUI {
    
    private Node guiNode;
    private AssetManager assetManager;
    
    // Elementos de la UI
    private BitmapText moneyText;
    private BitmapText scoreText;
    private BitmapText waveText;
    
    // Elementos para selección de torres
    private Node towerSelectionPanel;
    private BitmapText[] towerButtons;
    private BitmapText towerInfoText;
    
    // Torre seleccionada
    private TowerType selectedTowerType = TowerType.BASIC;
    
    public GameUI(Node guiNode, AssetManager assetManager) {
        this.guiNode = guiNode;
        this.assetManager = assetManager;
        
        initUI();
        createTowerSelectionPanel();
    }
    
    private void initUI() {
        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        
        // Texto para mostrar el dinero
        moneyText = new BitmapText(guiFont, false);
        moneyText.setSize(guiFont.getCharSet().getRenderedSize() * 1.5f);
        moneyText.setColor(new ColorRGBA(1f, 0.9f, 0.1f, 1f)); // Color dorado para el dinero
        moneyText.setText("Dinero: $100");
        moneyText.setLocalTranslation(20, moneyText.getLineHeight() + 450, 0);
        guiNode.attachChild(moneyText);
        
        // Texto para mostrar la puntuación
        scoreText = new BitmapText(guiFont, false);
        scoreText.setSize(guiFont.getCharSet().getRenderedSize() * 1.5f);
        scoreText.setColor(new ColorRGBA(0.3f, 1f, 0.3f, 1f)); // Color verde para la puntuación
        scoreText.setText("Puntuación: 0");
        scoreText.setLocalTranslation(20, moneyText.getLocalTranslation().y + scoreText.getLineHeight() + 10, 0);
        guiNode.attachChild(scoreText);
        
        // Texto para mostrar la oleada actual
        waveText = new BitmapText(guiFont, false);
        waveText.setSize(guiFont.getCharSet().getRenderedSize() * 1.5f);
        waveText.setColor(new ColorRGBA(1f, 0.5f, 0.5f, 1f)); // Color rojo claro
        waveText.setText("Oleada: 1");
        waveText.setLocalTranslation(20, scoreText.getLocalTranslation().y + waveText.getLineHeight() + 10, 0);
        guiNode.attachChild(waveText);
    }
    
    private void createTowerSelectionPanel() {
        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        
        // Panel principal
        towerSelectionPanel = new Node("TowerSelectionPanel");
        
        // Título del panel - Mover a la esquina inferior derecha
        BitmapText title = new BitmapText(guiFont, false);
        title.setSize(guiFont.getCharSet().getRenderedSize() * 1.2f);
        title.setColor(ColorRGBA.Yellow);
        title.setText("Seleccionar Torre:");
        title.setLocalTranslation(600, 210, 0); // Cambiar de 20 a 600 para mover a la derecha
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
            button.setLocalTranslation(600, 180 - (i * 25), 0); // Cambiar de 20 a 600
            
            towerButtons[i] = button;
            towerSelectionPanel.attachChild(button);
        }
        
        // Información de la torre seleccionada
        towerInfoText = new BitmapText(guiFont, false);
        towerInfoText.setSize(guiFont.getCharSet().getRenderedSize() * 0.8f);
        towerInfoText.setColor(new ColorRGBA(0.8f, 0.8f, 1f, 1f));
        updateTowerInfo();
        towerInfoText.setLocalTranslation(600, 102, 0); // Cambiar de 20 a 600
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
    public void update(int money, int score, int currentWave, boolean waveInProgress, float timeToNextWave) {
        moneyText.setText("Dinero: $" + money);
        scoreText.setText("Puntuación: " + score);
        
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
    public void showGameOverMessage(int score, int wave) {
        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        
        // Panel para Game Over
        Node gameOverPanel = new Node("GameOverPanel");
        
        // Título
        BitmapText titleLabel = new BitmapText(guiFont, false);
        titleLabel.setSize(guiFont.getCharSet().getRenderedSize() * 3f); // texto grande
        titleLabel.setColor(ColorRGBA.Red);
        titleLabel.setText("¡GAME OVER!");
        titleLabel.setLocalTranslation(300, 400, 0);
        gameOverPanel.attachChild(titleLabel);
        
        // Información
        BitmapText infoLabel = new BitmapText(guiFont, false);
        infoLabel.setSize(guiFont.getCharSet().getRenderedSize() * 1.5f);
        infoLabel.setText("Han escapado demasiados demonios.");
        infoLabel.setLocalTranslation(250, 350, 0);
        gameOverPanel.attachChild(infoLabel);
        
        // Puntuación
        BitmapText scoreLabel = new BitmapText(guiFont, false);
        scoreLabel.setSize(guiFont.getCharSet().getRenderedSize() * 1.5f);
        scoreLabel.setText("Puntuación final: " + score);
        scoreLabel.setLocalTranslation(300, 300, 0);
        gameOverPanel.attachChild(scoreLabel);
        
        // Oleada
        BitmapText waveLabel = new BitmapText(guiFont, false);
        waveLabel.setSize(guiFont.getCharSet().getRenderedSize() * 1.5f);
        waveLabel.setText("Oleada alcanzada: " + wave);
        waveLabel.setLocalTranslation(300, 250, 0);
        gameOverPanel.attachChild(waveLabel);
        
        // Añadir al nodo GUI
        guiNode.attachChild(gameOverPanel);
    }
}
