package org.hablapps.puretest
package examples.tictactoe
package test

import scalatestImpl.ScalatestFunSpec
import cats.instances.either._

class BoardStateSpec extends ScalatestFunSpec[BoardState.Program, TicTacToe.Error]
    with TicTacToeSpec[BoardState.Program] {

  val ticTacToe = BoardState.BoardTicTacToe
  val Tester = StateTester[BoardState.Program, BoardState, TicTacToe.Error].apply(BoardState.empty)

}
