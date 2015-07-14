package com.googlecode.loveemu.PetiteMM;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

public class Midi2MML {

	/**
	 * Name of the tool.
	 */
	public final static String NAME = "PetiteMM";

	/**
	 * Version of the tool.
	 */
	public final static String VERSION = "2013-09-02";

	/**
	 * Author of the tool.
	 */
	public final static String AUTHOR = "loveemu, gocha";

	/**
	 * Website of the tool.
	 */
	public final static String WEBSITE = "http://loveemu.googlecode.com/";

	/**
	 * Constant for using the input resolution.
	 */
	public final static int RESOLUTION_AS_IS = 0;

	/**
	 * Constant for the maximum precision.
	 */
	public static final int QUANTIZE_PRECISION_AS_IS = 0;

	/**
	 * Default ticks per quarter note of target MML.
	 */
	public final static int DEFAULT_RESOLUTION = 48;

	/**
	 * Default quantize precision value.
	 */
	public final static int DEFAULT_QUANTIZE_PRECISION = 32;

	/**
	 * Default maximum dot count for dotted note.
	 */
	public final static int DEFAULT_MAX_DOT_COUNT = -1;

	/**
	 * MML symbol set.
	 */
	private MMLSymbol mmlSymbol;

	/**
	 * Ticks per quarter of input MIDI. (0: as is)
	 */
	private int inputResolution = RESOLUTION_AS_IS;

	/**
	 * Ticks per quarter note of target MML. (0: same as input)
	 */
	private int targetResolution = DEFAULT_RESOLUTION;

	/**
	 * Minimum note length for quantization.
	 */
	private int quantizePrecision = DEFAULT_QUANTIZE_PRECISION;

	/**
	 * Maximum dot counts allowed for dotted-note.
	 */
	private int maxDots = DEFAULT_MAX_DOT_COUNT;

	/**
	 * true if adjust note length for simplifying the conversion result.
	 */
	private boolean quantizationEnabled = true;

	/**
	 * true if reverse the octave up/down effect.
	 */
	private boolean octaveReversed = false;

	/**
	 * true if replace triple single notes to triplet.
	 */
	private boolean useTriplet = false;

	/**
	 * true if write debug informations to stdout.
	 */
	final static private boolean debugDump = false;

	/**
	 * Construct a new MIDI to MML converter.
	 */
	public Midi2MML()
	{
		this(new MMLSymbol());
	}

	/**
	 * Construct a new MIDI to MML converter.
	 * @param mmlSymbol MML symbol set.
	 */
	public Midi2MML(MMLSymbol mmlSymbol)
	{
		this.mmlSymbol = mmlSymbol;
	}

	/**
	 * Construct a new MIDI to MML converter.
	 * @param mmlSymbol MML symbol set.
	 * @param targetResolution Ticks per quarter note of target MML.
	 */
	public Midi2MML(MMLSymbol mmlSymbol, int targetResolution)
	{
		this.mmlSymbol = mmlSymbol;
		this.setTargetResolution(targetResolution);
	}

	/**
	 * Construct a new MIDI to MML converter.
	 * @param mmlSymbol MML symbol set.
	 * @param inputResolution Ticks per quarter note of input sequence.
	 * @param targetResolution Ticks per quarter note of target MML.
	 */
	public Midi2MML(MMLSymbol mmlSymbol, int inputResolution, int targetResolution)
	{
		this.mmlSymbol = mmlSymbol;
		this.setInputResolution(inputResolution);
		this.setTargetResolution(targetResolution);
	}

	/**
	 * Construct a new MIDI to MML converter.
	 * @param obj
	 */
	public Midi2MML(Midi2MML obj)
	{
		mmlSymbol = new MMLSymbol(obj.mmlSymbol);
		maxDots = obj.maxDots;
		quantizationEnabled = obj.quantizationEnabled;
		octaveReversed = obj.octaveReversed;
		useTriplet = obj.useTriplet;
		inputResolution = obj.inputResolution;
		targetResolution = obj.targetResolution;
		quantizePrecision = obj.quantizePrecision;
	}

	/**
	 * Get MML symbol set.
	 * @return MML symbol set.
	 */
	public MMLSymbol getMmlSymbol() {
		return mmlSymbol;
	}

	/**
	 * Set MML symbol set.
	 * @param mmlSymbol
	 */
	public void setMmlSymbol(MMLSymbol mmlSymbol) {
		this.mmlSymbol = mmlSymbol;
	}

