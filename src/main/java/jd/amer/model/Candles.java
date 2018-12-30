package jd.amer.model;

import java.math.BigDecimal;

/**
 *
 */
public class Candles {
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

        Candles candles = (Candles) o;

        if (datetime != candles.datetime) {
            return false;
        }
        if (!volume.equals(candles.volume)) {
            return false;
        }
        if (!open.equals(candles.open)) {
            return false;
        }
        if (!high.equals(candles.high)) {
            return false;
        }
        if (!low.equals(candles.low)) {
            return false;
        }
        return close.equals(candles.close);

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
