import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;

import com.googlecode.loveemu.PetiteMM.Midi2MML;

public class PetiteMM {

	/**
	 * Removes the extension from a filename.
	 * @param filename the filename to query, null returns null
	 * @return the filename minus the extension
	 */
	public static String removeExtension(String filename)
	{
		if (filename == null)
			return null;

		int extensionIndex = filename.lastIndexOf(".");
		if (extensionIndex == -1)
			return filename;

		String separator = System.getProperty("file.separator");
		int lastSeparatorIndex = filename.lastIndexOf(separator);
		if (extensionIndex > lastSeparatorIndex)
			return filename.substring(0, extensionIndex);
		else
			return filename;
	}

	/**
	 * Convert the given MIDI file into MML.
	 * @param args Parameters, specify the empty array for details.
	 */
	public static void main(String[] args)
	{
		boolean showAbout = false;
		Midi2MML opt = new Midi2MML();
		String mmlFileName = null;

		// list of available option switches
		final String[] argsAvail = {
				"-o", "<filename>", "Specify the output MML filename.",
				"--dots", "<count>", "Maximum dot counts allowed for dotted-note, -1 for infinity. (default=" + Midi2MML.DEFAULT_MAX_DOT_COUNT + ")",
				"--timebase", "<TPQN>", "Timebase of target MML, " + Midi2MML.RESOLUTION_AS_IS + " to keep the input timebase. (default=" + Midi2MML.DEFAULT_RESOLUTION + ")",
				"--input-timebase", "<TPQN>", "Timebase of input sequence, " + Midi2MML.RESOLUTION_AS_IS + " to keep the input timebase. (default=" + Midi2MML.RESOLUTION_AS_IS + ")",
				"--quantize-precision", "<length>", "Specify the minimum note length for quantization.",
				"--no-quantize", "", "Prevent adjusting note length. Result will be more accurate but more complicated.",
				"--octave-reverse", "", "Swap the octave symbol.",
				"--use-triplet", "", "Use triplet syntax if possible. (really not so smart)",
		};

		int argi = 0;

		//args = new String[] { "test.mid" };

		// dispatch option switches
		while (argi < args.length && args[argi].startsWith("-"))
		{
			if (args[argi].equals("-o"))
			{
				if (argi + 1 >= args.length)
				{
					throw new IllegalArgumentException("Too few arguments for " + args[argi]);
				}
				mmlFileName = args[argi + 1];
				argi += 1;
			}
			else if (args[argi].equals("--dots"))
			{
				if (argi + 1 >= args.length)
				{
					throw new IllegalArgumentException("Too few arguments for " + args[argi]);
				}
				opt.setMaxDots(Integer.parseInt(args[argi + 1]));
				argi += 1;
			}
			else if (args[argi].equals("--timebase"))
			{
				if (argi + 1 >= args.length)
				{
					throw new IllegalArgumentException("Too few arguments for " + args[argi]);
				}
				opt.setTargetResolution(Integer.parseInt(args[argi + 1]));
				argi += 1;
			}
			else if (args[argi].equals("--input-timebase"))
			{
				if (argi + 1 >= args.length)
				{
					throw new IllegalArgumentException("Too few arguments for " + args[argi]);
				}
				opt.setInputResolution(Integer.parseInt(args[argi + 1]));
				argi += 1;
			}
			else if (args[argi].equals("--quantize-precision"))
			{
				if (argi + 1 >= args.length)
				{
					throw new IllegalArgumentException("Too few arguments for " + args[argi]);
				}
				opt.setQuantizePrecision(Integer.parseInt(args[argi + 1]));
				argi += 1;
			}
			else if (args[argi].equals("--no-quantize"))
			{
				opt.setQuantizationEnabled(false);
			}
			else if (args[argi].equals("--octave-reverse"))
			{
				opt.setOctaveReversed(true);
			}
			else if (args[argi].equals("--use-triplet"))
			{
				opt.setTripletPreference(true);
			}
			else
			{
				throw new IllegalArgumentException("Unsupported option [" + args[argi] + "]");
			}
			argi++;
		}

		// show about the program and exit, if needed
		if (argi >= args.length || showAbout)
		{
			System.out.println(Midi2MML.NAME + " " + Midi2MML.VERSION + " by " + Midi2MML.AUTHOR);
			System.out.println(Midi2MML.WEBSITE);
			System.out.println();

			System.out.println("Syntax: PetiteMM <options> input.mid");
			if (argsAvail.length > 0)
				System.out.println("Options:");
			for (int i = 0; i < argsAvail.length / 3; i++)
			{
				System.out.format("%-20s %-9s %s\n", argsAvail[i * 3], argsAvail[i * 3 + 1], argsAvail[i * 3 + 2]);
			}

			System.exit(1);
		}

		// target must be a single file
		if (argi + 1 < args.length)
		{
			throw new IllegalArgumentException("Too many arguments.");
		}

		// convert the given file
		File midiFile = new File(args[argi]);
		if (mmlFileName == null)
		{
			mmlFileName = PetiteMM.removeExtension(args[argi]) + ".mml";
		}
		File mmlFile = new File(mmlFileName);

		Midi2MML converter = new Midi2MML(opt);
		FileWriter fileWriter = null;
		boolean succeeded = false;
		try {
			if (!midiFile.exists())
				throw new FileNotFoundException(midiFile.getName() + " (The system cannot find the file specified)");

			StringWriter strWriter = new StringWriter();
			BufferedWriter writer = new BufferedWriter(strWriter);
			converter.writeMML(MidiSystem.getSequence(midiFile), writer);
			writer.flush();

			fileWriter = new FileWriter(mmlFile);
			fileWriter.write(strWriter.toString());

			succeeded = true;
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fileWriter != null)
			{
				try {
					fileWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		System.exit(succeeded ? 0 : 1);
	}

}
