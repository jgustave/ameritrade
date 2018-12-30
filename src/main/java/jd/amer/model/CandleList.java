package jd.amer.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class CandleList {
    private boolean empty;
    private String symbol;
    private List<Candles> candles = new ArrayList<>();

    public boolean isEmpty () {
        return empty;
    }

    public String getSymbol () {
        return symbol;
    }

    public List<Candles> getCandles () {
        return candles;
    }
}
