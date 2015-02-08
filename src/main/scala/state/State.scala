package state

/**
 * Begin sets up some state,  end destroys it.
 * flatMap takes some function
 */
trait State {
  def init(): Unit = () => ()
  def begin(): Unit = () => ()
  def end(): Unit = () => ()

  def run[B](f: => B) = {
    begin()
    try {
      f
    } finally {
      end()
    }
  }

  /**
   * Combine two states,  where this one is the "outer" state.
   */
  def push(other: State): State = {
    val self = this
    new State() {
      override def init() { self.init(); other.init() }
      override def begin() { self.begin(); other.begin() }
      override def end() { other.end(); self.end() }
    }
  }
}
