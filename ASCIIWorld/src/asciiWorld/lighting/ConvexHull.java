package asciiWorld.lighting;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

public class ConvexHull implements IConvexHull {

	private static final Color DEFAULT_COLOR = Color.white;	
	private static final float PI_OVER_TWO = (float)Math.PI / 2.0f;
	
	private Color _color;
	private Shape _shape;
	private float _depth;
	private Vector2f[] _points;
	
	public ConvexHull(Vector2f position, Shape shape, Color color) {
		_color = color;
		_shape = shape;
		_shape.setLocation(position);
		_depth = 0.0f;
		
		_points = new Vector2f[_shape.getPointCount()];
		for (int n = 0; n < _shape.getPointCount(); n++) {
			_points[n] = new Vector2f(_shape.getPoint(n));
		}
	}
	
	public ConvexHull(Vector2f position, Shape shape) {
		this(position, shape, DEFAULT_COLOR);
	}
	
	public void render(Graphics g) {
		g.setColor(_color);
		g.fill(_shape);
	}
	
	public void drawShadowGeometry(Light light) {
		drawShadowGeometry(_points, _depth, light);
	}
	
	public static void drawShadowGeometry(Vector2f[] points, float depth, Light light) {
        // Calculate all the front facing sides.
		Integer first = null;
		Integer last = null;
		boolean lastNodeFrontFacing = false;
		
		Vector2f lightPosition = light.getPosition();
		int shapePointCount = points.length;
        for (int index = -1; index < shapePointCount; index++) {
        	int index1 = (index + shapePointCount) % shapePointCount;
            Vector2f current_point = points[index1];
            
    		int index2 = ((index1 - 1) + shapePointCount) % shapePointCount;
            Vector2f prev_point = points[index2];

            Vector2f nv = new Vector2f(current_point.y - prev_point.y, current_point.x - prev_point.x);
            Vector2f lv = new Vector2f(current_point.x - lightPosition.x, current_point.y - lightPosition.y);

            // Check if the face is front-facing
            if (((nv.y * lv.y) - (nv.x * lv.x)) > 0) {
                if (!lastNodeFrontFacing) {
                    last = index2;
                }
                lastNodeFrontFacing = true;
            } else {
                if (lastNodeFrontFacing) {
                    first = index2;
                }
                lastNodeFrontFacing = false;
            }
        }

        if ((first == null) || (last == null)) {
            // The light source is inside the object
            return;
        }

        // Create shadow fins
        List<ShadowFin> start_fins = createShadowFins(points, light, first, 1);
        first = start_fins.get(0).getIndex();
        Vector2f first_vector = start_fins.get(0).getInner();
        
        List<ShadowFin> end_fins = createShadowFins(points, light, last, -1);
        last = end_fins.get(0).getIndex();
        Vector2f last_vector = end_fins.get(0).getInner();

        // Render shadow fins
        for (ShadowFin fin : start_fins) {
            fin.render();
        }
        for (ShadowFin fin : end_fins) {
            fin.render();
        }

        // Get a list of all the back edges
        List<Vector2f> backpoints = new ArrayList<Vector2f>();
        for (int x = first; x < first + shapePointCount; x++) {
        	backpoints.add(0, points[x % shapePointCount]);
            if ((x % shapePointCount) == last) {
                break;
            }
        }

        // Figure out the length of the back edges. We'll use this later for
        // weighted average between the shadow fins to find our umbra vectors.
        List<Float> back_length = new ArrayList<Float>();
        back_length.add(0.0f);
        float sum_back_length = 0;
        for (int x = 1; x < backpoints.size(); x++) {
        	Vector2f lastPoint = backpoints.get(x - 1);
        	Vector2f thisPoint = backpoints.get(x);
            float l = new Vector2f(lastPoint.x - thisPoint.x, lastPoint.y - thisPoint.y).length();
            back_length.add(0, l);
            sum_back_length += l;
        }

        // Draw the shadow geometry using a triangle strip
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
        float a = 0;
        for (int x = 0; x < backpoints.size(); x++) {
            Vector2f point = backpoints.get(x);
            GL11.glVertex3f(point.x, point.y, depth);
            // Draw our umbra using weighted average vectors
            if (x != backpoints.size() - 2) {
            	GL11.glVertex3f(
                		point.x + (first_vector.x * (a / sum_back_length)) + (last_vector.x * (1 - (a / sum_back_length))),
                        point.y + (first_vector.y * (a / sum_back_length)) + (last_vector.y * (1 - (a / sum_back_length))),
                        depth);
            } else {
            	GL11.glVertex3f(point.x + first_vector.x, point.y + first_vector.y, depth);
            }
            a += back_length.get(x);
        }
        GL11.glEnd();
	}
	
