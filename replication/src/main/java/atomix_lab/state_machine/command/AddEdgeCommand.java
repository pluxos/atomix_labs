package atomix_lab.state_machine.command;

import io.atomix.copycat.Command;

public class AddEdgeCommand implements Command<Boolean>
{
    public int id;
    public int id2;
    public String desc;

    public AddEdgeCommand(int id, int id2, String description)
    {
        this.id = id;
        this.id2 = id2;
        this.desc = description;
    }
}
