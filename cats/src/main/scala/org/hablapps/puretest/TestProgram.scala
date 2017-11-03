package org.hablapps.puretest

import cats.{Monad, MonadError, MonadState}

trait TestProgramModuleLower {

  implicit def testProgramMonadState[P[_], S, E](implicit MS2: MonadState[P, S]): MonadState[TestProgram[P, E, ?], S] = {
    type TP[A] = TestProgram[P, E, A]
    val M = testProgramMonad[P, E]
    new MonadState[TP, S] {
      def pure[A](x: A): TP[A] = M.pure(x)
      def flatMap[A, B](fa: TP[A])(f: A => TP[B]): TP[B] = M.flatMap(fa)(f)
      def tailRecM[A, B](a: A)(f: A => TP[Either[A, B]]): TP[B] = M.tailRecM(a)(f)

      def get: TP[S] = Right(MS2.get)
      def set(s: S): TP[Unit] = Right(MS2.set(s))
    }
  }

}

trait TestProgramModule extends TestProgramModuleLower {

  type TestProgram[P[_], E, A] = Either[PuretestError[E], P[A]]

  implicit def testProgramMonad[P[_], E](implicit M: Monad[P]): Monad[TestProgram[P, E, ?]] = {
    type TP[A] = TestProgram[P, E, A]
    new Monad[TP] {
      def pure[A](x: A): TP[A] = Right(M.pure(x))
      def flatMap[A, B](fa: TP[A])(f: A => TP[B]): TP[B] =
        fa map { pa =>
          M.flatMap(pa) { a =>
            f(a) match {
              case Left(l) =>
                println(s"123 ~> $l")
                throw new RuntimeException(l.toString)
              case Right(r) => r
            }
          }
        }
      def tailRecM[A, B](a: A)(f: A => TP[Either[A, B]]): TP[B] =
        f(a) map { pei =>
          M.tailRecM(a)(_ => pei)
        }
    }
  }

  implicit def testProgramMonadError[P[_], E](implicit ME2: MonadError[P, E]): MonadError[TestProgram[P, E, ?], E] = {
    type TP[A] = TestProgram[P, E, A]
    val M = testProgramMonad[P, E]
    new MonadError[TP, E] {
      def pure[A](x: A): TP[A] = M.pure(x)
      def flatMap[A, B](fa: TP[A])(f: A => TP[B]): TP[B] = M.flatMap(fa)(f)
      def tailRecM[A, B](a: A)(f: A => TP[Either[A, B]]): TP[B] = M.tailRecM(a)(f)

      def handleErrorWith[A](fa: TP[A])(f: E => TP[A]): TP[A] =
        fa map { st =>
          ME2.handleErrorWith(st) { e =>
            f(e) match {
              case Left(ApplicationError(e)) => println(s"Application Error: $e") ; ME2.raiseError(e)
              case Left(pe) => throw new RuntimeException(pe.toString)
              case Right(r) => r
            }
          }
        }

      def raiseError[A](e: E): TP[A] =
        Right(ME2.raiseError(e))
    }
  }

  implicit def testProgramHandleError[P[_], E](implicit
      HE: HandleError[P, E],
      RE: RaiseError[P, E]): HandleError[TestProgram[P, E, ?], E] = {
    type TP[A] = TestProgram[P, E, A]
    new HandleError[TP, E] {
      def handleError[A](pa: TP[A])(f: E => TP[A]): TP[A] =
        pa map { st =>
          HE.handleError(st) { e =>
            f(e) match {
              case Left(ApplicationError(e)) => println(s"Application Error: $e") ; RE.raiseError(e)
              case Left(pe) => throw new RuntimeException(pe.toString)
              case Right(r) => r
            }
          }
        }
    }
  }

  implicit def testProgramRaiseError[P[_], E]: RaiseError[TestProgram[P, E, ?], PuretestError[E]] = {
    type TP[A] = TestProgram[P, E, A]
    new RaiseError[TP, PuretestError[E]] {
      def raiseError[A](e: PuretestError[E]): TP[A] = Left(e)
    }
  }

  implicit def testProgramPureMatchers[P[_], E](implicit
      M2: Monad[P],
      HE2: HandleError[P, E],
      RE2: RaiseError[P, E]): PureMatchersTC[TestProgram[P, E, ?], E] = {
    type TP[A] = TestProgram[P, E, A]
    new PureMatchersTC[TP, E] {
      val M = Monad[TP]
      val HE = HandleError[TP, E]
      val RE = RaiseError[TP, PuretestError[E]]
    }
  }

  implicit def testProgramPureMatchers2[P[_], E](implicit // TODO(jfuentes): Pretty sure here there is code duplication
      PM: PureMatchersTC[TestProgram[P, PuretestError[E], ?], PuretestError[E]])
        : PureMatchersTC[TestProgram[P, PuretestError[E], ?], E] = {
    type TP[A] = TestProgram[P, PuretestError[E], A]
    new PureMatchersTC[TP, E] {
      val M = PM.M
      val HE = new HandleError[TP, E] {
        def handleError[A](p: TP[A])(f: E => TP[A]): TP[A] = {
          println("00000000")
          PM.HE.handleError(p) {
            case ApplicationError(e) => println("AAAAA") ; f(e)
            case other => println("BBBBB") ; RE.raiseError(other)
          }
        }
      }
      val RE = new RaiseError[TP, PuretestError[E]] {
        def raiseError[A](e: PuretestError[E]): TP[A] = {
          println(s"1111111111 ~> $e")
          Left(ApplicationError(e))
        }
      }
    }
  }

  implicit def testProgramTester[P[_], E](implicit T: Tester[P, E]): Tester[TestProgram[P, E, ?], PuretestError[E]] = {
    type TP[A] = TestProgram[P, E, A]
    new Tester[TP, PuretestError[E]] {
      def apply[A](fa: TP[A]): Either[PuretestError[E], A] =
        fa flatMap { ipa =>
          T(ipa).left.map(ApplicationError(_))
        }
    }
  }

}
