package mygame.enemies;

import com.jme3.math.ColorRGBA;

public enum EnemyType {
    BASIC("Zombie", 15, 0.7f, "Models/Zombie Agonizing/ZombieAgonizing.j3o", 3, ColorRGBA.LightGray),
    HELLHOUND("Perro Infernal", 25, 0.8f, "Models/helishdog/helishdog.j3o", 5, ColorRGBA.Red),
    TANK("Monstruo Tanque", 80, 0.4f, "Models/future monster_low/future monster_low.j3o", 7, ColorRGBA.Blue);
    // Quitar "assets\" de las rutas, JME3 ya asume que los recursos están en la carpeta assets
    
    private final String name;
    private final int health;
    private final float speed;
    private final String modelPath;
    private final int reward;
    private final ColorRGBA color; // Color para cuando se usa un cubo en lugar del modelo
    
    EnemyType(String name, int health, float speed, String modelPath, int reward, ColorRGBA color) {
        this.name = name;
        this.health = health;
        this.speed = speed;
        this.modelPath = modelPath;
        this.reward = reward;
        this.color = color;
    }
    
    // Getters
    public String getName() { return name; }
    public int getHealth() { return health; }
    public float getSpeed() { return speed; }
    public String getModelPath() { return modelPath; }
    public int getReward() { return reward; }
    public ColorRGBA getColor() { return color; }
}