package org.hablapps
package puretest
package test

import cats.{MonadError, MonadState}
import cats.data.StateT
import cats.implicits._

import SpecScalatestStateT.Program

class SpecScalatestStateT extends scalatestImpl.ScalatestFunSpec[Program, Throwable] with Spec[Program] {

  val MS = MonadState[Program, Int]
  val ME = MonadError[Program, Throwable]
  val Te = StateTester[Program, Int, PureTestError[Throwable]]
  val RE = RaiseError[Program, PureTestError[Throwable]]

  lazy val Tester = Te(0)
}

object SpecScalatestStateT {
  type Program[T] = StateT[Either[PureTestError[Throwable], ?], Int, T]
}