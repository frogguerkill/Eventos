package classes;

import models.Evento;
import models.Parkour1;

public enum EventoType
{
    PARKOUR1(Parkour1.class);
	
    
    private Class<? extends Evento> evento;
    
    private EventoType(final Class<? extends Evento> cls) {
        this.evento = cls;
    }
    
    public Class<? extends Evento> getEventoClass() {
        return this.evento;
    }
}
