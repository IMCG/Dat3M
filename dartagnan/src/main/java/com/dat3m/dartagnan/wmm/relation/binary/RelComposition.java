package com.dat3m.dartagnan.wmm.relation.binary;

import com.microsoft.z3.BoolExpr;
import com.dat3m.dartagnan.program.event.Event;
import com.dat3m.dartagnan.wmm.utils.Utils;
import com.dat3m.dartagnan.wmm.relation.Relation;
import com.dat3m.dartagnan.wmm.utils.Tuple;
import com.dat3m.dartagnan.wmm.utils.TupleSet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Florian Furbach
 */
public class RelComposition extends BinaryRelation {

    public static String makeTerm(Relation r1, Relation r2){
        return "(" + r1.getName() + ";" + r2.getName() + ")";
    }

    public RelComposition(Relation r1, Relation r2) {
        super(r1, r2);
        term = makeTerm(r1, r2);
    }

    public RelComposition(Relation r1, Relation r2, String name) {
        super(r1, r2, name);
        term = makeTerm(r1, r2);
    }

    @Override
    public TupleSet getMaySet(){
        if(maySet == null){
            maySet = new TupleSet();
            TupleSet set1 = r1.getMaySet();
            TupleSet set2 = r2.getMaySet();
            for(Tuple rel1 : set1){
                for(Tuple rel2 : set2.getByFirst(rel1.getSecond())){
                    maySet.add(new Tuple(rel1.getFirst(), rel2.getSecond()));
                }
            }
        }
        return maySet;
    }

    @Override
    public TupleSet getMaySetRecursive(){
        if(recursiveGroupId > 0 && maySet != null){
            TupleSet set1 = r1.getMaySetRecursive();
            TupleSet set2 = r2.getMaySetRecursive();
            for(Tuple rel1 : set1){
                for(Tuple rel2 : set2.getByFirst(rel1.getSecond())){
                    maySet.add(new Tuple(rel1.getFirst(), rel2.getSecond()));
                }
            }
            return maySet;
        }
        return getMaySet();
    }

    @Override
    public void addToActiveSet(TupleSet tuples){
        Set<Tuple> activeSet = new HashSet<>(tuples);
        activeSet.removeAll(this.activeSet);
        this.activeSet.addAll(tuples);
        activeSet.retainAll(maySet);

        if(!activeSet.isEmpty()){
            TupleSet r1Set = new TupleSet();
            TupleSet r2Set = new TupleSet();

            Map<Integer, Set<Integer>> myMap = new HashMap<>();
            for(Tuple tuple : activeSet){
                int id1 = tuple.getFirst().getCId();
                int id2 = tuple.getSecond().getCId();
                myMap.putIfAbsent(id1, new HashSet<>());
                myMap.get(id1).add(id2);
            }

            for(Tuple tuple1 : r1.getMaySet()){
                Event e1 = tuple1.getFirst();
                Set<Integer> ends = myMap.get(e1.getCId());
                if(ends == null) continue;
                for(Tuple tuple2 : r2.getMaySet().getByFirst(tuple1.getSecond())){
                    Event e2 = tuple2.getSecond();
                    if(ends.contains(e2.getCId())){
                        r1Set.add(tuple1);
                        r2Set.add(tuple2);
                    }
                }
            }

            r1.addToActiveSet(r1Set);
            r2.addToActiveSet(r2Set);
        }
    }

    @Override
    protected BoolExpr encodeKnaster() {
        BoolExpr enc = ctx.mkTrue();

        TupleSet r1Set = new TupleSet();
        r1Set.addAll(r1.getActiveSet());
        r1Set.retainAll(r1.getMaySet());

        TupleSet r2Set = new TupleSet();
        r2Set.addAll(r2.getActiveSet());
        r2Set.retainAll(r2.getMaySet());

        Map<Integer, BoolExpr> exprMap = new HashMap<>();
        for(Tuple tuple : activeSet){
            exprMap.put(tuple.hashCode(), ctx.mkFalse());
        }

        for(Tuple tuple1 : r1Set){
            Event e1 = tuple1.getFirst();
            Event e3 = tuple1.getSecond();
            for(Tuple tuple2 : r2Set.getByFirst(e3)){
                Event e2 = tuple2.getSecond();
                int id = Tuple.toHashCode(e1.getCId(), e2.getCId());
                if(exprMap.containsKey(id)){
                    BoolExpr e = exprMap.get(id);
                    e = ctx.mkOr(e, ctx.mkAnd(Utils.edge(r1.getName(), e1, e3, ctx), Utils.edge(r2.getName(), e3, e2, ctx)));
                    exprMap.put(id, e);
                }
            }
        }

        for(Tuple tuple : activeSet){
            enc = ctx.mkAnd(enc, ctx.mkEq(Utils.edge(this.getName(), tuple.getFirst(), tuple.getSecond(), ctx), exprMap.get(tuple.hashCode())));
        }

        return enc;
    }

