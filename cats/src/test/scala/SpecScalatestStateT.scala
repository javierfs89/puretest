package org.hablapps
package puretest
package test

import cats.MonadState
import cats.data.StateT
import cats.implicits._

import SpecScalatestStateT.Program

class SpecScalatestStateT extends scalatestImpl.ScalatestFunSpec[Program, Throwable] with Spec[Program] {

  val MS = MonadState[Program, Int]
  implicit val ME = PureTestError.toMonadError
  val Te = StateTester[Program, Int, PureTestError[Throwable]]
  val HE = HandleError[Program, Throwable]
  val RE = RaiseError[Program, PureTestError[Throwable]]

  lazy val Tester = Te(0)
}

object SpecScalatestStateT {
  type Program[T] = StateT[Either[PureTestError[Throwable], ?], Int, T]
}