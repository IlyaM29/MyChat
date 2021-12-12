package ru.gb;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;

public class HomeWorkL6Test {
    private static HomeWorkL6 hw;

    @BeforeClass
    public static void init() {
        hw = new HomeWorkL6();
    }

    @Test
    public void test1MasAfter4 () {
        int[] mas = {1, 2, 3, 4, 5, 6};
        int[] result = hw.masAfter4(mas);
        int[] otv = {5, 6};
        Assert.assertEquals(Arrays.toString(otv), Arrays.toString(result));
    }

    @Test
    public void test2MasAfter4 () {
        int[] mas = {1, 4, 2, 3, 4, 5, 6};
        int[] result = hw.masAfter4(mas);
        int[] otv = {5, 6};
        Assert.assertEquals(Arrays.toString(otv), Arrays.toString(result));
    }

    @Test
    public void test3MasAfter4 () {
        int[] mas = {1, 2, 3, 5, 6};
        int[] result = hw.masAfter4(mas);
        Assert.assertEquals(Arrays.toString((int[]) null), Arrays.toString(result));
    }

    @Test
    public void test1checked1and4 () {
        int[] mas = {1, 2, 3, 4, 5, 6};
        boolean result = hw.checked1and4(mas);
        boolean otv = true;
        Assert.assertEquals(otv, result);
    }

    @Test
    public void test2checked1and4 () {
        int[] mas = {2, 2, 3, 4, 5, 6};
        boolean result = hw.checked1and4(mas);
        boolean otv = false;
        Assert.assertEquals(otv, result);
    }

    @Test
    public void test3checked1and4 () {
        int[] mas = {1, 2, 3, 3, 5, 6};
        boolean result = hw.checked1and4(mas);
        boolean otv = false;
        Assert.assertEquals(otv, result);
    }

    @Test
    public void test4checked1and4 () {
        int[] mas = {0, 2, 3, 3, 5, 6};
        boolean result = hw.checked1and4(mas);
        boolean otv = false;
        Assert.assertEquals(otv, result);
    }
}