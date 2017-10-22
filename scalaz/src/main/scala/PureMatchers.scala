package org.hablapps.puretest

import scalaz.{Functor, Monad, MonadError}
import scalaz.syntax.monadError._

class PureMatchers[P[_], A, E](self: P[A])(implicit
  RE: RaiseError[P, PureTestError[E]],
  ME: MonadError[P, E],
  loc: Location){

  def shouldFail(p: E => Boolean,
    errorIfSuccess: A => PureTestError[E],
    errorIfFailure: E => PureTestError[E]): P[Unit] =
    (self >>= { a: A => 
      RE.raiseError[Unit](errorIfSuccess(a))
    }).handleError{
      case error =>
        if (p(error)) ().point[P]
        else RE.raiseError(errorIfFailure(error))
    }

  def shouldMatchFailure(p: E => Boolean): P[Unit] =
    shouldFail(p, NotFailed(_), NotMatchedFailure(_))

  def shouldFail: P[Unit] =
    shouldFail(_ => true, NotFailed(_), _ => ShouldNotHappen())

  def shouldFail(e: E): P[Unit] =
    shouldFail(_ == e, NotError(_, e), OtherError(_, e))

  def shouldSucceed(p: A => Boolean,
    errorIfSuccess: A => PureTestError[E],
    errorIfFailure: E => PureTestError[E]): P[A] =
    self.handleError{
      case error =>
        RE.raiseError[A](errorIfFailure(error))
    }.flatMap{
      a => if (p(a)) a.point[P]
        else RE.raiseError[A](errorIfSuccess(a))
    }

  def shouldMatch(p: A => Boolean): P[A] =
    shouldSucceed(p, NotMatched(_), NotSucceeded(_))

  def shouldBe(a: A): P[A] =
    shouldSucceed(_ == a, NotEqualTo(_, a), NotValue(_, a))

  def shouldSucceed: P[A] =
    shouldSucceed(_ => true, _ => ShouldNotHappen(), NotSucceeded(_))
}
