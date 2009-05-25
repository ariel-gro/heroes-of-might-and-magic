package tau.heroes.net;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

public class NetworkPeer
{
	private Socket socket;
	private Thread readThread;
	private boolean isWaiting;
	private Message waitingMessage;

	public NetworkPeer(Socket socket)
	{
		this.socket = socket;
	}

	public boolean connect(String host, int port)
	{
		try
		{
			socket.connect(new InetSocketAddress(host, port));
			startListening();
			return true;
		}
		catch (IOException e)
		{
			return false;
		}
	}
	
	public void disconnect()
	{
		disconnect(true);
	}

	public void disconnect(boolean sendDisconnectMessage)
	{
		try
		{
			if (isConnected() && sendDisconnectMessage)
				asyncSendMessage(new DisconnectMessage());
			socket.close();
			stopListening();
		}
		catch (IOException e)
		{
			System.out.println("IOException caught in disconnect():");
			e.printStackTrace();
		}
	}

	public void startListening()
	{
		readThread = new Thread(new Runnable() {
			public void run()
			{
				readLoop();
			}
		});

		readThread.start();
	}

	public void stopListening()
	{
		readThread.interrupt();
		try
		{
			readThread.join();
		}
		catch (InterruptedException e)
		{
			
		}
	}

	private void readLoop()
	{
		while (true)
		{
			Message message = null;
			try
			{
				if (socket.isClosed() || !socket.isConnected())
					break;
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				message = (Message) ois.readObject();
			}
			catch (EOFException e)
			{
				handleDisconnect(false);
				break;
			}
			catch (SocketException e)
			{
				if (!e.getMessage().equalsIgnoreCase("socket closed"))
					handleDisconnect(false);
				break;
			}
			catch (IOException e)
			{
				handleDisconnect(false);
				break;
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			}

			if (message != null)
			{
				synchronized (this)
				{
					if (isWaiting)
					{
						waitingMessage = message;
						notify();
					}
					else
						handleIncomingMessage(message);
				}
			}
		}
	}

	protected void handleDisconnect(boolean sendDisconnectMessage)
	{
		disconnect(sendDisconnectMessage);
	}

	private void handleIncomingMessage(Message message)
	{
		if (AsyncMessage.class.isAssignableFrom(message.getClass()))
			handleIncomingAsyncMessage((AsyncMessage) message);
		else
		{
			Message reply = handleIncomingSyncMessage((SyncMessage) message);
			asyncSendMessage(reply);			
		}
	}

	protected void handleIncomingAsyncMessage(AsyncMessage message)
	{
		
	}

	protected AsyncMessage handleIncomingSyncMessage(SyncMessage message)
	{
		return new OKMessage();
	}

	public Message syncSendMessage(Message message)
	{
		try
		{
			synchronized (this)
			{
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				waitingMessage = null;
				isWaiting = true;
				oos.writeObject(message);
				wait();
				isWaiting = false;
				return waitingMessage;
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	public void asyncSendMessage(Message message)
	{
		try
		{
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(message);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public Socket getSocket()
	{
		return socket;
	}
	
	public boolean isConnected()
	{
		return (!socket.isClosed() && socket.isConnected());
	}
}
