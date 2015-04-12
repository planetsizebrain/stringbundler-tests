package be.aca.liferay;

import com.liferay.portal.kernel.util.StringBundler;
import org.apache.commons.lang3.RandomStringUtils;
import org.openjdk.jmh.annotations.*;
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
//@Warmup(iterations = 5, batchSize = 10000)
//@Measurement(iterations = 5, batchSize = 10000)
@Warmup(iterations = 25)
@Measurement(iterations = 100)
@Fork(1)
@State(Scope.Benchmark)
public class StringBundlerBenchmark {

	private static final int NBR_OF_STRINGS = 100;
	private static final int STRING_LENGTH = 20;
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
//			randomStrings.add(RandomStringUtils.randomAlphanumeric(STRING_LENGTH));
			randomStrings.add(RandomStringUtils.randomAlphanumeric(randomLength));
		}
	}

	@Benchmark
	public String testStringConcat() {
		String s = "";
		for (int i = 0; i < length; i++) {
			s += randomStrings.get(i);
		}
		return s;
	}

	@Benchmark
	public String testStringBuilderWithInit() {
		int builderLength = 0;
		for (int i = 0; i < length; i++) {
			builderLength += randomStrings.get(i).length();
		}

//		StringBuilder sb = new StringBuilder(length * STRING_LENGTH);
		StringBuffer sb = new StringBuffer(builderLength);

		for (int i = 0; i < length; i++) {
			sb.append(randomStrings.get(i));
		}

		return sb.toString();
	}

	@Benchmark
	public String testStringBuilderWithoutInit() {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < length; i++) {
			sb.append(randomStrings.get(i));
		}

		return sb.toString();
	}

	@Benchmark
	public String testStringBufferWithInit() {
		int bufferLength = 0;
		for (int i = 0; i < length; i++) {
			bufferLength += randomStrings.get(i).length();
		}

//		StringBuffer sb = new StringBuffer(length * STRING_LENGTH);
		StringBuffer sb = new StringBuffer(bufferLength);

		for (int i = 0; i < length; i++) {
			sb.append(randomStrings.get(i));
		}

		return sb.toString();
	}

	@Benchmark
	public String testStringBufferWithoutInit() {
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < length; i++) {
			sb.append(randomStrings.get(i));
		}

		return sb.toString();
	}

	@Benchmark
	public String testStringBundlerWithInit() {
		StringBundler sb = new StringBundler(length);

		for (int i = 0; i < length; i++) {
			sb.append(randomStrings.get(i));
		}

		return sb.toString();
	}

	@Benchmark
	public String testStringBundlerWithoutInit() {
		StringBundler sb = new StringBundler();

		for (int i = 0; i < length; i++) {
			sb.append(randomStrings.get(i));
		}

		return sb.toString();
	}

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
				.include(".*" + StringBundlerBenchmark.class.getSimpleName() + ".*")
//				.jvmArgs("-prof gc")
				.build();

		new Runner(opt).run();
	}

}