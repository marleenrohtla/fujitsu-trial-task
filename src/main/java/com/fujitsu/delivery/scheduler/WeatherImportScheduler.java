package com.fujitsu.delivery.scheduler;

import com.fujitsu.delivery.entity.WeatherObservation;
import com.fujitsu.delivery.repository.WeatherObservationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.w3c.dom.NodeList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDateTime;

@Component
public class WeatherImportScheduler {

    //connection to the database
    private final WeatherObservationRepository repository;

    //spring automatically gives the repository when this class is created
    public WeatherImportScheduler(WeatherObservationRepository repository) {
        this.repository = repository;
    }

    @Scheduled(cron = "${weather.import.cron}")
    public void importWeatherData() {
        try {
            //opens the Estonian weather website XML feed
            URL url = new URL("https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php");

            //this is an XML reader (reads and understand XML files)
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            //downloads and reads the XML file from the weather website
            Document doc = builder.parse(url.openStream());

            //get a list of all weather stations from the XML file
            NodeList stations = doc.getElementsByTagName("station");

            //loops through every station in the XML file
            for (int i = 0; i < stations.getLength(); i++) {
                Element station = (Element) stations.item(i);

                //gets the names of the stations
                String name = getValue(station, "name");

                //only need 3 stations, skip the others
                if (name.equals("Tallinn-Harku") ||
                        name.equals("Tartu-Tõravere") ||
                        name.equals("Pärnu")) {

                    //creates new empty weather observation object
                    WeatherObservation obs = new WeatherObservation();

                    //fills it with data from XML
                    obs.setStationName(name);
                    obs.setWmoCode(getValue(station, "wmocode"));
                    obs.setAirTemperature(parseBigDecimal(getValue(station, "airtemperature")));
                    obs.setWindSpeed(parseBigDecimal(getValue(station, "windspeed")));
                    obs.setWeatherPhenomenon(getValue(station, "phenomenon"));

                    //records the current time as the timestamp
                    obs.setTimestamp(LocalDateTime.now());

                    repository.save(obs);
                }
            }
        } catch (Exception e) {
            //if anything goes wrong, prints the error but won't crash the app
            System.err.println("Failed to import weather data: " + e.getMessage());
        }
    }

    //helper method: finds a specific tag in the XML and returns its text value
    private String getValue(Element element, String tag) {
        NodeList list = element.getElementsByTagName(tag);
        if (list.getLength() > 0) return list.item(0).getTextContent();
        return ""; //returns empty string if the tag doesn't exist
    }

    //helper method: converts a text number like "-2.1" into an actual number
    private BigDecimal parseBigDecimal(String value) {
        try { return new BigDecimal(value); }
        catch (Exception e) { return null; }
    }
}
