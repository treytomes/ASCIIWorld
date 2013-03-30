package asciiWorld;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

import asciiWorld.chunks.Chunk;
import asciiWorld.chunks.ChunkFactory;
import asciiWorld.ui.MessageBox;
import asciiWorld.ui.RootVisualPanel;
import asciiWorld.ui.TextWrappingMode;

/**
 * Generate a chunk for a fresh game session.
 * 
 * @author ttomes
 *
 */
public class GenerateChunkGameState extends GameState {
	
	private static final TextWrappingMode DEFAULT_TEXT_WRAPPING_MODE = TextWrappingMode.CharacterWrap;
	
	private Chunk _chunk;
	
	private Thread _chunkGenerationThread;
	private ByteArrayOutputStream _logStream;
	private MessageBox _loggingWindow;

	public GenerateChunkGameState() {
		createLogStream();
		createLoggingWindow();
		createGenerationThread();
	}
	
	public Boolean isComplete() {
		return !_chunkGenerationThread.isAlive();
	}
	
	public Chunk getChunk() {
		return _chunk;
	}
	
	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) {
		try {
			_logStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		_loggingWindow.getMessageLabel().setText(_logStream.toString());
		
		if (isComplete()) {
			try {
				getManager().switchTo(new LoadGraphicsGameState(getChunk()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		try {
			RootVisualPanel.get().update(container, delta);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) {
		try {
			RootVisualPanel.get().render(g);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void createLogStream() {
		_logStream = new ByteArrayOutputStream();
	}
	
	private void createLoggingWindow() {
		try {
			_loggingWindow = RootVisualPanel.get().showMessageBox(true, "Generating chunk...", "Generating Chunk");
			_loggingWindow.getMessageLabel().setTextWrappingMode(DEFAULT_TEXT_WRAPPING_MODE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createGenerationThread() {
		_chunkGenerationThread = new Thread()
		{
			public void run()
			{
				generateChunk();
			}
		};
		_chunkGenerationThread.start();
	}
	
	private void generateChunk() {
		PrintStream printStream = new PrintStream(_logStream, true);
		try {
			_chunk = ChunkFactory.generateOverworld(printStream);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Unable to generate the chunk.");
		}
		if (_loggingWindow != null) {
			_loggingWindow.closeWindow();
		}
	}
}