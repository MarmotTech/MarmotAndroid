package me.jinheng.cityullm;

import org.junit.Test;

public class TestUtils {

    @Test
    public void testGetMemorySize() {
        long res = Utils.getTotalMemory();
        System.out.println(res);
    }

}
