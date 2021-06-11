import java.io.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        Map<String, Integer> map = parseIPAddressesAndPortsFromFile();
        map.forEach((ip, port) -> checkProxy(ip, port));
    }

    /**
     * Method parses IP addresses and numbers of ports from text file
     * The data was received from     https://hidemy.name/ru/proxy-list/
     * @return LinkedHashMap (key : IP address; value : port)
     */

    public static Map<String, Integer> parseIPAddressesAndPortsFromFile() {
        File listIp = new File("ip_list.txt");
        Map<String, Integer> addressCollector = new LinkedHashMap<>();
        try (BufferedReader buffer = new BufferedReader(new FileReader(listIp.getAbsolutePath()))) {
            String line;
            while ((line = buffer.readLine()) != null) {
                String[] array = line.split("\t");
                addressCollector.put(array[0], Integer.parseInt(array[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
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
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(10000);

            //InputStream is = urlConnection.getInputStream();
            //writeDataFromURLToFile(is);

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