	/**
	 * Get the maximum dot counts allowed for dotted-note.
	 * @return Maximum dot counts allowed for dotted-note.
	 */
	public int getMaxDots() {
		return maxDots;
	}

	/**
	 * Set the maximum dot counts allowed for dotted-note.
	 * @param mmlMaxDotCount Maximum dot counts allowed for dotted-note.
	 */
	public void setMaxDots(int mmlMaxDotCount) {
		if (mmlMaxDotCount < -1)
			throw new IllegalArgumentException("Maximum dot count must be a positive number or -1.");
		this.maxDots = mmlMaxDotCount;
	}

	/**
	 * Get whether the quantization logic is enabled.
	 * @return true if adjust note length for simplifying the conversion result.
	 */
	public boolean isQuantizationEnabled() {
		return quantizationEnabled;
	}

	/**
	 * Set whether the quantization logic is enabled.
	 * @param quantizationEnabled true if adjust note length for simplifying the conversion result.
	 */
	public void setQuantizationEnabled(boolean quantizationEnabled) {
		this.quantizationEnabled = quantizationEnabled;
	}

	/**
	 * Get if the octave up/down effect is reversed.
	 * @return True if reverse the octave up/down effect.
	 */
	public boolean isOctaveReversed() {
		return octaveReversed;
	}

	/**
	 * Set if the octave up/down effect is reversed.
	 * @param octaveReversed True if reverse the octave up/down effect.
	 */
	public void setOctaveReversed(boolean octaveReversed) {
		this.octaveReversed = octaveReversed;
	}

	public int getInputResolution() {
		return inputResolution;
	}

	public void setInputResolution(int inputResolution) {
		this.inputResolution = inputResolution;
	}

	/**
	 * Get TPQN of target MML.
	 * @return Ticks per quarter note of target MML.
	 */
	public int getTargetResolution() {
		return targetResolution;
	}

	/**
	 * Set TPQN of target MML.
	 * @param targetResolution Ticks per quarter note of target MML.
	 */
	public void setTargetResolution(int targetResolution) {
		if (targetResolution != RESOLUTION_AS_IS && targetResolution % 4 != 0)
			throw new IllegalArgumentException("TPQN must be multiple of 4.");
		this.targetResolution = targetResolution;
	}

	/**
	 * Get minimum note length for quantization.
	 * @return Minimum note length. (must be power of 2)
	 */
	public int getQuantizePrecision() {
		return quantizePrecision;
	}

	/**
	 * Set minimum note length for quantization.
	 * @param quantizePrecision Minimum note length. (must be power of 2)
	 */
	public void setQuantizePrecision(int quantizePrecision) {
		if (quantizePrecision != QUANTIZE_PRECISION_AS_IS && (quantizePrecision & (quantizePrecision - 1)) != 0)
			throw new IllegalArgumentException("Quantize precision must be power of 2.");
		this.quantizePrecision = quantizePrecision;
	}

