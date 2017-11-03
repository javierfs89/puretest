package org.hablapps.puretest
package test

import cats.syntax.all._

import WorkingProgram.Error

trait WorkingSpec[P[_]] extends FunSpec[P, Error] {
  val S: WorkingProgram[TP]
  import S._

  Describe("ShouldSucceed"){

    It("should work with working programs"){
      workingProgram.shouldSucceed >>
      workingProgramWithHandledError.shouldSucceed
    }

    It("is redundant with working programs"){
      workingProgram >>
      workingProgramWithHandledError
    }

    It("should work when the exact value is checked"){
      workingProgram shouldBe 1
    }

    It("should work if patterns are matched"){
      (workingProgramWithHandledError shouldMatch { _ == 2 }) >>
      MS.set(2) >>
      MS.get shouldMatch { _ == 2 }
    }

    It("should work if patterns are matched (for-comprehension)") {
      for {
        2 <- workingProgramWithHandledError
        _ <- MS.set(2)
        2 <- MS.get
      } yield ()
    }
  }

  Describe("ShouldFail"){

    It("should work with failing programs"){
      failingProgram shouldFail
    }

    It("should work when the exact error is checked"){
      failingProgram shouldFailWith Error(0)
    }

    It("should work if error pattern is matched"){
      failingProgram shouldMatchFailure[Error] (_ == Error(0))
    }
  }

}

object WorkingSpec {
  import cats.{MonadError, MonadState}

  class Scalatest[P[_]](
    S2: WorkingProgram[P],
    val T: Tester[P, Error])(implicit
    ME2: MonadError[P, Error],
    MS2: MonadState[P, Int],
    val PM: PureMatchersTC[TestProgram[P, Error, ?], Error])
  extends scalatestImpl.FunSpec[P, Error]
  with WorkingSpec[P] {
    val S = new WorkingProgram[TP] {
      val ME = MonadError[TP, Error]
      val MS = MonadState[TP, Int]
    }
  }

}
