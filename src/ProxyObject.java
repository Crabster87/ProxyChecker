import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProxyObject {

    private String ip;
    private int port;

    private ProxyObject(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    /**
     * Method extracts Document JSOUP from https://hidemy.name/ru/proxy-list/
     * @return Document JSOUP (HTML view)
     */

    private static Document getDocumentFromURL() {
        Document doc = null;
        try {
            doc = Jsoup.connect("https://hidemy.name/ru/proxy-list/")
                    .userAgent("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2")
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }

    /**
     * Method parses IP addresses and numbers of ports from Document and adds them to List
     * @return List of ProxyObjects
     */

    public static List<ProxyObject> createProxiesList() {
        List<ProxyObject> addressCollector = new ArrayList<>();
        Element table = getDocumentFromURL().select("table").first();  //Finding the first table of the document
        Elements rows = table.select("tr");  // Dividing the table into rows by teg
        for (int i = 1; i < rows.size(); i++) {  //Skipping the table header
            Element row = rows.get(i);  //Getting row by index
            Elements columns = row.select("td");  // Dividing the row into columns by teg

            String ip = columns.get(0).text();
            int port = Integer.parseInt(columns.get(1).text());
            addressCollector.add(new ProxyObject(ip, port));
        }
        return addressCollector;
    }


}
