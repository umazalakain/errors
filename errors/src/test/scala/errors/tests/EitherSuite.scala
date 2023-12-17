package errors.tests

import cats.Id
import errors.instances.eitherInstance
import errors.discipline.{HandleToTests, RaiseTests}
import munit.DisciplineSuite

class EitherSuite extends DisciplineSuite {
  checkAll("Either[String,Int]", RaiseTests[Either[String, _], String].raise[Int])
  checkAll("Either[String,Int]", HandleToTests[Either[String, _], Id, String].raise[Int])
}
