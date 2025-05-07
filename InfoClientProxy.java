public class InfoClientProxy implements InfoService
{
    private String serverName;
    private Requestor comm;
    private Marshaller marshaller;

    public InfoClientProxy(String targetServer)
    {
        serverName=targetServer;
        comm=new Requestor("InfoClient");
        marshaller=new Marshaller();
    }

    public String getInfo(String query)
    {
        try
        {
            Entry dispatcher=new Entry("localhost", 3069);
            byte[] lookupMessage=("LOOKUP "+serverName).getBytes();
            byte[] response=comm.deliver_and_wait_feedback(dispatcher, lookupMessage);
            String[] parts=new String(response).split(" ");
            Entry serverAddress=new Entry(parts[0], Integer.parseInt(parts[1]));

            Message reqMessage=new Message("InfoClient", query);
            byte[] data=marshaller.marshal(reqMessage);
            byte[] resp=comm.deliver_and_wait_feedback(serverAddress, data);
            Message respMessage=marshaller.unmarshal(resp);

            return respMessage.data;
        }
        catch(Exception e)
        {
            return "Error contacting server.";
        }
    }
}
