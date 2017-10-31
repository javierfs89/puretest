package org.hablapps.puretest
package examples.tictactoe
package test
package pure

import cats.MonadError
import cats.data.StateT

import TicTacToe._

object BoardStatePOC {

  /* Auxiliary types */

  type Program[A] = Either[PuretestError[Error], StateT[Either[Error, ?], BoardState, A]]

  /* Auxiliary values */

  val Inner = examples.tictactoe.BoardState.Instance

  /* Instance */

  object Instance extends TicTacToe[Program] {

    /* Evidences */
    val ME = new MonadError[Program, Error] {
      def pure[A](x: A): Program[A] = Right(Inner.ME.pure(x))
      def flatMap[A, B](fa: Program[A])(f: A => Program[B]): Program[B] =
        fa map { st =>
          Inner.ME.flatMap(st) { a =>
            f(a) match {
              case Left(pe) => throw new RuntimeException(pe.toString)
              case Right(r) => r
            }
          }
        }
      def tailRecM[A, B](a: A)(f: A => Program[Either[A, B]]): Program[B] = ???

      def handleErrorWith[A](fa: Program[A])(f: Error => Program[A]): Program[A] =
        fa map { st =>
          Inner.ME.handleErrorWith(st) { e =>
            f(e) match {
              case Left(ApplicationError(e)) => println(s"Application Error: $e") ; Inner.ME.raiseError(e)
              case Left(pe) => throw new RuntimeException(pe.toString)
              case Right(r) => r
            }

          }
        }
      def raiseError[A](e: Error): Program[A] =
        Right(Inner.ME.raiseError(e))
    }

    /* Transformers */
    def reset: Program[Unit] = Right(Inner.reset)
    def place(stone: Stone, position: Position): Program[Unit] = Right(Inner.place(stone, position))

    /* Observers */
    def in(position: Position): Program[Option[Stone]] = Right(Inner.in(position))
    def turn: Program[Option[Stone]] = Right(Inner.turn)
    def win(stone: Stone): Program[Boolean] = Right(Inner.win(stone))
  }
}
