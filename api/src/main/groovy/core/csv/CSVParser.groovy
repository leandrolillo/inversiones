package core.csv

import com.opencsv.CSVReader
import groovy.util.logging.Slf4j

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat


@Slf4j
class CSVParser {

    public static final String ANSI_ARROW = "\u27A1"

    String inputFile



    public CSVParser(String inputFile) {
        this.inputFile = inputFile
    }

    static final List<SimpleDateFormat> supportedDateFormats = [DateFormat.getDateInstance(DateFormat.SHORT, new Locale("es", "ar")),
                                                                new SimpleDateFormat("MM/dd/yyyy"), new SimpleDateFormat("MM/dd/yy hh:mm"), new SimpleDateFormat("MM/dd/yy"), new SimpleDateFormat("MM/dd/yyyy hh:mm:ss")]



    public static Date parseDate(String dateString) {
        Date date = null
        for (SimpleDateFormat dateFormat : supportedDateFormats) {
            try {
                return dateFormat.parse(dateString)
            } catch (ParseException parseException) {
            }
        }

        throw new ParseException("Date format not supported: $dateString".toString(), 0)
    }



    public Map readRow(Map columns, def tokens) {
        Map result = [:]

        columns.eachWithIndex { column, index ->
            if (tokens.size() > index) {
                try {
                    def columnValue = tokens[index]
                    if (column.value) {
                        if (column.value.getMaximumNumberOfParameters() >= 2)
                            columnValue = column.value(this, tokens[index])
                        else
                            columnValue = column.value(tokens[index])
                    }

                    result.put(column.key, columnValue)
                } catch (Throwable throwable) {
                    System.err.println("Could not run closure for column [${column.key}] on value [${tokens[index]}]: $throwable")
                    result.put(column.key, tokens[index])
                }
            }
        }

        return result
    }



    public Map readRow(List<String> columns, def tokens) {
        Map result = [:]

        columns.eachWithIndex { column, index ->
            if (tokens.size() > index) {
                result.put(column, tokens[index])
            }
        }

        return result
    }



    public void eachRow(Closure closure) {
        int currentLine = 0
        CSVReader csvReader = new CSVReader(new FileReader(inputFile))
        csvReader.readNext() // skip columns

        String [] tokens
        while ((tokens = csvReader.readNext()) != null) {
            currentLine++
            try {
                closure.call(currentLine, tokens)
            } catch (Throwable throwable) {
                log.warn("$ANSI_ARROW Line $currentLine of $inputFile: Skipping because: \n" + throwable.message)
                log.error("$ANSI_ARROW Line $currentLine of $inputFile: Skipping because: \n", throwable)
            }
        }
    }
}
