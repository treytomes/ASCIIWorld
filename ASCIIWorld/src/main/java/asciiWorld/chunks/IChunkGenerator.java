package asciiWorld.chunks;

import java.io.PrintStream;

public interface IChunkGenerator {

	Chunk generate(Chunk chunk, long seed, PrintStream logStream) throws Exception;
}
