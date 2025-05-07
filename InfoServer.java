public class InfoServer 
{
    public static void main(String[] args) 
    {
        Entry myAddress=new Entry("localhost", 2025);
        ByteReceiver receiver=new ByteReceiver("InfoServer", myAddress);

        byte[] receiveBytes=receiver.receive();
        Marshaller marshaller=new Marshaller();
        Message message= marshaller.unmarshal(receiveBytes);

        System.out.println("Server received message from " + message.sender + ": " + message.data);

        ////////////////////////////////////Milestone 2///////////////////////////////
        
        Entry dispatcher=new Entry("localhost", 3069);
        Requestor reg=new Requestor("InfoServer");

        String regMessage="REGISTER InfoServer localhost 2025";
        reg.deliver_and_wait_feedback(dispatcher, regMessage.getBytes());
    }
}
