package com.googlecode.loveemu.PetiteMM;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

public class MidiUtil {

	/**
	 * Maximum channel count in MIDI message.
	 */
	public final static int MIDI_MAX_CHANNELS = 16;

	public static final int META_SEQUENCE_NUMBER = 0x00;
	public static final int META_TEXT = 0x01;
	public static final int META_COPYRIGHT = 0x02;
	public static final int META_SEQUENCE_NAME = 0x03;
	public static final int META_TRACK_NAME = 0x03;
	public static final int META_INSTRUMENT_NAME = 0x04;
	public static final int META_LYRIC = 0x05;
	public static final int META_MARKER = 0x06;
	public static final int META_CUE_POINT = 0x07;
	public static final int META_PROGRAM_NAME = 0x08;
	public static final int META_DEVICE_NAME = 0x09;
	public static final int META_MIDI_CHANNEL_PREFIX = 0x20;
	public static final int META_MIDI_PORT = 0x21;
	public static final int META_END_OF_TRACK = 0x2f;
	public static final int META_TEMPO = 0x51;
	public static final int META_SMPTE_OFFSET = 0x54;
	public static final int META_TIME_SIGNATURE = 0x58;
	public static final int META_KEY_SIGNATURE = 0x59;

	/**
	 * Read variable length number from byte array.
	 * @param data Byte array to be read.
	 * @param index Offset to the variable length number.
	 * @return Decoded number.
	 */
	public static long readVarInt(byte[] data, int index)
	{
		long value = 0; // the variable-length int value
		int currentByte = 0;
		do {
			currentByte = data[index++] & 0xFF;
			value = (value << 7) + (currentByte & 0x7F);
		} while ((currentByte & 0x80) != 0);
		return value;
	}

	/**
	 * Separate tracks which contain messages to multiple channels.
	 * @param seq Sequence to be processed.
	 * @return New sequence which does not contain mixed-channel tracks.
	 * @throws InvalidMidiDataException throw if MIDI data is invalid.
	 */
	public static Sequence SeparateMixedChannel(Sequence sourceSeq) throws InvalidMidiDataException
	{
		Sequence seq = new Sequence(sourceSeq.getDivisionType(), sourceSeq.getResolution());

		// process all input tracks
		for (int trackIndex = 0; trackIndex < sourceSeq.getTracks().length; trackIndex++)
		{
			Track sourceTrack = sourceSeq.getTracks()[trackIndex];

			List<Track> targetTracks = new ArrayList<Track>();
			List<Integer> targetChannels = new ArrayList<Integer>();

			targetTracks.add(seq.createTrack());
			targetChannels.add(null);

			// process all events
			for (int eventIndex = 0; eventIndex < sourceTrack.size(); eventIndex++)
			{
				MidiEvent event = sourceTrack.get(eventIndex);
				if (event.getMessage() instanceof ShortMessage)
				{
					// channel message
					ShortMessage message = (ShortMessage) event.getMessage();
					int targetIndex;

					if (targetChannels.get(0) == null)
					{
						// set the channel number
						targetChannels.set(0, message.getChannel());
						targetIndex = 0;
					}
					else if ((targetIndex = targetChannels.indexOf(message.getChannel())) == -1)
					{
						targetIndex = targetChannels.size();
						targetTracks.add(seq.createTrack());
						targetChannels.add(message.getChannel());
					}

					Track targetTrack = targetTracks.get(targetIndex);
					targetTrack.add(event);
				}
				else
				{
					// non-channel message
					boolean addToAll = false;
					if (event.getMessage() instanceof MetaMessage)
					{
						MetaMessage message = (MetaMessage) event.getMessage();
						if (message.getType() == MidiUtil.META_END_OF_TRACK)
						{
							addToAll = true;
						}
					}

					if (addToAll)
					{
						for (Track targetTrack : targetTracks)
						{
							targetTrack.add(event);
						}
					}
					else
					{
						targetTracks.get(0).add(event);
					}
				}
			}
		}
		return seq;
	}

