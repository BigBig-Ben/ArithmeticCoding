package test;

import java.io.Console;

public class Decoder {
    //frequencyTable
    FrequencyTable freqs = new FrequencyTable();
    //当前域基段，即下限
    long base;
    //当前域上限
    long ceiling;
    //当前域大小
    long range;
    //解码段转换用码
    long code;
    //源码
    String source;
    //算术译码结果
    String decoderResult;
    //译码记录位
    int decodePtr;
    //解码器长度
    int length;


    //构造函数
    Decoder(FrequencyTable frequencyTable) {
        freqs = frequencyTable;
        base = 0;
        ceiling = 999999;
        range = 999999;
        decoderResult = "";
        length = 6;
    }

    //导入译码
    public void setCode(String str) {
        source = str;
        decodePtr = length;
        str = str.substring(0, length);
        code = Long.valueOf(str);
        while (true) {
            int oneFound = decoding();
            //碰到结束符
            if (oneFound == 128) {
                System.out.println("解码结束");
                break;
            }
            freqs.increment(oneFound);
        }
    }


    //编码细节
    public int decoding() {
        //在现有frequencyTable上先找一个译码
        //遇到结束符
        if (ceiling < base) {
            return 128;
        }
        range = ceiling - base + 1;
        double sum = freqs.getSum();
        double temp = (code - base) * sum;
        int target = (int) (temp / range);
        int fitChar = 128;
        for (int i = 0; i < 128; i++) {
            if (freqs.getLow(i) <= target && freqs.getHigh(i) > target) {
                fitChar = i;
                decoderResult += (char) (fitChar);
            }
        }
        //找不到相应信源,大概率遇到结束符
        if (fitChar == 128) {
            return fitChar;
        }
        //更新新的区间和译码
        long low = freqs.getLow(fitChar);
        long high = freqs.getHigh(fitChar);
        long newBase = (long) (range * (low / sum) + base);
        long newCeiling = (long) (range * (high / sum) + base) - 1;
        base = newBase;
        ceiling = newCeiling;
        deCheckFlow();
        return fitChar;
    }

    //返回编码结果
    public String getResult() {
        return decoderResult;
    }

    //溢出消去
    public void deCheckFlow() {
        String codeStr = String.valueOf(code);
        String baseStr = String.valueOf(base);
        String ceilingStr = String.valueOf(ceiling);
        //base&ceiling 补齐
        while (baseStr.length() < ceilingStr.length()) {
            baseStr = '0' + baseStr;
        }
        //恢复到解码器长度并同步解码段转换用码
        while (ceilingStr.length() < length) {
            baseStr = baseStr + '0';
            base = Long.valueOf(baseStr);
            ceilingStr = ceilingStr + '9';
            ceiling = Long.valueOf(ceilingStr);

            if (source.length() > decodePtr) {
                codeStr += source.charAt(decodePtr++);  //从源码中读一位
            }
            code = Long.valueOf(codeStr);
        }
        while (codeStr.length() < length) {
            codeStr = '0' + codeStr;
        }
        //上溢
        while (baseStr.charAt(0) == ceilingStr.charAt(0)) {
            baseStr = baseStr.substring(1);
            baseStr = baseStr + '0';
            base = Long.valueOf(baseStr);
            ceilingStr = ceilingStr.substring(1);
            ceilingStr = ceilingStr + '9';
            ceiling = Long.valueOf(ceilingStr);

            codeStr = codeStr.substring(1);
            if (source.length() > decodePtr) {
                codeStr += source.charAt(decodePtr++);  //从源码中读一位
            }
            code = Long.valueOf(codeStr);
        }
        //下溢
        if (ceilingStr.charAt(0) - baseStr.charAt(0) == 1) {
            boolean stillUnderFlow = true;    //用来判断下溢是否全部拾取
            while (stillUnderFlow) {
                if (baseStr.charAt(1) == '9' && ceilingStr.charAt(1) == '0') {
                    baseStr = baseStr.charAt(0) + baseStr.substring(2);
                    baseStr = baseStr + '0';
                    base = Long.valueOf(baseStr);
                    ceilingStr = ceilingStr.charAt(0) + ceilingStr.substring(2);
                    ceilingStr = ceilingStr + '9';
                    ceiling = Long.valueOf(ceilingStr);
                    codeStr = codeStr.charAt(0) + codeStr.substring(2);
                    if (source.length() > decodePtr) {
                        codeStr += source.charAt(decodePtr++);  //从源码中读一位
                    }
                    code = Long.valueOf(codeStr);
                } else {
                    stillUnderFlow = false;
                }
            }
        }
    }

}
