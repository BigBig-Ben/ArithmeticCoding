package test;

public class Encoder {
    //frequencyTable
    FrequencyTable freqs = new FrequencyTable();
    //当前域基段，即下限
    long base;
    //当前域上限
    long ceiling;
    //当前域大小
    long range;
    //源码
    String source;
    //算术编码结果
    String encoderResult;
    //下溢检查
    boolean ifUnderFlow;
    //下溢记录-base
    char underFlowMark;
    //下溢个数
    int underFlowCnt;
    //编码器长度
    int length;


    //构造函数
    Encoder(FrequencyTable frequencyTable) {
        freqs = frequencyTable;
        base = 0;
        ceiling = 999999;
        range = 999999;
        encoderResult = "";
        ifUnderFlow = false;
        underFlowCnt = 0;
        length = 6;
    }

    //导入源码
    public void setSource(String str) {
        source = str;
        char[] chars = source.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            /*
             * 自适应编码
             * */
            int character = (int)chars[i];
            encoding(character);
            freqs.increment(character);
        }
        //加入结束符
        encoding(128);
        encoderResult += String.valueOf(base);
        System.out.println("编码结束");
    }


    //编码细节
    public void encoding(int symbol) {
        range = ceiling - base + 1;
        double sum = freqs.getSum();
        double low = freqs.getLow(symbol);
        double high = freqs.getHigh(symbol);
        long newBase = (long) (range * (low / sum) + base);
        long newCeiling = (long) (range * (high / sum) + base) - 1;
        base = newBase;
        ceiling = newCeiling;
        checkFlow();
    }

    //返回编码结果
    public String getResult() {
        return encoderResult;
    }


    //溢出检查
    public void checkFlow() {
        //预处理
        String baseStr = String.valueOf(base);
        String ceilingStr = String.valueOf(ceiling);
        //恢复到编码器长度
        while (baseStr.length() != length) {
            baseStr = '0' + baseStr;
        }
        while (ceilingStr.length() != length) {
            ceilingStr = '0' + ceilingStr;
        }
        //上溢
        while (baseStr.charAt(0) == ceilingStr.charAt(0)) {
            char target = baseStr.charAt(0);
            encoderResult += target;
            baseStr = baseStr.substring(1);
            baseStr = baseStr + '0';
            base = Long.valueOf(baseStr);
            ceilingStr = ceilingStr.substring(1);
            ceilingStr = ceilingStr + '9';
            ceiling = Long.valueOf(ceilingStr);
            //之前有下溢
            if (ifUnderFlow) {
                for (int i = 0; i < underFlowCnt; i++) {
                    if (underFlowMark == target)     //收敛到base
                        encoderResult += '9';
                    else
                        encoderResult += '0';       //收敛到ceiling
                }
                underFlowCnt = 0;
                ifUnderFlow = false;
            }
        }
        //下溢
        if (ceilingStr.charAt(0) - baseStr.charAt(0) == 1) {
            boolean stillUnderFlow = true;    //用来判断下溢是否全部拾取
            while (stillUnderFlow) {
                if (baseStr.charAt(1) == '9' && ceilingStr.charAt(1) == '0') {
                    ifUnderFlow = true;
                    underFlowMark = baseStr.charAt(0);  //记录base收敛对象，下溢结束时作为判断依据
                    underFlowCnt++;
                    baseStr = baseStr.charAt(0) + baseStr.substring(2);
                    baseStr = baseStr + '0';
                    base = Long.valueOf(baseStr);
                    ceilingStr = ceilingStr.charAt(0) + ceilingStr.substring(2);
                    ceilingStr = ceilingStr + '9';
                    ceiling = Long.valueOf(ceilingStr);
                } else {
                    stillUnderFlow = false;
                }
            }
        }
    }

}
