package game;

public interface ISocket {
	void close();

	void sendScore(int score);
	
	void sendMapInfo(String name, String difficulty, String mods);
}
