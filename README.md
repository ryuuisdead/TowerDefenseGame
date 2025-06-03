# TowerDefenseGame# Tower Defense Infernal 🔥

Un juego de Tower Defense ambientado en un mundo infernal, desarrollado con JMonkeyEngine 3.

![Java](https://img.shields.io/badge/Java-8%2B-orange)
![JMonkeyEngine](https://img.shields.io/badge/JMonkeyEngine-3.6%2B-blue)
![License](https://img.shields.io/badge/License-MIT-green)

## 🎮 Características

- **Ambiente Infernal**: Terreno volcánico con caminos de lava brillante
- **Sistema de Torres**: Coloca torres estratégicamente para defender tu territorio
- **Enemigos Zombies**: Criaturas que siguen un camino predefinido en zigzag
- **Sistema de Oleadas**: Oleadas progresivas con dificultad creciente
- **Economía**: Sistema de dinero para comprar y mejorar torres
- **Gráficos 3D**: Modelos 3D, iluminación atmosférica y efectos visuales

## 🚀 Tecnologías Utilizadas

- **Java 8+**
- **JMonkeyEngine 3.6+**
- **NetBeans IDE** (recomendado)

## 📋 Requisitos del Sistema

- Java Development Kit (JDK) 8 o superior
- JMonkeyEngine 3.6+
- NetBeans IDE con plugin de JMonkeyEngine
- 2GB RAM mínimo
- Tarjeta gráfica compatible con OpenGL 2.0+

## 🛠️ Instalación y Ejecución

1. **Clonar el repositorio**
```bash
git clone https://github.com/TU_USUARIO/TowerDefenseGame.git
cd TowerDefenseGame
```

2. **Abrir en NetBeans**
   - Abre NetBeans IDE
   - File → Open Project
   - Selecciona la carpeta `TowerDefenseGame`

3. **Configurar JMonkeyEngine**
   - Asegúrate de tener el plugin de JME3 instalado
   - Verifica que las librerías estén correctamente vinculadas

4. **Ejecutar el juego**
   - Haz clic derecho en el proyecto → Run
   - O ejecuta directamente `src/mygame/Main.java`

## 🎯 Cómo Jugar

1. **Objetivo**: Evita que los enemigos zombies lleguen al final del camino de lava
2. **Colocar Torres**: 
   - Haz clic izquierdo en una posición verde válida
   - Las torres cuestan 50 monedas cada una
   - No puedes colocar torres en el camino (zona roja)
3. **Mecánicas**:
   - Cada enemigo eliminado te da dinero
   - Las oleadas aumentan en dificultad
   - Coloca torres estratégicamente para crear cruces de fuego

## 📁 Estructura del Proyecto

```
TowerDefenseGame/
├── src/
│   └── mygame/
│       ├── Main.java              # Clase principal y lógica del juego
│       ├── enemies/
│       │   └── Enemy.java         # Lógica de enemigos zombies
│       ├── towers/
│       │   └── Tower.java         # Sistema de torres y proyectiles
│       ├── map/
│       │   ├── GameMap.java       # Mapa infernal con decoraciones
│       │   └── Path.java          # Sistema de caminos y waypoints
│       └── ui/
│           └── GameUI.java        # Interfaz de usuario y HUD
├── assets/                        # Recursos del juego (texturas, modelos)
├── nbproject/                     # Configuración de NetBeans
├── build.xml                      # Script de construcción
└── README.md                      # Este archivo
```

## 🎨 Características Visuales

- **Terreno volcánico** con variaciones de color y altura
- **Camino de lava** brillante y contrastante
- **Decoraciones infernales**: pilares, rocas volcánicas, pozos de lava
- **Iluminación atmosférica** con tonos rojizos y naranjas
- **Efectos de partículas** en proyectiles y explosiones
- **Indicadores visuales** para colocación de torres

## 🔧 Desarrollo y Contribución

### Características Implementadas ✅
- [x] Mapa infernal procedural
- [x] Sistema de caminos con waypoints
- [x] Torres con sistema de disparo automático
- [x] IA de enemigos con pathfinding
- [x] Sistema de oleadas progresivas
- [x] Economía y sistema de compras
- [x] Interfaz de usuario funcional
- [x] Efectos visuales y ambientación

### Roadmap de Mejoras 🚧
- [ ] Diferentes tipos de torres (fuego, hielo, veneno)
- [ ] Variedades de enemigos (rápidos, tanques, voladores)
- [ ] Sistema de mejoras de torres
- [ ] Múltiples niveles/mapas
- [ ] Efectos de sonido y música
- [ ] Sistema de puntuaciones y récords
- [ ] Modo multijugador

### Para Contribuir
1. Fork el proyecto
2. Crea una rama (`git checkout -b feature/nueva-caracteristica`)
3. Commit tus cambios (`git commit -m 'Añadir nueva característica'`)
4. Push a la rama (`git push origin feature/nueva-caracteristica`)
5. Abre un Pull Request

## 🐛 Problemas Conocidos

- Ocasionalmente los proyectiles pueden atravesar enemigos muy rápidos
- La optimización puede mejorarse para mapas más grandes
- Algunos efectos visuales pueden variar según la tarjeta gráfica

## 📜 Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para más detalles.

## 👤 Autor

- **Tu Nombre** - [@TU_USUARIO](https://github.com/TU_USUARIO)

## 🙏 Reconocimientos

- [JMonkeyEngine Community](https://jmonkeyengine.org/) por el excelente motor de juego
- Inspiración en clásicos Tower Defense como Bloons TD y Defense Grid
- Texturas y efectos creados con herramientas open source

---

⭐ ¡Dale una estrella al proyecto si te gusta!