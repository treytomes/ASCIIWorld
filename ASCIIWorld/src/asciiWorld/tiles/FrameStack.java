package asciiWorld.tiles;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

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
	
	public void render(TileSet tiles) {
		for (IRenderable frame : _frames) {
			frame.render(tiles);
		}
		/*for (int index = 0; index < _frames.size(); index++) {
			_frames.get(index).render(tiles);
		}*/
	}

	@Override
	public void renderBatched(TileSet tiles, SpriteBatch spriteBatch, float x, float y, float rotation) {
		for (IRenderable frame : _frames) {
			frame.renderBatched(tiles, spriteBatch, x, y, rotation);
		}
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
