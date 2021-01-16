package net.haesleinhuepf.clijx.faclonheavy;

import net.haesleinhuepf.clij.CLIJ;
import net.haesleinhuepf.clijx.CLIJx;
import org.jocl.CL;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The CLIJxPool holds instances of CLIJx allowing to execute operations on multiple OpenCL devices / GPUs at a time.
 */
public class CLIJxPool {
    CLIJx[] pool;

    public CLIJxPool(int[] device_indices, int[] number_of_instances_per_clij) {
        pool = new CLIJx[device_indices.length * number_of_instances_per_clij.length];

        int count = 0;
        for (int i = 0; i < device_indices.length; i++) {
            for (int j = 0; j < number_of_instances_per_clij[i]; j++) {
                pool[count] = new CLIJx(new CLIJ(device_indices[i]));
                count ++;
            }
        }
    }

    public static CLIJxPool fromDeviceNames(String[] device_names, int[] number_of_instances_per_clij) {
        ArrayList<Integer> index_list = new ArrayList();
        ArrayList<Integer> instance_count_list = new ArrayList();

        int index = 0;
        for (String name : CLIJ.getAvailableDeviceNames()) {

            for (int i = 0; i < device_names.length; i++) {
                String search_string = device_names[i];
                if (name.contains(search_string)) {
                    index_list.add(index);
                    instance_count_list.add(number_of_instances_per_clij[i]);
                }
            }
            index++;
        }

        int[] indices = new int[index_list.size()];
        int[] instance_counts = new int[index_list.size()];

        for (int i = 0; i < index_list.size(); i++) {
            indices[i] = index_list.get(i);
            instance_counts[i] = instance_count_list.get(i);
        }

        return new CLIJxPool(indices, instance_counts);
    }

    public static CLIJxPool fullPool() {
        return CLIJxPool.fromDeviceNames(
                new String[]{"UHD", "gfx9", "mx", "1070", "2060", "2070", "2080"},
                new int[]   {     1,     1,     1,     1,      2,      2,      4}
        );
    }

    public static CLIJxPool powerPool() {
        return CLIJxPool.fromDeviceNames(
                new String[]{"1070", "2060", "2070", "2080"},
                new int[]   {     1,      2,      2,      4}
        );
    }

    public int size() {
        return pool.length;
    }

    public CLIJx get(int index) {
        return pool[index];
    }
}
