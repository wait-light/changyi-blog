package top.changyix.blog.common_utils;

public final class BitUtils {
    private BitUtils(){};
    /**
     *
     * @param originalNumber 原数
     * @param bits 原数要异或的数字
     * @return  所有数字异或以后的数字
     */
    public static int xor(int originalNumber,int ...bits){
        for (int b : bits){
            originalNumber ^= b;
        }
        return originalNumber;
    }

    /**
     *
     * @param bits 要异或的数字
     * @return  所有数字异或以后的结果
     */
    public static int allXor(int ...bits){
        int originalNumber = 0;
        for (int b : bits){
            originalNumber ^= b;
        }
        return originalNumber;
    }

    /**
     *
     *
     * @param originalNumber 原数
     * @param bits 原数要与的数字
     * @return  所有数字相与以后的数字
     */
    public static int and(int originalNumber,int ...bits){
        for (int b : bits){
            originalNumber &= b;
        }
        return originalNumber;
    }

    /**
     *
     * @param bits 所有要相与的数字
     * @return 相与后的结果
     */
    public static int allAnd(int ...bits){
        int originalNumber = 0;
        for (int b : bits){
            originalNumber &= b;
        }
        return originalNumber;
    }

    /**
     *
     * @param originalNumber 原数
     * @param bits 原数要或的数字
     * @return  所有数字相或以后的数字
     */
    public static int or(int originalNumber,int ...bits){
        for (int b : bits){
            originalNumber |= b;
        }
        return originalNumber;
    }

    /**
     *
     *
     * @param bits 或的数组
     * @return  所有数字相或以后的数字
     */
    public static int allOr(int ...bits){
        int originalNumber = 0;
        for (int b : bits){
            originalNumber |= b;
        }
        return originalNumber;
    }

}
