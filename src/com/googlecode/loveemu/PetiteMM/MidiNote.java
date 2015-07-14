package com.googlecode.loveemu.PetiteMM;

public class MidiNote {

	private int channel;

	private long time;

	private long length;

	private int noteNumber;

	private int velocity;

	public MidiNote(int channel, long time, long length, int noteNumber, int velocity) {
		this.setChannel(channel);
		this.setTime(time);
		this.setLength(length);
		this.setNoteNumber(noteNumber);
		this.setVelocity(velocity);
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public int getNoteNumber() {
		return noteNumber;
	}

	public void setNoteNumber(int noteNumber) {
		this.noteNumber = noteNumber;
	}

	public int getVelocity() {
		return velocity;
	}

	public void setVelocity(int velocity) {
		this.velocity = velocity;
	}

	@Override
	public String toString() {
		return String.format("MidiNote [channel=%d, time=%d, length=%d, noteNumber=%d, velocity=%d]",
				channel, time, length, noteNumber, velocity);
	}
}
