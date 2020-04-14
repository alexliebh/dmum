package be.alexandreliebh.picacademy.data.net;

import java.io.IOException;

import com.google.gson.Gson;

import be.alexandreliebh.picacademy.data.PicConstants;
import be.alexandreliebh.picacademy.data.net.packet.PicAbstractPacket;
import be.alexandreliebh.picacademy.data.net.packet.PicPacketType;
import be.alexandreliebh.picacademy.data.net.packet.utility.PicBadPacket;
import be.alexandreliebh.picacademy.data.util.Compressor;

public class PacketUtil {
	private static final Gson GSON = new Gson();

	private final static boolean displayJSON = PicConstants.DISPLAY_JSON;

	public static byte[] getPacketAsBytes(PicAbstractPacket pa) throws IOException {
		String json = pa.getType().getHeader() + GSON.toJson(pa);
		if (displayJSON)
			System.out.println("[-] "+json);
		return Compressor.compress(json);
		// return data.getBytes();
	}

	public static PicAbstractPacket getBytesAsPacket(byte[] by) throws IOException {
		if (by == null) {
			return new PicBadPacket();
		}
		String content = Compressor.decompress(by);
		String header = content.substring(0, 3);
		PicPacketType type = null;
		for (PicPacketType pt : PicPacketType.values()) {
			if (pt.getHeader().equals(header)) {
				type = pt;
				break;
			}
		}
		String serialClass = content.substring(3, content.length());
		if (displayJSON)
			System.out.println("[+] "+header + serialClass);
		return GSON.fromJson(serialClass, type.getPacketClass());
	}

	public enum DisconnectionReason {
		TIME_OUT, KICKED, LEFT;
	}

}
