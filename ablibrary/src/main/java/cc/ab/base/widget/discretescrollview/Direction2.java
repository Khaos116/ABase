package cc.ab.base.widget.discretescrollview;

/**
 * Created by yarolegovich on 16.03.2017.
 */
public class Direction2 {
  public static int applyTo(Direction d, int delta) {
    if (d == Direction.START) {
      return delta * -1;
    } else {
      return delta;
    }
  }

  public static boolean sameAs(Direction d, int direction) {
    if (d == Direction.START) {
      return direction < 0;
    } else {
      return direction > 0;
    }

  }

  public static Direction fromDelta(int delta) {
    return delta > 0 ? Direction.END : Direction.START;
  }
}