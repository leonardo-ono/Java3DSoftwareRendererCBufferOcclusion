package renderer.core;

import bsp3d.Node;
import bsp3d.Player;
import bsp3d.Triangle;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static renderer.core.Renderer.MatrixMode.*;
import renderer.math.Vec4;
import renderer.parser.wavefront.Obj;
import renderer.parser.wavefront.WavefrontParser;
import renderer.shader.GouraudShaderWithTexture;

/**
 *
 * @author leo
 */
public class ViewCanvas extends Canvas {
    
    private boolean running = false;
    private BufferStrategy bs;

    private Renderer renderer;
    private Thread thread;
    
    //private GouraudShader gouraudShader = new GouraudShader();
    private GouraudShaderWithTexture gouraudShader = new GouraudShaderWithTexture();
    
    private Node bspNode;
    private Player player = new Player();
    
    private Light light = new Light();
    
    private Font font = new Font("Arial", Font.BOLD, 20);
    private Font font2 = new Font("Arial", Font.BOLD, 30);
    
    public ViewCanvas() {
        setPreferredSize(new Dimension(800, 600));
        setSize(new Dimension(800, 600));
        addKeyListener(new KeyHandler());
    }
    
    public void init() {
        createBufferStrategy(2);
        bs = getBufferStrategy();
        
        if (1==0) {
            try {
                bspNode = Node.load(getClass().getResourceAsStream("/res/quake.preproc"));
            } catch (Exception ex) {
                Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
                System.exit(-1);
            }            
        }
        else {
            // load 3d model and preprocess 3d bsp tree
            try {
                WavefrontParser.load("/res/Doom_E1M1.obj", 50.0);
                //WavefrontParser.load("/res/test3.obj", 100.0);
                //WavefrontParser.load("/res/test4.obj", 1.0);
                //WavefrontParser.load("/res/test5.obj", 1.0);
                //WavefrontParser.load("/res/quake.obj", 100.0);

                System.out.println(WavefrontParser.obj.faces.size());
                
                List<Triangle> allFaces = new ArrayList<>();
                for (Obj obj : WavefrontParser.objs) {
                    allFaces.addAll(obj.faces);
                }
                
                bspNode = new Node();
                bspNode.preProcess(0, allFaces);
                

            } catch (Exception ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            }

//            try {
//                Node.save("d:/doom.preproc", bspNode);
//            } catch (Exception ex) {
//                Logger.getLogger(View.class.getName()).log(Level.SEVERE, null, ex);
//                System.exit(-1);
//            }
            
            System.out.println("ok");
        }
        
        renderer = new Renderer(800, 600);
        
        // Texture
        Image image = new Image("/res/brick.jpg");
        renderer.addTexture(image);
        
        // light
        light.diffuse.set(1, 1, 1, 1);
        renderer.addLight(light);

        renderer.setMatrixMode(PROJECTION);
        renderer.setPerspectiveProjection(Math.toRadians(90));
        
        renderer.setClipZNear(-1);
        //renderer.setClipZfar(-1000);
        
        //renderer.getColorBuffer().setBackgroundColor(getParent().getBackground());
        //renderer.getColorBuffer().setBackgroundColor(new Color(32, 64, 64));
                
        renderer.setMatrixMode(MODEL);
                
        running = true;
        thread = new Thread(new MainLoop());
        //thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
        
        MouseHandler mouseHandler = new MouseHandler();
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }
    
    private double lightX = -50;
    private double lightZ = 50;
    
    private double cameraMousePressedAngleX = 0;
    private double cameraMousePressedAngleY = 0;

    private double cameraTargetAngleX = 0;
    private double cameraTargetAngleY = 0;

    private double cameraAngleX = -0.199999999999933;
    private double cameraAngleY = 3.30000000000001;
    
    private Vec4 cameraPosition = new Vec4(-437.1725328110184, -7.9128236813055866, 358.1917371484425, 1.0);
    
    private void cameraMove(double d, double dAngle) {
        double s = Math.sin(cameraAngleY + dAngle);
        double c = Math.cos(cameraAngleY + dAngle);
        cameraPosition.x += (c * d);
        cameraPosition.z += (s * d);
        cameraPosition.y -= d * Math.sin(cameraAngleX);
        //System.out.println("camera position: " + cameraPosition);
        System.out.println("camera position: " + cameraAngleX + "," + cameraAngleY);
    }
    
