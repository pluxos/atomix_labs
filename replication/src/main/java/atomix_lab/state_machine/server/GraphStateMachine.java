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
        
        @Override
        public String toString()
        {
        	return "("+ a + "," + b + ")";
        }
        
        @Override
        public boolean equals(Object arg0) {
        	if (arg0 instanceof Pair)
        	{
        		@SuppressWarnings("rawtypes")
				Pair other = (Pair) arg0;
        		return a.equals(other.a) && b.equals(other.b);
        	}
        	else
        	{
        		return false;
        	}
        }
        
        @Override
        public int hashCode() {        	
        	return a.hashCode() + b.hashCode();
        }
    }
    
    private Map<Integer,Vertex> vertices = new HashMap<>();
    private Map<Pair<Integer,Integer>,Edge> edges = new HashMap<>();

    public Boolean AddEdge(Commit<AddEdgeCommand> commit){
        try{
            Pair<Integer,Integer> p = new Pair<>();
            p.a = commit.operation().id;
            p.b = commit.operation().id2;

            Edge e = new Edge(commit.operation().id, commit.operation().id2, commit.operation().desc);
            
            System.out.println("Adding " + e);
            
            return edges.putIfAbsent(p, e) == null;
        }finally{
            commit.close();
        }
    }
    
    public Boolean AddVertex(Commit<AddVertexCommand> commit){
        try{
            Vertex v = new Vertex(commit.operation().id, commit.operation().desc);
            
            System.out.println("Adding " + v);

            return vertices.putIfAbsent(commit.operation().id, v) == null;
        }finally{
            commit.close();
        }
    }

    public Edge GetEdge(Commit<GetEdgeQuery> commit){
    	try{
    		Pair<Integer,Integer> p = new Pair<>();
    		p.a = commit.operation().id;
    		p.b = commit.operation().id2;

    		System.out.println("Vertices:" + vertices);
    		System.out.println("Edges:" + edges);

    		Edge result = edges.get(p); 
    		System.out.println("GetEdge " + p + " = " + result);
            return result;
        }finally{
            commit.close();
        }
    }
    
    public Vertex GetVertex(Commit<GetVertexQuery> commit){
    	try{
    		System.out.println("Vertices:" + vertices);
    		System.out.println("Edges:" + edges);

    		Vertex result = vertices.get(commit.operation().id);
    		System.out.println("GetVertex " + commit.operation().id + " = " + result);
    		return result;
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
