package utils;

public class NumberUtils
{
    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        }
        catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }
}
