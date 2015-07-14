package com.googlecode.loveemu.PetiteMM;

import java.util.List;

public class MidiTimeSignature {

	/**
	 * Numerator of time signature.
	 */
	private int numerator = 4;

	/**
	 * Denominator of time signature. (expressed in log2(realValue))
	 */
	private int denominator = 2;

	/**
	 * Location of time signature in measure.
	 */
	private int measure;

	/**
	 * Constructs new time signature.
	 */
	public MidiTimeSignature() {
	}

	/**
	 * Constructs new time signature.
	 * @param numerator Numerator of time signature.
	 * @param denominator Denominator of time signature. (expressed in log2(realValue))
	 */
	public MidiTimeSignature(int numerator, int denominator) {
		this(numerator, denominator, 0);
	}

	/**
	 * Constructs new time signature.
	 * @param numerator Numerator of time signature.
	 * @param denominator Denominator of time signature. (expressed in log2(realValue))
	 * @param measure Location of time signature in measure.
	 */
	public MidiTimeSignature(int numerator, int denominator, int measure) {
		setNumerator(numerator);
		setDenominator(denominator);
		setMeasure(measure);
	}

	/**
	 * Get numerator of time signature.
	 * @return Numerator of time signature.
	 */
	public int getNumerator() {
		return numerator;
	}

	/**
	 * Set numerator of time signature.
	 * @param numerator Numerator of time signature.
	 */
	public void setNumerator(int numerator) {
		if (numerator <= 0)
			throw new IllegalArgumentException("Numerator must be greater than 0.");
		this.numerator = numerator;
	}

	/**
	 * Get denominator of time signature.
	 * @return Denominator of time signature. (expressed in log2(realValue))
	 */
	public int getDenominator() {
		return denominator;
	}

	/**
	 * Set denominator of time signature.
	 * @param denominator Denominator of time signature. (expressed in log2(realValue))
	 */
	public void setDenominator(int denominator) {
		if (denominator < 0)
			throw new IllegalArgumentException("Denominator must be a positive number.");
		this.denominator = denominator;
	}

	/**
	 * Get location of time signature.
	 * @return Location of time signature in measure.
	 */
	public int getMeasure() {
		return measure;
	}

	/**
	 * Set location of time signature.
	 * @param measure Location of time signature in measure.
	 */
	public void setMeasure(int measure) {
		this.measure = measure;
	}

	/**
	 * Get length of a measure.
	 * @param ppqn Ticks per quarter note.
	 * @return Length of a measure in ticks.
	 */
	public int getLength(int ppqn) {
		if (ppqn <= 0)
			throw new IllegalArgumentException("PPQN must be greater than 0.");
		return (ppqn * 4 * numerator) >> denominator;
	}

	/**
	 * Get measure number by tick.
	 * @param tick Tick count to be converted.
	 * @param timeSignatures List of time signatures (must be sorted).
	 * @param ppqn Ticks per quarter note.
	 * @return Measure number (starts from 0).
	 */
	static public int getMeasureByTick(long tick, List<MidiTimeSignature> timeSignatures, int ppqn)
	{
		if (tick < 0)
			throw new IllegalArgumentException("Tick must be a positive number.");
		if (ppqn <= 0)
			throw new IllegalArgumentException("PPQN must be greater than 0.");
		if (timeSignatures.isEmpty())
			throw new IllegalArgumentException("No time signature information.");
		if (timeSignatures.get(0).getMeasure() != 0)
			throw new IllegalArgumentException("First time signature is not located at the first measure.");

		long baseTick = 0;
		int baseMeasure = 0;
		int timeSigIndex;
		for (timeSigIndex = 0; timeSigIndex < timeSignatures.size() - 1; timeSigIndex++)
		{
			MidiTimeSignature timeSignature = timeSignatures.get(timeSigIndex);
			MidiTimeSignature nextTimeSignature = timeSignatures.get(timeSigIndex + 1);
			int numberOfMeasures = nextTimeSignature.getMeasure() - timeSignature.getMeasure();
			long interval = timeSignature.getLength(ppqn) * numberOfMeasures;

			if (tick < baseTick + interval)
				break;

			baseTick += interval;
			baseMeasure = nextTimeSignature.getMeasure();
		}

		MidiTimeSignature timeSignature = timeSignatures.get(timeSigIndex);
		return baseMeasure + (int)((tick - baseTick) / timeSignature.getLength(ppqn));
	}

	/**
	 * Get measure:tick string.
	 * @param tick Tick count to be converted.
	 * @param timeSignatures List of time signatures (must be sorted).
	 * @param ppqn Ticks per quarter note.
	 * @return Measure:Tick string.
	 */
	static public String getMeasureTickString(long tick, List<MidiTimeSignature> timeSignatures, int ppqn)
	{
		if (tick < 0)
			throw new IllegalArgumentException("Tick must be a positive number.");
		if (ppqn <= 0)
			throw new IllegalArgumentException("PPQN must be greater than 0.");
		if (timeSignatures.isEmpty())
			throw new IllegalArgumentException("No time signature information.");
		if (timeSignatures.get(0).getMeasure() != 0)
			throw new IllegalArgumentException("First time signature is not located at the first measure.");

		long baseTick = 0;
		int baseMeasure = 0;
		int timeSigIndex;
		for (timeSigIndex = 0; timeSigIndex < timeSignatures.size() - 1; timeSigIndex++)
		{
			MidiTimeSignature timeSignature = timeSignatures.get(timeSigIndex);
			MidiTimeSignature nextTimeSignature = timeSignatures.get(timeSigIndex + 1);
			int numberOfMeasures = nextTimeSignature.getMeasure() - timeSignature.getMeasure();
			long interval = timeSignature.getLength(ppqn) * numberOfMeasures;

			if (tick < baseTick + interval)
				break;

			baseTick += interval;
			baseMeasure = nextTimeSignature.getMeasure();
		}

		MidiTimeSignature timeSignature = timeSignatures.get(timeSigIndex);
		int measure = baseMeasure + (int)((tick - baseTick) / timeSignature.getLength(ppqn));
		int tickInMeasure = (int)((tick - baseTick) % timeSignature.getLength(ppqn));
		return String.format("%d:%04d", measure, tickInMeasure);
	}

	@Override
	public String toString() {
		return String.format("MidiTimeSignature [numerator=%d, denominator=%d measure=%d]", numerator, denominator, measure);
	}
}
