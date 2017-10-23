package atomix_lab.state_machine.type;

public class Edge {
    int id;
    int id2;
    String desc;

    public Edge(int id, int id2, String desc) {
        this.id = id;
        this.id2 = id2;
        this.desc = desc;
    }
}
