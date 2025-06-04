package mygame.towers;

import com.jme3.math.ColorRGBA;

public enum TowerType {
    BASIC("Torre Básica", 50, 40, 8.0f, 1.0f, ColorRGBA.Blue, ColorRGBA.Cyan, "Disparo estándar", 
          new int[]{60, 75}, new float[]{1.2f, 1.5f}, new int[]{50, 65}),
          
    SNIPER("Torre Francotirador", 100, 80, 12.0f, 0.5f, ColorRGBA.Gray, ColorRGBA.Red, "Alto daño, baja cadencia",
           new int[]{110, 150}, new float[]{1.3f, 1.5f}, new int[]{90, 120}),
           
    RAPID("Torre Rápida", 75, 20, 6.0f, 3.0f, ColorRGBA.Green, ColorRGBA.Yellow, "Disparo rápido de bajo daño",
          new int[]{25, 35}, new float[]{1.5f, 2.0f}, new int[]{80, 100});
    
    private final String name;
    private final int cost;
    private final int damage;
    private final float range;
    private final float fireRate;
    private final ColorRGBA baseColor;
    private final ColorRGBA topColor;
    private final String description;
    
    // Nuevos atributos para mejoras (por nivel)
    private final int[] upgradedDamage;      // Daño mejorado por nivel
    private final float[] upgradedFireRate;  // Multiplicador de cadencia por nivel
    private final int[] upgradeCost;         // Costo de mejora por nivel
    
    TowerType(String name, int cost, int damage, float range, float fireRate, 
              ColorRGBA baseColor, ColorRGBA topColor, String description,
              int[] upgradedDamage, float[] upgradedFireRate, int[] upgradeCost) {
        this.name = name;
        this.cost = cost;
        this.damage = damage;
        this.range = range;
        this.fireRate = fireRate;
        this.baseColor = baseColor;
        this.topColor = topColor;
        this.description = description;
        this.upgradedDamage = upgradedDamage;
        this.upgradedFireRate = upgradedFireRate;
        this.upgradeCost = upgradeCost;
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
    
    // Nuevos getters para mejoras
    public int getUpgradedDamage(int level) {
        if (level <= 0 || level > upgradedDamage.length) return damage;
        return upgradedDamage[level-1];
    }
    
    public float getUpgradedFireRate(int level) {
        if (level <= 0 || level > upgradedFireRate.length) return fireRate;
        return fireRate * upgradedFireRate[level-1];
    }
    
    public int getUpgradeCost(int level) {
        if (level <= 0 || level > upgradeCost.length) return 0;
        return upgradeCost[level-1];
    }
    
    public int getMaxUpgradeLevel() {
        return upgradeCost.length;
    }
}