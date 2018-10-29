package dartagnan.asserts;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import dartagnan.expression.IntExprInterface;
import dartagnan.program.Register;

public class AssertBasic extends AbstractAssert{

    private IntExprInterface e1;
    private final String op;
    private IntExprInterface e2;

    public AssertBasic(IntExprInterface e1, String op, IntExprInterface e2){
        this.e1 = e1;
        this.op = op;
        this.e2 = e2;
    }

    @Override
    public BoolExpr encode(Context ctx) {
        switch(op){
            case "==":
                return ctx.mkEq(e1.getLastValueExpr(ctx), e2.getLastValueExpr(ctx));
            case "!=":
                return ctx.mkNot(ctx.mkEq(e1.getLastValueExpr(ctx), e2.getLastValueExpr(ctx)));
        }
        throw new RuntimeException("Unrecognised assertion option " + op + " in " + this);
    }

    @Override
    public String toString(){
        return valueToString(e1) + op + valueToString(e2);
    }

    @Override
    public AbstractAssert clone(){
        return new AssertBasic(e1.clone(), op, e2.clone());
    }

    private String valueToString(IntExprInterface value){
        if(value instanceof Register){
            return ((Register)value).getPrintMainThreadId() + ":" + value;
        }
        return value.toString();
    }
}