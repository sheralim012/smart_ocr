package pk.edu.pucit.smartocr.utilities;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateTimeHelper {

    public static String getCurrentDate() {
        return new SimpleDateFormat("dd-mm-yyyy").format(new Date());
    }

    public static String getCurrentDateTime() {
        return new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    }

}
