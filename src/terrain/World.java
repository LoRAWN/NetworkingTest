package terrain;



import terrain.HeightMap.DiamondSquareHeightMap;

public class World {
	
	public static final float hillPercentage = 0.30f;
	public static final float waterPercentage = 0.10f;
	public static final int terrainMapSize = 4*1024 +1;
	public static final int blockSize = 64; //64 // in vector/location JME units
	public static final int mapScale = blockSize;
	public static final float mapXYScale = 10f; //0.06f*4
	public static final float mapZScale = 4.5f;//0.06f*4;
	public static final int worldSize = terrainMapSize * blockSize; // x and y size in JME units
	public static final int blocks = worldSize / blockSize; //x and y size 
        public static final long seed = 1337; //random number generator seed
	
	public static DiamondSquareHeightMap heightMap;
        
        

}
