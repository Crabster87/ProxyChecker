import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        Map<String, Integer> map = parseIPAddressesAndPortsFromFile();
        map.entrySet().parallelStream().forEach(e -> checkProxy(e.getKey(), e.getValue()));
    }

    /**
     * Method parses IP addresses and numbers of ports from https://hidemy.name/ru/proxy-list/
     * The data was received from <table></table>
     * @return HashMap (key : IP address; value : port)
     */

    public static Map<String, Integer> parseIPAddressesAndPortsFromFile() {
        Map<String, Integer> addressCollector = new HashMap<>();
        Document doc = null;
        try {
            doc = Jsoup.connect("https://hidemy.name/ru/proxy-list/")
                    .userAgent("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2")
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements listOfIpPort = doc.select("tr > td:nth-child(1):matches(([0-9]{1,3}[\\\\.]){3}[0-9]{1,3}), " +
                                           "td:nth-child(2):matches(^([0-9]{1,4}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$)");
        for (int i = 0; i < listOfIpPort.size() - 1; i++) {
            addressCollector.put(listOfIpPort.get(i).text(), Integer.parseInt(listOfIpPort.get(i + 1).text()));
            i++;
        }
        return addressCollector;
    }

    /**
     * Method creates URL connection and checks IP addresses, that can be used as proxies
     *
     * @param ip   IP address
     * @param port Number of port
     */

    public static void checkProxy(String ip, int port) {
        try {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
            URL url = new URL("https://www.google.com/maps");
            URLConnection urlConnection = url.openConnection(proxy);
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(20000);

            InputStream is = urlConnection.getInputStream();
            writeDataFromURLToFile(is);

            writeValidProxyIPAddressToFile(ip, port);
            System.out.println(ip + " - proxy функционирует!");
        } catch (IOException | NoSuchElementException e) {
            System.out.println(ip + " - не работает!");
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
     * @param ip   IP address
     * @param port Number of port
     */

    public static void writeValidProxyIPAddressToFile(String ip, int port) throws IOException {
        File outputResult = new File("good_ip.txt");
        StringBuilder stringBuilder = new StringBuilder(ip);
        try (FileOutputStream writer = new FileOutputStream(outputResult.getAbsolutePath(), true)) {
            stringBuilder.insert(0, "\n").append(": ").append(port);
            writer.write(stringBuilder.toString().getBytes());
        }
    }

}