	/**
	 * Write MML of given sequence.
	 * @param seq Sequence to be converted.
	 * @param writer Destination to write MML text.
	 * @throws IOException throws if I/O error is happened.
	 * @throws UnsupportedOperationException throws if the situation is not supported.
	 * @throws InvalidMidiDataException throws if unexpected MIDI event is appeared.
	 */
	public void writeMML(Sequence seq, Writer writer) throws IOException, UnsupportedOperationException, InvalidMidiDataException
	{
		// sequence must be tick-based
		if (seq.getDivisionType() != Sequence.PPQ)
		{
			throw new UnsupportedOperationException("SMPTE is not supported.");
		}

		// preprocess
		if (inputResolution != RESOLUTION_AS_IS)
			seq = MidiUtil.AssumeResolution(seq, inputResolution, true);
		// the converter assumes that all events in a track are for a single channel,
		// when the input file is SMF format 0 or something like that, it requires preprocessing.
		seq = MidiUtil.SeparateMixedChannel(seq);
		// adjust resolution for MML conversion
		if (targetResolution != RESOLUTION_AS_IS)
			seq = MidiUtil.ChangeResolution(seq, targetResolution);

		// get track count (this must be after the preprocess)
		int trackCount = seq.getTracks().length;

		// scan end timing for each tracks
		long[] midiTracksEndTick = new long[trackCount];
		for (int trackIndex = 0; trackIndex < trackCount; trackIndex++)
		{
			Track track = seq.getTracks()[trackIndex];
			midiTracksEndTick[trackIndex] = track.get(track.size() - 1).getTick();
		}

		// scan MIDI notes
		List<List<MidiNote>> midiTrackNotes = getMidiNotes(seq);

		// scan time signatures
		List<MidiTimeSignature> timeSignatures;
		try
		{
			timeSignatures = getMidiTimeSignatures(seq);
		} catch (InvalidMidiDataException e) {
			System.err.println("Warning: " + e.getMessage());
			timeSignatures = new ArrayList<MidiTimeSignature>();
			timeSignatures.add(new MidiTimeSignature(4, 2));
		}

		if (debugDump)
		{
			for (MidiTimeSignature timeSignature : timeSignatures)
				System.out.println(timeSignature);
		}

		// reset track parameters
		Midi2MMLTrack[] mmlTracks = new Midi2MMLTrack[trackCount];
		int[] noteIndex = new int[trackCount];
		int[] currNoteIndex = new int[trackCount];
		for (int trackIndex = 0; trackIndex < trackCount; trackIndex++)
		{
			mmlTracks[trackIndex] = new Midi2MMLTrack(mmlSymbol);
			mmlTracks[trackIndex].setUseTriplet(useTriplet);
		}
		// reset subsystems
		MMLNoteConverter noteConv = new MMLNoteConverter(mmlSymbol, seq.getResolution(), maxDots);

		// convert tracks at the same time
		// reading tracks one by one would be simpler than the tick-based loop,
		// but it would limit handling a global event such as time signature.
		long tick = 0;
		boolean mmlFinished = false;
		while (!mmlFinished)
		{
			for (int trackIndex = 0; trackIndex < trackCount; trackIndex++)
			{
				Midi2MMLTrack mmlTrack = mmlTracks[trackIndex];
				Track track = seq.getTracks()[trackIndex];
				List<MidiNote> midiNotes = midiTrackNotes.get(trackIndex);

				while (!mmlTrack.isFinished())
				{
					// stop conversion when all events are dispatched
					if (mmlTrack.getMidiEventIndex() >= track.size())
					{
						mmlTrack.setFinished(true);
						break;
					}

					// get next MIDI message
					MidiEvent event = track.get(mmlTrack.getMidiEventIndex());
					if (event.getTick() != tick)
					{
						break;
					}
					mmlTrack.setMidiEventIndex(mmlTrack.getMidiEventIndex() + 1);

					// dump for debug
					if (debugDump)
						System.out.format("MidiEvent: track=%d,tick=%d<%s>,message=%s\n", trackIndex, event.getTick(), MidiTimeSignature.getMeasureTickString(event.getTick(), timeSignatures, seq.getResolution()), byteArrayToString(event.getMessage().getMessage()));

					// branch by event type for more detailed access
					List<MMLEvent> mmlEvents = new ArrayList<MMLEvent>();
					long mmlLastTick = mmlTrack.getTick();
					int mmlLastNoteNumber = mmlTrack.getNoteNumber();
					boolean mmlKeepCurrentNote = (mmlLastNoteNumber != MMLNoteConverter.KEY_REST);
					if (event.getMessage() instanceof ShortMessage)
					{
						ShortMessage message = (ShortMessage)event.getMessage();

						if (message.getCommand() == ShortMessage.NOTE_OFF ||
								(message.getCommand() == ShortMessage.NOTE_ON && message.getData2() == 0))
						{
							MidiNote midiNextNote = (currNoteIndex[trackIndex] + 1 < midiNotes.size()) ? midiNotes.get(currNoteIndex[trackIndex] + 1) : null;
							long minLength = tick - mmlLastTick;
							long maxLength = ((midiNextNote != null) ? midiNextNote.getTime() : midiTracksEndTick[trackIndex]) - mmlLastTick;
							if (message.getData1() == mmlTrack.getNoteNumber() && minLength != 0)
							{
								long length = minLength;
								if (quantizationEnabled)
								{
									long wholeNoteCount = (minLength - 1) / (seq.getResolution() * 4);

									// remove whole notes temporarily
									minLength -= (seq.getResolution() * 4) * wholeNoteCount;
									maxLength -= (seq.getResolution() * 4) * wholeNoteCount;
	
									// find the nearest 2^n note
									// minLength/nearPow2 is almost always in [0.5,1.0]
									// (almost, because nearPow2 may have slight error at a very short note)
									// nearPow2 can be greater than maxLength
									long nearPow2 = seq.getResolution() * 4;
									while (nearPow2 / 2 >= minLength)
										nearPow2 /= 2;

									List<Double> rateCandidates = new ArrayList<Double>(Arrays.asList(0.5, 1.0));
									int maxDotCount = (maxDots != -1) ? maxDots : Integer.MAX_VALUE;
									double dottedNoteRate = 0.5;
									for (int dot = 1; dot <= maxDotCount; dot++)
									{
										if (nearPow2 % (1 << dot) != 0)
											break;

										dottedNoteRate += Math.pow(0.5, dot + 1);
										rateCandidates.add(dottedNoteRate); // dotted note (0.75, 0.875...)
									}
									if (nearPow2 * 2 % 3 == 0)
										rateCandidates.add(2.0/3.0); // triplet
									Collections.sort(rateCandidates);

									if (debugDump)
									{
										StringBuffer ratesBuffer = new StringBuffer();
										boolean firstItem = true;
										ratesBuffer.append("[");
										for (double rateCandidate : rateCandidates)
										{
											if (firstItem)
												firstItem = false;
											else
												ratesBuffer.append(String.format(",", rateCandidate));
											ratesBuffer.append(String.format("%.3f", rateCandidate));
										}
										ratesBuffer.append("]");
										System.out.println("rateCandidates=" + ratesBuffer.toString());
									}

									double rateLowerLimit = (double) minLength / nearPow2;
									double rateUpperLimit = (double) maxLength / nearPow2;

									long quantizeNoteLength = 0;
									if (quantizePrecision != QUANTIZE_PRECISION_AS_IS)
									{
										quantizeNoteLength = (seq.getResolution() * 4) / quantizePrecision; // can have error
									}

									double rateNearest = 0.0;
									double rateBestDistance = Double.MAX_VALUE;
									for (double rateCandidate : rateCandidates)
									{
										rateCandidate = Math.min(rateCandidate, rateUpperLimit);

										double rateDistance = Math.abs(rateLowerLimit - rateCandidate);
										if (rateDistance <= rateBestDistance)
										{
											boolean rateRequiresUpdate = true;
											if (nearPow2 >= quantizeNoteLength & rateCandidate < rateUpperLimit)
											{
												long noteLengthCandidate = Math.round(nearPow2 * rateCandidate);
												List<Integer> noteLengths = noteConv.getPrimitiveNoteLengths((int)noteLengthCandidate, true);
												rateRequiresUpdate = (noteLengths.get(noteLengths.size() - 1) >= quantizeNoteLength);
											}
											if (rateRequiresUpdate)
											{
												rateNearest = rateCandidate;
												rateBestDistance = rateDistance;
											}
										}
										if  (rateCandidate >= rateUpperLimit)
											break;
									}

									length = Math.round(nearPow2 * rateNearest);

									if (length < minLength)
									{
										List<Integer> restLengths = noteConv.getPrimitiveNoteLengths((int)(maxLength - length), false);
										for (int i = restLengths.size() - 1; i >= 0; i--)
										{
											int restLength = restLengths.get(i);
											if (length + restLength <= minLength)
											{
												length += restLength;
											}
											else
											{
												long oldDistance = minLength - length;
												long newDistance = (length + restLength) - minLength;
												if (newDistance <= oldDistance)
													length += restLength;
												break;
											}
										}
									}

									length += wholeNoteCount * (seq.getResolution() * 4);

									if (debugDump)
										System.out.format("Note Off: track=%d,tick=%d<%s>,mmlLastTick=%d<%s>,length=%d,minLength=%d,maxLength=%d,nearPow2=%d,rateLimit=[%.2f,%.2f],rateNearest=%.2f,next=%s\n", trackIndex, tick, MidiTimeSignature.getMeasureTickString(tick, timeSignatures, seq.getResolution()), mmlLastTick, MidiTimeSignature.getMeasureTickString(mmlLastTick, timeSignatures, seq.getResolution()), length, minLength, maxLength, nearPow2, rateLowerLimit, rateUpperLimit, rateNearest, (midiNextNote != null) ? midiNextNote.toString() : "null");
								}

								mmlTrack.setTick(mmlLastTick + length);
								mmlTrack.setNoteNumber(MMLNoteConverter.KEY_REST);
								mmlKeepCurrentNote = false;
							}
						}
						else if (message.getCommand() == ShortMessage.NOTE_ON)
						{
							int noteNumber = message.getData1();
							int noteOctave = noteNumber / 12;

							// write some initialization for the first note
							if (mmlTrack.isFirstNote())
							{
								mmlTrack.setOctave(noteOctave);
								mmlTrack.setFirstNote(false);
								mmlEvents.add(new MMLEvent(mmlSymbol.getOctave(), new String[] { String.format("%d", noteOctave) }));
							}

							// remember new note
							mmlTrack.setTick(tick);
							mmlTrack.setNoteNumber(noteNumber);
							mmlKeepCurrentNote = false;

							currNoteIndex[trackIndex] = noteIndex[trackIndex];
							noteIndex[trackIndex]++;
						}
						else
						{
							List<MMLEvent> newMML = convertMidiEventToMML(event, mmlTrack);
							if (newMML.size() != 0)
							{
								mmlEvents.addAll(newMML);
								if (tick >= mmlLastTick)
									mmlTrack.setTick(tick);
							}
						}
					}
					else
					{
						List<MMLEvent> newMML = convertMidiEventToMML(event, mmlTrack);
						if (newMML.size() != 0)
						{
							mmlEvents.addAll(newMML);
							if (tick >= mmlLastTick)
								mmlTrack.setTick(tick);
						}
					}

					// final event,
					// seek to the last whether the last event has been dispatched.
					if (mmlTrack.getMidiEventIndex() == track.size())
					{
						if (!mmlTrack.isEmpty())
						{
							if (mmlTrack.getTick() < tick)
							{
								mmlTrack.setTick(tick);
							}
						}
					}

					// timing changed,
					// write the last note/rest and finish the seek
					if (mmlTrack.getTick() != mmlLastTick)
					{
						if (debugDump)
							System.out.format("Timing: track=%d,%d<%s> -> %d<%s>\n", trackIndex, mmlLastTick, MidiTimeSignature.getMeasureTickString(mmlLastTick, timeSignatures, seq.getResolution()), mmlTrack.getTick(), MidiTimeSignature.getMeasureTickString(mmlTrack.getTick(), timeSignatures, seq.getResolution()));

						if (mmlLastNoteNumber == MMLNoteConverter.KEY_REST)
						{
							List<Integer> lengths = noteConv.getPrimitiveNoteLengths((int)(mmlTrack.getTick() - mmlLastTick), false);
							int totalLength = 0;
							for (int length : lengths)
							{
								totalLength += length;
								mmlTrack.add(new MMLEvent(noteConv.getNote(length, mmlLastNoteNumber)));

								int lastMeasure = MidiTimeSignature.getMeasureByTick(mmlLastTick, timeSignatures, seq.getResolution());
								int currentMeasure = MidiTimeSignature.getMeasureByTick(mmlLastTick + totalLength, timeSignatures, seq.getResolution());
								if (currentMeasure != lastMeasure)
								{		
									mmlTrack.add(new MMLEvent(System.getProperty("line.separator")));
									mmlTrack.setMeasure(currentMeasure);
								}
							}
						}
						else
						{
							int mmlOctave = mmlTrack.getOctave();
							int noteOctave = mmlLastNoteNumber / 12;
							while (mmlOctave < noteOctave)
							{
								mmlTrack.add(new MMLEvent(!octaveReversed ? mmlSymbol.getOctaveUp() : mmlSymbol.getOctaveDown()));
								mmlOctave++;
							}
							while (mmlOctave > noteOctave)
							{
								mmlTrack.add(new MMLEvent(!octaveReversed ? mmlSymbol.getOctaveDown() : mmlSymbol.getOctaveUp()));
								mmlOctave--;
							}
							mmlTrack.setOctave(noteOctave);

							mmlTrack.add(new MMLEvent(noteConv.getNote((int)(mmlTrack.getTick() - mmlLastTick), mmlLastNoteNumber)));
							if (mmlKeepCurrentNote)
							{
								mmlTrack.add(new MMLEvent(mmlSymbol.getTie()));
							}

							int lastMeasure = MidiTimeSignature.getMeasureByTick(mmlLastTick, timeSignatures, seq.getResolution());
							int currentMeasure = MidiTimeSignature.getMeasureByTick(mmlTrack.getTick(), timeSignatures, seq.getResolution());
							if (currentMeasure != lastMeasure)
							{
								mmlTrack.add(new MMLEvent(System.getProperty("line.separator")));
								mmlTrack.setMeasure(currentMeasure);
							}
						}
					}

					// event is dispatched,
					// write the new MML command
					if (mmlEvents.size() != 0)
					{
						mmlTrack.addAll(mmlEvents);
					}
				}
			}

			mmlFinished = true;
			for (int trackIndex = 0; trackIndex < trackCount; trackIndex++)
			{
				if (!mmlTracks[trackIndex].isFinished())
				{
					mmlFinished = false;
					break;
				}
			}

			tick++;
		}

		boolean firstTrackWrite = true;
		for (Midi2MMLTrack mmlTrack : mmlTracks)
		{
			if (!mmlTrack.isEmpty())
			{
				if (firstTrackWrite)
					firstTrackWrite = false;
				else
				{
					writer.write(mmlSymbol.getTrackEnd());
					writer.write(System.getProperty("line.separator"));
				}
				mmlTrack.writeMML(writer);
			}
		}
		writer.flush();
	}

