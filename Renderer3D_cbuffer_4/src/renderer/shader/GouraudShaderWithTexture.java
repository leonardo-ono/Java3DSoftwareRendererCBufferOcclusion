package renderer.shader;

import java.awt.Color;
import java.awt.Graphics;
import renderer.core.Image;
import renderer.core.Material;
import renderer.core.Renderer;
import renderer.core.Shader;
import renderer.math.Vec4;
import renderer.rasterizer.Vertex;

/**
 *
 * @author leonardo
 */
public class GouraudShaderWithTexture extends Shader {
    
    public GouraudShaderWithTexture() {
        super(0, 0, 3);
    }
    
    private Vec4 vertexLightDirection = new Vec4();

    @Override
    public void processVertex(Renderer renderer, Vertex vertex) {
        // renderer.doVertexMVPTransformation(vertex);
        
        // perspective correct texture mapping
        double zInv = 1 / vertex.p.z;
        vertex.vars[0] = zInv;
        vertex.vars[1] = vertex.st.x * zInv;
        vertex.vars[2] = vertex.st.y * zInv;
        
        // simple light
//        Light light = renderer.getLights().get(0);
//        vertexLightDirection.set(light.position);
//        vertexLightDirection.sub(vertex.p);
//        double p = vertex.normal.getRelativeCosBetween(vertexLightDirection);
//        if (p < 0.05) {
//            p = 0.05;
//        }
//        else if (p > 1) {
//            p = 1;
//        }
//        //p = 1;
//        vertex.vars[3] = p * light.diffuse.x;
//        vertex.vars[4] = p * light.diffuse.y;
//        vertex.vars[5] = p * light.diffuse.z;
    }
    
    @Override
    public void processPixel(Renderer renderer, int xMin, int xMax, int x, int y, double[] vars) {
        double depth = vars[0];
        double z = 1 / depth;
        double s = vars[1] * z;
        double t = vars[2] * z;

        //s = Util.clamp(s, 0, 1);
        //t = Util.clamp(t, 0, 1);
        
//        double colorp1 = 1; //vars[3];
//        double colorp2 = 1; //vars[4];
//        double colorp3 = 1; //vars[5];
        
//        colorp1 = 1;
//        colorp2 = 1;
//        colorp3 = 1;
        Material material = renderer.getMaterial();
        Image texture = null;
        
        //if (material != null) {
            texture = renderer.getMaterial().map_kd;
        //}
        
        if (texture != null) {
            double ns = s - (int) s;
            if (ns < 0) ns += 1.0;
            double nt = t - (int) t;
            if (nt < 0) nt += 1.0;
            int tx = (int) (ns * (texture.getWidth() - 1));
            int textureHeight = texture.getHeight() - 1;
            int ty = textureHeight - (int) (nt * textureHeight);
            texture.getPixel(tx, ty, color);
        }
//        else {
//            color[0] = 255;
//            color[1] = 255;
//            color[2] = 255;
//            color[3] = 255;
//        }
        
//        color[0] = 255;
//        color[1] = (int) (color[1] * colorp1);
//        color[2] = (int) (color[2] * colorp2);
//        color[3] = (int) (color[3] * colorp3);

        //if (color[1] > 0 || color[2] > 0 || color[3] > 0) {
            renderer.setPixel(x, y, color, depth);
        //}
    }

    private java.awt.Polygon polyTmp = new java.awt.Polygon();
    
    public void postDraw2(Renderer renderer, Vertex va, Vertex vb, Vertex vc) {
        polyTmp.reset();
        polyTmp.addPoint((int) va.p.x + 200, 150 - (int) va.p.y);
        polyTmp.addPoint((int) vb.p.x + 200, 150 - (int) vb.p.y);
        polyTmp.addPoint((int) vc.p.x + 200, 150 - (int) vc.p.y);
        Graphics g = renderer.getColorBuffer().getColorBuffer().getGraphics();
        g.setColor(Color.WHITE);
        g.drawPolygon(polyTmp);
    }

}
