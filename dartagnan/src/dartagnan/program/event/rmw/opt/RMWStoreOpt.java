package dartagnan.program.event.rmw.opt;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import dartagnan.expression.ExprInterface;
import dartagnan.program.Register;
import dartagnan.program.event.rmw.RMWLoadFromAddress;
import dartagnan.program.event.rmw.RMWStoreToAddress;
import dartagnan.program.event.utils.RegReaderAddress;
import dartagnan.program.event.utils.RegReaderData;
import dartagnan.utils.MapSSA;
import dartagnan.utils.Pair;

public class RMWStoreOpt extends RMWStoreToAddress implements RegReaderData, RegReaderAddress {

    public RMWStoreOpt(RMWLoadFromAddress loadEvent, Register address, ExprInterface value, String atomic){
        super(loadEvent, address, value, atomic);
    }

    @Override
    public BoolExpr encodeCF(Context ctx) {
        return ctx.mkImplies(ctx.mkNot(ctx.mkBoolConst(cfVar())), ctx.mkNot(executes(ctx)));
    }

    @Override
    public RMWStoreOpt clone() {
        if(clone == null){
            RMWLoadFromAddress newLoad = loadEvent != null ? loadEvent.clone() : null;
            clone = new RMWStoreOpt(newLoad, address.clone(), value.clone(), atomic);
            afterClone();
        }
        return (RMWStoreOpt)clone;
    }

    @Override
    public Pair<BoolExpr, MapSSA> encodeDF(MapSSA map, Context ctx) {
        Pair<BoolExpr, MapSSA> result = super.encodeDF(map, ctx);
        BoolExpr enc;
        if(loadEvent == null){
            enc = ctx.mkAnd(result.getFirst(), ctx.mkNot(executes(ctx)));
        } else {
            enc = ctx.mkImplies(executes(ctx), ctx.mkEq(addressExpr, loadEvent.getAddressExpr(ctx)));
        }
        return new Pair<>(enc, result.getSecond());
    }
}