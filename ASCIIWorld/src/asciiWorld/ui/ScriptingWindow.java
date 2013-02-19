package asciiWorld.ui;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.RoundedRectangle;

import asciiWorld.JavascriptContext;
import asciiWorld.TextFactory;

//Reference: https://developer.mozilla.org/en-US/docs/Rhino
//Reference: https://developer.mozilla.org/en-US/docs/Rhino_documentation
//Reference: https://developer.mozilla.org/en-US/docs/Rhino/Embedding_tutorial

public class ScriptingWindow extends TextEditorWindow {
	
	private JavascriptContext _context;
	
	public ScriptingWindow(GameContainer container, Rectangle bounds)
			throws Exception {
		super(container, bounds);
		
		_context = new JavascriptContext();
	}
	
	public ScriptingWindow(GameContainer container)
			throws Exception {
		this(container, new Rectangle(0, 0, container.getWidth(), container.getHeight()));
	}
	
	public void executeScript() {
		try {
			getRoot().showMessageBox(true, JavascriptContext.toString(_context.executeScript(getText())), "Script Output");
		} catch (Exception e) {
			getRoot().showMessageBox(true, e.getMessage(), "Runtime Error");
		}
	}

	public MessageBox showHelp() {
		try {
			return getRoot().showMessageBox(true, TextFactory.get().getResource("scriptingInstructions"), "Help");
		} catch (Exception e) {
			System.err.println("Unable to show the scripting instructions.");
			return null;
		}
	}
	
	@Override
	protected StackPanel getButtons(RoundedRectangle dialogBounds) throws Exception {
		StackPanel buttonPanel = super.getButtons(dialogBounds);
		buttonPanel.addChild(Button.createActionButton("Execute", new MethodBinding(this, "executeScript")));
		return buttonPanel;
	}
	
	@Override
	public void keyPressed(int key, char c) {
		super.keyPressed(key, c);
		if (key == Input.KEY_F11) {
			executeScript();
		}
	}
}