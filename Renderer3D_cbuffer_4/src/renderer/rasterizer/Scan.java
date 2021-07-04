package renderer.rasterizer;

import renderer.core.Renderer;
import renderer.core.Shader;

/**
 *
 * @author leonardo
 */
public class Scan {

    public int fullScansCount = 600;
    
    protected Edge e1;
    protected Edge e2;
    
    protected int x1;
    protected int x2;
    protected int y1;
    protected int y2;
    
    protected double vars[];
    protected double dVars[];
    
    public Scan() {
    }

    public void setVars(double[] vars, double dVars[]) {
        this.vars = vars;
        this.dVars = dVars;
    }
    
    public void setTop(Edge e1, Edge e2) {
        this.e1 = e1;
        this.e2 = e2;
        if (e1.dx > e2.dx) {
            swapE1E2();
        }
        y1 = (int) e1.a.p.y;
        y2 = (int) e1.b.p.y;
    }
    
    public void setBottom(Edge e1, Edge e2) {
        this.e1 = e1;
        this.e2 = e2;
        if (e1.dx < e2.dx) {
            swapE1E2();
        }
        y1 = (int) e1.a.p.y;
        y2 = (int) e1.b.p.y;
    }
    
    
    public void drawTop(Renderer renderer) {
        Shader shader = renderer.getShader();
        for (int y = y1; y <= y2; y++) {
            initX();
            for (int x=x1; x<=x2; x++) {
                shader.processPixel(renderer, x1, x2, x, y, vars);
                nextX();
            }
            e1.nextYTop();
            e2.nextYTop();
        }
    }

    public void drawBottom(Renderer renderer) {
        Shader shader = renderer.getShader();
        for (int y = y1; y > y2; y--) {
            initX();
            for (int x=x1; x<=x2; x++) {
                shader.processPixel(renderer, x1, x2, x, y, vars);
                nextX();
            }
            e1.nextYBottom();
            e2.nextYBottom();
        }
    }
    
    protected void swapE1E2() {
        Edge tmp = e1;
        e1 = e2;
        e2 = tmp;
    }
    
    protected void initX() {
        x1 = (int) e1.x;
        x2 = (int) e2.x;
        int dx = x2 - x1;
        dx = dx < 0 ? dx - 1 : dx + 1;
        System.arraycopy(e1.vars, 0, vars, 0, vars.length);
        double dxInv = 1.0 / dx;
        for (int k=0; k<vars.length; k++) {
            dVars[k] = (e2.vars[k] - e1.vars[k]) * dxInv;
        }
    }

    protected void nextX() {
        for (int k=0; k<vars.length; k++) {
            vars[k] += dVars[k];
        }
    }
    
    public void clear() {
    }    
    
}
