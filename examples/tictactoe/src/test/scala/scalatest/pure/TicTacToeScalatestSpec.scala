package org.hablapps.puretest
package examples.tictactoe
package test
package pure

import scalatestImpl.FunSpec
import cats.MonadError
import cats.instances.either._

import TicTacToe._
import BoardState.Program
import examples.tictactoe.BoardState.{empty => emptyBoard}

class BoardStateSpec extends FunSpec[Program, Error] with test.TicTacToeSpec[Program] {
  val PM = PureMatchersTC[TP, Error]

  lazy val T = StateTester[Program, BoardState, Error].apply(emptyBoard)
  val ticTacToe = new TicTacToe[TP] {
    val ME = testProgramMonadError(BoardState.Instance.ME)

    def reset: TP[Unit] = Right(BoardState.Instance.reset)
    def place(stone: Stone, position: Position): TP[Unit] = Right(BoardState.Instance.place(stone, position))

    def in(position: Position): TP[Option[Stone]] = Right(BoardState.Instance.in(position))
    def turn: TP[Option[Stone]] = Right(BoardState.Instance.turn)
    def win(stone: Stone): TP[Boolean] = Right(BoardState.Instance.win(stone))
  }
}
