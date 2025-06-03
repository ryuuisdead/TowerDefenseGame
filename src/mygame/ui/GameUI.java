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
    
    public GameUI(Node guiNode, AssetManager assetManager) {
        this.guiNode = guiNode;
        this.assetManager = assetManager;
        
        initUI();
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
    
    /**
     * Actualiza el texto de la interfaz con los valores actuales
     * @param money Dinero actual del jugador
     * @param score Puntuación actual
     * @param currentWave Número de oleada actual
     * @param waveInProgress Indica si hay una oleada en curso
     * @param timeToNextWave Tiempo hasta la próxima oleada (en segundos)
     */
    public void update(int money, int score, int currentWave, boolean waveInProgress, float timeToNextWave) {
        moneyText.setText("Dinero: $" + money);
        scoreText.setText("Puntuación: " + score);
        
        if (waveInProgress) {
            waveText.setText("Oleada: " + currentWave + " (en progreso)");
            waveText.setColor(new ColorRGBA(1f, 0.3f, 0.3f, 1f)); // Rojo más intenso durante las oleadas
        } else {
            int timeLeft = Math.round(timeToNextWave);
            waveText.setText("Oleada: " + currentWave + " (próxima en " + timeLeft + " s)");
            waveText.setColor(new ColorRGBA(1f, 0.5f, 0.5f, 1f)); // Color normal entre oleadas
        }
    }
}
