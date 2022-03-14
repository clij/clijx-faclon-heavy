package net.haesleinhuepf.clijx.faclonheavy;

import net.haesleinhuepf.clij.CLIJ;
import org.junit.Test;

import java.util.ArrayList;

public class TestCLIJxPool {
    @Test
    public void test_pool_from_indices() {
        CLIJxPool pool = new CLIJxPool(new int[]{0}, new int[]{1});
        assert pool.size() == 1;
    }

    // Bad assumption - get a device, then use that devices name to get a device from name?
    @Test
    public void test_pool_from_device_names() {
        ArrayList<String> names = CLIJ.getAvailableDeviceNames();
        for (String name : names) {
            CLIJxPool pool = CLIJxPool.fromDeviceNames(new String[]{name}, new int[]{1});
            assert pool.size() == 1;
        }
    }

    @Test
    public void test_pool_from_predefined() {
        // Bad test because its predicated on a non-exhaustive list of graphics hardware
        CLIJxPool pool = CLIJxPool.fullPool();
        assert pool.size() > 0;
    }

}
