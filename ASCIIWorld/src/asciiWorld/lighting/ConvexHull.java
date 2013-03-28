package asciiWorld.lighting;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

public class ConvexHull {

	private static final Color DEFAULT_COLOR = Color.white;
	
	private Color _color;
	private Shape _shape;
	private float _depth;
	private Boolean _lastNodeFrontFacing;
	
	public ConvexHull(Vector2f position, Shape shape, Color color) {
		_color = color;
		_shape = shape;
		_shape.setLocation(position);
		_depth = 0.0f;
	}
	
	public ConvexHull(Vector2f position, Shape shape) {
		this(position, shape, DEFAULT_COLOR);
	}
	
	private static int getIndexOfPointInShape(Shape shape, Vector2f point) {
		for (int index = 0; index < shape.getPointCount(); index++) {
			Vector2f thisPoint = new Vector2f(shape.getPoint(index));
			if ((thisPoint.x == point.x) && (thisPoint.y == point.y)) {
				return index;
			}
		}
		return -1;
	}
	
	public void drawShadowGeometry(Light light) {
        // Calculate all the front facing sides
		Integer first = null;
		Integer last = null;
		_lastNodeFrontFacing = false;
        for (int x = -1; x < _shape.getPointCount(); x++) {
        	int index1 = (x >= 0) ? x : (_shape.getPointCount() + x);
            Vector2f current_point = new Vector2f(_shape.getPoint(index1));
        	int index2 = ((index1 - 1) >= 0) ? (index1 - 1) : (_shape.getPointCount() + (index1 - 1));
            Vector2f prev_point = new Vector2f(_shape.getPoint(index2));

            Vector2f nv = new Vector2f(current_point.y - prev_point.y, current_point.x - prev_point.x);

            Vector2f lv = new Vector2f(current_point.x - light.getPosition().x, current_point.y - light.getPosition().y);

            // Check if the face is front-facing
            if ((nv.x * -1 * lv.x) + (nv.y * lv.y) > 0) {
                if (_lastNodeFrontFacing == false) {
                    last = getIndexOfPointInShape(_shape, prev_point);
                }
                _lastNodeFrontFacing = true;
            } else {
                if (_lastNodeFrontFacing) {
                    first = getIndexOfPointInShape(_shape, prev_point);
                }
                _lastNodeFrontFacing = false;
            }
        }

        if ((first == null) || (last == null)) {
            // The light source is inside the object
            return;
        }

        // Create shadow fins
        List<ShadowFin> start_fins = createShadowFins(light, first, 1);
        first = start_fins.get(0).getIndex();
        Vector2f first_vector = start_fins.get(0).getInner();
        
        List<ShadowFin> end_fins = createShadowFins(light, last, -1);
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
        for (int x = first; x < first + _shape.getPointCount(); x++) {
        	backpoints.add(0, new Vector2f(_shape.getPoint(x % _shape.getPointCount())));
            if ((x % _shape.getPointCount()) == last) {
                break;
            }
        }

        // Figure out the length of the back edges. We'll use this later for
        // weighted average between the shadow fins to find our umbra vectors.
        List<Float> back_length = new ArrayList<Float>();
        back_length.add(0.0f);
        float sum_back_length = 0;
        for (int x = 1; x < backpoints.size(); x++) {
            float l = backpoints.get(x - 1).copy().subtract(backpoints.get(x)).length();
            back_length.add(0, l);
            sum_back_length += l;
        }

        // Draw the shadow geometry using a triangle strip
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
        float a = 0;
        for (int x = 0; x < backpoints.size(); x++) {
            Vector2f point = backpoints.get(x);
            GL11.glVertex3f(point.x, point.y, _depth);
            // Draw our umbra using weighted average vectors
            if (x != backpoints.size() - 2) {
            	GL11.glVertex3f(
                		point.x + (first_vector.x * (a / sum_back_length)) + (last_vector.x * (1 - (a / sum_back_length))),
                        point.y + (first_vector.y * (a / sum_back_length)) + (last_vector.y * (1 - (a / sum_back_length))),
                        _depth);
            } else {
            	GL11.glVertex3f(point.x + first_vector.x, point.y + first_vector.y, _depth);
            }
            a += back_length.get(x);
        }
        GL11.glEnd();
	}
	
