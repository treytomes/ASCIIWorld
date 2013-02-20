package asciiWorld.chunks;

public interface IChunkGenerator {

	Chunk generate(Chunk chunk, long seed) throws Exception;
}
