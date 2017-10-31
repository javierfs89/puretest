package org.hablapps.puretest
package examples.tictactoe
package test
package pure

import scalatestImpl.ScalatestFunSpec
import cats.instances.either._

import BoardState.Program

class TicTacToeSpec extends ScalatestFunSpec[Program, TicTacToe.Error]
    with test.TicTacToeSpec[Program] {

  val ticTacToe = BoardState.Instance
  val Tester = StateTester[Program, BoardState, PuretestError[TicTacToe.Error]].apply(BoardState.empty)
  val RE = RaiseError[Program, PuretestError[TicTacToe.Error]]

}
