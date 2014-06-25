/*
 * UISampleGame2.scala
 * Purpose: 
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Trey Tomes
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package asciiWorld.sample

import org.newdawn.slick.BasicGame
import org.newdawn.slick.AppGameContainer
import org.newdawn.slick.SlickException
import org.newdawn.slick.GameContainer
import org.newdawn.slick.Graphics
import asciiWorld.ui.RootVisualPanel
import org.newdawn.slick.geom.Rectangle
import asciiWorld.ui.Label
import asciiWorld.ui.Button
import asciiWorld.ui.UIFactory
import org.newdawn.slick.geom.Vector2f
import org.newdawn.slick.Color
import asciiWorld.ui.StackPanel
import asciiWorld.ui.MethodBinding
import asciiWorld.ui.Orientation
import asciiWorld.ui.ButtonClickedEvent

/**
 * @author Trey Tomes <trey.tomes@gmail.com>
 *
 */
object UISampleGameStart {
  class UISampleGame extends BasicGame("UI Sample Game") {
    
    override def init(container: GameContainer) = {
      container.setVSync(true);
      container.setAlwaysRender(true);
      
      RootVisualPanel.initialize(container);
      initializeComponent(container);
    }
    
    override def update(container: GameContainer, delta: Int) = RootVisualPanel.get().update(container, delta);
    
    override def render(container: GameContainer, g: Graphics) = RootVisualPanel.get().render(g);
    
    def initializeComponent(container: GameContainer) = {
      val containerBounds = new Rectangle(0, 0, container.getWidth(), container.getHeight());
      
      val margin = 5;
      val buttonWidth = 202;
      val buttonHeight = 42;
      val numberOfMenuOptions = 5;
      val mainMenuButtonPanel = new StackPanel(new Rectangle(containerBounds.getWidth() - buttonWidth - margin, margin, buttonWidth, buttonHeight * numberOfMenuOptions), Orientation.Vertical);
      
      def clickAction(btn: Button): Unit = RootVisualPanel.get().showMessageBox(true, s"You pressed '${btn.getText()}'!", "Message Box")
      
      mainMenuButtonPanel.addChild(createActionButton("New Game! :-D", clickAction));
      mainMenuButtonPanel.addChild(createActionButton("Script Console", clickAction));
      mainMenuButtonPanel.addChild(createActionButton("Text Editor", clickAction));
      mainMenuButtonPanel.addChild(createActionButton("Audio Tests", clickAction));
      mainMenuButtonPanel.addChild(createActionButton("Exit :-(", (button) => container.exit()));
      
      val root = RootVisualPanel.get();
	  root.addChild(new Label(new Vector2f(10, 10), "ASCII World", Color.red));
	  root.addChild(new Button("A", new Rectangle(50, 50, 50, 50)));
	  root.addChild(new Button("B", new Rectangle(75, 75, 50, 50)));
	  root.addChild(mainMenuButtonPanel);
	  root.addChild(UIFactory.get().getResource("optionButtonPanel"));
    }
	
	def createActionButton(text: String, action: (Button) => Unit): Button = {
	  val newButton = new Button(text);
	  newButton.getMargin().setValue(5);
	  newButton.addClickListener(new ButtonClickedEvent() {
	    def click(button: Button) = action(button);
	  });
	  return newButton;
	}
  }
  
  def main(args: Array[String]): Unit = {
	val game = new UISampleGame();
  	val app = new AppGameContainer(game);
  	app.setDisplayMode(1280, 720, false);
  	app.start();
  }
}