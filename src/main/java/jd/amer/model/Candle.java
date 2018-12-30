package jd.amer.model;

import java.math.BigDecimal;

/**
 *
 */
public class Candle {
    private BigDecimal volume = null;
    private BigDecimal open = null;
    private BigDecimal high = null;
    private BigDecimal low = null;
    private BigDecimal close = null;
    private long datetime = 0;

    public BigDecimal getVolume () {
        return volume;
    }

    public BigDecimal getOpen () {
        return open;
    }

    public BigDecimal getHigh () {
        return high;
    }

    public BigDecimal getLow () {
        return low;
    }

    public BigDecimal getClose () {
        return close;
    }

    public long getDatetime () {
        return datetime;
    }

    @Override
    public String toString () {
        return "Candles{" + "volume=" + volume + ", open=" + open + ", high=" + high + ", low=" + low + ", close=" + close + ", datetime=" + datetime + '}';
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Candle candle = (Candle) o;

        if (datetime != candle.datetime) {
            return false;
        }
        if (!volume.equals(candle.volume)) {
            return false;
        }
        if (!open.equals(candle.open)) {
            return false;
        }
        if (!high.equals(candle.high)) {
            return false;
        }
        if (!low.equals(candle.low)) {
            return false;
        }
        return close.equals(candle.close);

    }

    @Override
    public int hashCode () {
        int result = volume.hashCode();
        result = 31 * result + open.hashCode();
        result = 31 * result + high.hashCode();
        result = 31 * result + low.hashCode();
        result = 31 * result + close.hashCode();
        result = 31 * result + (int) (datetime ^ (datetime >>> 32));
        return result;
    }
}
