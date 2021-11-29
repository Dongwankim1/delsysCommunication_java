package DelsysCommu;

public class TestMain {
	public static void main(String[] args) {
		byte[] data = {-70, -87, -68, -62};
		System.out.println(byteArrayToFloat(data));
	}

	public static float byteArrayToFloat(byte[] bytes) {
	    int intBits = bytes[0] << 24
	                    | (bytes[1] & 0xFF) << 16
	                    | (bytes[2] & 0xFF) << 8
	                    | (bytes[3] & 0xFF);
	    return Float.intBitsToFloat(intBits);
	}
}
