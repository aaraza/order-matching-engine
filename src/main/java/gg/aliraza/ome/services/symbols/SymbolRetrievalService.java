package gg.aliraza.ome.services.symbols;

import gg.aliraza.ome.models.symbols.Symbol;
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
public class SymbolRetrievalService {

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Fetches the list of valid symbols available for trading on US exchanges.
     * @return A map containing list of Symbols being traded on US exchanges. Key = Ticker name; Value = {@link Symbol}
     * @throws IOException If FinnHub API call returns a non 200 status code or there is an IO error while sending the request
     * @throws InterruptedException If FinnHub API call is interrupted
     */
    public Map<String, Symbol> fetchSymbols() throws IOException, InterruptedException {
        HttpRequest httpRequest = createHttpRequest();
        HttpResponse<InputStream> response = HTTP_CLIENT.send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
        return processFinnHubResponse(response);
    }

    private HttpRequest createHttpRequest() {
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

    private Map<String, Symbol> processFinnHubResponse(HttpResponse<InputStream> finnhubResponse) throws IOException {

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
