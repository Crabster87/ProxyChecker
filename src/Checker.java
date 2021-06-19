import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class Main {

    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RED = "\u001B[31m";

    public static void main(String[] args) {
        List<ProxyObject> list = ProxyObject.createProxiesList();
        list.parallelStream().forEach(e -> checkProxy(e));
    }

//    /**
//     * Method parses IP addresses and numbers of ports from https://hidemy.name/ru/proxy-list/
//     * @return Document JSOUP (HTML view)
//     */
//
//    public static Document getDocumentFromURL() {
//        Document doc = null;
//        try {
//            doc = Jsoup.connect("https://hidemy.name/ru/proxy-list/")
//                    .userAgent("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2")
//                    .get();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return doc;
//    }
//
//    /**
//     * Method extracts IP addresses and numbers of ports from Document and puts them into HashMap
//     * @return HashMap (key : IP address; value : port)
//     */
//
//    public static Map<String, Integer> createConnectionsList() {
//        Map<String, Integer> addressCollector = new HashMap<>();
//        Element table = getDocumentFromURL().select("table").first();  //Finding the first table of the document
//        Elements rows = table.select("tr");  // Dividing the table into rows by teg
//        for (int i = 1; i < rows.size(); i++) {  //Skipping the table header
//            Element row = rows.get(i);  //Getting row by index
//            Elements columns = row.select("td");  // Dividing the row into columns by teg
//            addressCollector.put(columns.get(0).text(), Integer.parseInt(columns.get(1).text()));
//        }
//        return addressCollector;
//    }

    /**
     * Method creates URL connection and checks IP addresses, that can be used as proxies
     *
     * @param object   Proxy address
     */

    public static void checkProxy(ProxyObject object) {
        try {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(object.getIp(), object.getPort()));
            URL url = new URL("https://www.google.com/maps");
            URLConnection urlConnection = url.openConnection(proxy);
            urlConnection.setConnectTimeout(20000);
            urlConnection.setReadTimeout(20000);

            InputStream is = urlConnection.getInputStream();
            writeDataFromURLToFile(is);

            writeValidProxyIPAddressToFile(object);
            System.out.println(ANSI_GREEN + object.getIp() + " - proxy is functioning!");
        } catch (IOException | NoSuchElementException e) {
            System.out.println(ANSI_RED + object.getPort() + " - is not available!");
        }
    }

    /**
     * OPTIONALLY
     * Method read data from URL and then writes it to the file "downloaded_data.txt"
     *
     * @param is InputStream of URL connection
     */

    public static void writeDataFromURLToFile(InputStream is) throws IOException {
        File outputResult = new File("downloaded_data.txt");
        StringBuilder result;
        try (InputStreamReader reader = new InputStreamReader(is);
             FileWriter writer = new FileWriter(outputResult.getAbsolutePath())) {
            int i;
            result = new StringBuilder();
            while ((i = reader.read()) != -1) {
                result.append((char) i);
            }
            writer.write(result.toString());
        }
    }

    /**
     * Method writes valid IP addresses, that can be used as proxies
     *
     * @param object   Proxy address
     */

    public static void writeValidProxyIPAddressToFile(ProxyObject object) throws IOException {
        File outputResult = new File("good_ip.txt");
        StringBuilder stringBuilder = new StringBuilder(object.getIp());
        try (FileOutputStream writer = new FileOutputStream(outputResult.getAbsolutePath(), true)) {
            stringBuilder.insert(0, "\n").append(": ").append(object.getPort());
            writer.write(stringBuilder.toString().getBytes());
        }
    }

}