package classes;

public enum EventoState
{
    FECHADO, 
    INICIANDO, 
    INICIADO;
    
    private static EventoState state;
    
    public void define() {
        EventoState.state = this;
    }
    
    public static EventoState getState() {
        return EventoState.state;
    }
    
    public boolean isState() {
        return this == getState();
    }
    
    static {
        EventoState.state = EventoState.FECHADO;
    }
}
