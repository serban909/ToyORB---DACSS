import java.util.HashMap;
import java.util.Map;

public class Dispatcher 
{
    private static final int DISPATCHER_PORT=3069;
    private static Map<String, Entry> registry= new HashMap<>();

    public static void main(String[] args) 
    {
        Entry dispatcherAddress=new Entry("localhost", DISPATCHER_PORT);
        Replyer replyer=new Replyer("Dispatcher", dispatcherAddress);

        while (true) 
        {
            replyer.receive_transform_and_send_feedback(bytes ->
            {
                String request=new String(bytes);
                String[] parts=request.split(" ");
                String command=parts[0];

                if("REGISTER".equals(command))
                {
                    String name=parts[1];
                    String host=parts[2];
                    int port=Integer.parseInt(parts[3]);
                    registry.put(name, new Entry(host, port));
                    return "OK".getBytes();
                }
                else if("LOOKUP".equals(command))
                {
                    String name=parts[1];
                    Entry entry=registry.get(name);
                    if(entry==null) return "NOT_FOUND".getBytes();
                    return (entry.dest()+" "+entry.port()).getBytes();
                }
                return "INVALID".getBytes();
            });
        }
    }
}
