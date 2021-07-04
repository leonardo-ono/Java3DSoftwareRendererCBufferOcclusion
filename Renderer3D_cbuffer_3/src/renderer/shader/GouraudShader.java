package renderer.shader;

import renderer.core.Image;
import renderer.core.Light;
import renderer.core.Renderer;
import renderer.core.Shader;
import renderer.math.Util;
import renderer.math.Vec4;
import renderer.rasterizer.Vertex;

/**
 *
 * @author leonardo
 */
public class GouraudShader extends Shader {

    public int cor = 255;
    private Vec4 vertexLightDirection = new Vec4();
    public int[] colorSrc = new int[] { 255, 255, 255, 255 };
    
    private Image texture;
    
    public GouraudShader() {
        super(0, 0, 6);
    }

    @Override
    public void processVertex(Renderer renderer, Vertex vertex) {
        // renderer.doVertexMVPTransformation(vertex);
        
        // perspective correct texture mapping
        double zInv = 1 / vertex.p.z;
        vertex.vars[0] = zInv;
        //vertex.vars[1] = vertex.st.x * zInv;
        //vertex.vars[2] = vertex.st.y * zInv;
        
        texture = renderer.getTextures().get(0);
        double tx = vertex.st.x * (texture.getWidth() - 1);
        int textureHeight = texture.getHeight() - 1;
        double ty = textureHeight - (vertex.st.y * textureHeight);
        
        vertex.vars[1] = tx * zInv;
        vertex.vars[2] = ty * zInv;
        
        
        // simple light
//        Light light = renderer.getLights().get(0);
//        vertexLightDirection.set(light.position);
//        vertexLightDirection.sub(vertex.p);
//        double p = vertex.normal.getRelativeCosBetween(vertexLightDirection);
//        if (p < 0.5) {
//            p = 0.5;
//        }
//        else if (p > 1) {
//            p = 1;
//        }
//        p = 1;
//        vertex.vars[3] = p * light.diffuse.x;
//        vertex.vars[4] = p * light.diffuse.y;
//        vertex.vars[5] = p * light.diffuse.z;
    }
    
    @Override
    public void processPixel(Renderer renderer, int xMin, int xMax, int x, int y, double[] vars) {
        double depth = vars[0];
        double z = 1 / depth;
//        double s = vars[1] * z;
//        double t = vars[2] * z;

          int s = (int) Util.clamp(vars[1] * z, 0, texture.getWidth() - 2);
          int t = (int) Util.clamp(vars[2] * z, 0, texture.getHeight() - 2);

//        double colorp1 = vars[3];
//        double colorp2 = vars[4];
//        double colorp3 = vars[5];
//        
//        colorp1 = 1;
//        colorp2 = 1;
//        colorp3 = 1;

//        Image texture = renderer.getTextures().get(0);
//        int tx = (int) (s * (texture.getWidth() - 1));
//        
//        int textureHeight = texture.getHeight() - 1;
//        int ty = textureHeight - (int) (t * textureHeight);
//        
//        if (tx < 0) tx = 0;
//        if (ty < 0) ty = 0;
//        
//        renderer.getColorBuffer().getPixel(x, y, colorSrc);
          
        texture.getPixel(s, t, color);
        
//        color[0] = cor;
//        color[1] = cor;
//        color[2] = cor;
//        color[3] = cor;
        
        //color[1] = (int) Util.clamp((color[1] + colorSrc[1]) * colorp1, 0, 255);
        //color[2] = (int) Util.clamp((color[2] + colorSrc[2]) * colorp2, 0, 255);
        //color[3] = (int) Util.clamp((color[3] + colorSrc[3]) * colorp3, 0, 255);
        
        renderer.setPixel(x, y, color, depth);
    }
    
}
