package ru.gb;

import java.util.Arrays;

public class HomeWorkL6 {
    public static void main(String[] args) {
        HomeWorkL6 hw = new HomeWorkL6();
        int[] arr = {1, 2, 3, 4, 5, 6};
        System.out.println(Arrays.toString(hw.masAfter4(arr)));
    }

    public int[] masAfter4(int[] mas) {
        int k = 0;
        for (int i = 0; i < mas.length; i++) {
            if (mas[i] == 4) {
                k = i+1;
            }
        }
        if (k == 0) {
            new RuntimeException("В массиве нет ни одной \"4\"");
            return null;
        }
        int[] result = new int[mas.length - k];
        System.arraycopy(mas, k, result, 0, result.length);
        return result;
    }

    public boolean checked1and4(int[] mas) {
        boolean is1 = false;
        boolean is4 = false;
        for (int m : mas) {
            if (m == 1) is1 = true;
            if (m == 4) is4 = true;
        }
        return is1 & is4;
    }
}
