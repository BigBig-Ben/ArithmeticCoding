package test;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;

public class ArithmeticCodingTest {
    public static void main(String args[]) {
        try {
            String choice = JOptionPane.showInputDialog("编码E/解码D？ 输入E or D");
            if (choice.equals("E")) {
                //编码
                String code = JOptionPane.showInputDialog("请输入源码");
                FrequencyTable encodeTable = new FrequencyTable();
                Encoder encoder = new Encoder(encodeTable);
                encoder.setSource(code);
                String encodeResult = encoder.getResult();
                System.out.println(encodeResult);
                //将编码结果写入文件
                File file = new File("encodeResult.txt");
                FileOutputStream output = new FileOutputStream(file);
                byte[] bytes = encodeResult.getBytes();
                output.write(bytes);
                output.close();
            } else if (choice.equals("D")) {
                //译码
                String code = JOptionPane.showInputDialog("请输入算术编码");
                FrequencyTable decodeTable = new FrequencyTable();
                Decoder decoder = new Decoder(decodeTable);
                decoder.setCode(code);
                String decodeResult = decoder.getResult();
                System.out.println(decodeResult);
                //将解码结果写入文件
                File file = new File("decodeResult.txt");
                FileOutputStream output = new FileOutputStream(file);
                byte[] bytes = decodeResult.getBytes();
                output.write(bytes);
                output.close();
            }else {
                JOptionPane.showMessageDialog(null, "请输入E或者R", "错误提示", 0);
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
