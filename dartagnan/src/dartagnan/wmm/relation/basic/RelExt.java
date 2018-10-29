package dartagnan.wmm.relation.basic;

import dartagnan.program.Thread;
import dartagnan.program.event.Event;
import dartagnan.program.utils.EventRepository;
import dartagnan.wmm.utils.Tuple;
import dartagnan.wmm.utils.TupleSet;

import java.util.Collection;

public class RelExt extends BasicRelation {

    public RelExt(){
        term = "ext";
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