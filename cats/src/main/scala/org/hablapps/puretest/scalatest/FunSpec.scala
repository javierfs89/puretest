package org.hablapps
package puretest
package scalatestImpl

trait FunSpec[P[_], E] extends org.scalatest.FunSpec
  with org.scalatest.Matchers
  with puretest.FunSpec[P, E] {

  type TP[A] = TestProgram[P, E, A]
  val T: Tester[P, E]
  implicit val TesterTP: Tester[TP, PuretestError[E]] = testProgramTester(T)

  def Describe(subject: String)(test: => Unit): Unit = // scalastyle:ignore
    describe(subject)(test)

  import ProgramMatchers.syntax._

  def It[A](condition: String)(program: => TP[A]): Unit = // scalastyle:ignore
    it(condition) {
      program should runWithoutErrors
    }

  def Holds(condition: String)(program: => TP[Boolean]): Unit = // scalastyle:ignore
    it(condition) {
      program should beSatisfied
    }
}