	/**
	 * Get MIDI notes from sequence.
	 * @param seq Input MIDI sequence.
	 * @return List of MIDI notes.
	 * @throws InvalidMidiDataException throws if unexpected MIDI event is appeared.
	 */
	private List<List<MidiNote>> getMidiNotes(Sequence seq) throws InvalidMidiDataException
	{
		final int trackCount = seq.getTracks().length;

		List<List<MidiNote>> midiTrackNotes = new ArrayList<List<MidiNote>>(trackCount);
		for (int trackIndex = 0; trackIndex < trackCount; trackIndex++)
		{
			Track track = seq.getTracks()[trackIndex];

			List<MidiNote> midiNotes = new ArrayList<MidiNote>();
			for (int midiEventIndex = 0; midiEventIndex < track.size(); midiEventIndex++)
			{
				MidiEvent event = track.get(midiEventIndex);
				if (event.getMessage() instanceof ShortMessage)
				{
					ShortMessage message = (ShortMessage)event.getMessage();

					if (message.getCommand() == ShortMessage.NOTE_OFF ||
							(message.getCommand() == ShortMessage.NOTE_ON && message.getData2() == 0))
					{
						// search from head, for overlapping notes
						ListIterator<MidiNote> iter = midiNotes.listIterator();
						while (iter.hasNext())
						{
							MidiNote note = iter.next();
							int noteNumber = message.getData1();
							if (note.getLength() == -1 && note.getNoteNumber() == noteNumber)
							{
								note.setLength(event.getTick() - note.getTime());
								break;
							}
						}
					}
					else if (message.getCommand() == ShortMessage.NOTE_ON)
					{
						midiNotes.add(new MidiNote(message.getChannel(), event.getTick(), -1, message.getData1(), message.getData2()));
					}
				}
			}
			for (MidiNote note : midiNotes)
			{
				if (note.getLength() == -1) {
					throw new InvalidMidiDataException("Sequence contains an unfinished note.");
				}
				// dump for debug
				if (debugDump)
					System.out.format("[ch%d/%d] Note (%d) len=%d vel=%d\n", note.getChannel(), note.getTime(), note.getNoteNumber(), note.getLength(), note.getVelocity());
			}
			midiTrackNotes.add(midiNotes);
		}
		return midiTrackNotes;
	}

