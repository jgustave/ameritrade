package jd.amer.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class CandleList {
    private boolean empty;
    private String symbol;
    private List<Candle> candles = new ArrayList<>();

    public boolean isEmpty () {
        return empty;
    }

    public String getSymbol () {
        return symbol;
    }

    public List<Candle> getCandles () {
        return candles;
    }

    @Override
    public String toString () {
        return "CandleList{" + "empty=" + empty + ", symbol='" + symbol + '\'' + ", candles=" + candles + '}';
    }
}
