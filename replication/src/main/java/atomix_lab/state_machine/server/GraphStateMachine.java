package atomix_lab.state_machine.server;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import atomix_lab.state_machine.command.AddEdgeCommand;
import atomix_lab.state_machine.command.AddVertexCommand;
import atomix_lab.state_machine.command.GetEdgeQuery;
import atomix_lab.state_machine.command.GetVertexQuery;
import atomix_lab.state_machine.type.Edge;
import atomix_lab.state_machine.type.Vertex;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.netty.NettyTransport;
import io.atomix.copycat.server.Commit;
import io.atomix.copycat.server.CopycatServer;
import io.atomix.copycat.server.StateMachine;
import io.atomix.copycat.server.storage.Storage;
import io.atomix.copycat.server.storage.StorageLevel;

public class GraphStateMachine extends StateMachine
{
    class Pair<A,B>{
        A a;
        B b;
    }
    
    private Map<Integer,Vertex> vertices = new HashMap<>();
    private Map<Pair<Integer,Integer>,Edge> edges = new HashMap<>();

    public Object AddEdge(Commit<AddEdgeCommand> commit){
        try{
            Pair<Integer,Integer> p = new Pair<>();
            p.a = commit.operation().id;
            p.b = commit.operation().id2;

            Edge e = new Edge(commit.operation().id, commit.operation().id2, commit.operation().desc);
            return edges.putIfAbsent(p, e) == null;
        }finally{
            commit.close();
        }
    }
    
    public Object AddVertex(Commit<AddVertexCommand> commit){
        try{
            Vertex v = new Vertex(commit.operation().id, commit.operation().desc);
            return vertices.putIfAbsent(commit.operation().id, v) == null;
        }finally{
            commit.close();
        }
    }

    public Object GetEdge(Commit<GetEdgeQuery> commit){
        try{
            Pair<Integer,Integer> p = new Pair<>();
            p.a = commit.operation().id;
            p.b = commit.operation().id2;

            return edges.get(p);
        }finally{
            commit.close();
        }
    }
    
    public Object GetVertex(Commit<GetVertexQuery> commit){
        try{
            return vertices.get(commit.operation().id);
        }finally{
            commit.close();
        }
    }



    public static void main( String[] args ){
    	int myId = Integer.parseInt(args[0]);
    	List<Address> addresses = new LinkedList<>();
    	
    	for(int i = 1; i <args.length; i+=2)
    	{
            Address address = new Address(args[i], Integer.parseInt(args[i+1]));
    		addresses.add(address);
    	}
    		
        CopycatServer.Builder builder = CopycatServer.builder(addresses.get(myId))
                                                     .withStateMachine(GraphStateMachine::new)
                                                     .withTransport( NettyTransport.builder()
                                                                     .withThreads(4)
                                                                     .build())
                                                     .withStorage( Storage.builder()
                                                                   .withDirectory(new File("logs_"+myId)) //Must be unique
                                                                   .withStorageLevel(StorageLevel.DISK)
                                                                   .build());
        CopycatServer server = builder.build();
        server.serializer().register(AddVertexCommand.class);
        server.serializer().register(AddEdgeCommand.class);
        server.serializer().register(GetVertexQuery.class);
        server.serializer().register(GetEdgeQuery.class);


        if(myId == 0)
        {
            server.bootstrap().join();
        }
        else
        {
            server.join(addresses).join();
        }
    }
}
