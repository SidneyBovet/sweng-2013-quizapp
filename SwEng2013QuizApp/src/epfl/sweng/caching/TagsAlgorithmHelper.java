package epfl.sweng.caching;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Class used to centralize the methods needed for the tag algorithm.
 * @author born4new
 *
 */
public abstract class TagsAlgorithmHelper {

	/**
	 * pre-condition: # of '?' in normalizedTagList == questionSetList.size()
	 * @param normalizedTagList
	 * @param questionsSetList
	 * @return
	 */
	public static Set<Long> evaluate(List<String> normalizedTagList,
			List<Set<Long>> questionsSetList) {
		while (normalizedTagList.contains(")")) {
			List<String> originalNormalizedTagList = new ArrayList<String>(
					normalizedTagList);
			int firstClosingParenthesisIndex = normalizedTagList.indexOf(")");
			int correspondingOpeningParenthesisIndex = 0;

			List<String> expressionParenthesized = new ArrayList<String>();
			normalizedTagList.remove(firstClosingParenthesisIndex);
			for (int i = firstClosingParenthesisIndex - 1; i >= 0; i--) {
				if (normalizedTagList.get(i).equals("(")) {
					normalizedTagList.remove(i);
					correspondingOpeningParenthesisIndex = i;
					break;
				} else {
					expressionParenthesized.add(normalizedTagList.get(i));
					normalizedTagList.remove(i);
				}
			}
			Collections.reverse(expressionParenthesized);
			// we now work to suppress the group between those two indexes

			List<Set<Long>> setListedParenthesized = getSubListedSet(
					originalNormalizedTagList, questionsSetList,
					correspondingOpeningParenthesisIndex,
					firstClosingParenthesisIndex);

			reduceGroup(expressionParenthesized, setListedParenthesized);

			normalizedTagList.add(correspondingOpeningParenthesisIndex, "?");
		}
		reduceGroup(normalizedTagList, questionsSetList);

		return questionsSetList.get(0);
	}

	/**
	 * 
	 * @param normalizedTagList
	 * @param questionsSetList
	 * @param start
	 * @param end
	 * @return
	 */
	private static List<Set<Long>> getSubListedSet(List<String> normalizedTagList,
			List<Set<Long>> questionsSetList, int start, int end) {
		int firstElementsCount = 0;
		int parenthesizedCount = 0;
		for (int i = 0; i < end; i++) {
			if (normalizedTagList.get(i).equals("?")) {
				if (i < start) {
					firstElementsCount++;
				} else {
					parenthesizedCount++;
				}
			}
		}

		return questionsSetList.subList(firstElementsCount, firstElementsCount
				+ parenthesizedCount);
	}

	/**
	 *  pre-condition: tagList does not contain parenthesis and is well-formed
	 * @param tagList
	 * @param questionsSetList
	 * @return
	 */
	private static Set<Long> reduceGroup(List<String> tagList,
			List<Set<Long>> questionsSetList) {
		while (tagList.contains("*")) {
			int firstANDIndex = tagList.indexOf("*");

			int leftOperandIndex = -1;
			for (int i = 0; i < firstANDIndex; i++) {
				if (tagList.get(i).equals("?")) {
					leftOperandIndex++;
				}
			}

			// compute the INTERSECTION of the two operands
			Set<Long> leftOperand = questionsSetList.get(leftOperandIndex);
			Set<Long> rightOperand = questionsSetList
					.remove(leftOperandIndex + 1);
			leftOperand.retainAll(rightOperand);

			// update the tagList
			tagList.remove(firstANDIndex);
			tagList.remove(firstANDIndex); // this will remove the right-hand
											// operand
		}

		while (tagList.contains("+")) {
			int firstANDIndex = tagList.indexOf("+");

			int leftOperandIndex = -1;
			for (int i = 0; i < firstANDIndex; i++) {
				if (tagList.get(i).equals("?")) {
					leftOperandIndex++;
				}
			}

			// compute the UNION of the two operands
			Set<Long> leftOperand = questionsSetList.get(leftOperandIndex);
			Set<Long> rightOperand = questionsSetList
					.remove(leftOperandIndex + 1);
			leftOperand.addAll(rightOperand);

			// update the tagList
			tagList.remove(firstANDIndex);
			tagList.remove(firstANDIndex); // this will remove the right-hand
											// operand
		}
		return questionsSetList.get(0);
	}
}
