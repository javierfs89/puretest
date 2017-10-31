package org.hablapps.puretest
package examples.tictactoe
package test
package pure

import scalatestImpl.ScalatestFunSpec
import cats.instances.either._

import BoardStatePOC.Program
import examples.tictactoe.BoardState.{empty => emptyBoard}

class TicTacToeSpecPOC extends ScalatestFunSpec[Program, TicTacToe.Error]
    with test.TicTacToeSpec[Program] {

  val ticTacToe = BoardStatePOC.Instance
  val Tester = (new StateTester[Program, BoardState, PuretestError[TicTacToe.Error]] {
      def apply(state: BoardState) = new Tester[Program, PuretestError[TicTacToe.Error]] {
        def apply[A](fa: Program[A]): Either[PuretestError[TicTacToe.Error], A] =
          fa.flatMap(_.runA(state).left.map(ApplicationError(_)))
      }
    }).apply(emptyBoard)
  val RE = new RaiseError[Program, PuretestError[TicTacToe.Error]] {
    def raiseError[A](e: PuretestError[TicTacToe.Error]): Program[A] = Left(e)
  }

}
