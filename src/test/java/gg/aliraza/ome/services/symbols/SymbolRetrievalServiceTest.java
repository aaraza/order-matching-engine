package gg.aliraza.ome.services.symbols;

import gg.aliraza.ome.models.symbols.Symbol;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests retrieval of valid symbols from FINNHUB API.
 */
class SymbolRetrievalServiceTest {


    @BeforeAll
    static void verifyApiKeyIsSet() {
        assertNotNull(System.getenv("FINNHUB_TOKEN"),
                "FINNHUB_TOKEN environment variable must be set to run this test");
    }

    @Test
    void fetchSymbols() throws IOException, InterruptedException {
        SymbolRetrievalService.initialize();
        SymbolRetrievalService symbolRetrievalService = SymbolRetrievalService.getInstance();
        Map<String, Symbol> symbols = symbolRetrievalService.getSymbols();
        assertFalse(symbols.isEmpty(), "Expected at-least one symbol from FINNHUB");
    }

}
