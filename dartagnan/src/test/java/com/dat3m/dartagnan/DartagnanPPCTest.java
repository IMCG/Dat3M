package com.dat3m.dartagnan;

import com.dat3m.dartagnan.wmm.Wmm;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;

@RunWith(Parameterized.class)
public class DartagnanPPCTest extends AbstractDartagnanTest {

    @Parameterized.Parameters(name = "{index}: {0} {2} untoll={4} relax={5} idl={6}")
    public static Iterable<Object[]> data() throws IOException {
        return buildParameters("litmus/PPC/", "cat/power.cat", "power", 2);
    }

    public DartagnanPPCTest(String input, boolean expected, String target, Wmm wmm, int unroll, boolean relax, boolean idl) {
        super(input, expected, target, wmm, unroll, relax, idl);
    }
}