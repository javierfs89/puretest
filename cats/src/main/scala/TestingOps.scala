package org.hablapps.puretest

import cats.{ApplicativeError, Functor}
import cats.syntax.applicativeError._
import cats.syntax.functor._

/**
 * Utilities for test specifications
 */
trait TestingOps {

  // TODO(jfuentes): Add test description here instead of scalatest

  // TODO(jfuentes): Add testing errors
  // sealed abstract class PuretestError[AppE]
  // case class IsErrorError[AppE](msg: String) extends PuretestError[AppE]
  // case class AppError[AppE](appE: AppE) extends PuretestError[AppE]

  implicit class TestingOps[P[_], A](self: P[A]){

    def isError[E: ApplicativeError[P, ?]](e: E): P[Boolean] =
      (self as false) handleError { _ == e }

    // def isError2[E](e: E)(implicit E: ApplicativeError[P, Either[E, PuretestError]]): P[Boolean] =
    //   self handleErrorWith {
    //     case `e` => true.point[P]
    //     case pe: PuretestError =>
    //     case _ => IsErrorError("foo").left.raiseError
    //   }

    // def isError3[E](e: E)(implicit E: ApplicativeError[P, PuretestError[E]]): P[Boolean] =
    //   self handleErrorWith {
    //     case AppError(`e`) => true.point[P]
    //     case _ => self as false
    //   }

    def inspect[E: ApplicativeError[P, ?]]: P[Either[E, A]] =
      (self map (Right(_): Either[E, A])).handleError(Left.apply[E, A])

    def isEqual(a: A)(implicit F: Functor[P]): P[Boolean] =
      F.map(self)(_ == a)

  }
}

