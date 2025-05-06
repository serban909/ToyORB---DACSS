import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

class ByteSender
{
    private Socket socket;
    private DataOutputStream out;

    public ByteSender(String host, int port) throws IOException
    {
        socket=new Socket(host, port);
        out=new DataOutputStream(socket.getOutputStream());
    }

    public void sendBytes(byte[] data) throws IOException
    {
        out.writeInt(data.length);
        out.write(data);
        out.flush();
    }

    public void close() throws IOException
    {
        out.close();
        socket.close();
    }
}

class ByteReceiver
{
    private Socket socket;
    private DataInputStream in;

    public ByteReceiver(Socket socket) throws IOException
    {
        this.socket=socket;
        in=new DataInputStream(socket.getInputStream());
    }

    public byte[]  receiveBytes() throws IOException
    {
        int length=in.readInt();

        byte[] data=new byte[length];
        in.readFully(data);
        
        return data;
    }

    public void close() throws IOException
    {
        in.close();
        socket.close();
    }
}

class ByteListener
{
    private ServerSocket serverSocket;

    public ByteListener(int port) throws IOException
    {
        serverSocket=new ServerSocket(port);
    }

    public Socket acceptConnection() throws IOException
    {
        return serverSocket.accept();
    }

    public void close() throws IOException
    {
        serverSocket.close();
    }
}

class InfoServer
{
    public static void main(String[] args) throws IOException
    {
        int port=12345;
        ByteListener listener=new ByteListener(port);

        System.out.println("InfoServer started on port " + port);

        while(true)
        {
            Socket clientSocket =listener.acceptConnection();
            ByteReceiver receiver=new ByteReceiver(clientSocket);
            byte[] requestBytes=receiver.receiveBytes();
            String request=new String(requestBytes);

            String response=handleRequest(request);
            receiver.sendBytes(response.getBytes());

            clientSocket.close();
        }
    }

    private static String handleRequest(String request)
    {
        if (request.startsWith("get_road_info:")) 
        {
            int roadId = Integer.parseInt(request.split(":")[1]);
            return "Road " + roadId + " is clear";
        } 
        else if (request.startsWith("get_temp:")) 
        {
            String city = request.split(":")[1];
            return "Temperature in " + city + " is 22°C";
        } 
        else {
            return "Unknown command";
        }
    }
}

class InfoClient
{
    public static void main(String[] args) throws IOException
    {
        ByteSender sender=new ByteSender("localhost", 12345);

        String request1="get_road_info:101";
        sender.sendBytes(request1.getBytes());
        byte[] response1=sender.receiveBytes();
        System.out.println("Response: " + new String(response1));

        String request2="get_temp:Bucharest";
        sender.sendBytes(request2.getBytes());
        byte[] response2=sender.receiveBytes();
        System.out.println("Response: " + new String(response2));

        sender.close();
    }
}

class NamingService
{
    private static final int port=8888;
    private static final Map<String, String> registry=new HashMap<>();

    public static void main(String[] args) throws IOException
    {
        ByteListener listener=new ByteListener(port);
        System.out.println("NamingService started on port " + PORT);

        registry.put("InfoServer", "localhost:12345");

        while(true)
        {
            Socket clientSocket listener.acceptConnection();
            ByteReceiver receiver=new ByteReceiver(clientSocket);

            byte[] requestBytes=receiver.receiveBytes();
            String request = new String(requestBytes);
            String response=handleRequest(request);

            receiver.sendBytes(response.getBytes());

            clientSocket.close();
        }
    }

    private static String handleRequest(String request)
    {
        
    }
}

public class ToyORB 
{
    
}
