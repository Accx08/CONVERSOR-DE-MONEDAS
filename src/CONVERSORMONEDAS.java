import okhttp3.*;
import com.google.gson.*;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;
import java.util.LinkedHashMap;
import java.util.Map;

public class CONVERSORMONEDAS {

    private static final String API_KEY = "3317e79a59740c3a64c99557";
    private static final String BASE_URL = "https://api.exchangerate-api.com/v4/latest/";

    private static final Map<Integer, String> MONEDAS = new LinkedHashMap<>() {{
        put(1, "USD"); // Dólar estadounidense
        put(2, "EUR"); // Euro
        put(3, "JPY"); // Yen japonés
        put(4, "GBP"); // Libra esterlina
        put(5, "MXN"); // Dólar australiano
        put(6, "CAD"); // Dólar canadiense
        put(7, "CHF"); // Franco suizo
        put(8, "CNY"); // Yuan chino
        put(9, "HKD"); // Dólar de Hong Kong
        put(10, "AUD"); // Dólar neozelandés
    }};

    public static double convertir(String monedaOrigen, String monedaDestino, double cantidad) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(BASE_URL + monedaOrigen)
                .addHeader("apikey", API_KEY)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Error en la solicitud a la API: " + response);
            }

            Gson gson = new Gson();
            JsonObject json = gson.fromJson(Objects.requireNonNull(response.body()).charStream(), JsonObject.class);

            JsonObject rates = json.getAsJsonObject("rates");
            if (!rates.has(monedaDestino)) {
                throw new IllegalArgumentException("Moneda destino no válida: " + monedaDestino);
            }
            double tasa = rates.get(monedaDestino).getAsDouble();

            return cantidad * tasa;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Selecciona la moneda de origen:");
        System.out.println("-----------------------------");
        System.out.printf("%-5s %-10s%n", "Opción", "Moneda");
        System.out.println("-----------------------------");
        for (Map.Entry<Integer, String> entry : MONEDAS.entrySet()) {
            System.out.printf("%-5d %-10s%n", entry.getKey(), entry.getValue());
        }
        System.out.println("-----------------------------");

        int opcionOrigen;
        do {
            System.out.print("Opción: ");
            while (!scanner.hasNextInt()) {
                System.out.println("Error: Ingresa un número válido.");
                scanner.next(); // Limpia el buffer
            }
            opcionOrigen = scanner.nextInt();
        } while (!MONEDAS.containsKey(opcionOrigen));
        String monedaOrigen = MONEDAS.get(opcionOrigen);
        scanner.nextLine();

        System.out.println("\nSelecciona la moneda destino:");
        System.out.println("-----------------------------");
        System.out.printf("%-5s %-10s%n", "Opción", "Moneda");
        System.out.println("-----------------------------");
        for (Map.Entry<Integer, String> entry : MONEDAS.entrySet()) {
            System.out.printf("%-5d %-10s%n", entry.getKey(), entry.getValue());
        }
        System.out.println("-----------------------------");


        int opcionDestino;
        do {
            System.out.print("Opción: ");
            while (!scanner.hasNextInt()) {
                System.out.println("Error: Ingresa un número válido.");
                scanner.next();
            }
            opcionDestino = scanner.nextInt();
        } while (!MONEDAS.containsKey(opcionDestino));
        String monedaDestino = MONEDAS.get(opcionDestino);
        scanner.nextLine();

        System.out.print("Ingresa la cantidad: ");
        while (!scanner.hasNextDouble()) {
            System.out.println("Error: Ingresa una cantidad numérica válida.");
            scanner.next();
        }
        double cantidad = scanner.nextDouble();

        try {
            double resultado = convertir(monedaOrigen, monedaDestino, cantidad);
            System.out.printf("%.2f %s = %.2f %s%n", cantidad, monedaOrigen, resultado, monedaDestino);
        } catch (IOException e) {
            System.out.println("Error de conexión: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}

