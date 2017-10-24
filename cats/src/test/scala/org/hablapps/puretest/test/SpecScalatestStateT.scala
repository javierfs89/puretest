package org.hablapps.puretest
package test

import cats.{MonadError, MonadState}
import cats.data.StateT
import cats.instances.either._

import SpecScalatestStateT.Program

class SpecScalatestStateT extends scalatestImpl.ScalatestFunSpec[Program, Throwable] with Spec[Program] {

  val MS = MonadState[Program, Int]
  val MPE = MonadError[Program, PureTestError[Throwable]]
  val Te = StateTester[Program, Int, PureTestError[Throwable]]

  lazy val Tester = Te(0)
}

object SpecScalatestStateT {
  type Program[T] = StateT[Either[PureTestError[Throwable], ?], Int, T]
}