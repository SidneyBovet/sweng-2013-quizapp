package epfl.sweng.test.cache;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.annotation.SuppressLint;
import epfl.sweng.caching.TagsAlgorithmHelper;

import junit.framework.TestCase;

@SuppressLint("UseValueOf")
public class TagsAlgorithmHelperTest extends TestCase {
	public void testSingleTag() {
		Set<Long> id1 = new HashSet<Long>();
		id1.add(new Long(1));
		id1.add(new Long(2));
		List<Set<Long>> idList = new ArrayList<Set<Long>>();
		idList.add(id1);
		
		List<String> tags = new ArrayList<String>();
		tags.add("?");
		
		Set<Long> evaluated = TagsAlgorithmHelper.evaluate(tags, idList);
		assertEquals(id1, evaluated);
	}

	public void testT1andT2() {
		Set<Long> id1 = new HashSet<Long>();
		id1.add(new Long(1));
		id1.add(new Long(2));
		Set<Long> id2 = new HashSet<Long>();
		id2.add(new Long(1));
		id2.add(new Long(3));
		List<Set<Long>> idList = new ArrayList<Set<Long>>();
		idList.add(id1);
		idList.add(id2);
		
		List<String> tags = new ArrayList<String>();
		tags.add("?");
		tags.add("*");
		tags.add("?");
		
		Set<Long> idExpected = new HashSet<Long>();
		idExpected.add(new Long(1));
		
		Set<Long> evaluated = TagsAlgorithmHelper.evaluate(tags, idList);
		assertEquals(idExpected, evaluated);
	}


	public void testT1orT2() {
		Set<Long> id1 = new HashSet<Long>();
		id1.add(new Long(1));
		id1.add(new Long(2));
		Set<Long> id2 = new HashSet<Long>();
		id2.add(new Long(1));
		id2.add(new Long(3));
		List<Set<Long>> idList = new ArrayList<Set<Long>>();
		idList.add(id1);
		idList.add(id2);
		
		List<String> tags = new ArrayList<String>();
		tags.add("?");
		tags.add("+");
		tags.add("?");
		
		Set<Long> idExpected = new HashSet<Long>();
		idExpected.add(new Long(1));
		idExpected.add(new Long(2));
		idExpected.add(new Long(3));
		
		Set<Long> evaluated = TagsAlgorithmHelper.evaluate(tags, idList);
		assertEquals(idExpected, evaluated);
	}
}
