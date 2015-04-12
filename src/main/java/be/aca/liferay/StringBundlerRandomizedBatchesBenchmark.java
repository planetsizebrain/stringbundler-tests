package be.aca.liferay;

import com.liferay.portal.kernel.util.StringBundler;
import org.apache.commons.lang3.RandomStringUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, batchSize = 10000)
@Measurement(iterations = 5, batchSize = 10000)
@Fork(1)
@State(Scope.Benchmark)
public class StringBundlerRandomizedBatchesBenchmark {

	private static final int NBR_OF_STRINGS = 100;
	private static final int MIN_STRING_LENGTH = 10;
	private static final int MAX_STRING_LENGTH = 100;
	private Random random = new Random();
	private List<String> randomStrings = new ArrayList<String>(NBR_OF_STRINGS);

	@Param({"5", "10", "20", "30", "40", "50"})
	public int length;

	@Setup
	public void init() {
		for (int i = 0; i < NBR_OF_STRINGS; i++) {
			int randomLength = random.nextInt((MAX_STRING_LENGTH - MIN_STRING_LENGTH) + 1) + MIN_STRING_LENGTH;
			randomStrings.add(RandomStringUtils.randomAlphanumeric(randomLength));
		}
	}

	private List<String> getRandomStrings() {
		List<String> strings = new ArrayList<>(length);
		for (int i = 0; i < length; i++) {
			int randomIndex = random.nextInt(NBR_OF_STRINGS);
			strings.add(randomStrings.get(randomIndex));
		}

		return strings;
	}

	@Benchmark
	public String testStringConcat() {
		List<String> strings = getRandomStrings();
		String s = "";

		for (int i = 0; i < length; i++) {
			s += strings.get(i);
		}
		return s;
	}

	@Benchmark
	public String testStringBuilderWithInit() {
		List<String> strings = getRandomStrings();

		int builderLength = 0;
		for (int i = 0; i < length; i++) {
			builderLength += strings.get(i).length();
		}

		StringBuffer sb = new StringBuffer(builderLength);

		for (int i = 0; i < length; i++) {
			sb.append(strings.get(i));
		}

		return sb.toString();
	}

	@Benchmark
	public String testStringBuilderWithoutInit() {
		List<String> strings = getRandomStrings();
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < length; i++) {
			sb.append(strings.get(i));
		}

		return sb.toString();
	}

	@Benchmark
	public String testStringBufferWithInit() {
		List<String> strings = getRandomStrings();
		int bufferLength = 0;
		for (int i = 0; i < length; i++) {
			bufferLength += strings.get(i).length();
		}

		StringBuffer sb = new StringBuffer(bufferLength);

		for (int i = 0; i < length; i++) {
			sb.append(strings.get(i));
		}

		return sb.toString();
	}

	@Benchmark
	public String testStringBufferWithoutInit() {
		List<String> strings = getRandomStrings();
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < length; i++) {
			sb.append(strings.get(i));
		}

		return sb.toString();
	}

	@Benchmark
	public String testStringBundlerWithInit() {
		List<String> strings = getRandomStrings();
		StringBundler sb = new StringBundler(length);

		for (int i = 0; i < length; i++) {
			sb.append(strings.get(i));
		}

		return sb.toString();
	}

	@Benchmark
	public String testStringBundlerWithoutInit() {
		List<String> strings = getRandomStrings();
		StringBundler sb = new StringBundler();

		for (int i = 0; i < length; i++) {
			sb.append(strings.get(i));
		}

		return sb.toString();
	}

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
				.include(".*" + StringBundlerRandomizedBatchesBenchmark.class.getSimpleName() + ".*")
				.addProfiler(GCProfiler.class)
//				.jvmArgs("-prof gc")
				.build();

		new Runner(opt).run();
	}

}