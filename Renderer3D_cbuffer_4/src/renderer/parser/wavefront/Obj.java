package renderer.parser.wavefront;

import bsp3d.Triangle;
import java.util.ArrayList;
import java.util.List;
import renderer.core.Material;

/**
 *
 * @author leonardo
 */
public class Obj {
    
    public List<Triangle> faces = new ArrayList<>();
    public Material material;
    
}
