package tau.heroes.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NetworkServer
{
	private ServerSocketChannel serverSocketChannel;
	private int serverPort;
	private Thread listenThread;

	public NetworkServer(int port) throws IOException
	{
		serverPort = port;
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);
	}

	private void listen()
	{
		Selector selector;
		try
		{
			selector = Selector.open();
			serverSocketChannel.socket().bind(new InetSocketAddress(serverPort));
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

			while (true)
			{
				int count = selector.select();
				if (count == 0)
					break;
				Iterator<SelectionKey> it = selector.selectedKeys().iterator();
				while (it.hasNext())
				{
					SelectionKey selKey = it.next();
					it.remove();
					if (selKey.isAcceptable())
					{
						SocketChannel sChannel = serverSocketChannel.accept();
						handleNewPeer(sChannel.socket());
					}
				}
			}
		}
		catch (IOException e)
		{
			System.out.println("IOException caught in listen():");
			e.printStackTrace();
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
