import org.scalatest.flatspec.AnyFlatSpec
import prototype.the.seer.examples.ExampleMain

class TestRun extends AnyFlatSpec {
  "TheSeer" should "work" in {
    ExampleMain.main(Array.empty)
  }
}
