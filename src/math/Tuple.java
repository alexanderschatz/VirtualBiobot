package math;


/* custom Tuple class 
 * by @maerics and @Aram Kocharyan from stackoverflow
 * https://stackoverflow.com/questions/2670982/using-pairs-or-2-tuples-in-java
 * last access: 30 Nov 2019
 */
public class Tuple<X, Y> {
	public final X x;
	public final Y y;

	public Tuple(X x, Y y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}

		if (!(other instanceof Tuple)) {
			return false;
		}

		Tuple<X, Y> other_ = (Tuple<X, Y>) other;

		// this may cause NPE if nulls are valid values for x or y. The logic may be
		// improved to handle nulls properly, if needed.
		return other_.x.equals(this.x) && other_.y.equals(this.y);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((x == null) ? 0 : x.hashCode());
		result = prime * result + ((y == null) ? 0 : y.hashCode());
		return result;
	}
}
