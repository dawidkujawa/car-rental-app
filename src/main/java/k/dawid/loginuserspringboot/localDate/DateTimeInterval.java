package k.dawid.loginuserspringboot.localDate;


import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Date;

public class DateTimeInterval {

    private LocalDate myStartDate;

    private LocalDate myEndDate;

    public DateTimeInterval(LocalDate aStartDate, LocalDate aEndDate) {
        if (aStartDate == null) {
            throw new NullPointerException(
                    "Supplied start date may not be null");
        }

        if (aEndDate == null) {
            throw new NullPointerException("");
        }

        myStartDate = aStartDate;
        myEndDate = aEndDate;


        if (myStartDate.isAfter(myEndDate)) {
            throw new IllegalArgumentException(
                    "Supplied 'start' LocalDate is after supplied 'end' LocalDate");
        }
    }

    public LocalDate getStart() {
        return myStartDate;
    }

    public LocalDate getEnd() { return myEndDate; }

    public boolean contains(LocalDate aLocalDate) {
        return !aLocalDate.isAfter(myEndDate)
                && !aLocalDate.isBefore(myStartDate);
    }

    @Override
    public String toString() {
        return myStartDate.toString() + "/" + myEndDate.toString();
    }

}
