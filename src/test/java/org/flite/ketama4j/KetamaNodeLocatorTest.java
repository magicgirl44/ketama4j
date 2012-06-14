package org.flite.ketama4j;

import org.apache.commons.lang.*;
import org.testng.annotations.*;

import java.util.*;

import static org.testng.AssertJUnit.*;
/**
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public class KetamaNodeLocatorTest {

    @Test
    public void testNodeArrays() {
        final int maxSize = 10;
        final List<String> nodes = generateRandomStrings(maxSize);
        final long start1 = System.currentTimeMillis();
        final KetamaNodeLocator locator = new KetamaNodeLocator(nodes);
        System.out.println("Generated in [ms]: " + (System.currentTimeMillis() - start1));

        final List<String> keys = generateRandomStrings(5);

        for (final String key : keys) {
            System.out.println("Working key: " + key);
            final String single = locator.getPrimary(key);
            final List<String> subset = locator.getPriorityList(key, 5);
            final List<String> superset = locator.getPriorityList(key, 15);

            assertTrue(superset.size() <= maxSize);
            assertTrue(subset.size() <= 5);
            assertEquals(single, subset.get(0));
            assertEquals(single, superset.get(0));

            for (int jx = 0; jx < 5; jx++) {
                assertEquals(subset.get(jx), superset.get(jx));
            }

            assertTrue(superset.containsAll(nodes));
        }
    }

    @Test
    public void testDistribution() {
        final int maxSize = 10;
        final List<String> nodes = generateRandomStrings(maxSize);
        final long start1 = System.currentTimeMillis();
        final KetamaNodeLocator locator = new KetamaNodeLocator(nodes);
        System.out.println("Generated in [ms]: " + (System.currentTimeMillis() - start1));

        final int[] counts = new int[maxSize];
        for (int ix = 0; ix < maxSize; ix++) { counts[ix] = 0; }

        final List<String> keys = generateRandomStrings(10000);

        for (final String key : keys) {
            final String primary = locator.getPrimary(key);
            counts[nodes.indexOf(primary)] += 1;
        }

        // TODO: How to test this? What vaiance allowed?
        for (int ix = 0; ix < maxSize; ix++) {
            System.out.println(counts[ix] + ": " + nodes.get(ix));
        }
    }

    @Test
    public void testWeightedDistribution() {
        final int maxSize = 5;
        final List<String> nodes = generateRandomStrings(maxSize);
        final List<String> weightedNodes = new ArrayList<String>(nodes);
        weightedNodes.add(nodes.get(3)); // 20%
        for (int ix = 0; ix < 4; ix++) { weightedNodes.add(nodes.get(4)); } // 50%
        final long start1 = System.currentTimeMillis();
        final KetamaNodeLocator locator = new KetamaNodeLocator(weightedNodes);
        System.out.println("Generated in [ms]: " + (System.currentTimeMillis() - start1));

        final int[] counts = new int[maxSize];
        for (int ix = 0; ix < maxSize; ix++) { counts[ix] = 0; }

        final List<String> keys = generateRandomStrings(10000);

        for (final String key : keys) {
            final String primary = locator.getPrimary(key);
            counts[nodes.indexOf(primary)] += 1;
        }

        // TODO: How to test this? What vaiance allowed?
        for (int ix = 0; ix < maxSize; ix++) {
            System.out.println(counts[ix] + ": " + nodes.get(ix));
        }
    }

    public static List<String> generateRandomStrings(final int size) {
        final List<String> results = new ArrayList<String>(size);
        for (int ix = 0; ix < size; ix++) {
            results.add(
                    new StringBuilder(RandomStringUtils.randomAlphanumeric(5))
                            .append(RandomStringUtils.randomAlphabetic(ix % 5))
                            .append(ix)
                            .toString()
            );
        }
        return results;
    }
}
