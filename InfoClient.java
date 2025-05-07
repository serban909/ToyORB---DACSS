public class InfoClient 
{
    public static void main(String[] args) 
    {
        Entry serverAddress=new Entry("localhost", 2025);

        Message message=new Message("InfoClient", "Hello, Server!");
        Marshaller marshaller=new Marshaller();
        byte[] data=marshaller.marshal(message);

        ByteSender sender=new ByteSender("InfoClient");
        sender.deliver(serverAddress, data);

        System.out.println("Client sent message to server.");

        ////////////////////////////////////Milestone 2///////////////////////////////
        
        Entry dispatcher=new Entry("localhost", 3069);
        Requestor req =new Requestor("InfoClient");

        String lookup="LOOKUP InfoServer";
        byte[] response=req.deliver_and_wait_feedback(dispatcher, lookup.getBytes());
        String[] resp=new String(response).split(" ");
        
        serverAddress=new Entry(resp[0], Integer.parseInt(resp[1]));
    }
}
