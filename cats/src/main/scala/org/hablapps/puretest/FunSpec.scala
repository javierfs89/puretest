package org.hablapps.puretest

trait FunSpec[P[_], E] extends PuretestErrorImplicits with PureMatchersTC.Syntax {

  type TP[_]
  implicit val PM: PureMatchersTC[TP, E]

  // scalastyle:off
  def Describe(subject: String)(test: => Unit): Unit

  def It[A](condition: String)(program: => TP[A]): Unit

  def Holds(condition: String)(program: => TP[Boolean]): Unit
  // scalastyle:on

}
