package org.hablapps.puretest

import scalaz.MonadError

/**
 * Puretest errors
 */

sealed abstract class PuretestError[E](msg: String){
  override def toString = msg
}

object PuretestError extends PuretestErrorImplicits {
  def simplifyLocation(location: Location): String = {
    val fileext = raw".*/(.*)".r
    val filename = location._1.value match {
      case fileext(name) => name
      case other => other
    }
    s"($filename:${location._2.value})"
  }
}

trait PuretestErrorImplicits {
  implicit def toMonadError[P[_],E](implicit
      ME: MonadError[P,PuretestError[E]]) =
    new MonadError[P,E]{
      def point[A](a: => A) = ME.point(a)
      def bind[A,B](p: P[A])(f: A => P[B]) = ME.bind(p)(f)
      def raiseError[A](e: E) =
        ME.raiseError(ApplicationError(e))
      def handleError[A](p: P[A])(f: E => P[A]) =
        ME.handleError(p) { e2 =>
          e2 match {
            case ApplicationError(e1) => f(e1)
            case _ => ME.raiseError(e2)
          }
        }
    }
}

import PuretestError.simplifyLocation

case class ApplicationError[E](e: E) extends PuretestError[E](s"Application error: $e")

case class NotEqualTo[E,A](found: A, expected: A)(implicit location: Location)
  extends PuretestError[E](s"Value $expected expected but found value $found ${simplifyLocation(location)}")

case class NotFailed[E,A](found: A)(implicit location: Location)
  extends PuretestError[E](s"Error expected but found value $found ${simplifyLocation(location)}")

case class NotSucceeded[E](found: E)(implicit location: Location)
  extends PuretestError[E](s"Value expected but found error $found ${simplifyLocation(location)}")

case class NotError[A, E](found: A, expected: E)(implicit location: Location)
  extends PuretestError[E](s"Error $expected expected but found value $found ${simplifyLocation(location)}")

case class NotValue[A, E](found: E, expected: A)(implicit location: Location)
  extends PuretestError[E](s"Value $expected expected but found error $found ${simplifyLocation(location)}")

case class OtherError[E](found: E, expected: E)(implicit location: Location)
  extends PuretestError[E](s"Error $expected expected but found error $found ${simplifyLocation(location)}")

case class NotMatched[A,E](found: A)(implicit location: Location)
  extends PuretestError[E](s"Expected pattern doesn't match found value $found ${simplifyLocation(location)}")

case class NotMatchedFailure[E](found: E)(implicit location: Location)
  extends PuretestError[E](s"Expected pattern doesn't match found error $found ${simplifyLocation(location)}")

case class ShouldNotHappen[E]()(implicit location: Location)
  extends PuretestError[E](s"This error shouldn't ever be thrown ${simplifyLocation(location)}")