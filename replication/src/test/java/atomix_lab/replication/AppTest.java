package atomix_lab.replication;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.LinkedList;
import java.util.List;

import atomix_lab.state_machine.client.*;
import atomix_lab.state_machine.server.*;
import atomix_lab.state_machine.command.AddEdgeCommand;
import atomix_lab.state_machine.command.AddVertexCommand;
import atomix_lab.state_machine.command.GetEdgeQuery;
import atomix_lab.state_machine.command.GetVertexQuery;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.netty.NettyTransport;
import io.atomix.copycat.client.CopycatClient;
import io.atomix.copycat.server.StateMachine;


public class AppTest 
    extends TestCase
{
    public AppTest( String testName )
    {
        super( testName );
    }

    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    public void testApp()
    {
    	String[] args0 = new String[]{"0", "127.0.0.1","5000", "127.0.0.1", "5001", "127.0.0.1", "5002"};
    	GraphStateMachine.main(args0);

    	String[] args1 = new String[]{"1", "127.0.0.1","5000", "127.0.0.1", "5001", "127.0.0.1", "5002"};
    	GraphStateMachine.main(args1);

    	String[] args2 = new String[]{"2", "127.0.0.1","5000", "127.0.0.1", "5001", "127.0.0.1", "5002"};
    	GraphStateMachine.main(args2);

    	String[] argsC = new String[]{"127.0.0.1","5000", "127.0.0.1", "5001", "127.0.0.1", "5002"};
    	GraphClient.main(argsC);
    }    
}
