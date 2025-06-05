package mygame.menu;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.system.AppSettings;
import com.jme3.ui.Picture;

/**
 * Estado de aplicación que maneja el menú principal del juego
 */
public class MenuState extends AbstractAppState implements ActionListener {
    
    private SimpleApplication app;
    private Node guiNode;
    private AssetManager assetManager;
    private AppStateManager stateManager;
    private InputManager inputManager;
    
    // Nodos para los diferentes elementos del menú
    private Node menuNode = new Node("Menu");
    private Geometry startButton;
    private BitmapText titleText;
    private BitmapText startButtonText;
    
    // Audio
    private com.jme3.audio.AudioNode menuMusic;
    private com.jme3.audio.AudioNode startGameSound;
    
    // Callback para cuando se presiona el botón start
    private Runnable startGameCallback;

    public MenuState(Runnable startGameCallback) {
        this.startGameCallback = startGameCallback;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        
        this.app = (SimpleApplication) app;
        this.guiNode = this.app.getGuiNode();
        this.assetManager = app.getAssetManager();
        this.stateManager = stateManager;
        this.inputManager = app.getInputManager();
        
        // Configurar audio
        setupAudio();
        
        // IMPORTANTE: Establecer el viewport y configurar la cámara para la GUI
        this.app.getViewPort().setClearFlags(true, true, true);
        
        // Añadir el nodo del menú al GUI
        guiNode.attachChild(menuNode);
        
        // Configurar la entrada para capturar clics en el menú
        setupInput();
        
        // Crear y configurar los elementos visuales del menú
        createMenu();
    }
    
