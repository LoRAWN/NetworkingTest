package terrain.HeightMap;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

import com.jme3.math.FastMath;
import com.jme3.terrain.heightmap.AbstractHeightMap;

public class DiamondSquareHeightMap extends AbstractHeightMap {
	// Written by Sami Pietila 2011
	// Diamond Square algorithm for generating terrain
	// Implemented according to algorithm described in
	// http://www.lighthouse3d.com/opengl/terrain/index.php?mpd2

	int roughness = 1;
	int maxDisplacement = 1;
	float waterPercentage;
	float hillPercentage;
	
	private float minHeight = 0;
	private float maxHeight = 0;
	private float maxHeightCap = 0;
	private float minHeightCap = 0;
	private Random randomGenerator;

	public DiamondSquareHeightMap(int size, float waterPercentage, float hillPercentage, long seed) throws Exception {
                randomGenerator = new Random(seed);
		if (!FastMath.isPowerOfTwo(size-1)) {
			throw new Exception("HeightMap size must be 2^n+1.");
		}

		if (waterPercentage < 0 
				|| hillPercentage < 0 
				|| (waterPercentage+hillPercentage >= 1)) {
			throw new Exception("Wrong water,land percentages.");
		}

		this.size = size;
		this.waterPercentage = waterPercentage;
		this.hillPercentage = hillPercentage; 
		load();
	}

	public DiamondSquareHeightMap(int size, int roughness, int maxDisplacement, float waterPercentage, float hillPercentage, long seed) throws Exception {
                randomGenerator = new Random(seed);
		if (!FastMath.isPowerOfTwo(size-1)) {
			throw new Exception("HeightMap size must be 2^n+1.");
		}

		if (waterPercentage < 0 
				|| hillPercentage < 0 
				|| (waterPercentage+hillPercentage >= 1)) {
			throw new Exception("Wrong water,land percentages.");
		}

		this.roughness = roughness;
		this.maxDisplacement = maxDisplacement;
		this.waterPercentage = waterPercentage;
		this.hillPercentage = hillPercentage; 
		this.size = size;
		load();
	}


	private double getRandom(double scale) {
		return randomGenerator.nextGaussian() * scale;
	}


	private void diamondStep(int x, int y, int l, double d) {

		float upperLeft = getHeight(x - l, y - l);
		float upperRight = getHeight(x + l, y - l);
		float lowerLeft = getHeight(x - l, y + l);
		float lowerRight = getHeight(x + l, y + l);

		float centerHeight = (upperLeft+upperRight+lowerLeft+lowerRight)/4 + (float) getRandom(d);
		setHeight(x, y, centerHeight);

	}

	void squareStep(int x, int y, int l, double d) {

		float upLeft = getHeight(x - l, y - l);
		float upRight = getHeight(x + l, y - l);
		float downLeft = getHeight(x - l, y + l);
		float downRight = getHeight(x + l, y + l);
		float centerHeight = getHeight(x, y);

		// up
		if (y-l > 0) {
			float upup = getHeight(x, y - 2*l);
			setHeight(x, y-l, (upLeft+upRight+centerHeight+upup)/4 + (float) getRandom(d)); 
		} else {
			setHeight(x, y-l, (upLeft+upRight+centerHeight)/3 + (float) getRandom(d)); 
		}

		// down
		if (y+l < size-1) {
			float downdown = getHeight(x, y + 2*l);
			setHeight(x, y+l, (downLeft+downRight+centerHeight+downdown)/4 + (float) getRandom(d));
		} else {
			setHeight(x, y+l, (downLeft+downRight+centerHeight)/3 + (float) getRandom(d));
		}

		// left
		if (x-l > 0) {
			float leftleft = getHeight(x - 2*l, y);
			setHeight(x-l, y, (upLeft+downLeft+centerHeight+leftleft)/4 + (float) getRandom(d));
		} else {
			setHeight(x-l, y, (upLeft+downLeft+centerHeight)/3 + (float) getRandom(d));
		}

		// right
		if (x+l < size-1) {
			float rightright = getHeight(x + 2*l, y);
			setHeight(x+l, y, (upRight+downRight+centerHeight+rightright)/4 + (float) getRandom(d));
		} else {
			setHeight(x+l, y, (upRight+downRight+centerHeight)/3 + (float) getRandom(d));
		}

	}

	void generateDiamondSquareHeightMap() {
		double d = maxDisplacement;
		initCorners(d);

		for (int k = size-1; k > 1; k = k/2) {

			for (int j = k/2; j < size-1; j += k) {
				for (int i = k/2; i < size-1; i += k) {
					diamondStep(i, j, k/2, d);
				}
			}

			for (int j = k/2; j < size-1; j += k) {
				for (int i = k/2; i < size-1; i += k) {
					squareStep(i, j, k/2, d);
				}
			}

			d *= java.lang.Math.pow(2,-roughness);
		}

	}

	void initCorners(double maxDisplacement) {
		setHeight(0,0, (float) getRandom(maxDisplacement));
		setHeight(0,size-1, (float) getRandom(maxDisplacement));
		setHeight(size-1, 0, (float) getRandom(maxDisplacement));
		setHeight(size-1, size-1, (float) getRandom(maxDisplacement));
	}
	
	float getHeight(int x, int y)
	{
		assert y < size;
		assert x < size;
		return heightData[y*size+x];
	}

	void setHeight(int x, int y, float height)
	{
		assert y < size;
		assert x < size;
		heightData[y*size+x]=height;
	}

