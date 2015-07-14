package com.googlecode.loveemu.PetiteMM;

public class MMLSymbol {

	/**
	 * MML note name table.
	 */
	private String[] notes = { "c", "c+", "d", "d+", "e", "f", "f+", "g", "g+", "a", "a+", "b" };

	/**
	 * MML text for rest.
	 */
	private String rest = "r";

	/**
	 * MML text for tie.
	 */
	private String tie = "^";

	/**
	 * MML text for setting octave.
	 */
	private String octave = "o";

	/**
	 * MML text for tempo.
	 */
	private String tempo = "t";

	/**
	 * MML text for instrument.
	 */
	private String instrument = "@";

	/**
	 * MML text for increasing octave.
	 */
	private String octaveUp = "<";

	/**
	 * MML text for decreasing octave.
	 */
	private String octaveDown = ">";

	/**
	 * MML text for end of track.
	 */
	private String trackEnd = ";";

	/**
	 * MML text for triplet start.
	 */
	private String tripletStart = "{";

	/**
	 * MML text for triplet end.
	 */
	private String tripletEnd = "}";

	/**
	 * true if triplet should have length in bracket. ({c4c4c4} or {ccc})
	 */
	private boolean tripletHaveLengthInBracket = true;

	/**
	 * Construct a new MML symbol set.
	 */
	public MMLSymbol()
	{
	}

	/**
	 * Construct a new MML symbol set.
	 * @param obj
	 */
	public MMLSymbol(MMLSymbol obj)
	{
		notes = obj.notes;
		rest = obj.rest;
		tie = obj.tie;
		octave = obj.octave;
		tempo = obj.tempo;
		instrument = obj.instrument;
		octaveUp = obj.octaveUp;
		octaveDown = obj.octaveDown;
		trackEnd = obj.trackEnd;
		tripletStart = obj.tripletStart;
		tripletEnd = obj.tripletEnd;
	}

	public String getNote(int index) {
		return notes[index];
	}

	public String[] getNotes() {
		return notes;
	}

	public void setNotes(String[] notes) {
		this.notes = notes;
	}

	public String getRest() {
		return rest;
	}

	public void setRest(String rest) {
		this.rest = rest;
	}

	public String getTie() {
		return tie;
	}

	public void setTie(String tie) {
		this.tie = tie;
	}

	public String getOctave() {
		return octave;
	}

	public void setOctave(String octave) {
		this.octave = octave;
	}

	public String getTempo() {
		return tempo;
	}

	public void setTempo(String tempo) {
		this.tempo = tempo;
	}

	public String getInstrument() {
		return instrument;
	}

	public void setInstrument(String instrument) {
		this.instrument = instrument;
	}

	public String getOctaveUp() {
		return octaveUp;
	}

	public void setOctaveUp(String octaveUp) {
		this.octaveUp = octaveUp;
	}

	public String getOctaveDown() {
		return octaveDown;
	}

	public void setOctaveDown(String octaveDown) {
		this.octaveDown = octaveDown;
	}

	public String getTrackEnd() {
		return trackEnd;
	}

	public void setTrackEnd(String trackEnd) {
		this.trackEnd = trackEnd;
	}

	public String getTripletStart(int totalLength) {
		return tripletStart;
	}

	public void setTripletStart(String tripletStart) {
		this.tripletStart = tripletStart;
	}

	public String getTripletEnd(int totalLength) {
		return tripletEnd;
	}

	public void setTripletEnd(String tripletEnd) {
		this.tripletEnd = tripletEnd;
	}

	public boolean shouldTripletHaveLengthInBracket() {
		return tripletHaveLengthInBracket;
	}

	public void setTripletHaveLengthInBracket(boolean tripletHaveLengthInBracket) {
		this.tripletHaveLengthInBracket = tripletHaveLengthInBracket;
	}
}