    public void update() {
        if (Mouse.pressed) {
            cameraTargetAngleY = cameraMousePressedAngleY + 0.025 * (Mouse.x - Mouse.pressedX);
            cameraTargetAngleX = cameraMousePressedAngleX - 0.025 * (Mouse.y - Mouse.pressedY);

            cameraAngleX += ((cameraTargetAngleX) - cameraAngleX) * 0.25;
            cameraAngleY += ((cameraTargetAngleY) - cameraAngleY) * 0.25;
        }
        
        double speed = 1;
        
        if (Keyboard.keyDown[37]) {
            cameraMove(speed, Math.toRadians(90));
        }
        else if (Keyboard.keyDown[39]) {
            cameraMove(-speed, Math.toRadians(90));
        }
        
        if (Keyboard.keyDown[38]) {
            cameraMove(-speed, 0);
        }
        else if (Keyboard.keyDown[40]) {
            cameraMove(speed, 0);
        }

        if (Keyboard.keyDown[KeyEvent.VK_1]) {
            renderer.triangleRasterizer.setOcclusionEnabled(true);
        }
        if (Keyboard.keyDown[KeyEvent.VK_2]) {
            renderer.triangleRasterizer.setOcclusionEnabled(false);
        }

        if (Keyboard.keyDown[KeyEvent.VK_Q]) {
            cameraPosition.y -= speed;
        }
        else if (Keyboard.keyDown[KeyEvent.VK_A]) {
            cameraPosition.y += speed;
        }
        
        if (Keyboard.keyDown[87]) {
            lightZ += 5;
        }
        else if (Keyboard.keyDown[83]) {
            lightZ -= 5;
        }

        if (Keyboard.keyDown[65]) {
            lightX -= 5;
        }
        else if (Keyboard.keyDown[68]) {
            lightX += 5;
        }
        
        if (Mouse.pressed) {
            lightX = Mouse.x;
            lightZ = Mouse.y;
        }
        
        lightX = 0;
        //lightZ = -50 -200 * (1.5 - sy);
        light.position.set(lightX, lightZ, 100, 1);
        
        renderer.clearAllBuffers(); 
        //renderer.getDepthBuffer().clear();
        
        renderer.setBackfaceCullingEnabled(true);
        
        //renderer.setBackfaceCullingEnabled(false);
        renderer.setShader(gouraudShader);
        
        player.position.x = cameraPosition.x;
        player.position.y = cameraPosition.y;
        player.position.z = cameraPosition.z;
        player.angleX = cameraAngleX;
        player.angleY = cameraAngleY;
        player.updateDirection();
        
        
        renderer.setMatrixMode(VIEW);
        renderer.setIdentity();
        renderer.rotateX(player.angleX + Math.PI * 2);
        renderer.rotateY(-player.angleY + Math.PI * 0.5);
        renderer.translate(-player.position.x, -player.position.y, -player.position.z);
        
        double angle = 0;
        renderer.setMatrixMode(MODEL);
        renderer.setIdentity();
        //renderer.translate(0, 0, -10 - angle);
        //renderer.scale(200, 200, 200);
        //renderer.rotateX(Math.toRadians(Mouse.y));
        //renderer.rotateZ(Math.toRadians(angle));
                
        bspNode.transverse(player, renderer);
    }
    
    public void draw(Graphics2D g) {
        //Graphics2D g2d = (Graphics2D) renderer.getColorBuffer().getColorBuffer().getGraphics();
        //g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);        
        //g2d.drawImage(renderer.getColorBuffer().getColorBuffer(), 0, 0, 400, 300, 0, 0, 800, 600, null);
        //drawCBuffer((Graphics2D) renderer.getColorBuffer().getColorBuffer().getGraphics());
        g.drawImage(renderer.getColorBuffer().getColorBuffer(), 0, 0, 800, 600, 0, 0, 800, 600, null);
        
        g.setFont(font2);
        g.setColor(Color.WHITE);
        g.drawString("FPS: " + Time.fps, 10, 30);
        if (renderer.triangleRasterizer.isOcclusionEnabled()) {
            g.setColor(Color.GREEN);
            g.drawString("OCCLUSION ENABLED", 10, 90);
        }
        else {
            g.setColor(Color.RED);
            g.drawString("OCCLUSION DISABLED", 10, 90);
        }
        g.setFont(font);
        g.setColor(Color.WHITE);
        g.drawString("Canvas Resolution: (" + renderer.getColorBuffer().getWidth() + ", " + renderer.getColorBuffer().getHeight() + ")", 10, 60);
    }
//
//    public void drawCBuffer(Graphics2D g) {
//        g.translate(400, 305);
//        g.scale(1, -1);
//        int y = -renderer.triangleRasterizer.scan.halfHeight;
//        
//        
//        for (Scan scan : renderer.triangleRasterizer.scan.scans) {
//            //scan.checkFull();
//            if (scan.isFull()) {
//                g.setColor(Color.RED);
//            }
//            else {
//                g.setColor(Color.BLUE);
//            }
//            
//            Span span = scan.getCbuffer().right;
//            while (span != scan.endSpan) {
//                g.drawLine(span.x1, y, span.x2, y);
//                span = span.right;
//            }
//            y++;
//        }
//    }
    
    private class MainLoop implements Runnable {

        @Override
        public void run() {
            while (running) {
                Time.update();
                update();
                Graphics2D g = (Graphics2D) bs.getDrawGraphics();
                draw(g);
                g.dispose();
                bs.show();
                //Toolkit.getDefaultToolkit().sync();      
                //try {
                //    Thread.sleep(1);
                //} catch (InterruptedException ex) { }
            }
        }
        
    }

    private class MouseHandler extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            Mouse.pressed = true;
//            Mouse.pressedX = e.getX() - getWidth() * 0.5;
//            Mouse.pressedY = getHeight() * 0.5 - e.getY();
            Mouse.x = e.getX();
            Mouse.y = e.getY();
            Mouse.pressedX = e.getX();
            Mouse.pressedY = e.getY();
            cameraMousePressedAngleX = cameraAngleX;
            cameraMousePressedAngleY = cameraAngleY;
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            Mouse.pressed = false;
        }
        
        @Override
        public void mouseMoved(MouseEvent e) {
            //Mouse.x = e.getX() - getWidth() * 0.5;
            //Mouse.y = getHeight() * 0.5 - e.getY();
            //Mouse.x = e.getX();
            //Mouse.y = e.getY();
            //Mouse.pressed = false;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            //Mouse.x = e.getX() - getWidth() * 0.5;
            //Mouse.y = getHeight() * 0.5 - e.getY();
            Mouse.x = e.getX();
            Mouse.y = e.getY();
        }
        
    }

    private class KeyHandler extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            Keyboard.keyDown[e.getKeyCode()] = true;
        }

        @Override
        public void keyReleased(KeyEvent e) {
            Keyboard.keyDown[e.getKeyCode()] = false;
        }
        
    }
    
}
