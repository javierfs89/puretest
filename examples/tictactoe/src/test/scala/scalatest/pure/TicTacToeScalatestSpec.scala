package org.hablapps.puretest
package examples.tictactoe
package test
package pure

import scalatestImpl.ScalatestFunSpec
import cats.instances.either._

import BoardState.Program
import examples.tictactoe.BoardState.{empty => emptyBoard}

class TicTacToeSpec extends ScalatestFunSpec[Program, TicTacToe.Error]
    with test.TicTacToeSpec[Program] {

  val ticTacToe = BoardState.Instance
  val Tester = StateTester[Program, BoardState, PuretestError[TicTacToe.Error]].apply(emptyBoard)
  val RE = RaiseError[Program, PuretestError[TicTacToe.Error]]

}