	/**
	 * Get MIDI time signatures from sequence.
	 * @param seq Input MIDI sequence.
	 * @return List of MIDI time signatures.
	 * @throws InvalidMidiDataException throws if unexpected MIDI event is appeared.
	 */
	private List<MidiTimeSignature> getMidiTimeSignatures(Sequence seq) throws InvalidMidiDataException
	{
		List<MidiTimeSignature> timeSignatures = new ArrayList<MidiTimeSignature>();

		final int trackCount = seq.getTracks().length;
		final int defaultNumerator = 4;
		final int defaultDenominator = 2;

		int numerator = defaultNumerator;
		int denominator = defaultDenominator;
		long measureLength = ((seq.getResolution() * 4 * numerator) >> denominator);
		long nextMeasureTick = measureLength;

		long tick = 0;
		int measure = 0;
		int measureOfLastSignature = -1;
		boolean finished = false;
		int[] eventIndex = new int[trackCount];
		while (!finished)
		{
			if (tick == nextMeasureTick)
			{
				nextMeasureTick += measureLength;
				measure++;
			}

			for (int trackIndex = 0; trackIndex < trackCount; trackIndex++)
			{
				Track track = seq.getTracks()[trackIndex];
				while (eventIndex[trackIndex] < track.size())
				{
					MidiEvent event = track.get(eventIndex[trackIndex]);
					if (event.getTick() != tick)
						break;
					eventIndex[trackIndex]++;

					if (event.getMessage() instanceof MetaMessage)
					{
						MetaMessage message = (MetaMessage)event.getMessage();
						byte[] data = message.getData();

						switch (message.getType())
						{
						case MidiUtil.META_TIME_SIGNATURE:
							if (data.length != 4)
							{
								throw new InvalidMidiDataException("Illegal time signature event.");
							}

							if (nextMeasureTick - measureLength != tick)
							{
								throw new InvalidMidiDataException("Time signature event is not located at the measure boundary.");
							}

							if (measure == measureOfLastSignature)
							{
								throw new InvalidMidiDataException("Two or more time signature event are located at the same time.");
							}

							if (timeSignatures.isEmpty() && measure != 0)
							{
								throw new InvalidMidiDataException("First time signature is not located at the first measure.");
							}

							MidiTimeSignature newTimeSignature = new MidiTimeSignature(data[0] & 0xff, data[1] & 0xff, measure);
							int newMeasureLength = newTimeSignature.getLength(seq.getResolution());
							nextMeasureTick = (nextMeasureTick - measureLength) + newMeasureLength;
							measureLength = newMeasureLength;
							measureOfLastSignature = measure;
							timeSignatures.add(newTimeSignature);
							break;
						}
					}
				}
			}

			finished = true;
			for (int trackIndex = 0; trackIndex < trackCount; trackIndex++)
			{
				Track track = seq.getTracks()[trackIndex];
				if (eventIndex[trackIndex] < track.size())
				{
					finished = false;
					break;
				}
			}

			tick++;
		}
		
		if (timeSignatures.isEmpty())
		{
			timeSignatures.add(new MidiTimeSignature(defaultNumerator, defaultDenominator));
		}

		return timeSignatures;
	}

