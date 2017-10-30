package org.hablapps
package puretest
package test

import scalaz._
import BooleanSpecStateT.Program

class BooleanSpecStateT extends BooleanSpec.Scalatest[Program](
  MonadState[Program, Int],
  MonadError[Program, Throwable],
  RaiseError[Program, PuretestError[Throwable]],
  StateTester[Program,Int,PuretestError[Throwable]].apply(0))

object BooleanSpecStateT {
  type Program[T] = StateT[PuretestError[Throwable] \/ ?, Int, T]
}