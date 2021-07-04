package renderer.rasterizer;

import java.util.Arrays;
import renderer.core.Renderer;
import renderer.core.Shader;

/**
 *
 * @author leo
 */
public class TriangleRasterizer {

    private Edge e1 = new Edge();
    private Edge e2 = new Edge();
    
    public Scan scan;
    
    public Scan scanOcclusionOff = new Scan();
    public CBufferScan scanOcclusionOn;
    
    private boolean occlusionEnabled = false;
          
    private Vertex[] vertices = new Vertex[3];
    
    public TriangleRasterizer(int width, int height) {
        scanOcclusionOn = new CBufferScan(width, height);
        scanOcclusionOff = new Scan();
        scan = scanOcclusionOff;
    }

    public void setOcclusionEnabled(boolean enabled) {
        if (enabled) {
            scan = scanOcclusionOn;
        }
        else {
            scan = scanOcclusionOff;
        }
        occlusionEnabled = enabled;
    }

    public boolean isOcclusionEnabled() {
        return occlusionEnabled;
    }
    
    public void clear() {
        scan.clear();
    }
    
    public void draw(Renderer renderer, Vertex v1, Vertex v2, Vertex v3) {
        this.vertices[0] = v1;
        this.vertices[1] = v2;
        this.vertices[2] = v3;
        Arrays.sort(vertices);
        
        Shader shader = renderer.getShader();
        scan.setVars(shader.varsScan, shader.dVarsScan);
        drawTop(renderer);
        drawBottom(renderer);
    }
    
    private void drawTop(Renderer renderer) {
        Shader shader = renderer.getShader();
        e1.set(vertices[0], vertices[1], shader.varsE1, shader.dVarsE1);
        e2.set(vertices[0], vertices[2], shader.varsE2, shader.dVarsE2);
        scan.setTop(e1, e2);
        scan.drawTop(renderer);
    }

    private void drawBottom(Renderer renderer) {
        Shader shader = renderer.getShader();
        e1.set(vertices[2], vertices[1], shader.varsE1, shader.dVarsE1);
        e2.set(vertices[2], vertices[0], shader.varsE2, shader.dVarsE2);
        scan.setBottom(e1, e2);
        scan.drawBottom(renderer);
    }
    
}
