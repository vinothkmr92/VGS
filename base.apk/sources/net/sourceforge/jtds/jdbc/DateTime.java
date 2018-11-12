package net.sourceforge.jtds.jdbc;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.GregorianCalendar;

public class DateTime {
    static final int DATE_NOT_USED = Integer.MIN_VALUE;
    static final int TIME_NOT_USED = Integer.MIN_VALUE;
    private int date;
    private Date dateValue;
    private short day;
    private short hour;
    private short millis;
    private short minute;
    private short month;
    private short second;
    private String stringValue;
    private int time;
    private Time timeValue;
    private Timestamp tsValue;
    private boolean unpacked;
    private short year;

    DateTime(int i, int i2) {
        this.date = i;
        this.time = i2;
    }

    DateTime(short s, short s2) {
        this.date = s & (short) -1;
        this.time = (s2 * 60) * 300;
    }

    DateTime(Timestamp timestamp) throws SQLException {
        this.tsValue = timestamp;
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(timestamp);
        if (gregorianCalendar.get(0) != 1) {
            throw new SQLException(Messages.get("error.datetime.range.era"), "22007");
        }
        if (!Driver.JDBC3) {
            gregorianCalendar.set(14, timestamp.getNanos() / 1000000);
        }
        this.year = (short) gregorianCalendar.get(1);
        this.month = (short) (gregorianCalendar.get(2) + 1);
        this.day = (short) gregorianCalendar.get(5);
        this.hour = (short) gregorianCalendar.get(11);
        this.minute = (short) gregorianCalendar.get(12);
        this.second = (short) gregorianCalendar.get(13);
        this.millis = (short) gregorianCalendar.get(14);
        packDate();
        packTime();
        this.unpacked = true;
    }

    DateTime(Time time) throws SQLException {
        this.timeValue = time;
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(time);
        if (gregorianCalendar.get(null) != 1) {
            throw new SQLException(Messages.get("error.datetime.range.era"), "22007");
        }
        this.date = Integer.MIN_VALUE;
        this.year = (short) 1900;
        this.month = (short) 1;
        this.day = (short) 1;
        this.hour = (short) gregorianCalendar.get(11);
        this.minute = (short) gregorianCalendar.get(12);
        this.second = (short) gregorianCalendar.get(13);
        this.millis = (short) gregorianCalendar.get(14);
        packTime();
        this.year = (short) 1970;
        this.month = (short) 1;
        this.day = (short) 1;
        this.unpacked = true;
    }

    DateTime(Date date) throws SQLException {
        this.dateValue = date;
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(date);
        if (gregorianCalendar.get(0) != 1) {
            throw new SQLException(Messages.get("error.datetime.range.era"), "22007");
        }
        this.year = (short) gregorianCalendar.get(1);
        this.month = (short) (gregorianCalendar.get(2) + 1);
        this.day = (short) gregorianCalendar.get(5);
        this.hour = (short) 0;
        this.minute = (short) 0;
        this.second = (short) 0;
        this.millis = (short) 0;
        packDate();
        this.time = Integer.MIN_VALUE;
        this.unpacked = true;
    }

    int getDate() {
        return this.date == Integer.MIN_VALUE ? 0 : this.date;
    }

    int getTime() {
        return this.time == Integer.MIN_VALUE ? 0 : this.time;
    }

    private void unpackDateTime() {
        if (this.date == Integer.MIN_VALUE) {
            this.year = (short) 1970;
            this.month = (short) 1;
            this.day = (short) 1;
        } else if (this.date == 0) {
            this.year = (short) 1900;
            this.month = (short) 1;
            this.day = (short) 1;
        } else {
            int i = (this.date + 68569) + 2415021;
            int i2 = (i * 4) / 146097;
            i -= ((146097 * i2) + 3) / 4;
            int i3 = ((i + 1) * 4000) / 1461001;
            i = (i - ((i3 * 1461) / 4)) + 31;
            int i4 = (i * 80) / 2447;
            i -= (i4 * 2447) / 80;
            int i5 = i4 / 11;
            i4 = (i4 + 2) - (i5 * 12);
            this.year = (short) ((((i2 - 49) * 100) + i3) + i5);
            this.month = (short) i4;
            this.day = (short) i;
        }
        if (this.time == Integer.MIN_VALUE) {
            this.hour = (short) 0;
            this.minute = (short) 0;
            this.second = (short) 0;
        } else {
            i = this.time / 1080000;
            this.time -= 1080000 * i;
            int i6 = this.time / 18000;
            this.time -= i6 * 18000;
            i2 = this.time / 300;
            this.time -= i2 * 300;
            this.time = Math.round(((float) (this.time * 1000)) / 300.0f);
            this.hour = (short) i;
            this.minute = (short) i6;
            this.second = (short) i2;
            this.millis = (short) this.time;
        }
        this.unpacked = true;
    }

    public void packDate() throws SQLException {
        if (this.year >= (short) 1753) {
            if (this.year <= (short) 9999) {
                this.date = ((((this.day - 32075) + ((((this.year + 4800) + ((this.month - 14) / 12)) * 1461) / 4)) + ((((this.month - 2) - (((this.month - 14) / 12) * 12)) * 367) / 12)) - (((((this.year + 4900) + ((this.month - 14) / 12)) / 100) * 3) / 4)) - 2415021;
                return;
            }
        }
        throw new SQLException(Messages.get("error.datetime.range"), "22003");
    }

