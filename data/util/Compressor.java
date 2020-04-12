package be.alexandreliebh.picacademy.data.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Compressor {

	public static final byte[] compress(final String str) throws IOException {
		if ((str == null) || (str.length() == 0)) {
			return null;
		}

		ByteArrayOutputStream obj = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(obj);
		gzip.write(str.getBytes("UTF-8"));
		gzip.close();
//		System.out.println("SIZE BF COMP: " + str.getBytes().length);
//		System.out.println("SIZE AF COMP: " + obj.toByteArray().length);
		return obj.toByteArray();
	}

	public static final String decompress(final byte[] compressed) throws IOException {
		String outStr = "";

		if ((compressed == null) || (compressed.length == 0)) {
			return "";
		}
		if (isCompressed(compressed)) {
			GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(compressed));
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				outStr += line;
			}
			gis.close();
		} else {
			outStr = new String(compressed);
		}

		return outStr;
	}

	private static final boolean isCompressed(final byte[] compressed) {
		return (compressed[0] == (byte) (GZIPInputStream.GZIP_MAGIC)) && (compressed[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8));
	}

}
