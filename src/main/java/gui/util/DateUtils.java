package gui.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    public static String formattaData(Object dataObj) {
        if (dataObj == null) {
            return "N/A";
        }

        try {
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

            if (dataObj instanceof ZonedDateTime) {
                ZonedDateTime zdt = ((ZonedDateTime) dataObj).withZoneSameInstant(ZoneId.of("Europe/Rome"));
                return zdt.format(outputFormatter);
            } else if (dataObj instanceof LocalDateTime) {
                return ((LocalDateTime) dataObj).format(outputFormatter);
            }

            String dataStr = dataObj.toString();

            if (dataStr.contains("+")) {
                ZonedDateTime zdt = ZonedDateTime.parse(dataStr);
                zdt = zdt.withZoneSameInstant(ZoneId.of("Europe/Rome"));
                return zdt.format(outputFormatter);
            }
            
            if (dataStr.contains(".")) {
                dataStr = dataStr.substring(0, dataStr.indexOf('.'));
            }
            LocalDateTime dateTime = LocalDateTime.parse(dataStr);
            return dateTime.format(outputFormatter);
        } catch (Exception e) {
            return dataObj.toString();
        }
    }
}
