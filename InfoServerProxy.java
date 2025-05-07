public class InfoServerProxy 
{
    public static void main(String[] args) 
    {
        Entry serverAddress =new Entry("localhost", 2025);
        Replyer replyer=new Replyer("InfoServer", serverAddress);
        InfoService realServer=new InfoServerImpl();
        Marshaller marshaller=new Marshaller();

        Entry dispatcher=new Entry("localhost", 3069);
        Requestor reg=new Requestor("InfoServer");
        String regMessage="REGISTER InfoServer localhost 2025";
        reg.deliver_and_wait_feedback(dispatcher, regMessage.getBytes());

        while (true) 
        {
            replyer.receive_transform_and_send_feedback(reqBytes ->
            {
                Message reqMessage=marshaller.unmarshal(reqBytes);
                String result=realServer.getInfo(reqMessage.data);
                Message respMessage=new Message("InfoServer", result);
                return marshaller.marshal(respMessage);
            });    
        }
    }    
}
