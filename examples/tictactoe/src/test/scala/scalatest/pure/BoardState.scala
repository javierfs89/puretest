package org.hablapps.puretest
package examples.tictactoe
package test
package pure

import cats.MonadError
import cats.data.StateT
import cats.instances.either._

import TicTacToe._

object BoardState {

  /* Auxiliary types */

  type Program[A] = StateT[Either[PuretestError[Error], ?], BoardState, A]

  /* Auxiliary values */

  val Inner = examples.tictactoe.BoardState.Instance

  /* Instance */

  object Instance extends TicTacToe[Program] {

    /* Evidences */
    val ME: MonadError[Program, Error] = PuretestError.toMonadError

    /* Transformers */
    def reset: Program[Unit] = Inner.reset.liftTo[Program]
    def place(stone: Stone, position: Position): Program[Unit] = Inner.place(stone, position).liftTo[Program]

    /* Observers */
    def in(position: Position): Program[Option[Stone]] = Inner.in(position).liftTo[Program]
    def turn: Program[Option[Stone]] = Inner.turn.liftTo[Program]
    def win(stone: Stone): Program[Boolean] = Inner.win(stone).liftTo[Program]
  }
}
