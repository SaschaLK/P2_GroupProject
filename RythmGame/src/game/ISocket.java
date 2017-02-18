package game;

public interface ISocket {
	void close();

	void sendScore(int score);
}
