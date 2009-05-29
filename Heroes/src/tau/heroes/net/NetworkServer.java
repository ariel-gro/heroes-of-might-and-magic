package tau.heroes.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkServer
{
	private ServerSocket serverSocket;
	private Thread listenThread;

	public NetworkServer(int port) throws IOException
	{
		serverSocket = new ServerSocket(port);
	}

	private void listen()
	{
		try
		{
			while (true)
			{
				Socket socket = serverSocket.accept();
				handleNewPeer(socket);
			}
		}
		catch (IOException e)
		{
			if (!e.getMessage().equalsIgnoreCase("socket closed"))
			{
				System.out.println("IOException caught in listen():");
				e.printStackTrace();
			}
		}
	}

	protected void handleNewPeer(Socket newSocket)
	{

	}

	public void startListening()
	{
		listenThread = new Thread(new Runnable() {
			public void run()
			{
				listen();
			}
		});

		listenThread.start();
	}

	public void stopListening()
	{
		try
		{
			serverSocket.close();
		}
		catch (IOException e)
		{
		}
		
		listenThread.interrupt();
		
		try
		{
			listenThread.join();
		}
		catch (InterruptedException e)
		{
			System.out.println("InterruptedException caught in stopListening():");
			e.printStackTrace();
		}
	}
}
