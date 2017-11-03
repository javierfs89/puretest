package org.hablapps.puretest
package test

import cats.{MonadError, MonadState}
import cats.data.StateT
import cats.instances.either._

import WorkingProgram.Error, ShouldSpecStateT.{Inner, Program}
import PuretestError._

class ShouldSpecStateT
    extends scalatestImpl.FunSpec[Program, PuretestError[Error]]
    with ShouldSpec[Program] {

  // type TP[T] = Either[PuretestError[PuretestError[Error]], Either[PuretestError[Error], StateT[Either[Error, ?], Int, T]]]
  // type TP[T] = TestProgram[
  //                TestProgram[
  //                  StateT[Either[Error, ?], Int, ?],
  //                  Error,
  //                  ?
  //                ],
  //                PuretestError[Error],
  //                T
  //              ]

  implicit val PM: PureMatchersTC[TP, PuretestError[Error]] = testProgramPureMatchers[Program, PuretestError[Error]](
    implicitly,
    HandleError.handleErrorEither[PuretestError[Error]].asInstanceOf[HandleError[Program, PuretestError[Error]]],
    implicitly)
  val InnerPM: PureMatchersTC[TP, Error] = PureMatchersTC[TP, Error]

  lazy val T = {
    implicit val inner = StateTester[Inner, Int, Error].apply(0)
    Tester[Program, PuretestError[Error]]
  }

  val S = new WorkingProgram[TP] {
    // private val S2 = WorkingProgram[Inner]
    // val ME3: MonadError[Inner, Error] = S2.ME
    private val S3 = WorkingProgram[Program]
    val ME2: MonadError[Program, Error] = S3.ME
    // val ME: MonadError[TP, Error] = testProgramMonadError[Program, Error](ME2)
    // val ME: MonadError[TP, Error] = ().asInstanceOf[MonadError[TP, Error]] // ??? // testProgramMonadError[Program, Error](ME2)
    val ME: MonadError[TP, Error] = testProgramMonadError[Program, Error](ME2).asInstanceOf[MonadError[TP, Error]]
    val MS = MonadState[TP, Int]
  }

}


object ShouldSpecStateT {
  // type Program[T] = StateT[Either[PuretestError[PuretestError[Error]], ?], Int, T]
  type Inner[T] = StateT[Either[Error, ?], Int, T]
  type Program[T] = Either[PuretestError[Error], StateT[Either[Error, ?], Int, T]]
}
