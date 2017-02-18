package game;

public interface ISocket {
	void close();

	void sendScore(int score);
	
	void sendMapName(String name, String difficulty);
}
