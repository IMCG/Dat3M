package com.dat3m.dartagnan.wmm.relation.basic;

import com.dat3m.dartagnan.program.Thread;
import com.dat3m.dartagnan.program.event.Event;
import com.dat3m.dartagnan.program.utils.EventRepository;
import com.dat3m.dartagnan.wmm.utils.Tuple;
import com.dat3m.dartagnan.wmm.utils.TupleSet;

import java.util.Collection;

public class RelExt extends BasicRelation {

    public RelExt(){
        term = "ext";
        isStatic = true;
    }

    @Override
    public TupleSet getMaxTupleSet(){
        if(maxTupleSet == null){
            maxTupleSet = new TupleSet();
            for(Thread t1 : program.getThreads()){
                Collection<Event> eventsT1 = t1.getEventRepository().getEvents(EventRepository.VISIBLE);
                for(Thread t2 : program.getThreads()){
                    if(t1 != t2){
                        Collection<Event> eventsT2 = t2.getEventRepository().getEvents(EventRepository.VISIBLE);
                        for(Event e1 : eventsT1){
                            for(Event e2 : eventsT2){
                                maxTupleSet.add(new Tuple(e1, e2));
                            }
                        }
                    }
                }
            }
        }
        return maxTupleSet;
    }
}