    public void packTime() {
        this.time = this.hour * 1080000;
        this.time += this.minute * 18000;
        this.time += this.second * 300;
        this.time += Math.round((((float) this.millis) * 300.0f) / 1000.0f);
        if (this.time > 25919999) {
            this.time = 0;
            this.hour = (short) 0;
            this.minute = (short) 0;
            this.second = (short) 0;
            this.millis = (short) 0;
            if (this.date != Integer.MIN_VALUE) {
                GregorianCalendar gregorianCalendar = new GregorianCalendar();
                gregorianCalendar.set(1, this.year);
                gregorianCalendar.set(2, this.month - 1);
                gregorianCalendar.set(5, this.day);
                gregorianCalendar.add(5, 1);
                this.year = (short) gregorianCalendar.get(1);
                this.month = (short) (gregorianCalendar.get(2) + 1);
                this.day = (short) gregorianCalendar.get(5);
                this.date++;
            }
        }
    }

    public Timestamp toTimestamp() {
        if (this.tsValue == null) {
            if (!this.unpacked) {
                unpackDateTime();
            }
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.set(1, this.year);
            gregorianCalendar.set(2, this.month - 1);
            gregorianCalendar.set(5, this.day);
            gregorianCalendar.set(11, this.hour);
            gregorianCalendar.set(12, this.minute);
            gregorianCalendar.set(13, this.second);
            gregorianCalendar.set(14, this.millis);
            this.tsValue = new Timestamp(gregorianCalendar.getTime().getTime());
        }
        return this.tsValue;
    }

    public Date toDate() {
        if (this.dateValue == null) {
            if (!this.unpacked) {
                unpackDateTime();
            }
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.set(1, this.year);
            gregorianCalendar.set(2, this.month - 1);
            gregorianCalendar.set(5, this.day);
            gregorianCalendar.set(11, 0);
            gregorianCalendar.set(12, 0);
            gregorianCalendar.set(13, 0);
            gregorianCalendar.set(14, 0);
            this.dateValue = new Date(gregorianCalendar.getTime().getTime());
        }
        return this.dateValue;
    }

    public Time toTime() {
        if (this.timeValue == null) {
            if (!this.unpacked) {
                unpackDateTime();
            }
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.set(1, 1970);
            gregorianCalendar.set(2, 0);
            gregorianCalendar.set(5, 1);
            gregorianCalendar.set(11, this.hour);
            gregorianCalendar.set(12, this.minute);
            gregorianCalendar.set(13, this.second);
            gregorianCalendar.set(14, this.millis);
            this.timeValue = new Time(gregorianCalendar.getTime().getTime());
        }
        return this.timeValue;
    }

    public Object toObject() {
        if (this.date == Integer.MIN_VALUE) {
            return toTime();
        }
        if (this.time == Integer.MIN_VALUE) {
            return toDate();
        }
        return toTimestamp();
    }

    public String toString() {
        if (this.stringValue == null) {
            int i;
            if (!this.unpacked) {
                unpackDateTime();
            }
            short s = this.day;
            short s2 = this.month;
            short s3 = this.year;
            short s4 = this.millis;
            short s5 = this.second;
            short s6 = this.minute;
            short s7 = this.hour;
            char[] cArr = new char[23];
            if (this.date != Integer.MIN_VALUE) {
                cArr[9] = (char) ((s % 10) + 48);
                cArr[8] = (char) (((s / 10) % 10) + 48);
                cArr[7] = '-';
                cArr[6] = (char) ((s2 % 10) + 48);
                cArr[5] = (char) (((s2 / 10) % 10) + 48);
                cArr[4] = '-';
                cArr[3] = (char) ((s3 % 10) + 48);
                int i2 = s3 / 10;
                cArr[2] = (char) ((i2 % 10) + 48);
                i2 /= 10;
                cArr[1] = (char) ((i2 % 10) + 48);
                cArr[0] = (char) (((i2 / 10) % 10) + 48);
                if (this.time != Integer.MIN_VALUE) {
                    i = 11;
                    cArr[10] = ' ';
                } else {
                    i = 10;
                }
            } else {
                i = 0;
            }
            if (this.time != Integer.MIN_VALUE) {
                i = (i + 12) - 1;
                cArr[i] = (char) ((s4 % 10) + 48);
                int i3 = s4 / 10;
                i--;
                cArr[i] = (char) ((i3 % 10) + 48);
                i--;
                cArr[i] = (char) (((i3 / 10) % 10) + 48);
                i--;
                cArr[i] = '.';
                i--;
                cArr[i] = (char) ((s5 % 10) + 48);
                i--;
                cArr[i] = (char) (((s5 / 10) % 10) + 48);
                i--;
                cArr[i] = ':';
                i--;
                cArr[i] = (char) ((s6 % 10) + 48);
                i--;
                cArr[i] = (char) (((s6 / 10) % 10) + 48);
                i--;
                cArr[i] = ':';
                i--;
                cArr[i] = (char) ((s7 % 10) + 48);
                i--;
                cArr[i] = (char) (((s7 / 10) % 10) + 48);
                i += 12;
                if (cArr[i - 1] == '0') {
                    i--;
                }
                if (cArr[i - 1] == '0') {
                    i--;
                }
            }
            this.stringValue = String.valueOf(cArr, 0, i);
        }
        return this.stringValue;
    }
}
