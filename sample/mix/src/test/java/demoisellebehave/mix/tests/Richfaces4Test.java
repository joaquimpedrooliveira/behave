package demoisellebehave.mix.tests;

import org.junit.Test;

import demoisellebehave.mix.steps.MySteps;
import br.gov.frameworkdemoiselle.behave.controller.BehaveContext;

public class Richfaces4Test {

	private BehaveContext eng = null;

	public Richfaces4Test() {
		eng = BehaveContext.getInstance();
	}

	@Test
	public void testTree() throws Throwable {
		eng.addSteps(new MySteps());
		eng.run("/stories/richfaces4/");
	}
}
