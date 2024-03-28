// IN2011 Computer Networks
// Coursework 2023/2024
//
// Construct the hashID for a string

import java.lang.StringBuilder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class HashID {
    public static byte [] computeHashID(String line) throws Exception {
		if (line.endsWith("\n")) {
			// What this does and how it works is covered in a later lecture
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(line.getBytes(StandardCharsets.UTF_8));
			return md.digest();

		} else {
			// 2D#4 computes hashIDs of lines, i.e. strings ending with '\n'
			throw new Exception("No new line at the end of input to HashID");
		}
    }

	public static int calculateDistance(byte[] hashID1, byte[] hashID2) {
		int distance = 0;

		for (int i = 0; i < hashID1.length; i++) {
			byte xorResult = (byte) (hashID1[i] ^ hashID2[i]);

			for (int bit = 7; bit >= 0; bit--) { // Iterate over bits within a byte
				if ((xorResult & (1 << bit)) == 0) {
					distance++;  // If bit match, increment distance
				} else {
					break; // Non-matching bit found, stop checking this byte
				}
			}
		}

		return 256 - distance; // 256 - (number of matching bits)
	}

	public static String computeHashHex(String s) throws Exception {
		byte[] hashedString = computeHashID(s);
		StringBuilder hexBuilder = new StringBuilder(hashedString.length * 2);
		for (byte b : hashedString) {
			hexBuilder.append(String.format("%02X", b));
		}
		return hexBuilder.toString();
	}

	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
					+ Character.digit(s.charAt(i+1), 16));
		}
		return data;
	}

}
