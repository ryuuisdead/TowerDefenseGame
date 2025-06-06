package mygame.towers;

import com.jme3.math.ColorRGBA;

public enum TowerType {
    BASIC("Torre Básica", 30, 3, 2.5f, 1.0f, ColorRGBA.Blue, ColorRGBA.Cyan, "Disparo estándar", 
          new int[]{5, 8}, // Daño mejorado por nivel
          new float[]{1.1f, 1.3f}, // Multiplicador de cadencia por nivel
          new int[]{15, 25}, // Costo de mejora por nivel
          new float[]{3.0f, 3.5f}, // Rango mejorado por nivel (opcional)
          "Sounds/Towers/basic.wav"), // Sonido de disparo
          
    SNIPER("Torre Francotirador", 50, 4, 4.0f, 0.6f, ColorRGBA.Gray, ColorRGBA.Red, "Alto daño, baja cadencia",
           new int[]{8, 15}, // Daño mejorado por nivel
           new float[]{0.7f, 0.8f}, // Multiplicador de cadencia por nivel
           new int[]{35, 45}, // Costo de mejora por nivel
           new float[]{5.0f, 6.5f}, // Rango mejorado por nivel (opcional)
           "Sounds/Towers/sniper.wav"), // Sonido de disparo
    RAPID("Torre Rápida", 65, 2, 2.5f, 1.5f, ColorRGBA.Green, ColorRGBA.Yellow, "Disparo rápido de bajo daño",
          new int[]{3, 5}, // Daño mejorado por nivel
          new float[]{1.7f, 2.0f}, // Multiplicador de cadencia por nivel
          new int[]{45, 50}, // Costo de mejora por nivel
          new float[]{2.8f, 3.0f}, // Rango mejorado por nivel (opcional)
          "Sounds/Towers/rapid.wav"); // Sonido de disparo
    
    private final String name;
    private final int cost;
    private final int damage;
    private final float range;
    private final float fireRate;
    private final ColorRGBA baseColor;
    private final ColorRGBA topColor;
    private final String description;
    private final String soundPath; // Ruta del sonido de disparo
    
    // Nuevos atributos para mejoras (por nivel)
    private final int[] upgradedDamage;      // Daño mejorado por nivel
    private final float[] upgradedFireRate;  // Multiplicador de cadencia por nivel
    private final int[] upgradeCost;         // Costo de mejora por nivel
    private final float[] upgradedRange;      // Rango mejorado por nivel (opcional)
      TowerType(String name, int cost, int damage, float range, float fireRate, 
              ColorRGBA baseColor, ColorRGBA topColor, String description,
              int[] upgradedDamage, float[] upgradedFireRate, int[] upgradeCost, float[] upgradedRange,
              String soundPath) {
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
        this.upgradedRange = upgradedRange;
        this.soundPath = soundPath;
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

    public float getUpgradedRange(int level) {
        if (level <= 0 || level > upgradedRange.length) return range;
        return upgradedRange[level-1];
    }
    
    public int getMaxUpgradeLevel() {
        return upgradeCost.length;
    }

    public String getSoundPath() {
        return soundPath;
    }
}