package mealy;

public class State<K>{
    private K state;

    public State(K state){
        this.state = state;
    }

    public void setState(K state){
        this.state = state;
    }

    public K getState(){
        return this.state;
    }

}
