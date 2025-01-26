package OS;

import java.util.Formatter;


public class Hex {
    public static String toHex(String string) {
        byte[] bytes = string.getBytes();

        StringBuilder hexString = new StringBuilder();
        try (Formatter formatter
                     = new Formatter(hexString)) {
            for (byte b : bytes) {
                formatter.format("%02x:", b);
            }
            hexString.deleteCharAt(hexString.length() - 1);
        }

        return hexString.toString();
    }

    public static String fromHex(String hex) {
        String[] hexValues = hex.split(":");
        byte[] parsedBytes = new byte[hexValues.length];
        for (int i = 0; i < hexValues.length; i++) {
            parsedBytes[i] = (byte)Integer.parseInt(hexValues[i], 16);
        }
        return new String(parsedBytes);
    }
}
