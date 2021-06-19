import java.io.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class Checker {

    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RED = "\u001B[31m";

    public static void main(String[] args) {
        List<ProxyObject> list = ProxyObject.createProxiesList();
        list.parallelStream().forEach(e -> checkProxy(e));
    }

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
            System.out.println(ANSI_RED + object.getIp() + " - is not available!");
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