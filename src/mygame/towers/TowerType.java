package mygame.towers;

import com.jme3.math.ColorRGBA;

public enum TowerType {
    BASIC("Torre Básica", 50, 40, 8.0f, 1.0f, ColorRGBA.Blue, ColorRGBA.Cyan, "Disparo estándar"),
    SNIPER("Torre Francotirador", 100, 80, 12.0f, 0.5f, ColorRGBA.Gray, ColorRGBA.Red, "Alto daño, baja cadencia"),
    RAPID("Torre Rápida", 75, 20, 6.0f, 3.0f, ColorRGBA.Green, ColorRGBA.Yellow, "Disparo rápido de bajo daño");
    
    private final String name;
    private final int cost;
    private final int damage;
    private final float range;
    private final float fireRate;
    private final ColorRGBA baseColor;
    private final ColorRGBA topColor;
    private final String description;
    
    TowerType(String name, int cost, int damage, float range, float fireRate, 
              ColorRGBA baseColor, ColorRGBA topColor, String description) {
        this.name = name;
        this.cost = cost;
        this.damage = damage;
        this.range = range;
        this.fireRate = fireRate;
        this.baseColor = baseColor;
        this.topColor = topColor;
        this.description = description;
    }
    
    // Getters
    public String getName() { return name; }
    public int getCost() { return cost; }
    public int getDamage() { return damage; }
    public float getRange() { return range; }
    public float getFireRate() { return fireRate; }
    public ColorRGBA getBaseColor() { return baseColor; }
    public ColorRGBA getTopColor() { return topColor; }
    public String getDescription() { return description; }
}