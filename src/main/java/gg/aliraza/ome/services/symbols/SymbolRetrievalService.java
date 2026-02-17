package gg.aliraza.ome.services.symbols;

import gg.aliraza.ome.models.symbols.Symbol;
import lombok.Getter;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Calls the FINNHUB /symbol API to get a list of valid symbols being traded on US exchanges.
 */
@Getter
public class SymbolRetrievalService {

    private static volatile SymbolRetrievalService instance;
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * List of valid symbols available for trading on US exchanges.<br>
     * Key = Ticker name; Value = {@link Symbol}
     */
    private final Map<String, Symbol> symbols;

    private SymbolRetrievalService(Map<String, Symbol> symbols) {
        this.symbols = Map.copyOf(symbols);
    }

    /**
     * <b>Double-checked locking pattern for thread-safe singletons initialization</b>
     *
     * <p>The first null check outside the synchronized block is in place because once the singleton is initialized,
     * all subsequent calls to initialize will return immediately without touching the lock.</p>
     *
     * <p>If the instance hasn't been created, multiple threads may try to create it at once. The synchronized keyword
     * acquires a lock on the class object, only allowing one thread to enter the block at a time. The other threads
     * need to wait till the first thread's execution of the synchronized block is complete.</p>
     *
     * <p>Once the first thread has completed its execution of the synchronized block, the second null check shall return
     * false, preventing any other threads that are waiting for the class' lock to be released from re-initializing
     * the class</p>
     *
     * <p>We need to use a volatile for instance here because when a new instance is created, the JVM:
     *   <ol>
     *     <li>Allocates memory for the object</li>
     *     <li>Populates the class' fields</li>
     *     <li>Assigns a reference to instance</li>
     *   </ol>
     * </p>
     *
     * <p>Without volatile, the JVM can reorder 2 and 3. Thread A could assign a reference before the constructor
     * finishes. Thread B sees the instance is not null and skips the synchronized block. Subsequently, it could read
     * from the map that hasn't yet been initialized.</p>
     *
     * <p>Volatile guarantees that by the time a reference is assigned and visible to other threads, all the fields have
     * been initialized.</p>
     */
    public static void initialize() throws IOException, InterruptedException {
        if(instance == null) {
            synchronized (SymbolRetrievalService.class) {
                if (instance == null) {
                    instance = new SymbolRetrievalService(fetchSymbols());
                }
            }
        }
    }

    public static SymbolRetrievalService getInstance() {
        if(instance == null) {
            throw new IllegalStateException("Symbol retrieval service not initialized");
        }
        return instance;
    }

    /**
     * Returns symbols from the local CSV cache if it is less than 24 hours old,
     * otherwise fetches fresh data from the FinnHub API and updates the cache.
     * @return A map containing list of Symbols being traded on US exchanges. Key = Ticker name; Value = {@link Symbol}
     * @throws IOException If FinnHub API call returns a non 200 status code or there is an IO error while sending the request
     * @throws InterruptedException If FinnHub API call is interrupted
     */
    private static Map<String, Symbol> fetchSymbols() throws IOException, InterruptedException {
        if (SymbolCache.isCacheValid()) {
            System.out.println("Reading Symbols from cache");
            return SymbolCache.readCache();
        }
        Map<String, Symbol> symbols = fetchFromFinnHub();
        SymbolCache.writeCache(symbols);
        return symbols;
    }

    private static Map<String, Symbol> fetchFromFinnHub() throws IOException, InterruptedException {
        System.out.println("Finnhub cache invalid. Making API call");
        HttpRequest httpRequest = createHttpRequest();
        HttpResponse<InputStream> response = HTTP_CLIENT.send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
        return processFinnHubResponse(response);
    }

    private static HttpRequest createHttpRequest() {
        String apiKey = System.getenv("FINNHUB_TOKEN");
        if (null == apiKey || apiKey.isBlank()) {
            throw new IllegalStateException("Required environment variable FINNHUB_TOKEN is not set.");
        }

        return HttpRequest.newBuilder()
                .uri(URI.create("https://finnhub.io/api/v1/stock/symbol?exchange=US"))
                .header("X-Finnhub-Token", apiKey)
                .GET()
                .build();
    }

    private static Map<String, Symbol> processFinnHubResponse(HttpResponse<InputStream> finnhubResponse) throws IOException {

        if(finnhubResponse.statusCode() != 200) {
            throw new IOException("FinnHub API failed: " + finnhubResponse.statusCode());
        }

        try(InputStream stream = finnhubResponse.body()) {
            List<Symbol> symbolList = OBJECT_MAPPER.readValue(stream, new TypeReference<>() {});
            return symbolList.stream()
                    .collect(Collectors.toMap(
                            Symbol::getTicker,
                            Function.identity(),
                            (existing, replacement) -> existing
                    ));
        }
    }

}
