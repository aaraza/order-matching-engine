package gg.aliraza.ome.services.symbols;

import gg.aliraza.ome.models.symbols.Symbol;
import tools.jackson.databind.cfg.CoercionAction;
import tools.jackson.databind.cfg.CoercionInputShape;
import tools.jackson.dataformat.csv.CsvMapper;
import tools.jackson.dataformat.csv.CsvSchema;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Handles reading and writing symbol data to a CSV cache file in src/main/resources.
 * The cache is considered stale after 24 hours.
 */
class SymbolCache {

    private static final Path CACHE_PATH = Path.of("src/main/resources/symbols.csv");
    private static final Duration CACHE_TTL = Duration.ofHours(24);
    private static final CsvMapper CSV_MAPPER = CsvMapper.builder()
            .withCoercionConfigDefaults(config ->
                    config.setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsNull))
            .build();
    private static final CsvSchema SCHEMA = CSV_MAPPER.schemaFor(Symbol.class).withHeader();

    /**
     * Checks whether a valid, non-stale cache file exists.
     * @return true if the cache file exists and was last modified within the last 24 hours
     */
    static boolean isCacheValid() {
        if (!Files.exists(CACHE_PATH)) {
            return false;
        }
        try {
            Instant lastModified = Files.getLastModifiedTime(CACHE_PATH).toInstant();
            return Duration.between(lastModified, Instant.now()).compareTo(CACHE_TTL) < 0;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Reads symbols from the CSV cache file.
     * @return A map of ticker to {@link Symbol}
     * @throws IOException If the cache file cannot be read
     */
    static Map<String, Symbol> readCache() throws IOException {
        List<Symbol> symbols = CSV_MAPPER
                .readerFor(Symbol.class)
                .with(SCHEMA)
                .<Symbol>readValues(CACHE_PATH.toFile())
                .readAll();

        return symbols.stream()
                .collect(Collectors.toMap(Symbol::getTicker, Function.identity(), (a, b) -> a));
    }

    /**
     * Writes symbols to the CSV cache file.
     * @param symbols The map of symbols to cache
     * @throws IOException If the cache file cannot be written
     */
    static void writeCache(Map<String, Symbol> symbols) throws IOException {
        Files.createDirectories(CACHE_PATH.getParent());
        CSV_MAPPER.writerFor(Symbol.class)
                .with(SCHEMA)
                .writeValues(CACHE_PATH.toFile())
                .writeAll(symbols.values());
    }
}
