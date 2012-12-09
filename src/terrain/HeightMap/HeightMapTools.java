package terrain.HeightMap;

import java.nio.ByteBuffer;

public class HeightMapTools {

	public static java.nio.ByteBuffer combineChannels(ByteBuffer red, ByteBuffer green, ByteBuffer blue, ByteBuffer alpha) {

		int len = 0;

		if (red != null) {
			red.rewind();
			len = red.capacity();
		}
		if (green != null) {
			green.rewind();
			len = green.capacity();
		}
		if (blue != null) {
			blue.rewind();
			len = blue.capacity();
		}
		if (alpha != null) {
			alpha.rewind();
			len = alpha.capacity();
		}

		java.nio.ByteBuffer buff = ByteBuffer.allocateDirect(len*4);
		for (int i = 0; i < len; i++) {

			if (red != null) {
				buff.put(red.get());
			} else {
				buff.put((byte) 0);
			}

			if (green != null) {
				buff.put(green.get());
			} else {
				buff.put((byte) 0);
			}

			if (blue != null) {
				buff.put(blue.get());
			} else {
				buff.put((byte) 0);
			}

			
			if (alpha != null) {
				buff.put(alpha.get());
			} else {
				buff.put((byte) 0);
			}

		}
		return buff;
	}
		
}
