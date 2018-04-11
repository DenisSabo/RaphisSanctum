public class Symbol<K>{
    private K symbol;

    public Symbol(K symbol){
        this.symbol = symbol;
    }

    public void setSymbol(K symbol){
        this.symbol = symbol;
    }

    public K getSymbol(){
        return this.symbol;
    }
}