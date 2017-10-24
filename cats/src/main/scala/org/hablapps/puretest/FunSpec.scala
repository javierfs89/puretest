package org.hablapps.puretest

import cats.MonadError

trait FunSpec[P[_], E] extends PureTestErrorImplicits {

  val MPE: MonadError[P, PureTestError[E]]
  implicit lazy val ME: MonadError[P, E] = toMonadError(MPE)
  implicit lazy val HE: HandleError[P, E] = HandleError.fromMonadError
  implicit lazy val RPE: RaiseError[P, PureTestError[E]] = RaiseError.fromMonadError(MPE)

  // scalastyle:off
  def Describe(subject: String)(test: => Unit): Unit

  def It[A](condition: String)(program: => P[A]): Unit

  def Holds(condition: String)(program: => P[Boolean]): Unit
  // scalastyle:on

}