	// get height value of plain level height
	public float getLevelHeight() {
		return maxHeightCap;
	}

	// get height value of plain level height
	public float getLevelHeightPercentage() {
		float diff = maxHeight - minHeight;
		return ((getLevelHeight() - minHeight) / diff);
	}
	
	public float heightGetAbsolute(float percentage) {
		float diff = maxHeight - minHeight;
		return diff * percentage + minHeight;
	}

	public float heightGetRelative(float percentage) {
		float diff = maxHeight - minHeight;
		return diff * percentage;
	}

	// get height value of plain level using normalized scale
	public float getNormalizedLevelHeight(int normalizeMax) {
		float diff = maxHeight - minHeight;
       	return ((getLevelHeight() - minHeight) / diff) * normalizeMax;
	}

	void getCaps(double waterPercentage, double hillPercentage) {

		assert hillPercentage >= 0; 
		assert waterPercentage >= 0; 
		assert hillPercentage + waterPercentage < 1;

		float[] orderedHeightData = heightData.clone();
		Arrays.sort(orderedHeightData);

		this.minHeight = orderedHeightData[0];
		this.maxHeight = orderedHeightData[orderedHeightData.length-1];
		
		this.minHeightCap = orderedHeightData[(int)Math.floor(size*size*waterPercentage)];
		this.maxHeightCap = orderedHeightData[(int)Math.floor(size*size*(1-hillPercentage))];
	}


	public void filterTerrain() {

		getCaps(this.waterPercentage, this.hillPercentage); 

		float capLen = maxHeightCap - minHeightCap;

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (heightData[i*size+j] <= maxHeightCap && heightData[i*size+j] > minHeightCap) {
					heightData[i*size+j] = maxHeightCap;
				}
			}
		}

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (heightData[i*size+j] < minHeightCap) {
					heightData[i*size+j] += capLen;
				}
			}
		}
		this.minHeight += capLen;

	}
	
	public void normalize(float value) {
		float diff = maxHeight - minHeight;
		float heightValue;
		float normalizedHeightValue;

		for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
            	heightValue = heightData[i + j * size];
            	normalizedHeightValue =	((heightValue - minHeight) / diff) * value;
                heightData[i + j * size] = normalizedHeightValue;
            }
        }
        
	}
		
	// Reduce HeightMap size to 1/n. Return heightData
	public float[] quadraticHeightMapReduce(int n) {
		int side = (this.getSize() -1) / n;
		int smallSide = side+1;
		int largeSide = getSize();
		
		float[] inHm = getHeightMap();
		float[] outHm = new float[(side+1)*(side+1)];
		float levelHeight = getLevelHeight();
		int sqPx = n * n;
		
		for (int j = 0; j < side; j++) {
			for (int i = 0; i < side; i ++) {

				float avg = 0;
				boolean levelQuad = false;
				for (int l = 0; l < n; l ++) {
					for (int m = 0; m < n; m++) {
						float height = inHm[n*j*(largeSide)+l*(largeSide)+n*i+m];
						if (height == levelHeight) {
							levelQuad = true;
						}
						avg += height;
					}
				}
				
				outHm[j*(smallSide) + i] = (levelQuad) ? levelHeight : (float) Math.floor(avg / sqPx);
				
			}
		}

		for (int i = 0; i < side; i ++) {
			float avgX = 0;
			float avgY = 0;
			for (int l = 0; l < n; l++) {
				avgY += inHm[(i*n+l)*(largeSide)+largeSide-1];
				avgX += inHm[(largeSide)*(largeSide-1)+i*n+l];
			}
			outHm[i*smallSide+(smallSide-1)] = avgY / n;
			outHm[(smallSide)*(smallSide-1)+i] = avgX / n;
		}

		outHm[smallSide*smallSide-1] = inHm[largeSide*largeSide-1];
		
		return outHm;
		
	}

	
	public void reduce(int n) {
		heightData = quadraticHeightMapReduce(n);
		size = (size -1) / n +1; 
	}

	
	public java.nio.ByteBuffer createAlphaChannel(float fadeIn, float fullIn, 
			float fullOut, float fadeOut) {

		java.nio.ByteBuffer buff = ByteBuffer.allocateDirect(size*size);
		
		for (int j = 0; j < size; j++) {
			for (int i = 0; i < size; i++) {
				float height = heightData[(size-j-1)*size+i];
				byte alphaMapColor = 0;
				if (height < fadeIn || height > fadeOut) {
					alphaMapColor = 0;
				} else if (height < fullIn) {
					float len = Math.abs(fullIn - fadeIn);
					float k = Math.abs(height - fadeIn);
					alphaMapColor = (len > 0) ? (byte) ((255 * k / len)) : (byte) 255;
				} else if (height <= fullOut) {
					alphaMapColor = (byte) 0xFF;
				} else if (height < fadeOut) {
					float len = Math.abs(fadeOut - fullOut);
					float k = Math.abs(height - fadeOut);
					alphaMapColor = (len > 0) ? (byte) ((255 * k / len)) : (byte) 255;
				}
				buff.put(alphaMapColor);
			}
		}
		return buff;
	}
	
	public boolean load()
	{

		if (null != heightData)
		{
			unloadHeightMap();
		}
		heightData = new float[size*size];  

		generateDiamondSquareHeightMap();

		erodeTerrain();

		filterTerrain();

		return true; 
	}

}
