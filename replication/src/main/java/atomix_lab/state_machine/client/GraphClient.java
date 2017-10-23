package atomix_lab.state_machine.client;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.LinkedList;
import java.util.List;

import atomix_lab.state_machine.command.AddEdgeCommand;
import atomix_lab.state_machine.command.AddVertexCommand;
import atomix_lab.state_machine.command.GetEdgeQuery;
import atomix_lab.state_machine.command.GetVertexQuery;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.netty.NettyTransport;
import io.atomix.copycat.client.CopycatClient;
import io.atomix.copycat.server.StateMachine;

public class GraphClient extends StateMachine
{
    public static void main( String[] args ){
    	List<Address> addresses = new LinkedList<>();

        CopycatClient.Builder builder = CopycatClient.builder()
                                                     .withTransport( NettyTransport.builder()
                                                     .withThreads(4)
                                                     .build());
        CopycatClient client = builder.build();
        client.serializer().register(AddVertexCommand.class);
        client.serializer().register(AddEdgeCommand.class);
        client.serializer().register(GetVertexQuery.class);
        client.serializer().register(GetEdgeQuery.class);

        for(int i = 0; i <args.length;i+=2)
    	{
            Address address = new Address(args[i], Integer.parseInt(args[i+1]));
    		addresses.add(address);
    	}
        
        CompletableFuture<CopycatClient> future = client.connect(addresses);
        future.join();

        CompletableFuture[] futures = new CompletableFuture[3];
        futures[0] = client.submit(new AddVertexCommand(1,1, "Hello world!"));
        futures[1] = client.submit(new AddVertexCommand(2,2, "world! Hello"));
        futures[2] = client.submit(new AddEdgeCommand(1,2, "Edge"));

        CompletableFuture.allOf(futures).thenRun(() -> System.out.println("Commands completed!"));

        client.submit(new GetVertexQuery(1)).thenAccept(result -> {
            System.out.println("1: " + result);
        });

        client.submit(new GetVertexQuery(2)).thenAccept(result -> {
            System.out.println("2: " + result);
        });

        client.submit(new GetEdgeQuery(1,2)).thenAccept(result -> {
            System.out.println("1-2: " + result);
        });
    }
}
