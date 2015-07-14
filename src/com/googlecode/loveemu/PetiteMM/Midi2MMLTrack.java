package com.googlecode.loveemu.PetiteMM;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

class Midi2MMLTrack {

	/**
	 * Output MML text.
	 */
	private List<MMLEvent> mmlEventList = new LinkedList<MMLEvent>();

	/**
	 * Current position of conversion in tick.
	 */
	private long tick = 0L;

	/**
	 * Measure number of current position.
	 */
	private int measure = 0;

	/**
	 * Current octave.
	 */
	private int octave = 4;

	/**
	 * true if the first note is not processed yet.
	 */
	private boolean firstNote = true;

	/**
	 * Current note number. (for MIDI2MML conversion)
	 */
	private int noteNumber = MMLNoteConverter.KEY_REST;

	/**
	 * Current MIDI event index.
	 */
	private int midiEventIndex = 0;

	/**
	 * True if conversion is already finished.
	 */
	private boolean finished = false;

	/**
	 * True if use triplet syntax for some simple triplets.
	 */
	private boolean useTriplet = false;

	/**
	 * MML symbol set.
	 */
	private MMLSymbol mmlSymbol;

	/**
	 * Construct new MML track conversion object.
	 * @param mmlSymbol MML symbol set.
	 */
	Midi2MMLTrack(MMLSymbol mmlSymbol)
	{
		this.mmlSymbol = mmlSymbol;
	}

	/**
	 * Get current position of conversion in tick.
	 * @return Current time in tick.
	 */
	public long getTick() {
		return tick;
	}

	/**
	 * Set current position of conversion in tick.
	 * @param tick Current time in tick.
	 */
	public void setTick(long tick) {
		this.tick = tick;
	}

	/**
	 * Get measure number of current position.
	 * @return Measure number of current position.
	 */
	public int getMeasure() {
		return measure;
	}

	/**
	 * Set measure number of current position.
	 * @param measure Measure number of current position.
	 */
	public void setMeasure(int measure) {
		this.measure = measure;
	}

	/**
	 * Get current octave.
	 * @return Current octave.
	 */
	public int getOctave() {
		return octave;
	}

	/**
	 * Set current octave.
	 * @param octave Current octave.
	 */
	public void setOctave(int octave) {
		this.octave = octave;
	}

	/**
	 * Increase current octave.
	 */
	public void increaseOctave() {
		this.octave++;
	}

	/**
	 * Decrease current octave.
	 */
	public void decreaseOctave() {
		this.octave--;
	}

	/**
	 * Get current note number.
	 * @return Current note number, MMLNoteConverter.KEY_REST if rest.
	 */
	public int getNoteNumber() {
		return noteNumber;
	}

	/**
	 * Set current note number.
	 * @param noteNumber Current note number, MMLNoteConverter.KEY_REST if rest.
	 */
	public void setNoteNumber(int noteNumber) {
		this.noteNumber = noteNumber;
	}

	/**
	 * Get current MIDI event index.
	 * @return Current MIDI event index.
	 */
	public int getMidiEventIndex() {
		return midiEventIndex;
	}

	/**
	 * Set current MIDI event index.
	 * @param midiEventIndex Current MIDI event index.
	 */
	public void setMidiEventIndex(int midiEventIndex) {
		this.midiEventIndex = midiEventIndex;
	}

	/**
	 * Get if triplet is preferred.
	 * @return true if triplet is preferred.
	 */
	public boolean getUseTriplet() {
		return useTriplet;
	}

	/**
	 * Set if triplet is preferred.
	 * @param useTriplet true if triplet is preferred.
	 */
	public void setUseTriplet(boolean useTriplet) {
		this.useTriplet = useTriplet;
	}

	/**
	 * Clear the current MML text.
	 */
	void clear()
	{
		mmlEventList.clear();
	}

	/**
	 * Get if the first note is processed or not.
	 * @return true if the first note is not processed yet.
	 */
	public boolean isFirstNote() {
		return firstNote;
	}

	/**
	 * Set if the first note is processed or not.
	 * @param firstNote true if the first note is not processed yet.
	 */
	public void setFirstNote(boolean firstNote) {
		this.firstNote = firstNote;
	}

	/**
	 * Get if conversion is already finished.
	 * @return True if conversion is already finished.
	 */
	public boolean isFinished() {
		return finished;
	}

	/**
	 * Set if conversion is already finished.
	 * @return True if conversion is already finished.
	 */
	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	/**
	 * Appends the specified MML event.
	 * @param str MML event.
	 */
	public void add(MMLEvent event)
	{
		mmlEventList.add(event);
	}

