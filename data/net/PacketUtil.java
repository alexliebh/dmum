package be.alexandreliebh.picacademy.data.net;

import java.io.IOException;

import com.google.gson.Gson;

import be.alexandreliebh.picacademy.data.net.packet.PicBadPacket;
import be.alexandreliebh.picacademy.data.net.packet.PicPacket;
import be.alexandreliebh.picacademy.data.net.packet.PicPacketType;
import be.alexandreliebh.picacademy.data.util.Compressor;

public class PacketUtil {
	private static final Gson GSON = new Gson();

	private final static boolean displayJSON = false;

	public static byte[] getPacketAsBytes(PicPacket pa) throws IOException {
		String json = pa.getType().getHeader() + GSON.toJson(pa);
		if (displayJSON)
			System.out.println(json);
		return Compressor.compress(json);
		// return data.getBytes();
	}

	public static PicPacket getBytesAsPacket(byte[] by) throws IOException {
		if (by == null) {
			return new PicBadPacket();
		}
		String json = Compressor.decompress(by);
		String header = json.substring(0, 3);
		PicPacketType type = null;
		for (PicPacketType pt : PicPacketType.values()) {
			if (pt.getHeader().equals(header)) {
				type = pt;
				break;
			}
		}
		String serialClass = json.substring(3, json.length());
		if (displayJSON)
			System.out.println(serialClass);
		return GSON.fromJson(serialClass, type.getPacketClass());
	}

	public enum DisconnectionReason {
		TIME_OUT, KICKED, LEFT;
	}

}
