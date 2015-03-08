package subspace.math

trait Matrix {
  def rowCount: Int
  def columnCount: Int
  def apply(columnIndex: Int, rowIndex: Int): Float

  def determinant: Float
}
