package org.hablapps.puretest
package test

import cats.{MonadState, MonadError}
import cats.syntax.all._

trait BooleanPrograms[P[_]] { self: FunSpec[P, Throwable] =>

  implicit val MS: MonadState[TP, Int]
  val ME: MonadError[TP, Throwable]

  def trueProgram: TP[Boolean] =
    for {
      _ <- MS.set(1)
      1 <- MS.get
    } yield true

  def falseProgram: TP[Boolean] =
    false.pure[TP]

  def failingMatchBoolProgram: TP[Boolean] =
    for {
      _ <- MS.set(1)
      2 <- MS.get
    } yield false

  def raisedErrorBoolProgram: TP[Boolean] =
    ME.raiseError(new RuntimeException("forced exception"))
}
