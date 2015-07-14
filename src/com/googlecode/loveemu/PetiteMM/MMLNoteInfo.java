package com.googlecode.loveemu.PetiteMM;

public class MMLNoteInfo {

	/**
	 * Constant number for rest.
	 */
	public final static int KEY_REST = -1000;

	/**
	 * Raw MML text for the note.
	 */
	private String text;

	/**
	 * MML symbol set.
	 */
	private MMLSymbol mmlSymbol;

	/**
	 * Construct a new note info.
	 * @param mmlSymbol MML symbol set.
	 */
	public MMLNoteInfo(MMLSymbol mmlSymbol) {
		this.mmlSymbol = mmlSymbol;
	}

	/**
	 * Construct a new note info.
	 * @param mmlSymbol MML symbol set.
	 * @param text MML for the note, $N will be replaced to a requested key at getText().
	 */
	public MMLNoteInfo(MMLSymbol mmlSymbol, String text) {
		this.mmlSymbol = mmlSymbol;
		setText(text);
	}

	/**
	 * Construct a new note info.
	 * @param mmlSymbol MML symbol set.
	 * @param note Source note info.
	 */
	public MMLNoteInfo(MMLSymbol mmlSymbol, MMLNoteInfo note) {
		this.mmlSymbol = mmlSymbol;
		setText(note.getText());
	}

	/**
	 * Get the MML text of a note.
	 */
	public String getText() {
		return text;
	}

	/**
	 * Get the MML text of a note.
	 * @param key Key of note, specify KEY_REST for a rest.
	 * @return MML text for the note. (octave will not be included)
	 */
	public String getText(int key) {
		if (text == null)
		{
			return null;
		}
		else if (key == KEY_REST)
		{
			return text.replaceAll("\\$N", mmlSymbol.getRest()).replaceAll("\\" + mmlSymbol.getTie(), "");
		}
		else
		{
			int keyIndex;
			if (key >= 0)
			{
				keyIndex = key % mmlSymbol.getNotes().length;
			}
			else
			{
				keyIndex = -(-key % mmlSymbol.getNotes().length);
				if (keyIndex < 0)
				{
					keyIndex += mmlSymbol.getNotes().length;
				}
			}
			return text.replaceAll("\\$N", mmlSymbol.getNote(keyIndex));
		}
	}

	/**
	 * Set the MML text of a note.
	 * @param text MML for the note, $N will be replaced to a requested key at getText().
	 */
	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return getText();
	}
}
