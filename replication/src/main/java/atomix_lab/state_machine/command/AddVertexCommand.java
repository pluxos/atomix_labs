package atomix_lab.state_machine.command;

import io.atomix.copycat.Command;

public class AddVertexCommand implements Command<Boolean>
{
    public int id;
    public int color;
    public String desc;

    public AddVertexCommand(int id, int color, String description)
    {
        this.id = id;
        this.color = color;
        this.desc = description;
    }
}