	/**
	 * Appends the specified MML events.
	 * @param str Collection of MML events.
	 */
	public void addAll(Collection<MMLEvent> events)
	{
		mmlEventList.addAll(events);
	}

	/**
	 * Returns true if, and only if, length() is 0.
	 * @return true if length() is 0, otherwise false
	 */
	public boolean isEmpty()
	{
		return mmlEventList.size() == 0;
	}

	/**
	 * Write the final MML.
	 * @param writer Destination to write MML text.
	 * @throws IOException throws if I/O error is happened.
	 */
	void writeMML(Writer writer) throws IOException
	{
		if (mmlEventList.size() != 0)
		{
			StringBuffer mmlBuffer = new StringBuffer();
			for (MMLEvent event : mmlEventList)
			{
				mmlBuffer.append(event.toString());
			}

			String mmlString = mmlBuffer.toString();
			if (useTriplet)
				mmlString = convertToTriplet(mmlString);
			writer.write(mmlString);

			if (!mmlString.endsWith(System.getProperty("line.separator")))
			{
				writer.write(System.getProperty("line.separator"));
			}
		}
	}

	/**
	 * Use triplet rather than a simple note.
	 */
	private String convertToTriplet(String mmlString)
	{
		for (int i = 0; i < 8; i++)
		{
			int noteLenTo = 1 << i;
			int noteLenFrom = noteLenTo * 3;
			mmlString = mmlString.replaceAll("([<>]*[abcdefgr\\^][\\+\\-]*)" + noteLenFrom + "(\\s*[<>]*[abcdefgr\\^][\\+\\-]*)" + noteLenFrom + "(\\s*[<>]*[abcdefgr\\^][\\+\\-]*)" + noteLenFrom, "\\" + mmlSymbol.getTripletStart(noteLenTo) + "$1" + (mmlSymbol.shouldTripletHaveLengthInBracket() ? "\\" + noteLenTo : "") + "$2" + (mmlSymbol.shouldTripletHaveLengthInBracket() ? "\\" + noteLenTo : "") + "$3" + (mmlSymbol.shouldTripletHaveLengthInBracket() ? "\\" + noteLenTo : "") + "\\" + mmlSymbol.getTripletEnd(noteLenTo)); // c12c12c12 -> {c4c4c4}
			if (mmlSymbol.shouldTripletHaveLengthInBracket())
			{
				mmlString = mmlString.replaceAll("([<>]*[abcdefgr\\^][\\+\\-]*)" + (noteLenFrom * 2) + "(\\s*[<>]*[abcdefgr\\^][\\+\\-]*)" + noteLenFrom, "\\" + mmlSymbol.getTripletStart(noteLenTo) + "$1\\" + (noteLenTo * 2) + "$2\\" + noteLenTo + "\\" + mmlSymbol.getTripletEnd(noteLenTo)); // c6c12 -> {c2c4}
				mmlString = mmlString.replaceAll("([<>]*[abcdefgr\\^][\\+\\-]*)" + noteLenFrom + "(\\s*[<>]*[abcdefgr\\^][\\+\\-]*)" + (noteLenFrom * 2), "\\" + mmlSymbol.getTripletStart(noteLenTo) + "$1\\" + noteLenTo + "$2\\" + (noteLenTo * 2) + "\\" + mmlSymbol.getTripletEnd(noteLenTo)); // c12c6 -> {c4c2}
			}
			else
			{
				mmlString = mmlString.replaceAll("([<>]*[abcdefgr\\^][\\+\\-]*)" + (noteLenFrom * 2) + "(\\s*[<>]*[abcdefgr\\^][\\+\\-]*)" + noteLenFrom, "\\" + mmlSymbol.getTripletStart(noteLenTo) + "$1\\" + mmlSymbol.getTie() + "$2\\" + mmlSymbol.getTripletEnd(noteLenTo)); // c6c12 -> {cc^}
				mmlString = mmlString.replaceAll("([<>]*[abcdefgr\\^][\\+\\-]*)" + noteLenFrom + "(\\s*[<>]*[abcdefgr\\^][\\+\\-]*)" + (noteLenFrom * 2), "\\" + mmlSymbol.getTripletStart(noteLenTo) + "$1$2\\" + mmlSymbol.getTie() + "\\" + mmlSymbol.getTripletEnd(noteLenTo)); // c12c6 -> {c^c}
			}
		}
		return mmlString;
	}
}
