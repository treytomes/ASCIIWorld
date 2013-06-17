package asciiWorld;

public class DateTime {
	
	private static final int SEC_PER_MIN = 60;
	private static final int MIN_PER_HR = 60;
	private static final int HR_PER_DAY = 24;
	private static final int DAY_PER_MO = 30;
	private static final int MO_PER_YR = 12;
	
	private int _year;
	private int _month;
	private int _day;
	private int _hour;
	private int _minute;
	private int _second;
	
	public DateTime(int hour, int minute, int second) {
		_year = 0;
		_month = 0;
		_day = 0;
		_hour = hour;
		_minute = minute;
		_second = second;
	}
	
	public DateTime() {
		this(0, 0, 0);
	}
	
	public int getYear() {
		return _year;
	}
	
	public int getMonth() {
		return _month;
	}
	
	public int getDay() {
		return _day;
	}
	
	public int getHour() {
		return _hour;
	}
	
	public int getMinute() {
		return _minute;
	}
	
	public int getSecond() {
		return _second;
	}
	
	public void update(int deltaTimeMS) {
		_second += deltaTimeMS;
		while (_second >= SEC_PER_MIN) {
			_minute++;
			_second -= SEC_PER_MIN;
		}
		while (_minute >= MIN_PER_HR) {
			_hour++;
			_minute -= MIN_PER_HR;
		}
		while (_hour >= HR_PER_DAY) {
			_day++;
			_hour -= HR_PER_DAY;
		}
		while (_day >= DAY_PER_MO) {
			_month++;
			_day -= DAY_PER_MO;
		}
		while (_month >= MO_PER_YR) {
			_year++;
			_month -= MO_PER_YR;
		}
	}
	
	@Override
	public String toString() {
		return String.format("%04d-%02d-%02d %02d:%02d:%02d", _year, _month, _day, _hour, _minute, _second);
	}
}