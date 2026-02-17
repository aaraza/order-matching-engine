# Order Matching Engine

A high-performance order matching engine built in Java 25.

## Goals

- Model core exchange concepts: orders, symbols, order books 
- High performance: no framework overhead, caching, and efficient data structures 

## Architecture

```
gg.aliraza.ome
├── models
│   ├── amount      # Amount, AmountType (shares vs. dollars)
│   ├── order       # Order, OrderType (bid/ask), OrderMechanism (limit/market), OrderStatus
│   └── symbols     # Symbol, TradingStatus
└── services
    ├── orders      # CreateOrderService (interface)
    └── symbols     # SymbolRetrievalService, SymbolCache
```

## Market Microstructure Concepts

| Concept | Where it appears |
|---|---|
| Bid/Ask sides | `OrderType.BID`, `OrderType.ASK` |
| Limit vs. Market orders | `OrderMechanism.LIMIT`, `OrderMechanism.MARKET` |
| Order lifecycle | `OrderStatus`: OPEN → PARTIALLY_FILLED → FILLED → SETTLED |
| Share-based and notional orders | `AmountType.SHARES`, `AmountType.DOLLARS` |
| Symbol metadata (MIC, tick size, lot size) | `Symbol` model, sourced from Finnhub |
| Trading halts | `TradingStatus`: TRADING, HALTED, CLOSED |

## Symbol Data

Symbols are sourced from the [Finnhub API](https://finnhub.io/) and cached locally as CSV with a 24-hour TTL. On startup, the engine checks the cache before making an API call.

## Building and Running

```bash
# Build
mvn clean compile

# Run tests (requires FINNHUB_TOKEN)
mvn test
```

## Tech Stack

- **Java 25**
- **Jackson 3** — JSON and CSV serialization
- **Lombok** — boilerplate reduction
- **JUnit 6** — testing

## Roadmap

- [ ] Order book implementation (price-time priority matching)
- [ ] CreateOrderService implementation with order validation
- [ ] Trade execution and fill reporting
- [ ] Performance benchmarks (latency, throughput)
