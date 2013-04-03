package asciiWorld.tiles;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.newdawn.slick.geom.Vector2f;

public class FrameStack implements IRenderable {
	
	private List<IRenderable> _frames;
	
	public FrameStack() {
		_frames = new ArrayList<IRenderable>();
	}
	
	public FrameStack(FrameStack clone) {
		this();
		for (IRenderable frame : clone._frames) {
			_frames.add(frame.clone());
		}
	}
	
	public FrameStack clone() {
		return new FrameStack(this);
	}
	
	public void render(TileSet tiles, Vector2f position, float rotation, TransformEffect transform) {
		for (int index = 0; index < _frames.size(); index++) {
			_frames.get(index).render(tiles,  position, rotation, transform);
		}
	}
	
	public void render(TileSet tiles, Vector2f position, float rotation) {
		render(tiles, position, rotation, TransformEffect.None);
	}
	
	public void render(TileSet tiles, Vector2f position) {
		render(tiles, position, 0);
	}
	
	public void render(TileSet tiles) {
		render(tiles, Vector2f.zero());
	}
	
	private void addFrame(IRenderable frame) {
		_frames.add(frame);
	}
	
	public static FrameStack load(String path) throws JDOMException, IOException {
		return fromXml((Element)new SAXBuilder().build(new File(path)).getRootElement());
	}
	
	public static FrameStack fromXml(Element elem) {
		FrameStack frameStack = new FrameStack();
		for (Element frameElem : elem.getChildren("Frame")) {
			if (frameElem.getName() == "Frame") {
				frameStack.addFrame(Frame.fromXml(frameElem));
			} else if (frameElem.getName() == "FrameStack") {
				frameStack.addFrame(FrameStack.fromXml(frameElem));
			}
		}
		return frameStack;
	}
}