	/**
	 * Convert specified MIDI event to MML.
	 * @param event MIDI event to be converted.
	 * @param mmlTrack MML track status.
	 * @return Converted text, null if event is ignored.
	 * @throws InvalidMidiDataException throws if unexpected MIDI event is appeared.
	 */
	private List<MMLEvent> convertMidiEventToMML(MidiEvent event, Midi2MMLTrack mmlTrack) throws InvalidMidiDataException
	{
		List<MMLEvent> mmlEvents = new ArrayList<MMLEvent>();
		if (event.getMessage() instanceof ShortMessage)
		{
			ShortMessage message = (ShortMessage)event.getMessage();

			if (message.getCommand() == ShortMessage.NOTE_ON)
			{
				// for some reasons, this function does not dispatch note on.
			}
			else if (message.getCommand() == ShortMessage.PROGRAM_CHANGE)
			{
				//mmlEvents.add(new MMLEvent(MMLSymbol.INSTRUMENT, new String[] { String.format("%d", message.getData1()) }));
			}
		}
		else if (event.getMessage() instanceof MetaMessage)
		{
			MetaMessage message = (MetaMessage)event.getMessage();
			byte[] data = message.getData();

			switch (message.getType())
			{
			case MidiUtil.META_TEMPO:
				if (data.length != 3)
				{
					throw new InvalidMidiDataException("Illegal tempo event.");
				}

				int usLenOfQN = ((data[0] & 0xff) << 16) | ((data[1] & 0xff) << 8) | (data[2] & 0xff);
				double bpm = 60000000.0 / usLenOfQN;
				mmlEvents.add(new MMLEvent(mmlSymbol.getTempo(), new String[] { String.format("%.0f", bpm) }));
				break;
			}
		}
		return mmlEvents;
	}

	/**
	 * Get triplet preference.
	 * @return true if replace triple single notes to triplet.
	 */
	public boolean getTripletPreference() {
		return useTriplet;
	}

	/**
	 * Set triplet preference.
	 * @return true if replace triple single notes to triplet.
	 */
	public void setTripletPreference(boolean useTriplet) {
		this.useTriplet = useTriplet;
	}

	private String byteArrayToString(byte[] bytes) {
		StringBuffer buf = new StringBuffer();
		for (byte b : bytes) {
			if (buf.length() != 0)
				buf.append(" ");
			buf.append(String.format("%02X", b));
		}
		return buf.toString();
	}
}
