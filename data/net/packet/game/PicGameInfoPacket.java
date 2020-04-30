package be.alexandreliebh.picacademy.data.net.packet.game;

import be.alexandreliebh.picacademy.data.game.PicGame;
import be.alexandreliebh.picacademy.data.game.PicGameInfo;
import be.alexandreliebh.picacademy.data.net.packet.PicPacketType;

public class PicGameInfoPacket extends PicAbstractGamePacket {

	private PicGameInfo gameInfo;

	public PicGameInfoPacket(PicGame game) {
		super(PicPacketType.GAME_INFO, game.getGameID());
		this.gameInfo = game.getGameInfo();
	}

	public PicGameInfo getGameInfo() {
		return gameInfo;
	}

}
