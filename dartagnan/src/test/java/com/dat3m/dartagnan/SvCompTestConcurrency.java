package com.dat3m.dartagnan;

import com.dat3m.dartagnan.parsers.cat.ParserCat;
import com.dat3m.dartagnan.utils.ResourceHelper;
import com.dat3m.dartagnan.wmm.Wmm;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static com.dat3m.dartagnan.utils.ResourceHelper.TEST_RESOURCE_PATH;

@RunWith(Parameterized.class)
public class SvCompTestConcurrency extends AbstractSvCompTest {

	@Parameterized.Parameters(name = "{index}: {0} bound={2}")
    public static Iterable<Object[]> data() throws IOException {
        Wmm wmm = new ParserCat().parse(new File(ResourceHelper.CAT_RESOURCE_PATH + "cat/svcomp.cat"));

        List<Object[]> data = new ArrayList<>();

        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/fib_bench-1.bpl", wmm, 6});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/fib_bench-2.bpl", wmm, 6});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/fib_bench_longer-1.bpl", wmm, 7});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/fib_bench_longer-2.bpl", wmm, 7});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/lazy01.bpl", wmm, 1});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/triangular-1.bpl", wmm, 6});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/triangular-2.bpl", wmm, 6});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/gcd-2.bpl", wmm, 1});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/read_write_lock-1.bpl", wmm, 1});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/read_write_lock-2.bpl", wmm, 1});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/18_read_write_lock.bpl", wmm, 1});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/19_time_var_mutex.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe000_pso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/safe001_tso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/thin000_pso.oepc.bpl", wmm, 2});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/race-1_1-join.bpl", wmm, 1});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/race-1_2-join.bpl", wmm, 1});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/race-1_3-join.bpl", wmm, 1});
        data.add(new Object[]{TEST_RESOURCE_PATH + "boogie/concurrency/race-3_2-container_of-global.bpl", wmm, 1});
        
        return data;
    }

	public SvCompTestConcurrency(String path, Wmm wmm, int bound) {
		super(path, wmm, bound);
	}
}