    private void setupInput() {
        // Registrar acción para el botón de inicio
        inputManager.addMapping("StartGame", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(this, "StartGame");
    }
    
    private void createMenu() {
        // Obtener las dimensiones de la pantalla
        AppSettings settings = app.getContext().getSettings();
        int width = settings.getWidth();
        int height = settings.getHeight();
        
        // Crear fondo del menú
        createMenuBackground(width, height);
        
        // Crear título del juego
        createTitle(width, height);
        
        // Crear botón de inicio
        createStartButton(width, height);
    }
    
    private void createMenuBackground(int width, int height) {
        // Usar Picture en lugar de Geometry con Quad
        com.jme3.ui.Picture pic = new com.jme3.ui.Picture("MenuBackground");
        pic.setImage(assetManager, "Textures/fondomenu.jpg", true);
        pic.setWidth(width);
        pic.setHeight(height);
        pic.setPosition(0, 0);
        
        menuNode.attachChild(pic);
        
        // En lugar de usar Picture para el overlay, usamos Geometry con un Quad
        // ya que Geometry sí soporta setColor()
        Quad overlayQuad = new Quad(width, height);
        Geometry overlay = new Geometry("MenuOverlay", overlayQuad);
        
        Material overlayMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        overlayMat.setColor("Color", new ColorRGBA(0.0f, 0.0f, 0.0f, 0.3f)); // Negro semitransparente
        overlayMat.getAdditionalRenderState().setBlendMode(com.jme3.material.RenderState.BlendMode.Alpha);
        overlay.setMaterial(overlayMat);
        
        // Posicionar encima del fondo pero detrás de otros elementos
        overlay.setLocalTranslation(0, 0, -0.5f);
        menuNode.attachChild(overlay);
    }
    
    private void createTitle(int width, int height) {
        // Crear texto para el título
        BitmapFont font = assetManager.loadFont("Interface/Fonts/Default.fnt");
        titleText = new BitmapText(font);
        titleText.setText("TOWER DEFENSE INFERNAL");
        titleText.setSize(font.getCharSet().getRenderedSize() * 2); // Duplicar tamaño
        titleText.setColor(new ColorRGBA(1f, 0.6f, 0.2f, 1f)); // Color naranja
        
        // Centrar horizontalmente y posicionar en la parte superior
        float titleWidth = titleText.getLineWidth();
        float titleX = (width - titleWidth) / 2;
        float titleY = height - 100;
        titleText.setLocalTranslation(titleX, titleY, 0);
        
        menuNode.attachChild(titleText);
    }      private void createStartButton(int width, int height) {        // Crear un botón usando Picture para la imagen
        com.jme3.ui.Picture startButtonPic = new com.jme3.ui.Picture("StartButton");
        
        // Cargar la textura y configurar sus parámetros
        com.jme3.texture.Texture2D tex = (com.jme3.texture.Texture2D) assetManager.loadTexture("Images/boton_iniciar.png");
        tex.setMinFilter(com.jme3.texture.Texture.MinFilter.BilinearNoMipMaps);
        tex.setMagFilter(com.jme3.texture.Texture.MagFilter.Bilinear);
        
        // Configurar el material para la mejor calidad
        Material buttonMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        buttonMat.setTexture("ColorMap", tex);
        buttonMat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        buttonMat.setColor("Color", new ColorRGBA(1f, 1f, 1f, 1f));
        
        startButtonPic.setMaterial(buttonMat);
          // Calcular el tamaño del botón manteniendo la proporción
        float originalAspectRatio = 1.0f; // Proporción original del botón (ancho/alto)
        float maxDesiredWidth = width * 0.40f; // 25% del ancho de la pantalla como máximo
        float minScreenDimension = Math.min(width, height);
        float buttonWidth = Math.min(maxDesiredWidth, minScreenDimension * 0.25f); // Usar la dimensión más pequeña de la pantalla
        float buttonHeight = buttonWidth / originalAspectRatio;
        
        // Definir área de colisión igual al área visible
        startButton = new Geometry("StartButtonGeometry", new Quad(buttonWidth, buttonHeight));
        
        startButtonPic.setWidth(buttonWidth);
        startButtonPic.setHeight(buttonHeight);
        
        // Centrar el botón horizontalmente y posicionarlo verticalmente
        float buttonX = (width - buttonWidth) / 2;
        float buttonY = height / 2 - buttonHeight / 2;
        startButtonPic.setPosition(buttonX, buttonY);
        startButtonPic.setLocalTranslation(buttonX, buttonY, 1);
        
        // Guardar la referencia del botón para poder usarla en otras partes
        startButton = new Geometry("StartButtonGeometry", new Quad(buttonWidth, buttonHeight));
        startButton.setLocalTranslation(buttonX, buttonY, 0);
        
        // Hacer el material del geometry invisible pero mantener la capacidad de detectar clicks
        Material invisibleMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        invisibleMat.setColor("Color", new ColorRGBA(0, 0, 0, 0));
        invisibleMat.getAdditionalRenderState().setBlendMode(com.jme3.material.RenderState.BlendMode.Alpha);
        startButton.setMaterial(invisibleMat);
        
        // Guardar referencia a la imagen para las animaciones
        startButton.setUserData("buttonPic", startButtonPic);
        
        menuNode.attachChild(startButtonPic);
        menuNode.attachChild(startButton);
    }
    
    private void setupAudio() {
        // Configurar música del menú
        menuMusic = new com.jme3.audio.AudioNode(assetManager, "Sounds/Music/menu_music.wav", false);
        menuMusic.setLooping(true);
        menuMusic.setPositional(false);
        menuMusic.setVolume(0.5f);
        
        // Configurar sonido de inicio
        startGameSound = new com.jme3.audio.AudioNode(assetManager, "Sounds/Misc/start_game.wav", false);
        startGameSound.setLooping(false);
        startGameSound.setPositional(false);
        startGameSound.setVolume(0.8f);
        
        // Reproducir música del menú
        menuMusic.play();
    }    @Override
    public void cleanup() {
        // Detener y limpiar audio al salir del menú
        if (menuMusic != null) {
            menuMusic.stop();
            menuMusic = null;
        }
        if (startGameSound != null) {
            startGameSound.stop();
            startGameSound = null;
        }
        
        // Limpiar recursos y desregistrar mapeos de entrada
        guiNode.detachChild(menuNode);
        inputManager.deleteMapping("StartGame");
        inputManager.removeListener(this);
        
        super.cleanup();
    }

    private void handleStartGame() {
        // Reproducir sonido de inicio
        startGameSound.playInstance();
        
        // Detener música del menú gradualmente
        app.enqueue(() -> {
            float volume = menuMusic.getVolume();
            while (volume > 0) {
                volume -= 0.1f;
                menuMusic.setVolume(volume);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            menuMusic.stop();
            return null;
        });
        
        // Iniciar el juego después de un pequeño retraso
        app.enqueue(() -> {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            if (startGameCallback != null) {
                startGameCallback.run();
            }
            return null;
        });
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals("StartGame") && isPressed) {
            System.out.println("Click detectado en: " + inputManager.getCursorPosition());
            
            // Verificar si el clic fue en el botón de inicio
            if (isClickOnButton()) {
                System.out.println("¡Botón de inicio presionado!");
                
                // Animar el botón al hacer clic
                animateButtonClick();
                
                // Manejar el inicio del juego con efectos de sonido
                handleStartGame();
            } else {
                System.out.println("Clic fuera del botón");
            }
        }
    }
    
    /**
     * Anima el botón de inicio cuando se hace clic.
     * Crea una secuencia de animación simple con cambio de color.
     */    private void animateButtonClick() {
        com.jme3.ui.Picture buttonPic = (com.jme3.ui.Picture) startButton.getUserData("buttonPic");
        if (buttonPic == null) return;
        
        // Crear un temporizador para manejar la animación
        java.util.Timer timer = new java.util.Timer();
        
        // Primera fase: reducir tamaño y cambiar opacidad
        app.enqueue(() -> {
            // Guardar la posición actual
            Vector3f pos = buttonPic.getLocalTranslation();
            
            // Aplicar escala
            startButton.setLocalScale(0.95f);
            buttonPic.setLocalScale(0.95f);
            
            // Restaurar posición Z
            buttonPic.setLocalTranslation(pos.x, pos.y, pos.z);
            
            // Aplicar transparencia
            Material mat = buttonPic.getMaterial();
            if (mat != null) {
                mat.setFloat("Alpha", 0.7f);
            }
            return null;
        });
        
        // Segunda fase: restaurar
        timer.schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                app.enqueue(() -> {
                    // Guardar la posición actual
                    Vector3f pos = buttonPic.getLocalTranslation();
                    
                    // Restaurar escala
                    startButton.setLocalScale(1.0f);
                    buttonPic.setLocalScale(1.0f);
                    
                    // Restaurar posición Z
                    buttonPic.setLocalTranslation(pos.x, pos.y, pos.z);
                    
                    // Restaurar opacidad
                    Material mat = buttonPic.getMaterial();
                    if (mat != null) {
                        mat.setFloat("Alpha", 1.0f);
                    }
                    return null;
                });
            }
        }, 150);
    }
    
    private boolean isClickOnButton() {
        // Obtener la posición del cursor
        Vector2f click2d = inputManager.getCursorPosition();
        float x = click2d.x;
        float y = click2d.y;
        
        // Obtener las coordenadas del botón
        float buttonX = startButton.getLocalTranslation().x;
        float buttonY = startButton.getLocalTranslation().y;
        float buttonWidth = ((Quad) startButton.getMesh()).getWidth();
        float buttonHeight = ((Quad) startButton.getMesh()).getHeight();
        
        // Verificar si el clic está dentro de los límites del botón
        // Nota: en jMonkeyEngine, las coordenadas Y crecen hacia arriba
        return (x >= buttonX && x <= buttonX + buttonWidth &&
                y >= buttonY && y <= buttonY + buttonHeight);
    }
    
    private boolean isHovering = false;    @Override
    public void update(float tpf) {
        super.update(tpf);
        
        // Verificar si el cursor está sobre el botón (efecto hover)
        if (isMouseOverButton()) {
            if (!isHovering) {
                isHovering = true;                // El cursor acaba de entrar en el botón
                System.out.println("Cursor sobre el botón: " + inputManager.getCursorPosition());                // El cursor acaba de entrar en el botón                isHovering = true;
                com.jme3.ui.Picture buttonPic = (com.jme3.ui.Picture) startButton.getUserData("buttonPic");                if (buttonPic != null) {
                    Material mat = buttonPic.getMaterial();
                    if (mat != null) {
                        mat.setColor("Color", new ColorRGBA(1f, 1f, 1f, 0.8f));
                    }
                }
            }
        } else if (isHovering) {
            isHovering = false;            // El cursor acaba de salir del botón
            System.out.println("Cursor fuera del botón: " + inputManager.getCursorPosition());            // El cursor acaba de salir del botón            isHovering = false;
            com.jme3.ui.Picture buttonPic = (com.jme3.ui.Picture) startButton.getUserData("buttonPic");            if (buttonPic != null) {
                Material mat = buttonPic.getMaterial();
                if (mat != null) {
                    mat.setColor("Color", new ColorRGBA(1f, 1f, 1f, 1.0f));
                }
            }
        }
    }

    /**
     * Verifica si el cursor está actualmente sobre el botón
     */    private boolean isMouseOverButton() {
        Vector2f cursorPos = inputManager.getCursorPosition();
        float x = cursorPos.x;
        float y = cursorPos.y;
        
        // Obtener las coordenadas del botón
        float buttonX = startButton.getLocalTranslation().x;
        float buttonY = startButton.getLocalTranslation().y;
        float buttonWidth = ((Quad) startButton.getMesh()).getWidth();
        float buttonHeight = ((Quad) startButton.getMesh()).getHeight();
        
        return (x >= buttonX && x <= buttonX + buttonWidth &&
                y >= buttonY && y <= buttonY + buttonHeight);
    }
}