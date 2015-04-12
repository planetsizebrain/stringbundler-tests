package be.aca.liferay;

import com.liferay.portal.kernel.util.StringBundler;
import org.apache.commons.lang3.RandomStringUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
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
@Warmup(iterations = 25)
@Measurement(iterations = 100)
@Fork(1)
@State(Scope.Benchmark)
public class StringBundlerWithBlackholesBenchmark {

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

	@Benchmark
	public void testStringConcat(Blackhole bh) {
		String s = "";
		for (int i = 0; i < length; i++) {
			s += randomStrings.get(i);
		}

		bh.consume(s);
	}

	@Benchmark
	public void testStringBuilderWithInit(Blackhole bh) {
		int builderLength = 0;
		for (int i = 0; i < length; i++) {
			builderLength += randomStrings.get(i).length();
		}

		StringBuffer sb = new StringBuffer(builderLength);

		for (int i = 0; i < length; i++) {
			sb.append(randomStrings.get(i));
		}

		bh.consume(sb.toString());
	}

	@Benchmark
	public void testStringBuilderWithoutInit(Blackhole bh) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < length; i++) {
			sb.append(randomStrings.get(i));
		}

		bh.consume(sb.toString());
	}

	@Benchmark
	public void testStringBufferWithInit(Blackhole bh) {
		int bufferLength = 0;
		for (int i = 0; i < length; i++) {
			bufferLength += randomStrings.get(i).length();
		}

		StringBuffer sb = new StringBuffer(bufferLength);

		for (int i = 0; i < length; i++) {
			sb.append(randomStrings.get(i));
		}

		bh.consume(sb.toString());
	}

	@Benchmark
	public void testStringBufferWithoutInit(Blackhole bh) {
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < length; i++) {
			sb.append(randomStrings.get(i));
		}

		bh.consume(sb.toString());
	}

	@Benchmark
	public void testStringBundlerWithInit(Blackhole bh) {
		StringBundler sb = new StringBundler(length);

		for (int i = 0; i < length; i++) {
			sb.append(randomStrings.get(i));
		}

		bh.consume(sb.toString());
	}

	@Benchmark
	public void testStringBundlerWithoutInit(Blackhole bh) {
		StringBundler sb = new StringBundler();

		for (int i = 0; i < length; i++) {
			sb.append(randomStrings.get(i));
		}

		bh.consume(sb.toString());
	}

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
				.include(".*" + StringBundlerWithBlackholesBenchmark.class.getSimpleName() + ".*")
				.jvmArgs("-prof perfnorm")
				.build();

		new Runner(opt).run();
	}

}