import java.net.*;
import java.io.*;
//import Commons.Address;
import java.util.Hashtable;

interface Address 
{
    String dest();
    int port();
}

interface ByteStreamTransformer
{
	public byte[] transform(byte[] in);
}

interface InfoService
{
    String getInfo(String query);
}

class InfoServerImpl implements InfoService
{
    public String getInfo(String query)
    {
        return "Hello from server, you said: " + query;
    }
}

class ByteReceiver
{
	private ServerSocket srvS;
	private Socket s;
	private InputStream iStr;

	private String myName;
	private Address myAddr;

	public ByteReceiver(String theName, Address theAddr)
	{
		myName = theName;
		myAddr = theAddr;
		try
		{
			srvS = new ServerSocket(myAddr.port(), 1000);
			System.out.println("Receiver Serversocket:" + srvS);
		}
		catch (Exception e)
		{
			System.out.println("Error opening server socket");
		}
	}


	public byte[] receive()
	{
		int val;
		byte buffer[] = null;
		try
		{
			s = srvS.accept();
			System.out.println("Receiver accept: Socket" + s);
			iStr = s.getInputStream();
			val = iStr.read();
			buffer = new byte[val];
			iStr.read(buffer);


			iStr.close();
			s.close();

		}
		catch (IOException e)
		{
			System.out.println("IOException in receive_transform_and_feedback");
		}
		return buffer;
	}

	public void close() throws IOException
    {
        if(srvS!= null && !srvS.isClosed()) srvS.close();
    }
}

class ByteSender
{
	private Socket s;
	private OutputStream oStr;
	private String myName;
	public ByteSender(String theName) { myName = theName; }


	public void deliver(Address theDest, byte[] data)
	{
		try
		{
			s = new Socket(theDest.dest(), theDest.port());
			System.out.println("Sender: Socket" + s);
			oStr = s.getOutputStream();
			oStr.write(data);
			oStr.flush();
			oStr.close();
			s.close();
		}
		catch (IOException e)
		{
			System.out.println("IOException in deliver");
		}
	}
}

class Marshaller
{
	public byte[] marshal(Message theMsg)
	{
		String m = "  " + theMsg.sender + ":" + theMsg.data;
		byte b[] = new byte[m.length()];
		b = m.getBytes();
		b[0] = (byte)m.length();
		return b;
	}
	public Message unmarshal(byte[] byteArray)
	{
		String msg = new String(byteArray);
		String sender = msg.substring(1, msg.indexOf(":"));
		String m = msg.substring(msg.indexOf(":")+1, msg.length()-1);
		return new Message(sender, m);
	}

}

class Message
{
	public String sender;
	public String data;
	public Message(String theSender, String rawData)
	{
		sender = theSender;
		data = rawData;
	}
}

class Entry implements Address
{
	private String destinationId;
	private int portNr;
	public Entry(String theDest, int thePort)
	{
		destinationId = theDest;
		portNr = thePort;
	}
	public String dest()
	{
		return destinationId;
	}
	public int port()
	{
		return portNr;
	}
}

class Registry
{
	private Hashtable hTable = new Hashtable();
	private static Registry _instance = null;

	private Registry() { }

	public static Registry instance()
	{
		if (_instance == null)
			_instance = new Registry();
		return _instance;
	}

	public void put(String theKey, Entry theEntry)
	{
		hTable.put(theKey, theEntry);
	}
	public Entry get(String aKey)
	{
		return (Entry)hTable.get(aKey);
	}
}

class Replyer
{
	private ServerSocket srvS;
	private Socket s;
	private InputStream iStr;
	private OutputStream oStr;
	private String myName;
    private Address myAddr;
	
	public Replyer(String theName, Address theAddr) 
    {
              myName = theName; 
              myAddr=theAddr;
              try 
              {
              	srvS = new ServerSocket(myAddr.port(), 1000);
                System.out.println("Replyer Serversocket:"+srvS);
	          } 
              catch (Exception e) 
              { 
                 System.out.println("Error opening server socket");
		      }
	}


	public void receive_transform_and_send_feedback(ByteStreamTransformer t)
	{
		int val;
		byte buffer[] = null;
		try
		{
			s = srvS.accept();
            System.out.println("Replyer accept: Socket"+s);
			iStr = s.getInputStream();
			val = iStr.read();
			buffer = new byte[val];
			iStr.read(buffer);
	
			byte[] data = t.transform(buffer);

			oStr = s.getOutputStream();
			oStr.write(data);
			oStr.flush();
			oStr.close();
            iStr.close();
			s.close();
			
		}
		catch (IOException e) { 
                      System.out.println("IOException in receive_transform_and_feedback"); }
		
	}

	public void close() throws IOException
    {
        if(srvS!= null && !srvS.isClosed()) srvS.close();
    }
}

class Requestor
{

	private Socket s;
	private OutputStream oStr;
	private InputStream iStr;
	private String myName;
	public Requestor(String theName) { myName = theName; }


	public byte[] deliver_and_wait_feedback(Address theDest, byte[] data)
	{

		byte[] buffer = null;
		int val;
		try
		{
			s = new Socket(theDest.dest(), theDest.port());
            System.out.println("Requestor: Socket"+s);
			oStr = s.getOutputStream();
			oStr.write(data);
			oStr.flush();
			iStr = s.getInputStream();
			val = iStr.read();
			buffer = new byte[val];
			iStr.read(buffer);
			iStr.close();
                        oStr.close();
			s.close();
			}
		catch (IOException e) { 
                       System.out.println("IOException in deliver_and_wait_feedback"); }
		return buffer;
	}

}

public class ToyORB2si 
{
    // cd C:\Users\Serban\Documents\DACSS\Assignment 4
}
