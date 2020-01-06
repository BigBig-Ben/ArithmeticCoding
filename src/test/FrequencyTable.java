package test;

/*
 * adaptive modification 12-17 23:00
 * */
public class FrequencyTable {
    //各字符频率  128ASCII + 结束符128
    private int frequencies[] = new int[129];
    //前置字符频率累计
    private int cumulative[] = new int[130];
    //字符数目累计
    private int sum;

    //构造函数初始化
    FrequencyTable() {
        sum = 0;
        for (int i = 0; i < 128; i++) {
            frequencies[i] = 1;
            sum++;
        }
    }

    //某字符频率加一
    public void increment(int symbol) {
        checkSymbol(symbol);
        frequencies[symbol] = checkedAdd(frequencies[symbol], 1);
        sum = checkedAdd(sum, 1);
        //cumulative[128] = 0 使cumulative重新生成
        cumulative[128] = 0;
    }


    //累计初始化
    public void initCumulative() {
        int temp = 0;
        for (int i = 0; i < 128; i++) {
            temp = checkedAdd(temp, frequencies[i]);
            cumulative[i + 1] = temp;
        }
    }

    //获取频段下限
    public int getLow(int symbol) {
        checkSymbol(symbol);
        if (cumulative[128] < 1) {
            initCumulative();
        }
        return cumulative[symbol];
    }

    //获取频段上限
    public int getHigh(int symbol) {
        checkSymbol(symbol);
        if (cumulative[128] < 1) {
            initCumulative();
        }
        return cumulative[symbol + 1];
    }

    //获得字符总数
    public int getSum() {
        return sum;
    }

    //检查字符是否合法
    private void checkSymbol(int symbol) {
        if (symbol < 0 || symbol >= frequencies.length)
            throw new IllegalArgumentException("字符不在编码范围内");
    }

    //检查是否溢出
    private static int checkedAdd(int x, int y) {
        int z = x + y;
        if (y > 0 && z < x || y < 0 && z > x)
            throw new ArithmeticException("数值溢出");
        else
            return z;
    }

    //toString()
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < frequencies.length; i++)
            sb.append(String.format("%d\t%d%n", i, frequencies[i]));
        sb.append(sum);
        return sb.toString();
    }
}
