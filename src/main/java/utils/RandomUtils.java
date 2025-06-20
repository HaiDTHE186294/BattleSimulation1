package utils;

import java.util.concurrent.ThreadLocalRandom;

public class RandomUtils {
    private RandomUtils() {} // Không cho khởi tạo

    public static int randomBonus(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}