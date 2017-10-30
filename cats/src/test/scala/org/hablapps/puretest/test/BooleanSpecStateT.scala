package org.hablapps
package puretest
package test

import cats.{MonadError, MonadState}
import cats.data.StateT
import cats.instances.either._

import BooleanSpecStateT.Program
import PuretestError._

class BooleanSpecStateT extends BooleanSpec.Scalatest[Program](
  MonadState[Program, Int],
  MonadError[Program, Throwable],
  RaiseError[Program, PuretestError[Throwable]],
  StateTester[Program, Int, PuretestError[Throwable]].apply(0))

object BooleanSpecStateT {
  type Program[T] = StateT[Either[PuretestError[Throwable], ?], Int, T]
}
