package org.hablapps.puretest
package examples.tictactoe
package test

import scalatestImpl.ScalatestFunSpec
import cats.instances.either._
import BoardState.Program

class BoardStateFilterSpec extends ScalatestFunSpec[Program, TicTacToe.Error]
    with TicTacToeFilterSpec[Program] {

  val ticTacToe = BoardState.BoardTicTacToe
  val Tester = StateTester[Program, BoardState, PureTestError[TicTacToe.Error]].apply(BoardState.empty)
  override val MPE = implicitly

}