	/**
	 * Change resolution (TPQN) and retiming events.
	 * @param seq Sequence to be processed.
	 * @param resolution Ticks per quarter note of new sequence.
	 * @return New sequence with new resolution.
	 * @throws InvalidMidiDataException throw if MIDI data is invalid.
	 */
	public static Sequence ChangeResolution(Sequence sourceSeq, int resolution) throws InvalidMidiDataException
	{
		// sequence must be tick-based
		if (sourceSeq.getDivisionType() != Sequence.PPQ)
		{
			throw new UnsupportedOperationException("SMPTE is not supported.");
		}

		Sequence seq = new Sequence(sourceSeq.getDivisionType(), resolution);

		// process all input tracks
		for (int trackIndex = 0; trackIndex < sourceSeq.getTracks().length; trackIndex++)
		{
			Track sourceTrack = sourceSeq.getTracks()[trackIndex];
			Track track = seq.createTrack();

			// process all events
			double timingRate = (double) resolution / sourceSeq.getResolution();
			for (int eventIndex = 0; eventIndex < sourceTrack.size(); eventIndex++)
			{
				MidiEvent sourceEvent = sourceTrack.get(eventIndex);
				MidiEvent event = new MidiEvent(sourceEvent.getMessage(), Math.round(sourceEvent.getTick() * timingRate));
				track.add(event);
			}
		}

		// if the target resolution is shorter than source resolution,
		// events at different timing might be located at the same timing.
		// As a result, there might be zero-length note and/or
		// same control changes at the same timing.
		// 
		// Probably, they should be removed for better conversion.
		// I do not remove them anyway at the moment,
		// because it does not cause any major problems.

		return seq;
	}

	/**
	 * Change resolution (TPQN) without retiming events.
	 * @param seq Sequence to be processed.
	 * @param resolution Ticks per quarter note of new sequence.
	 * @param adjustTempo true if adjust the tempo value to keep the song tempo.
	 * @return New sequence with new resolution.
	 * @throws InvalidMidiDataException throw if MIDI data is invalid.
	 */
	public static Sequence AssumeResolution(Sequence sourceSeq, int resolution, boolean adjustTempo) throws InvalidMidiDataException
	{
		// sequence must be tick-based
		if (sourceSeq.getDivisionType() != Sequence.PPQ)
		{
			throw new UnsupportedOperationException("SMPTE is not supported.");
		}

		Sequence seq = new Sequence(sourceSeq.getDivisionType(), resolution);

		// process all input tracks
		double tempoScale = (double) sourceSeq.getResolution() / seq.getResolution();
		for (int trackIndex = 0; trackIndex < sourceSeq.getTracks().length; trackIndex++)
		{
			Track sourceTrack = sourceSeq.getTracks()[trackIndex];
			Track track = seq.createTrack();

			// process all events
			for (int eventIndex = 0; eventIndex < sourceTrack.size(); eventIndex++)
			{
				MidiEvent sourceEvent = sourceTrack.get(eventIndex);
				MidiEvent event = new MidiEvent(sourceEvent.getMessage(), sourceEvent.getTick());
				if (adjustTempo)
				{
					if (event.getMessage() instanceof MetaMessage)
					{
						MetaMessage message = (MetaMessage) event.getMessage();
						if (message.getType() == MidiUtil.META_TEMPO)
						{
							byte[] data = message.getData();
							if (data.length != 3)
							{
								throw new InvalidMidiDataException("Illegal tempo event.");
							}

							int sourceTempo = ((data[0] & 0xff) << 16) | ((data[1] & 0xff) << 8) | (data[2] & 0xff);
							int newTempo = (int) Math.floor(sourceTempo / tempoScale);
							data = new byte[] { (byte) ((newTempo >> 16) & 0xff), (byte) ((newTempo >> 8) & 0xff), (byte) (newTempo & 0xff) };
							message.setMessage(MidiUtil.META_TEMPO, data, data.length);
						}
					}
				}
				track.add(event);
			}
		}
		return seq;
	}
}