    @Override
    protected BoolExpr encodeIDL() {
        if(recursiveGroupId == 0){
            return encodeKnaster();
        }

        BoolExpr enc = ctx.mkTrue();

        boolean recurseInR1 = (r1.getRecursiveGroupId() & recursiveGroupId) > 0;
        boolean recurseInR2 = (r2.getRecursiveGroupId() & recursiveGroupId) > 0;

        TupleSet r1Set = new TupleSet();
        r1Set.addAll(r1.getActiveSet());
        r1Set.retainAll(r1.getMaySet());

        TupleSet r2Set = new TupleSet();
        r2Set.addAll(r2.getActiveSet());
        r2Set.retainAll(r2.getMaySet());

        Map<Integer, BoolExpr> orClauseMap = new HashMap<>();
        Map<Integer, BoolExpr> idlClauseMap = new HashMap<>();
        for(Tuple tuple : activeSet){
            orClauseMap.put(tuple.hashCode(), ctx.mkFalse());
            idlClauseMap.put(tuple.hashCode(), ctx.mkFalse());
        }

        for(Tuple tuple1 : r1Set){
            Event e1 = tuple1.getFirst();
            Event e3 = tuple1.getSecond();
            for(Tuple tuple2 : r2Set.getByFirst(e3)){
                Event e2 = tuple2.getSecond();
                int id = Tuple.toHashCode(e1.getCId(), e2.getCId());
                if(orClauseMap.containsKey(id)){
                    BoolExpr opt1 = Utils.edge(r1.getName(), e1, e3, ctx);
                    BoolExpr opt2 = Utils.edge(r2.getName(), e3, e2, ctx);
                    orClauseMap.put(id, ctx.mkOr(orClauseMap.get(id), ctx.mkAnd(opt1, opt2)));

                    if(recurseInR1){
                        opt1 = ctx.mkAnd(opt1, ctx.mkGt(Utils.intCount(this.getName(), e1, e2, ctx), Utils.intCount(r1.getName(), e1, e3, ctx)));
                    }
                    if(recurseInR2){
                        opt2 = ctx.mkAnd(opt2, ctx.mkGt(Utils.intCount(this.getName(), e1, e2, ctx), Utils.intCount(r1.getName(), e3, e2, ctx)));
                    }
                    idlClauseMap.put(id, ctx.mkOr(idlClauseMap.get(id), ctx.mkAnd(opt1, opt2)));
                }
            }
        }

        for(Tuple tuple : activeSet){
            enc = ctx.mkAnd(enc, ctx.mkEq(Utils.edge(this.getName(), tuple.getFirst(), tuple.getSecond(), ctx), orClauseMap.get(tuple.hashCode())));
            enc = ctx.mkAnd(enc, ctx.mkEq(Utils.edge(this.getName(), tuple.getFirst(), tuple.getSecond(), ctx), idlClauseMap.get(tuple.hashCode())));
        }

        return enc;
    }

    @Override
    public BoolExpr encodeIteration(int groupId, int iteration){
        BoolExpr enc = ctx.mkTrue();

        if((groupId & recursiveGroupId) > 0 && iteration > lastEncodedIteration) {
            lastEncodedIteration = iteration;
            String name = this.getName() + "_" + iteration;

            if(iteration == 0 && isRecursive){
                for(Tuple tuple : activeSet){
                    enc = ctx.mkAnd(ctx.mkNot(Utils.edge(name, tuple.getFirst(), tuple.getSecond(), ctx)));
                }
            } else {
                int childIteration = isRecursive ? iteration - 1 : iteration;

                boolean recurseInR1 = (r1.getRecursiveGroupId() & groupId) > 0;
                boolean recurseInR2 = (r2.getRecursiveGroupId() & groupId) > 0;

                String r1Name = recurseInR1 ? r1.getName() + "_" + childIteration : r1.getName();
                String r2Name = recurseInR2 ? r2.getName() + "_" + childIteration : r2.getName();

                TupleSet r1Set = new TupleSet();
                r1Set.addAll(r1.getActiveSet());
                r1Set.retainAll(r1.getMaySet());

                TupleSet r2Set = new TupleSet();
                r2Set.addAll(r2.getActiveSet());
                r2Set.retainAll(r2.getMaySet());

                Map<Integer, BoolExpr> exprMap = new HashMap<>();
                for(Tuple tuple : activeSet){
                    exprMap.put(tuple.hashCode(), ctx.mkFalse());
                }

                for(Tuple tuple1 : r1Set){
                    Event e1 = tuple1.getFirst();
                    Event e3 = tuple1.getSecond();
                    for(Tuple tuple2 : r2Set.getByFirst(e3)){
                        Event e2 = tuple2.getSecond();
                        int id = Tuple.toHashCode(e1.getCId(), e2.getCId());
                        if(exprMap.containsKey(id)){
                            BoolExpr e = exprMap.get(id);
                            e = ctx.mkOr(e, ctx.mkAnd(Utils.edge(r1Name, e1, e3, ctx), Utils.edge(r2Name, e3, e2, ctx)));
                            exprMap.put(id, e);
                        }
                    }
                }

                for(Tuple tuple : activeSet){
                    enc = ctx.mkAnd(enc, ctx.mkEq(Utils.edge(name, tuple.getFirst(), tuple.getSecond(), ctx), exprMap.get(tuple.hashCode())));
                }

                if(recurseInR1){
                    enc = ctx.mkAnd(enc, r1.encodeIteration(groupId, childIteration));
                }

                if(recurseInR2){
                    enc = ctx.mkAnd(enc, r2.encodeIteration(groupId, childIteration));
                }
            }
        }

        return enc;
    }
}
