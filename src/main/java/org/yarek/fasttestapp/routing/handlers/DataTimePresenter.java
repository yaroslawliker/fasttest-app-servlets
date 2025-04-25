package org.yarek.fasttestapp.routing.handlers;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class DataTimePresenter {

    public static String[] presentDateTimes(LocalDateTime startTime, LocalDateTime finishTime, final String notFinishedText) {
        if (finishTime == null) {
            return new String[] { presentTime(startTime) + " " + presentDate(startTime), notFinishedText };
        } else {
            return formatDateTimes(startTime, finishTime);
        }
    }

    public static String[] formatDateTimes(@NotNull LocalDateTime startTime, @NotNull LocalDateTime finishTime) {
        String[] result = new String[2];
        String startTimeResult;
        String finishTimeResult;

        startTimeResult = presentTime(startTime);
        finishTimeResult = presentTime(finishTime);

        if (startTime.getDayOfMonth() != finishTime.getDayOfMonth()
                || startTime.getMonth() != finishTime.getMonth()
                || startTime.getYear() != finishTime.getYear()) {
            startTimeResult = startTimeResult + " " +  presentDate(startTime);
            finishTimeResult = finishTimeResult + " " + presentDate(finishTime);
        }

        result[0] = startTimeResult;
        result[1] = finishTimeResult;
        return result;
    }

    public static String presentTime(LocalDateTime datetime) {
        return datetime.getHour() + ":" + datetime.getMinute() + ":" + formatSeconds(datetime.getSecond());
    }
    public static String presentDate(LocalDateTime datetime) {
        return  datetime.getDayOfMonth() + "." + datetime.getMonthValue() + "." + datetime.getYear();
    }
    public static String formatSeconds(int seconds) {
        if (seconds < 10) {
            return "0" + seconds;
        } else {
            return String.valueOf(seconds);
        }
    }
}
