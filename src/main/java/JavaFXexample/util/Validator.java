package JavaFXexample.util;

public class Validator {

    public static boolean isEmpty(String text){
        return text == null || text.isBlank();
    }

    public static boolean isValidGPA(double gpa){
        return gpa >= 0 && gpa <= 4;
    }

    public static boolean isDouble(String text){
        try {
            Double.parseDouble(text);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isInteger(String text){
        try {
            Integer.parseInt(text);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