	private List<ShadowFin> createShadowFins(Light light, int origin, int step) {
        List<ShadowFin> shadowfins = new ArrayList<ShadowFin>();

        // Go backwards to see if we need any shadow fins
        int i = origin;
        while (true) {
            Vector2f p1 = new Vector2f(_shape.getPoint(i));

            // Make sure we wrap around.
            i -= step;
            if (i < 0) {
                i = _shape.getPointCount() - 1;
            } else if (i == _shape.getPointCount()) {
                i = 0;
            }

            Vector2f p0 = new Vector2f(_shape.getPoint(i));

            Vector2f edge = p1.copy().subtract(p0).normalise();

            ShadowFin shadowfin = new ShadowFin(p0);
            shadowfin.setIndex(i);

            float angle = edge.getAngle() - light.outerVector(p0, step).getAngle();

            if (step == 1) {
                if ((angle < 0) || (angle > Math.PI * 0.5)) {
                    break;
                }
            } else if (step == -1) {
                // Make sure the angle is within the right quadrant.
                if (angle > Math.PI) {
                    angle -= Math.PI * 2;
                }
                if ((angle > 0) || (angle < -Math.PI * 0.5)) {
                    break;
                }
            }

            shadowfin.setOuter(light.outerVector(p0, step));
            shadowfin.setInner(edge.copy().scale(light.innerVector(p0, step).length()));

            shadowfins.add(shadowfin);
            //break
        }
        
        // Go forwards and see if we need any shadow fins.
        i = origin;
        while (true) {
        	ShadowFin shadowfin = new ShadowFin(new Vector2f(_shape.getPoint(i)));
            shadowfin.setIndex(i);

            shadowfin.setOuter(light.outerVector(new Vector2f(_shape.getPoint(i)), step));
            shadowfin.setInner(light.innerVector(new Vector2f(_shape.getPoint(i)), step));

            if (shadowfins.size() > 0) {
                shadowfin.setOuter(shadowfins.get(0).getInner());
            }

            Vector2f p0 = new Vector2f(_shape.getPoint(i));

            // Make sure we wrap around.
            i += step;
            if (i < 0) {
                i = _shape.getPointCount() - 1;
            } else if (i == _shape.getPointCount()) {
                i = 0;
            }

            Vector2f p1 = new Vector2f(_shape.getPoint(i));

            Vector2f edge = p1.copy().subtract(p0).normalise();

            boolean done = true;
            Vector2f penumbra = shadowfin.getOuter().copy().normalise();
            Vector2f umbra = shadowfin.getInner().copy().normalise();
            
            if (Math.acos(edge.dot(penumbra)) < Math.acos(umbra.dot(penumbra))) {
                shadowfin.setInner(edge.copy().scale(light.outerVector(p0, step).length()));
                done = false;
            }

            shadowfins.add(0, shadowfin);

            if (done) {
                break;
            }
        }
        
        // Get the total angle.
        float sum_angles = 0;
        for (int x = 0; x < shadowfins.size(); x++) {
            sum_angles += shadowfins.get(x).getAngle();
        }
        
        // Calculate the inner and outer intensity of the shadowfins.
        float angle = 0;
        for (int x = 0; x < shadowfins.size(); x++) {
            shadowfins.get(x).setUmbraIntensity(angle / sum_angles);
            angle += shadowfins.get(x).getAngle();
            shadowfins.get(x).setPenumbraIntensity(angle / sum_angles);
        }

        // We'll use these for our umbra generation.
        return shadowfins;
	}
	
	public void render(Graphics g) {
        GL11.glColor4f(_color.r, _color.g, _color.b, 1.0f);
        GL11.glBegin(GL11.GL_POLYGON);
        for (int x = 0; x < _shape.getPointCount(); x++) {
        	float[] pnt = _shape.getPoint(x);
            GL11.glVertex3f(pnt[0], pnt[1], _depth);
        }
        GL11.glEnd();
		/*g.pushTransform();
		g.translate(_position.x, _position.y);
		g.setColor(_color);
		g.fill(_shape);
		g.popTransform();*/
	}
}
