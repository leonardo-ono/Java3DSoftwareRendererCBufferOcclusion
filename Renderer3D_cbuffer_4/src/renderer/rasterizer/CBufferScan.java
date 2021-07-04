package renderer.rasterizer;

import renderer.occlusion.cbuffer.Span;
import renderer.core.Renderer;
import renderer.core.Shader;

/**
 *
 * @author leonardo
 */
public class CBufferScan extends Scan {

    private int halfWidth;
    public int halfHeight;
    
    public int fullScansCount;
    public renderer.occlusion.cbuffer.Scan[] scans;
    
    public CBufferScan(int width, int height) {
        halfWidth = width / 2;
        halfHeight = height / 2;
        scans = new renderer.occlusion.cbuffer.Scan[height];
        for (int i=0; i<scans.length; i++) {
            scans[i] = new renderer.occlusion.cbuffer.Scan(-halfWidth, halfWidth);
        }
    }
    
    @Override
    public void clear() {
        fullScansCount = scans.length;
        for (int i = 0; i < scans.length; i++) {
            scans[i].clear();
        }
    }
    
    @Override
    public void drawTop(Renderer renderer) {
        Shader shader = renderer.getShader();
        for (int y = y1; y <= y2; y++) {
            renderer.occlusion.cbuffer.Scan cbuffer = scans[y + halfHeight];
            if (!cbuffer.isFull()) {
                initX();
                Span visibleSpan = cbuffer.insert(x1, x2);
                if (visibleSpan != null) {
                    nextX(visibleSpan.x1 - x1);
                    Span firstVisibleSpan = visibleSpan;
                    do {
                        for (int x=visibleSpan.x1; x<=visibleSpan.x2; x++) {
                            shader.processPixel(renderer, x1, x2, x, y, vars);
                            nextX();
                        }
                        if (visibleSpan.right != null) {
                            nextX(visibleSpan.right.x1 - visibleSpan.x2);
                        }
                        visibleSpan = visibleSpan.right;
                    }
                    while (visibleSpan != null && visibleSpan != firstVisibleSpan);
                }
                cbuffer.checkFull();
                if (cbuffer.isFull()) {
                    fullScansCount--;
                }
            }
            e1.nextYTop();
            e2.nextYTop();
        }
    }

    @Override
    public void drawBottom(Renderer renderer) {
        Shader shader = renderer.getShader();
        for (int y = y1; y > y2; y--) {
            renderer.occlusion.cbuffer.Scan cbuffer = scans[y + halfHeight];
            if (!cbuffer.isFull()) {
                initX();
                Span visibleSpan = cbuffer.insert(x1, x2);
                if (visibleSpan != null) {
                    nextX(visibleSpan.x1 - x1);
                    Span firstVisibleSpan = visibleSpan;
                    do {
                        for (int x=visibleSpan.x1; x<=visibleSpan.x2; x++) {
                            shader.processPixel(renderer, x1, x2, x, y, vars);
                            nextX();
                        }
                        if (visibleSpan.right != null) {
                            nextX(visibleSpan.right.x1 - visibleSpan.x2);
                        }
                        visibleSpan = visibleSpan.right;
                    }
                    while (visibleSpan != null && visibleSpan != firstVisibleSpan);
                }
                cbuffer.checkFull();
                if (cbuffer.isFull()) {
                    fullScansCount--;
                }
            }
            e1.nextYBottom();
            e2.nextYBottom();
        }
    }

    protected void nextX(int s) {
        for (int k=0; k<vars.length; k++) {
            vars[k] += (s * dVars[k]);
        }
    }
    
}