	private static List<ShadowFin> createShadowFins(Vector2f[] points, Light light, int origin, int step) {
        List<ShadowFin> shadowfins = new ArrayList<ShadowFin>();

        // Go backwards to see if we need any shadow fins
        int i = origin;
        int shapePointCount = points.length;
        while (true) {
            Vector2f p1 = points[i];

            // Make sure we wrap around.
            i = ((i - step) + shapePointCount) % shapePointCount;

            Vector2f p0 = points[i];

            Vector2f edge = new Vector2f(p1.x - p0.x, p1.y - p0.y).normalise();

            ShadowFin shadowfin = new ShadowFin(p0, i);

            float angle = edge.getAngle() - light.outerVector(p0, step).getAngle();

            if (step == 1) {
                if ((angle < 0) || (angle > PI_OVER_TWO)) {
                    break;
                }
            } else if (step == -1) {
                // Make sure the angle is within the right quadrant.
                if (angle > Math.PI) {
                    angle -= Math.PI * 2;
                }
                if ((angle > 0) || (angle < -PI_OVER_TWO)) {
                    break;
                }
            }

            shadowfin.setOuter(light.outerVector(p0, step));
            shadowfin.setInner(edge.scale(light.innerVector(p0, step).length()));

            shadowfins.add(shadowfin);
        }
        
        // Go forwards and see if we need any shadow fins.
        i = origin;
        while (true) {
        	ShadowFin shadowfin = new ShadowFin(points[i], i);

            shadowfin.setOuter(light.outerVector(points[i], step));
            shadowfin.setInner(light.innerVector(points[i], step));

            if (shadowfins.size() > 0) {
                shadowfin.setOuter(shadowfins.get(0).getInner());
            }

            Vector2f p0 = points[i];

            // Make sure we wrap around.
            i = ((i + step) + shapePointCount) % shapePointCount;

            Vector2f p1 = points[i];

            Vector2f edge = new Vector2f(p1.x - p0.x, p1.y - p0.y).normalise();

            boolean done = true;
            Vector2f penumbra = shadowfin.getOuter().copy().normalise();
            Vector2f umbra = shadowfin.getInner().copy().normalise();
            
            if (Math.acos(edge.dot(penumbra)) < Math.acos(umbra.dot(penumbra))) {
                shadowfin.setInner(edge.scale(light.outerVector(p0, step).length()));
                done = false;
            }

           	shadowfins.add(0, shadowfin);

            if (done) {
                break;
            }
        }
        
        // Get the total angle.
        float sum_angles = 0;
        for (ShadowFin fin : shadowfins) {
            sum_angles += fin.getAngle();
        }
        
        // Calculate the inner and outer intensity of the shadowfins.
        float angle = 0;
        for (ShadowFin fin : shadowfins) {
        	fin.setUmbraIntensity(angle / sum_angles);
			angle += fin.getAngle();
			fin.setPenumbraIntensity(angle / sum_angles);
        }

        // We'll use these for our umbra generation.
        return shadowfins;
	}
	
	public static void drawShadowGeometry(Vector2f[] points, Light light) {
		drawShadowGeometry(points, 0.0f, light);
	}
}
